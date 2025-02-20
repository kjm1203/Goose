package com.ssafy.firstproject.ui.newslistresult.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.firstproject.base.ApplicationClass.Companion.contentSearchRepository
import com.ssafy.firstproject.data.model.request.KeywordSearch
import com.ssafy.firstproject.data.model.response.NewsAnalysisArticle
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

private const val TAG = "NewsListResultViewModel_ssafy"

class NewsListResultViewModel : ViewModel() {
    private val _newsAnalysisArticles: MutableLiveData<List<NewsAnalysisArticle>> =
        MutableLiveData()
    val newsAnalysisArticles: LiveData<List<NewsAnalysisArticle>> get() = _newsAnalysisArticles

    fun getNewsArticle(keywordSearch: KeywordSearch) {
        viewModelScope.launch {
            var attempt = 0
            val maxRetry = 3
            var readTimeout = 10000L  // 초기 readTimeout (10초)

            while (attempt < maxRetry) {
                kotlin.runCatching {
                    Log.d(TAG, "getNewsArticle: $readTimeout")
                    contentSearchRepository.searchKeywords(keywordSearch, readTimeout)
                }.onSuccess { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _newsAnalysisArticles.value = it
                        }
                        Log.d(TAG, "getNewsArticle: ${response.body()}")
                    } else {
                        Log.d(TAG, "getNewsArticle fail: ${response.code()}")
                    }
                    return@launch // 성공하면 종료
                }.onFailure { throwable ->
                    if (throwable is SocketTimeoutException) {
                        Log.e(
                            TAG,
                            "getNewsArticle attempt ${attempt + 1}: ${throwable.message}",
                            throwable
                        )

                        if (attempt >= maxRetry - 1) {
                            Log.e(TAG, "최대 재시도 횟수를 초과했습니다.")
                            return@launch
                        }

                        readTimeout *= 2  // readTimeout 시간 늘리기 (10 → 20 → 40초)
                        attempt++
                    } else {
                        Log.e(TAG, "getNewsArticle: ${throwable.message}", throwable)
                        return@launch // 타임아웃 외의 에러는 종료
                    }
                }
            }
        }
    }

}