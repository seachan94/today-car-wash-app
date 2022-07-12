package com.nenne.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.FusedLocationSource
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.domain.model.state.NetworkResultState
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.model.ClusteredItem
import com.nenne.presentation.util.getClusteredItem
import com.nenne.presentation.util.getLatLng
import com.nenne.presentation.util.setImgFromDrawable
import com.nenne.presentation.util.zoomToDistance
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.CustomMakerBinding
import com.nocompany.presentation.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ted.gun0912.clustering.naver.TedNaverClustering

@AndroidEntryPoint
class CarWashMapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map),
    OnMapReadyCallback {

    val TAG = "sechan"
    private val viewModel: MapViewModel by viewModels()

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                (it.key == Manifest.permission.ACCESS_COARSE_LOCATION) || (it.key == Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (responsePermissions.filter { it.value == true }.size == locationPermission.size) {
                initializeFirstLocationSet()
            } else {
                Toast.makeText(requireContext(), "위치 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private lateinit var mLocationManager: LocationManager
    private lateinit var mNaverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    var isFirstAttach = false
    private val mLocationListener = LocationListener {
        viewModel.currentLocation = it.latitude getLatLng it.longitude
        initializeNaverMap()
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun initViewStatus() = with(binding) {
        locationPermissionLauncher.launch(locationPermission)
        location.setOnClickListener { moveToCameraMyLocation() }
        filter.setOnClickListener { }
        detailLayer.setOnClickListener {
            navigate(R.id.action_mapFragment_to_detailCarWashShop,
                bundleOf("data" to viewModel.detailData.value))
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        searchHere.setOnClickListener {
            searchCarWashLocation()
            viewModel.getReverseGeoCode(mNaverMap.cameraPosition.target)
        }
        vm = viewModel
        observeData()
    }


    private fun initializeNaverMap() = with(binding) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.mapFragment, it).commit()
            }
        mapFragment.getMapAsync(this@CarWashMapFragment)
    }

    private fun moveToCameraMyLocation() {
        if (::mNaverMap.isInitialized) {
            mNaverMap.moveCamera(CameraUpdate.scrollTo(viewModel.currentLocation))
        }
    }

    private fun searchCarWashLocation() {
        val cameraPosition = mNaverMap.cameraPosition
        val latitude = cameraPosition.target.latitude
        val longitude = cameraPosition.target.longitude
        val zoomLevel = cameraPosition.zoom
        viewModel.getCarWashShopAroundHere(latitude, longitude, zoomLevel.zoomToDistance())
    }

    private fun observeData() {

        viewModel.resultState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResultState.Success -> {
                    setClusteredMarker(it.data.map {
                        it.getClusteredItem()
                    })
                }
                else -> {
                    Log.d(TAG, "observeData: $it")
                }
            }
        }

    }


    private fun setClusteredMarker(items: List<ClusteredItem>) {

        if (items.isEmpty()) {
            binding.detailLayer.isGone = true
            Toast.makeText(requireContext(), "근처에 세차장이 없어요", Toast.LENGTH_SHORT).show()
            return
        }

        val nearestShop = items.first()
        if(isFirstAttach){
            isFirstAttach = true
            mNaverMap.cameraPosition = CameraPosition(nearestShop.latitude getLatLng nearestShop.longitude,mNaverMap.cameraPosition.zoom)
        }
        binding.detailLayer.isVisible = true
        viewModel.detailData.value = nearestShop


        val tedClustering =
            TedNaverClustering.with<ClusteredItem>(requireContext(), mNaverMap)
                .customMarker {
                    Marker(it.latitude getLatLng it.longitude).apply {
                        icon = OverlayImage.fromView(getMarkerCustomView(it.type))
                    }
                }
                .markerClickListener {
                    viewModel.detailData.value = it
                    binding.detailLayer.isVisible = true
                }
                .minClusterSize(10)
                .make()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            tedClustering.clearItems()
            tedClustering.addItems(items)
        }

    }

    fun getMarkerCustomView(type: ShopType) = CustomMakerBinding.inflate(layoutInflater).apply {
        when (type) {
            ShopType.AUTO -> {
                shopName.text = "자동 세차"
                carwashImg.setImageResource(R.drawable.location_black)
                shopName.setTextColor(Color.BLACK)
            }
            else -> {
                shopName.text = "셀프 세차"
            }
        }
    }.root

    private fun setWindowInfoAdapter(type: ShopType) = object : InfoWindow.ViewAdapter() {
        override fun getView(p0: InfoWindow): View {
            return CustomMakerBinding.inflate(layoutInflater).apply {
                when (type) {
                    ShopType.AUTO -> {
                        shopName.text = "자동 세차"
                        carwashImg.setImageResource(R.drawable.location_black)
                        shopName.setTextColor(Color.BLACK)
                    }
                    else -> {
                        shopName.text = "셀프 세차"
                    }
                }
            }.root
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeFirstLocationSet() {
        if (::mLocationManager.isInitialized.not()) {
            mLocationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val minTime = 1500L
        val minDistance = 100f
        with(mLocationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, minTime, minDistance, mLocationListener
            )
        }
    }

    @SuppressLint("MissingPermission")
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        mLocationManager.removeUpdates(mLocationListener)
        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
        mNaverMap = naverMap.apply {
            uiSettings.isZoomControlEnabled = false
            locationOverlay.isVisible = true
            locationSource = this@CarWashMapFragment.locationSource
            locationTrackingMode = LocationTrackingMode.Follow
            moveCamera(CameraUpdate.scrollTo(viewModel.currentLocation))
            setOnMapClickListener { _, _ ->
                binding.detailLayer.isGone = !binding.detailLayer.isGone
            }
            addOnLocationChangeListener {
                viewModel.currentLocation = it.latitude getLatLng it.longitude
            }
        }

        viewModel.getReverseGeoCode(viewModel.currentLocation)
        searchCarWashLocation()

    }


    companion object {
        val locationPermission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


}