package com.nenne.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.model.ClusteredItem
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentDetailCarWashShopBinding

class DetailCarWashShop : BaseFragment<FragmentDetailCarWashShopBinding>(R.layout.fragment_detail_car_wash_shop) {


    override fun initViewStatus() =with(binding){
        binding.data = arguments?.getSerializable("data") as ClusteredItem
        backBtn.setOnClickListener {
            navigateUp()
        }
    }

}