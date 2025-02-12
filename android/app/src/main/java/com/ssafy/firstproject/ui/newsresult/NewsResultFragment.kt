package com.ssafy.firstproject.ui.newsresult

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsResultBinding

class NewsResultFragment : BaseFragment<FragmentNewsResultBinding>(
    FragmentNewsResultBinding::bind,
    R.layout.fragment_news_result
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarNewsResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        animateProgress(binding.pbTruth, 30)
        animateProgress(binding.pbAi, 60)
        animateProgress(binding.pbBias, 90)
    }

    private fun animateProgress(progressBar: ProgressBar, targetProgress: Int) {
        val currentProgress = progressBar.progress

        if (targetProgress <= currentProgress) return

        // 애니메이션 설정
        val animator = ObjectAnimator.ofInt(progressBar, "progress", currentProgress, targetProgress).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
        }

        animator.start()
    }
}