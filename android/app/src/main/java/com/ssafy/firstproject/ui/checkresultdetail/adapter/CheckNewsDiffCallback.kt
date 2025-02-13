package com.ssafy.firstproject.ui.checkresultdetail.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ssafy.firstproject.data.model.NewsContent

class CheckNewsDiffCallback : DiffUtil.ItemCallback<NewsContent>() {
    override fun areItemsTheSame(oldContent: NewsContent, newContent: NewsContent): Boolean {
        return oldContent.content == newContent.content  // 예제에서는 content를 기준으로 비교
    }

    override fun areContentsTheSame(oldContent: NewsContent, newContent: NewsContent): Boolean {
        return oldContent == newContent
    }
}