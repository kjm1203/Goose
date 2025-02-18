package com.ssafy.firstproject.ui.newsresult

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.response.NewsAnalysisArticle
import com.ssafy.firstproject.databinding.FragmentNewsResultBinding
import com.ssafy.firstproject.ui.newsresult.viewmodel.NewsResultViewModel
import com.ssafy.firstproject.util.ViewAnimationUtil.animateProgress

private const val TAG = "NewsResultFragment_ssafy"
class NewsResultFragment : BaseFragment<FragmentNewsResultBinding>(
    FragmentNewsResultBinding::bind,
    R.layout.fragment_news_result
) {
    private val args: NewsResultFragmentArgs by navArgs()
    private val viewModel by viewModels<NewsResultViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupNewsResult.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        binding.lavLoadingAnimation.visibility = View.VISIBLE

        val url = args.url
        val mode = args.mode

        binding.btnOtherCheck.text = mode

        Log.d(TAG, "onViewCreated: $url")

        //url이 비었으면, item으로 들어온 것
        if (url.isEmpty()) {
            args.newsArticle.let { viewModel.setNewsArticle(it) }
        } else {
            viewModel.searchByUrl(url)
        }

        binding.btnCheckDetail.setOnClickListener {
            val newsAnalysisArticle = viewModel.newsAnalysisResult.value

            newsAnalysisArticle?.let {
                val action = NewsResultFragmentDirections
                    .actionDestNewsResultToDestCheckResultDetail(it)

                findNavController().navigate(action)
            }
        }

        binding.toolbarNewsResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnOtherCheck.setOnClickListener {
            val action = NewsResultFragmentDirections.actionDestNewsResultToDestCheck()
            findNavController().navigate(action)
        }

        observeSearchNews()
    }

    private fun updateNewsArticleUI(newsArticle: NewsAnalysisArticle) {
        // 신뢰도 관련 처리
        newsArticle.reliability?.let {
            val trustScore = getString(R.string.trust_percentage, it.toInt())
            val fullText = getString(R.string.article_trust_score, trustScore)

            // 신뢰도 텍스트 색상 적용
            setTextWithColoredSubString(binding.tvBubbleTruth, fullText, trustScore, Color.BLUE)

            // 신뢰도 퍼센트 표시
            binding.tvPercentTruth.text = trustScore
            animateProgress(binding.pbTruth, it.toInt())
        }

        // Bias 점수 관련 처리
        newsArticle.biasScore?.let {
            binding.tvBiasPercent.text = getString(R.string.trust_percentage, it.toInt())
            animateProgress(binding.pbBias, it.toInt())
        }

        // AI 점수 관련 처리 (고정값 60%)
        animateProgress(binding.pbAi, 60)
        binding.tvAiWhetherPercent.text = getString(R.string.trust_percentage, 60)
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

    private fun observeSearchNews() {
        viewModel.newsAnalysisResult.observe(viewLifecycleOwner) {
            updateNewsArticleUI(it)

            binding.groupNewsResult.visibility = View.VISIBLE
            binding.tvLoading.visibility = View.GONE
            binding.lavLoadingAnimation.visibility = View.GONE
        }
    }
}