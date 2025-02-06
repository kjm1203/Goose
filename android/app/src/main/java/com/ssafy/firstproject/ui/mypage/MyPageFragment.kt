package com.ssafy.firstproject.ui.mypage

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentMyPageBinding
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val exp = 90 // 예제
        ObjectAnimator.ofInt(binding.pbProgressBar, "progress", exp)
            .setDuration(300)
            .start()

        setBarChart()
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
            color = Color.parseColor("#7189FF") // 주황색 막대 색상
            valueTextSize = 12f                // 값 텍스트 크기
        }

        // 3️⃣ BarData 객체 생성 및 차트에 설정
        val barData = BarData(dataSet)
        barChart.data = barData

        // 4️⃣ X축 설정 (하단 표시)
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM   // X축 위치 하단으로 설정
            setDrawGridLines(false)                // 격자선 제거
            granularity = 1f                       // 간격 고정 (1 단위)
        }

        // 5️⃣ 기타 차트 속성 설정
        barChart.apply {
            description.isEnabled = false          // 설명 텍스트 제거
            animateY(1000)                         // Y축 애니메이션 적용 (1초)
            invalidate()                           // 차트 새로고침
        }
    }
}