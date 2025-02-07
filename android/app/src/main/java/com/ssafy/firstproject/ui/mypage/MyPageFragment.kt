package com.ssafy.firstproject.ui.mypage

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentMyPageBinding
import android.graphics.Color
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

private const val TAG = "MyPageFragment"

class MyPageFragment : BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setBarChart()
        setBarChartAnimation()
    }

    private fun setBarChartAnimation() {
        val exp = 90 // 예제 90까지 슬라이딩
        ObjectAnimator.ofInt(binding.pbProgressBar, "progress", exp)
            .setDuration(300)
            .start()
    }

    private fun setBarChart() {
        val barChart = binding.barChart

        // 1️⃣ BarEntry 데이터 생성 (X축과 Y축 값)
        val entries = listOf(
            BarEntry(1f, 200f),
            BarEntry(2f, 300f),
            BarEntry(3f, 350f),
            BarEntry(4f, 250f),
            BarEntry(5f, 180f),
            BarEntry(6f, 100f)
        )

        // 2️⃣ 데이터셋 생성 및 스타일 지정
        val dataSet = BarDataSet(entries, "월 별").apply {
            color = Color.parseColor("#7189FF")
            valueTextSize = 12f
        }

        // 3️⃣ BarData 객체 생성 및 차트에 설정
        val barData = BarData(dataSet)
        barChart.data = barData

        // 4️⃣ X축 설정 (하단 표시)
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
        }

        // 5️⃣ 기타 차트 속성 설정
        barChart.apply {
            description.isEnabled = false
            animateY(300)
            setTouchEnabled(false)
            invalidate()
        }
    }
}