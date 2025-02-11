package com.ssafy.firstproject.ui.gameresult

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentGameResultBinding

private const val TAG = "GameResultFragment"

class GameResultFragment : BaseFragment<FragmentGameResultBinding>(
    FragmentGameResultBinding::bind,
    R.layout.fragment_game_result
) {

    private val args: GameResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnResultDetail.setOnClickListener {
            findNavController().navigate(R.id.dest_game_result_detail)
        }

        setData()
    }

    private fun setData() {
        binding.tvTime.text =
            getString(R.string.total_time_spent, millisecondsToSeconds(args.totalTimeSpent))
    }

    private fun millisecondsToSeconds(milliseconds: Long): Double {
        return milliseconds / 1000.0
    }
}