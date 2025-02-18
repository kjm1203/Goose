package com.ssafy.firstproject.ui.game.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.firstproject.base.ApplicationClass.Companion.gameRepository
import com.ssafy.firstproject.data.model.request.GameResultRequest
import kotlinx.coroutines.launch

private const val TAG = "ChoiceDialogViewModel"

class ChoiceDialogViewModel : ViewModel() {

    private val _isGameResultSubmitSuccess = MutableLiveData<Boolean>()
    val isGameResultSubmitSuccess: LiveData<Boolean> get() = _isGameResultSubmitSuccess

    fun submitGameResult(gameResultRequest: GameResultRequest) {
        viewModelScope.launch {
            runCatching {
                gameRepository.submitFakeNewsResult(gameResultRequest)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    _isGameResultSubmitSuccess.value = true
                    Log.d(TAG, "updateGameResult: ${response.body()}")
                } else {
                    Log.d(TAG, "updateGameResult fail: ${response.code()}")
                }
            }.onFailure {
                _isGameResultSubmitSuccess.value = false
                Log.e(TAG, "updateGameResult: ${it.message}", it)
            }
        }
    }
}