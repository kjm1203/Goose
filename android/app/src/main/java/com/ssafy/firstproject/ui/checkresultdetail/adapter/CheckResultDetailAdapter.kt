package com.ssafy.firstproject.ui.checkresultdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.firstproject.data.model.NewsContent
import com.ssafy.firstproject.databinding.ItemCheckResultDetailBinding
import com.ssafy.firstproject.util.ViewAnimationUtil.animateProgress
import com.ssafy.firstproject.util.ViewAnimationUtil.rotateImage

class CheckResultDetailAdapter :
    ListAdapter<NewsContent, CheckResultDetailAdapter.CheckResultDetailViewHolder>(
        CheckNewsDiffCallback()
    ) {
    inner class CheckResultDetailViewHolder(private val binding: ItemCheckResultDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentRotation = 0f // 현재 회전 각도 상태 저장

        fun bind(newsContent: NewsContent) {
            binding.tvNewsItemContent.text = newsContent.content
//            binding.tvResult.text = newsContent.result

            val cleanText = binding.tvItemResultDetail.text.toString().replace(" ", "\u00A0")
            binding.tvItemResultDetail.text = cleanText

            val randomProgress = (1..100).random()

            animateProgress(binding.pbNewsParagraphTruth, randomProgress)

            binding.tvItemPercent.text = "${randomProgress}%"

            binding.root.setOnClickListener {
                currentRotation += if (currentRotation % 180 == 0f) 90f else -90f
                rotateImage(binding.ivItem, currentRotation)

                binding.tvItemResultDetail.apply {
                    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckResultDetailViewHolder {
        val binding = ItemCheckResultDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CheckResultDetailViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CheckResultDetailViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}