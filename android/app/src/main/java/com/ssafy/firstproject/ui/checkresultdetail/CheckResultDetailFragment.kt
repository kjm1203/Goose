package com.ssafy.firstproject.ui.checkresultdetail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.NewsContent
import com.ssafy.firstproject.databinding.FragmentCheckResultDetailBinding
import com.ssafy.firstproject.ui.checkresultdetail.adapter.CheckResultDetailAdapter
import com.ssafy.firstproject.util.ViewAnimationUtil.animateProgress

private const val TAG = "CheckResultDetailFragment_ssafy"
class CheckResultDetailFragment : BaseFragment<FragmentCheckResultDetailBinding>(
    FragmentCheckResultDetailBinding::bind,
    R.layout.fragment_check_result_detail
) {
    private val args: CheckResultDetailFragmentArgs by navArgs()
    private lateinit var checkResultDetailAdapter: CheckResultDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsArticle = args.newsArticle

        Log.d(TAG, "onViewCreated: $newsArticle")

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCheckOtherNews.setOnClickListener {
            val action =
                CheckResultDetailFragmentDirections.actionDestCheckResultDetailToDestCheck()
            findNavController().navigate(action)
        }

        checkResultDetailAdapter = CheckResultDetailAdapter()

        binding.rvNewsContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = checkResultDetailAdapter
        }

        val newsList = listOf(
            NewsContent(
                "트럼프 대통령의 이같은 관세 압박... 트럼프 대통령의 이같은 관세 압박... \n" +
                        "트럼프 대통령의 이같은 관세 압박... 트럼프 대통령의 이같은 관세 압박... \n" +
                        "트럼프 대통령의 이같은 관세 압박...",
                "잘못된 정보가 없습니다."
            ),
            NewsContent("경제 성장률이 예상보다 낮아...", "일부 과장된 정보 포함"),
            NewsContent("경제 성장률이 예상보다 낮아...", "일부 과장된 정보 포함"),
            NewsContent("경제 성장률이 예상보다 낮아...", "일부 과장된 정보 포함"),
            NewsContent("경제 성장률이 예상보다 낮아...", "일부 과장된 정보 포함")
        )

        checkResultDetailAdapter.submitList(newsList)

        animateProgress(binding.pbDetailTruth, 30)
        animateProgress(binding.pbDetailAi, 60)
        animateProgress(binding.pbDetailBias, 90)
    }
}