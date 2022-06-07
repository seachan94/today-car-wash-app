package com.nenne.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nenne.presentation.base.BaseFragment
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            val responsePermissions = permissions.entries.filter{
                (it.key == Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        (it.key == Manifest.permission.ACCESS_FINE_LOCATION)
            }
            Log.d("sechan", ": $responsePermissions")
            if(responsePermissions.filter { it.value == true }.size == locationPermission.size){
                Toast.makeText(requireContext(),"위치 권한 설정이 완료.",Toast.LENGTH_SHORT).show()
                drawMapView()
            }else{
                Toast.makeText(requireContext(),"위치 권한 설정이 필요합니다.",Toast.LENGTH_SHORT).show()
            }
        }

    override fun initViewStatus() = with(binding) {

        locationPermissionLauncher.launch(locationPermission)

    }

    fun drawMapView() = with(binding){
        val mapView = MapView(requireActivity())
        clKakaoMapView.addView(mapView)
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.54892296550104, 126.99089033876304), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(4, true);
    }

    companion object{
        val locationPermission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}