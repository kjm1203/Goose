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

    private val _isIdValid = MutableLiveData<Boolean>()
    val isIdValid: LiveData<Boolean> get() = _isIdValid

    private val _isPasswordValid = MutableLiveData<Boolean>()
    val isPasswordValid: LiveData<Boolean> get() = _isPasswordValid

    private val _isPasswordMatch = MutableLiveData<Boolean>()
    val isPasswordMatch: LiveData<Boolean> get() = _isPasswordMatch

    private val _isNicknameValid = MutableLiveData<Boolean>()
    val isNicknameValid: LiveData<Boolean> get() = _isNicknameValid

    private val _isAllValid = MutableLiveData<Boolean>()
    val isAllValid: LiveData<Boolean> get() = _isAllValid

    fun checkIdValidation(id: String) {
        _isIdValid.value = id.matches("^[a-z0-9]{4,12}$".toRegex())
    }

    fun checkPasswordValidation(password: String) {
        _isPasswordValid.value = password.matches("^[a-z0-9]{4,12}$".toRegex())
    }

    fun checkPasswordMatch(password: String, confirmPassword: String) {
        _isPasswordMatch.value = password == confirmPassword
    }

    fun checkNickNameValidation(nickname: String) {
        _isNicknameValid.value = nickname.matches("^[가-힣a-zA-Z0-9]{2,12}$".toRegex())
    }

    fun checkAllValidation() {
        _isAllValid.value = (isIdValid.value == true &&
                isPasswordValid.value == true &&
                isPasswordMatch.value == true &&
                isNicknameValid.value == true)
    }

    // 회원가입 API 연동
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