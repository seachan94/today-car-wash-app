package com.nenne.presentation.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.nenne.presentation.base.BaseFragment
import com.nenne.presentation.data.CarShopType
import com.nenne.presentation.data.datastore.UserSelectedDataStore
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    override fun initViewStatus() = with(binding) {
//        navigate(R.id.action_loginFragment_to_mapFragment)
        var count = 0
        startBtn.setOnClickListener {
            navigate(R.id.action_loginFragment_to_mapFragment)
        }
    }
}