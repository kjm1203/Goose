package com.ssafy.firstproject.ui.newsdetail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsDetailBinding

class NewsDetailFragment : BaseFragment<FragmentNewsDetailBinding>(
    FragmentNewsDetailBinding::bind,
    R.layout.fragment_news_detail
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPieChart()
    }

    private fun setPieChart() {

        val trustPercentage = 70f

        binding.pcTrustPercent.setUsePercentValues(true)

        // 데이터 세팅
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(trustPercentage, getString(R.string.trust_percent)))
        entries.add(PieEntry(100f - trustPercentage, getString(R.string.not_trust_percent)))

        // 색상 세팅
        val colorsArray = listOf(
            Color.parseColor("#50A56F"),
            Color.parseColor("#D2D1D4")
        )

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            colors = colorsArray
            setDrawValues(false)
        }

        val pieData = PieData(pieDataSet)
        binding.pcTrustPercent.apply {
            data = pieData
            legend.isEnabled = false
            description.isEnabled = false
            isRotationEnabled = false
            centerText = getString(R.string.trust_percentage, trustPercentage.toInt())
            setEntryLabelColor(Color.BLACK)
            setCenterTextSize(12f)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
            setTouchEnabled(false)
            setDrawEntryLabels(false)
        }
    }

}