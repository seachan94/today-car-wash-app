package com.nenne.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nenne.presentation.base.BaseFragment
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapView

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {

    override fun initViewStatus() = with(binding){
        val mapView = MapView(requireActivity())
        clKakaoMapView.addView(mapView)

        val deniedPermissions = getDeniedPermissions()
        requestPermissions(deniedPermissions)

    }

    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.USE_FULL_SCREEN_INTENT,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )
    //test
    private fun getDeniedPermissions():ArrayList<String>{
        var deniedPermissions = ArrayList<String>()

        for(p in requiredPermissions){
            if(ContextCompat.checkSelfPermission(requireContext(), p) == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(p)
            }
        }

        return deniedPermissions
    }

    private fun requestPermissions(permissions: ArrayList<String>){
        if(permissions.isNotEmpty()){
            val array = arrayOfNulls<String>(permissions.size)
            ActivityCompat.requestPermissions(requireActivity(), permissions.toArray(array), 1)
        }
    }

}