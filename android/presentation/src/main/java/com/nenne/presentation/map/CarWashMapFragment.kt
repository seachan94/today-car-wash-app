package com.nenne.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.fragment.app.viewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.util.LoadFakeDataFromAssets
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.CustomMakerBinding
import com.nocompany.presentation.databinding.FragmentMapBinding


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
                setMyLocation()
                initializeNaverMap()
            } else {
                Toast.makeText(requireContext(), "위치 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    lateinit var mLocationManager: LocationManager
    val mLocationListener = LocationListener { doLocationChanged(it) }
    lateinit var mNaverMap : NaverMap

    @SuppressLint("MissingPermission")
    fun setMyLocation() {
        if (::mLocationManager.isInitialized.not()) {
            mLocationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val minTime = 1500L
        val minDistance = 100f
        with(mLocationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, mLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, mLocationListener
            )
        }
    }

    override fun initViewStatus() = with(binding) {

        //퍼미션 체크 해야함
        locationPermissionLauncher.launch(locationPermission)
        location.setOnClickListener {
            detailLayer.visibility = View.GONE
        }
        filter.setOnClickListener {
            detailLayer.visibility = View.VISIBLE
        }

    }


    fun initializeNaverMap() = with(binding) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as MapFragment?
            ?:MapFragment.newInstance().also{
                childFragmentManager.beginTransaction().add(R.id.mapFragment,it).commit()
            }
        mapFragment.getMapAsync(this@CarWashMapFragment)
    }



    @SuppressLint("MissingPermission")
    fun doLocationChanged(location: Location) {


    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.uiSettings.apply{
            isZoomControlEnabled = false
        }
        mNaverMap = naverMap.apply{
            moveCamera(CameraUpdate.scrollTo(LatLng(37.5159317,127.0944873)))
        }

        testWindowInfo(naverMap)

    }


    companion object {
        val locationPermission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }



    fun testWindowInfo(naverMap : NaverMap){
        getFakeDataCarWashInfo().forEach{
            with(it){
                type = if(num ==1) ShopType.SELF else ShopType.AUTO
            }
            InfoWindow().apply{
                adapter = object : InfoWindow.ViewAdapter(){
                    override fun getView(p0: InfoWindow): View {
                        return CustomMakerBinding.inflate(layoutInflater).apply{
                            when(it.type){
                                ShopType.AUTO ->{
                                    shopName.text = "자동 세차"
                                    carwashImg.setImageResource(R.drawable.location_black)
                                    shopName.setTextColor(Color.BLACK)
                                }
                                else ->{
                                    shopName.text = "셀프 세차"
                                }
                            }
                        }.root
                    }
                }

                position = LatLng(it.latitude,it.longtitude)
                open(naverMap)
            }
        }
    }
    fun getFakeDataCarWashInfo() =
        LoadFakeDataFromAssets(requireContext()).getObjectFromJson<CarWashData>("MockCarwashData.json",
            CarWashData::class.java).item
}