package com.ssafy.firstproject.ui

import android.os.Bundle
import com.ssafy.firstproject.base.BaseActivity
import com.ssafy.firstproject.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}