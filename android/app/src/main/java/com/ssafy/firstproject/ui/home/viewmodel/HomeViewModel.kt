package com.ssafy.firstproject.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.firstproject.base.ApplicationClass
import com.ssafy.firstproject.data.model.News
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    private val _newsList: MutableLiveData<List<News>> = MutableLiveData()
    val newsList: LiveData<List<News>> get() = _newsList

    init {
        getNewsList()
    }

    private fun getNewsList() {
        viewModelScope.launch {
            kotlin.runCatching {
                ApplicationClass.newsRepository.getNewsList()
            }.onSuccess {
                _newsList.value = it
                Log.d(TAG, "getNewsList: $it")
            }.onFailure {
                Log.e(TAG, "getNewsList: ${it.message}", it)
            }
        }
    }

}