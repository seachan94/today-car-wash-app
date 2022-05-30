package com.nenne.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

abstract class BaseFragment<VB :ViewDataBinding>
    (private val layoutId : Int) : Fragment(layoutId){

    protected lateinit var binding : VB
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,layoutId,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    protected fun navigate(@IdRes actionResId: Int, bundle: Bundle? = null) {
        findNavController().navigate(actionResId, bundle)
    }

    protected fun navigate(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }
}