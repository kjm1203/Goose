package com.ssafy.firstproject.ui.newsdetail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsDetailBinding
import com.ssafy.firstproject.ui.newsdetail.viewmodel.NewsDetailViewModel
import com.ssafy.firstproject.util.CommonUtils

class NewsDetailFragment : BaseFragment<FragmentNewsDetailBinding>(
    FragmentNewsDetailBinding::bind,
    R.layout.fragment_news_detail
) {

    private val args by navArgs<NewsDetailFragmentArgs>()
    private val viewModel by viewModels<NewsDetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeNewsArticle()
        viewModel.getNewsArticle(args.newsId)
    }

    private fun observeNewsArticle() {
        viewModel.newsArticle.observe(viewLifecycleOwner) {
            binding.apply {
                Glide.with(requireContext())
                    .load(it.topImage)
                    .into(iv)
                tvTitle.text = it.title
                tvDate.text = CommonUtils.formatDateYYMMDD(it.pubDateTimestamp)
                tvBody.text = it.content
            }
            setPieChart(it.reliability.toFloat())
        }
    }

    private fun setPieChart(reliability: Float) {

        binding.pcTrustPercent.setUsePercentValues(true)

        // 데이터 세팅
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(reliability, getString(R.string.trust_percent)))
        entries.add(PieEntry(100f - reliability, getString(R.string.not_trust_percent)))

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
            centerText = getString(R.string.trust_percentage, reliability.toInt())
            setEntryLabelColor(Color.BLACK)
            setCenterTextSize(12f)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
            setTouchEnabled(false)
            setDrawEntryLabels(false)
        }
    }

}