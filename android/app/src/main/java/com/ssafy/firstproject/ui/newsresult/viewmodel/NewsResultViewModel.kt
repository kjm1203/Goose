package com.ssafy.firstproject.ui.newsresult.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.firstproject.base.ApplicationClass
import com.ssafy.firstproject.data.model.response.NewsAnalysisArticle
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

private const val TAG = "NewsResultViewModel_ssafy"

class NewsResultViewModel : ViewModel() {
    private val _newsAnalysisResult = MutableLiveData<NewsAnalysisArticle>()
    val newsAnalysisResult: LiveData<NewsAnalysisArticle> get() = _newsAnalysisResult

    fun searchByUrl(url: String) {
        viewModelScope.launch {
            var attempt = 0
            val maxRetry = 3
            var readTimeout = 10000L  // 초기 readTimeout (10초)

            while (attempt < maxRetry) {
                kotlin.runCatching {
                    ApplicationClass.contentSearchRepository.searchByUrl(url, readTimeout)
                }.onSuccess { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _newsAnalysisResult.value = it
                        }
                        Log.d(TAG, "searchByUrl: ${response.body()}")
                    } else {
                        Log.d(TAG, "searchByUrl fail: ${response.code()}")
                    }
                    return@launch  // 성공하면 종료
                }.onFailure { throwable ->
                    if (throwable is SocketTimeoutException) {
                        Log.e(
                            TAG,
                            "searchByUrl attempt ${attempt + 1}: ${throwable.message}",
                            throwable
                        )

                        if (attempt >= maxRetry - 1) {
                            Log.e(TAG, "최대 재시도 횟수를 초과했습니다.")
                            return@launch  // 최대 재시도 횟수 초과 시 종료
                        }

                        readTimeout *= 2  // readTimeout 시간 늘리기 (10 → 20 → 40초)
                        attempt++  // 재시도
                    } else {
                        Log.e(TAG, "searchByUrl: ${throwable.message}", throwable)
                        return@launch  // 타임아웃 외의 에러는 종료
                    }
                }
            }
        }
    }

    fun setNewsArticle(newsAnalysisArticle: NewsAnalysisArticle) {
        _newsAnalysisResult.value = newsAnalysisArticle
    }
}