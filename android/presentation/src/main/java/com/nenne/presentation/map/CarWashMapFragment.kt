package com.nenne.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations.map
import com.naver.maps.map.MapFragment
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.util.LoadFakeDataFromAssets
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentMapBinding


class CarWashMapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {

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
            } else {
                Toast.makeText(requireContext(), "위치 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    lateinit var mLocationManager: LocationManager
    val mLocationListener = LocationListener { doLocationChanged(it) }

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

        locationPermissionLauncher.launch(locationPermission)
        location.setOnClickListener {
            detailLayer.visibility = View.GONE
        }
        filter.setOnClickListener {
            detailLayer.visibility = View.VISIBLE
        }

    }


    fun drawMapView() = with(binding) {


    }



    @SuppressLint("MissingPermission")
    fun doLocationChanged(location: Location) {


    }

    private fun makeMarkerItems(item: List<Item>) =
        item.map {

        }

    fun getFakeDataCarWashInfo() =
        LoadFakeDataFromAssets(requireContext()).getObjectFromJson<CarWashData>("MockCarwashData.json",
            CarWashData::class.java).item
            .mapIndexed { idx, it ->
                if (idx % 2 == 0) {
                    it.type = ShopType.SELF
                } else {
                    it.type = ShopType.AUTO
                }
                it
            }

    companion object {
        val locationPermission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


}