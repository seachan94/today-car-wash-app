package com.nenne.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<VB :  ViewDataBinding, VM : BaseViewModel>(@LayoutRes private val layoutResId : Int)
    : AppCompatActivity(layoutResId){

    protected lateinit var binding : VB
        private set
    protected abstract val viewModel : VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,layoutResId)

    }

    private fun performDataBinding(){
        binding.apply{
            lifecycleOwner = this@BaseActivity
        }
    }


}