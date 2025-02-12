package com.ssafy.firstproject.ui.checkresultdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.firstproject.data.model.NewsContent
import com.ssafy.firstproject.databinding.ItemCheckResultDetailBinding
import com.ssafy.firstproject.util.ViewAnimationUtil.animateProgress

class CheckResultDetailAdapter :
    ListAdapter<NewsContent, CheckResultDetailAdapter.CheckResultDetailViewHolder>(
        CheckNewsDiffCallback()
    ) {
    inner class CheckResultDetailViewHolder(private val binding: ItemCheckResultDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(newsContent: NewsContent) {
            binding.tvNewsItemContent.text = newsContent.content
            binding.tvResult.text = newsContent.result

            val randomProgress = (1..100).random()

            animateProgress(binding.pbNewsParagraphTruth, randomProgress)
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
        holder: CheckResultDetailAdapter.CheckResultDetailViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}