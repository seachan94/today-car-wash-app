package com.nenne.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
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
        initViewStatus()
        return binding.root
    }


    open fun initViewStatus() = Unit


    protected fun navigateUp(destinationId: Int, inclusive: Boolean) {
        if (parentFragmentManager.backStackEntryCount == 0) {
            requireActivity().finish()
        } else {
            findNavController().popBackStack(destinationId, inclusive)
        }
    }
    protected fun navigateUp() {
        if (parentFragmentManager.backStackEntryCount == 0) {
            requireActivity().finish()
        } else {
            findNavController().popBackStack()
        }
    }
    protected fun navigate(@IdRes actionResId: Int, bundle: Bundle? = null) {
        findNavController().navigate(actionResId, bundle)
    }

}