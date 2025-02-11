package com.ssafy.firstproject.ui.game

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentGameBinding

private const val TAG = "GameFragment"

class GameFragment : BaseFragment<FragmentGameBinding>(
    FragmentGameBinding::bind,
    R.layout.fragment_game
) {

    private var startTime: Long = 0
    private var totalTimeSpent: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.fab.setOnClickListener {
            calcTotalTimeSpent()
            val action =
                GameFragmentDirections.actionDestGameToDestChoiceDialog(totalTimeSpent)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        calcTotalTimeSpent()
    }

    private fun calcTotalTimeSpent() {
        val endTime = System.currentTimeMillis()
        val timeSpent = endTime - startTime
        totalTimeSpent += timeSpent

        Log.d(TAG, "현재 화면 체류 시간: ${timeSpent}ms, 누적 시간: ${totalTimeSpent}ms")
    }
}