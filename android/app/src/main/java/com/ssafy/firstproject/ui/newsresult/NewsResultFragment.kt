package com.ssafy.firstproject.ui.newsresult

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.response.NewsAnalysisArticle
import com.ssafy.firstproject.databinding.FragmentNewsResultBinding
import com.ssafy.firstproject.util.ViewAnimationUtil.animateProgress

private const val TAG = "NewsResultFragment_ssafy"
class NewsResultFragment : BaseFragment<FragmentNewsResultBinding>(
    FragmentNewsResultBinding::bind,
    R.layout.fragment_news_result
) {
    private val args: NewsResultFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarNewsResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnOtherCheck.setOnClickListener { findNavController().popBackStack() }

        binding.btnCheckDetail.setOnClickListener {
            findNavController().navigate(R.id.dest_check_result_detail)
        }

        val newsArticle = args.newsArticle

        Log.d(TAG, "onViewCreated: $newsArticle")

        updateNewsArticleUI(newsArticle, binding)
    }

    private fun updateNewsArticleUI(newsArticle: NewsAnalysisArticle, binding: FragmentNewsResultBinding) {
        // 신뢰도 관련 처리
        newsArticle.reliability?.let {
            val trustScore = "${it.toInt()}%"
            val fullText = "해당 기사의\n신뢰도는\n$trustScore 입니다."

            // 신뢰도 텍스트 색상 적용
            setTextWithColoredSubString(binding.tvBubbleTruth, fullText, trustScore, Color.BLUE)

            // 신뢰도 퍼센트 표시
            binding.tvPercentTruth.text = trustScore
            animateProgress(binding.pbTruth, it.toInt())
        }

        // Bias 점수 관련 처리
        newsArticle.biasScore?.let {
            binding.tvBiasPercent.text = "${it.toInt()}%"
            animateProgress(binding.pbBias, it.toInt())
        }

        // AI 점수 관련 처리 (고정값 60%)
        animateProgress(binding.pbAi, 60)
        binding.tvAiWhetherPercent.text = "60%"
    }

    private fun setTextWithColoredSubString(textView: TextView, fullText: String, targetSubstring: String, color: Int) {
        val spannable = SpannableStringBuilder(fullText)

        // targetSubstring 부분의 시작 인덱스 찾기
        val startIndex = fullText.indexOf(targetSubstring)
        val endIndex = startIndex + targetSubstring.length

        // 특정 부분만 색상 변경
        spannable.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // TextView에 적용
        textView.text = spannable
    }
}