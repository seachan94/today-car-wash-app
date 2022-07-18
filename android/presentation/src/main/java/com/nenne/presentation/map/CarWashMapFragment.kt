package com.nenne.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PointF
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.nenne.presentation.data.CarShopType
import com.nenne.presentation.data.datastore.UserSelectedDataStore
import com.nenne.presentation.model.ClusteredItem
import com.nenne.presentation.util.*
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.CustomMakerBinding
import com.nocompany.presentation.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ted.gun0912.clustering.TedClustering
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
    private var circleOverlay = CircleOverlay()
    
    lateinit var tedClustering : TedNaverClustering<ClusteredItem>
    lateinit var userSelectedDataStore : UserSelectedDataStore
    var isFirstAttach = false
    
    
    private val mLocationListener = LocationListener {
        viewModel.currentLocation = it.latitude getLatLng it.longitude
        initializeNaverMap()
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun initViewStatus() = with(binding) {
        
        locationPermissionLauncher.launch(locationPermission)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        userSelectedDataStore = UserSelectedDataStore(requireContext())
        
        location.setOnClickListener { moveToCameraMyLocation() }
        filter.setOnClickListener { makeFilterDialog() }
        detailLayer.setOnClickListener {
            navigate(R.id.action_mapFragment_to_detailCarWashShop,
                bundleOf("data" to viewModel.detailData.value))
        }
        searchHere.setOnClickListener {
            searchCarWashLocation()
            viewModel.getReverseGeoCode(mNaverMap.cameraPosition.target)
        }
        vm = viewModel
        observeData()
    }

    val dialogList = arrayOf("전체","자동만","셀프만")
    var checkedPosition = 0


    private fun makeFilterDialog(){

        AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(dialogList,checkedPosition) { _, selected ->
                checkedPosition = selected
            }
            .setPositiveButton("완료") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch{
                    userSelectedDataStore.updateMapCarWashShopFilterType(
                        when (checkedPosition) {
                            0 -> CarShopType.ALL
                            1 -> CarShopType.AUTO
                            else -> CarShopType.SELF
                        }
                    )
                }
            }
            .show()
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

    private fun drawRangeOnMap(){

        val cameraPosition = mNaverMap.cameraPosition
        val latitude = cameraPosition.target.latitude
        val longitude = cameraPosition.target.longitude
        val zoomLevel = cameraPosition.zoom

        circleOverlay.apply{
            color = Color.TRANSPARENT
            center = latitude getLatLng longitude
            radius = zoomLevel.getRadiusForRange()
            map = mNaverMap
            outlineWidth = 5
            outlineColor = ContextCompat.getColor(requireContext(),R.color.APPCOLOR)
        }
    }

    private fun observeData() {

        viewModel.resultState.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResultState.Success -> {
                    setClusteredMarker(it.data.map { it.getClusteredItem() })
                    drawRangeOnMap()
                }
                is NetworkResultState.Loading ->{

                }
                else -> {
                    Toast.makeText(requireContext(),"잠시 오류가 있습니다 잠시 후 사용해주세요",Toast.LENGTH_SHORT).show()
                }
            }
        }

        userSelectedDataStore.mapCarWashShopFilterType.asLiveData().observe(viewLifecycleOwner){
            Log.d(TAG, "observeData: $it")
        }
    }


    private fun setClusteredMarker(items: List<ClusteredItem>) {

        if (items.isEmpty()) {
            binding.detailLayer.isGone = true
            Toast.makeText(requireContext(), "근처에 세차장이 없어요", Toast.LENGTH_SHORT).show()
            return
        }

        val nearestShop = items.first()
        if(!isFirstAttach){
            isFirstAttach = true
            mNaverMap.cameraPosition = CameraPosition(nearestShop.latitude getLatLng nearestShop.longitude,mNaverMap.cameraPosition.zoom)
        }
        binding.detailLayer.isVisible = true
        viewModel.detailData.value = nearestShop


        if(::tedClustering.isInitialized.not()){
            initializedTedNaverClustered()
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            tedClustering.clearItems()
            tedClustering.addItems(items)
        }

    }

    fun initializedTedNaverClustered(){
        tedClustering =
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