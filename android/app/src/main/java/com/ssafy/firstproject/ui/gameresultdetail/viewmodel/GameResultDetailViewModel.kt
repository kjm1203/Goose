package com.ssafy.firstproject.ui.gameresultdetail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.firstproject.base.ApplicationClass.Companion.gameRepository
import com.ssafy.firstproject.data.model.response.GameStatistics
import kotlinx.coroutines.launch

private const val TAG = "GameResultDetailViewModel"

class GameResultDetailViewModel : ViewModel() {

    private val _gameStatistics = MutableLiveData<GameStatistics>()
    val gameStatistics: LiveData<GameStatistics> get() = _gameStatistics

    fun getGameStatistics(newsId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                gameRepository.getGameStatistics(newsId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        _gameStatistics.value = it
                    }
                    Log.d(TAG, "getGameStatistics: ${response.body()}")
                } else {
                    Log.d(TAG, "getGameStatistics: ${response.code()}")
                }
            }.onFailure {
                Log.e(TAG, "getGameStatistics: ${it.message}", it)
            }
        }
    }
}