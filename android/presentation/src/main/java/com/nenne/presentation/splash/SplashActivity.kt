package com.nenne.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.nenne.presentation.main.MainActivity
import com.nocompany.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        countSplashTime()
    }

    private fun countSplashTime(){
        lifecycleScope.launch{
            delay(1500L)
            Intent(this@SplashActivity, MainActivity::class.java).run {
                startActivity(this)
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                finish()
            }
        }
    }

}