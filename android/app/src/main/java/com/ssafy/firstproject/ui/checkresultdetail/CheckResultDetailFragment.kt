package com.ssafy.firstproject.ui.checkresultdetail

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.NewsContent
import com.ssafy.firstproject.databinding.FragmentCheckResultDetailBinding
import com.ssafy.firstproject.util.ViewAnimationUtil.animateProgress

class CheckResultDetailFragment : BaseFragment<FragmentCheckResultDetailBinding>(
    FragmentCheckResultDetailBinding::bind,
    R.layout.fragment_check_result_detail
) {
    private lateinit var checkResultDetailAdapter: CheckResultDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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