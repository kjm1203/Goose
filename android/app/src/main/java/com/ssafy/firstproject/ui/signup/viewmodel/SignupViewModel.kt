package com.ssafy.firstproject.ui.signup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.firstproject.base.ApplicationClass
import com.ssafy.firstproject.data.model.User
import kotlinx.coroutines.launch

private const val TAG = "SignupViewModel"

class SignupViewModel : ViewModel() {

    private val _isSignupSuccess = MutableLiveData<Boolean>()
    val isSignupSuccess: LiveData<Boolean>
        get() = _isSignupSuccess

    fun signUp(user: User) {
        viewModelScope.launch {
            kotlin.runCatching {
                ApplicationClass.userRepository.signUp(user)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        _isSignupSuccess.value = it.success
                    }
                    Log.d(TAG, "signUp: ${response.body()}")
                } else {
                    _isSignupSuccess.value = false
                    Log.d(TAG, "signUp fail: ${response.code()}")
                }
            }.onFailure {
                _isSignupSuccess.value = false
                Log.e(TAG, "signUp: ${it.message}", it)
            }
        }
    }
}