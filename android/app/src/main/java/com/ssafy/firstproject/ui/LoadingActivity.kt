package com.ssafy.firstproject.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.ssafy.firstproject.R

// 스플래쉬 화면으로 변경 예정
class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_goose)

        // ImageView 연결
        val imageView = findViewById<ImageView>(R.id.iv_loading)

        // AnimationDrawable로 캐스팅
        val animationDrawable = imageView.drawable as AnimationDrawable

        // 애니메이션 시작
        animationDrawable.start()

        // 로딩 완료 후 다음 화면으로 전환 (예: 3초 후)
        imageView.postDelayed({
            // 다음 화면으로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3000ms = 3초
    }
}
