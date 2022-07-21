package com.nenne.presentation.detail

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.map.CarWashMapFragment
import com.nenne.presentation.model.ClusteredItem
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentDetailCarWashShopBinding

class DetailCarWashShop : BaseFragment<FragmentDetailCarWashShopBinding>(R.layout.fragment_detail_car_wash_shop) {

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                removeAddedFragment()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun initViewStatus() =with(binding){
        binding.data = arguments?.getSerializable(shopKey) as ClusteredItem

        backBtn.setOnClickListener {
            removeAddedFragment()
        }

    }

    fun removeAddedFragment(){
        activity?.supportFragmentManager?.commit {
            remove(this@DetailCarWashShop)
        }
    }

    companion object{
        val FRAGMENT_TAG = "DetailCarWashShop"
        private const val shopKey = "SELECTED_SHOP"

        fun detailCarWashMapFragment(data : ClusteredItem) =
            DetailCarWashShop().apply{
                arguments = bundleOf(
                    shopKey to data
                )
            }
    }

}