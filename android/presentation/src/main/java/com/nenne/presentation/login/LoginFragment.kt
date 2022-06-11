package com.nenne.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nenne.presentation.base.BaseFragment
import com.nocompany.presentation.R
import com.nocompany.presentation.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    override fun initViewStatus() =with(binding){
        navigate(R.id.action_loginFragment_to_mapFragment)
        startBtn.setOnClickListener {
            navigate(R.id.action_loginFragment_to_mapFragment)
        }
    }
}