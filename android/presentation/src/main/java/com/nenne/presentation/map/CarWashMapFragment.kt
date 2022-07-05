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
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.util.FusedLocationSource
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.domain.model.state.NetworkResultState
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.util.getLatLng
import com.nenne.presentation.util.zoomToDistance
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.CustomMakerBinding
import com.nocompany.presentation.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarWashMapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map),
    OnMapReadyCallback {

    val TAG = "sechan"
    private val viewModel: MapViewModel by viewModels()

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                (it.key == Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        (it.key == Manifest.permission.ACCESS_FINE_LOCATION)
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


    private val mLocationListener = LocationListener {
        viewModel.currentLocation = it.latitude getLatLng it.longitude
        initializeNaverMap()
        viewModel.getReverseGeoCode()
    }

    private var infoWindows = mutableListOf<InfoWindow>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun initViewStatus() = with(binding) {
        locationPermissionLauncher.launch(locationPermission)
        location.setOnClickListener { moveToCameraMyLocation() }
        filter.setOnClickListener { }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        searchHere.setOnClickListener { searchCarWashLocation() }

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
        Log.d(TAG, "searchCarWashLocation: $latitude $longitude")
        viewModel.getCarWashShopAroundHere(latitude, longitude, zoomLevel.zoomToDistance())
    }

    private fun observeData() {

        viewModel.resultState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResultState.Success -> {
                    setWindowInfo(it.data)
                }
                else -> {
                    Log.d(TAG, "observeData: $it")
                }
            }
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


    private fun setWindowInfo(items: List<Item>) {
        infoWindows.forEach { it.close() }

        binding.detailLayer.isGone = false
        items.sortedBy { it.distance }
            .also {
                if (it.isNotEmpty()) {
//                    binding.detail = it[0]
                    if (!viewModel.isFirstBooting) {
                        mNaverMap.moveCamera(CameraUpdate.scrollTo(it[0].latitude getLatLng it[0].longitude))
                        viewModel.isFirstBooting = true
                    }
                } else {
                    binding.detailLayer.isGone = true
                    Toast.makeText(requireContext(),"근처에 세차장이 없어요",Toast.LENGTH_SHORT).show()
                }
            }
            .forEach { data ->
                infoWindows.add(
                    InfoWindow().apply {
                        adapter = setWindowInfoAdapter(data.type)
                        position = data.latitude getLatLng data.longitude
                        setOnClickListener { _ ->
//                            binding.detail = data
                            if (binding.detailLayer.isGone) {
                                binding.detailLayer.visibility = View.VISIBLE
                            }
                            true
                        }
                        open(mNaverMap)
                    })
            }
    }

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