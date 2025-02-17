package com.ssafy.firstproject.ui.record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.firstproject.data.model.UserNews
import com.ssafy.firstproject.databinding.ItemNewsBinding
import com.ssafy.firstproject.util.CommonUtils

class UserNewsAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<UserNews, UserNewsAdapter.UserNewsViewHolder>(UserNewsDiffCallback) {

    companion object UserNewsDiffCallback : DiffUtil.ItemCallback<UserNews>() {
        override fun areItemsTheSame(oldItem: UserNews, newItem: UserNews): Boolean {
            return oldItem.newsId == newItem.newsId
        }

        override fun areContentsTheSame(oldItem: UserNews, newItem: UserNews): Boolean {
            return oldItem == newItem
        }
    }

    fun interface ItemClickListener {
        fun onClick(newsId: String)
    }

    inner class UserNewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserNews) {
            binding.tvTitle.text = item.newsTitle
            binding.tvSummary.text = item.newsContent
            binding.tvTruthPercent.text = "신뢰도: ${item.reliability}%"
            binding.tvDate.text = "판단 날짜: $CommonUtils.formatDateYYMMDD(item.determinedAt)" // 날짜 포맷 필요

            binding.root.setOnClickListener {
                itemClickListener.onClick(item.newsId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserNewsViewHolder {
        val binding =
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserNewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserNewsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}
