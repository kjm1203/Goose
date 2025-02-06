package com.ssafy.firstproject.ui.newsdetail

import android.os.Bundle
import android.view.View
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsDetailBinding

class NewsDetailFragment : BaseFragment<FragmentNewsDetailBinding>(
    FragmentNewsDetailBinding::bind,
    R.layout.fragment_news_detail
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val total = 100f
        val target = 70f

        // 그래프에 나타낼 데이터
        val dataAnychart = mutableListOf<DataEntry>()
        dataAnychart.add(ValueDataEntry("신뢰도", target))
        dataAnychart.add(ValueDataEntry("불신도", total - target))

        // 그래프 색상(데이터 순서)
        val fillColors = arrayOf<String>(
            "#50A56F",
            "#D2D1D4"
        )

        // AnyChart의 Pie차트 생성
        val anyPieChart = AnyChart.pie()
        // 데이터 설정
        anyPieChart.data(dataAnychart)
        // 라벨, 범례,크레딧 텍스트 비활성화
        anyPieChart.labels(false)
        anyPieChart.legend(false)
        anyPieChart.credits(false)
        // 그래프 색상 설정
        anyPieChart.palette(fillColors)

        // 그래프를 화면에 표시
        binding.acvTruthPercent.setChart(anyPieChart)
    }


}