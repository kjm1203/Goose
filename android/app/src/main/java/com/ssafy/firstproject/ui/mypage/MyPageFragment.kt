package com.ssafy.firstproject.ui.mypage

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentMyPageBinding
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.ssafy.firstproject.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.firstproject.data.model.response.UserGrowth
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MyPageFragment"

class MyPageFragment : BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind, R.layout.fragment_my_page
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMyNewsList.setOnClickListener {
            findNavController().navigate(R.id.dest_record)
        }

        // 로그인 여부 확인 (false:비로그인)
        val isLogin = sharedPreferencesUtil.checkLogin()
        handleLoginState(isLogin)

        // 기존 클릭 리스너 유지
        binding.tvProfileEdit.setOnClickListener {
            findNavController().navigate(R.id.dest_profile_edit)
        }

        binding.tvLogout.setOnClickListener {
            findNavController().navigate(R.id.dest_logout_dialog)
        }

        setBarChart()
        fetchUserGrowthData()
    }

    private fun setBarChart() {
        val barChart = binding.barChart

        // 1️⃣ BarEntry 데이터 생성 (X축과 Y축 값)
        val entries = listOf(
            BarEntry(1f, 200f),
            BarEntry(2f, 300f),
            BarEntry(3f, 350f),
            BarEntry(4f, 250f),
            BarEntry(5f, 180f),
            BarEntry(6f, 100f)
        )

        // 2️⃣ 데이터셋 생성 및 스타일 지정
        val dataSet = BarDataSet(entries, "월 별").apply {
            color = Color.parseColor("#7189FF")
            valueTextSize = 12f
        }

        // 3️⃣ BarData 객체 생성 및 차트에 설정
        val barData = BarData(dataSet)
        barChart.data = barData

        // 4️⃣ X축 설정 (하단 표시)
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
        }

        // 5️⃣ 기타 차트 속성 설정
        barChart.apply {
            description.isEnabled = false
            animateY(300)
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun handleLoginState(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            // 로그인된 경우: 마이페이지 콘텐츠 표시
            binding.clLoginRequired.visibility = View.GONE
            binding.nsv.visibility = View.VISIBLE
        } else {
            // 비로그인된 경우: "로그인이 필요합니다" 메시지 표시
            binding.clLoginRequired.visibility = View.VISIBLE
            binding.nsv.visibility = View.GONE

            // 로그인 버튼 클릭 이벤트 설정
            binding.btnLogin.setOnClickListener {
                findNavController().navigate(R.id.dest_login) // 로그인 화면으로 이동
            }

            // 회원가입 버튼 클릭 이벤트 설정
            binding.btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.dest_signup) // 회원가입 화면으로 이동
            }
        }
    }

    private fun fetchUserGrowthData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitUtil.userGrowthService.getUserGrowth()

                if (response.isSuccessful) {
                    val userGrowth = response.body()

                    if (userGrowth != null) {
                        withContext(Dispatchers.Main) {
                            updateUI(userGrowth)
                        }
                    } else {
                        Log.e(TAG, "User growth data is null")
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user growth data", e)
            }
        }
    }

    private fun updateUI(userGrowth: UserGrowth) {

        val userName = getString(R.string.user_name, userGrowth.nickname)
        binding.tvUserName.text = userName

        // TextView 업데이트 (문제 수와 맞춘 문제 수)
        val fullText =
            getString(R.string.my_growth_text, userGrowth.totalQuestions, userGrowth.correctCount)
        val totalQuestionsPart = "${userGrowth.totalQuestions}문제"
        val correctCountPart = "${userGrowth.correctCount}문제"

        // SpannableStringBuilder로 특정 부분 굵게 설정
        val spannable = SpannableStringBuilder(fullText)

        // %1$d문제 부분 굵게 설정
        val totalStartIndex = fullText.indexOf(totalQuestionsPart)
        if (totalStartIndex != -1) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                totalStartIndex,
                totalStartIndex + totalQuestionsPart.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // %2$d문제 부분 굵게 설정
        val correctStartIndex = fullText.indexOf(correctCountPart)
        if (correctStartIndex != -1) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                correctStartIndex,
                correctStartIndex + correctCountPart.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvGrowthText.text = spannable

        // ProgressBar 업데이트
        ObjectAnimator.ofInt(binding.pbProgressBar, "progress", userGrowth.correctRate.toInt())
            .apply {
                duration = 300L
                start()
            }

        // 퍼센트 텍스트 업데이트
        binding.tvProgressPercentage.text = "${userGrowth.correctRate.toInt()}%"
    }
}
