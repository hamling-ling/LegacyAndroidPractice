package com.example.veryoldapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val api: IAwsApi
): ViewModel() {

    private val _text = MutableLiveData<String>("Hello ViewModel")
    val text: LiveData<String>
        get() = _text

    fun updateText() {
        _text.postValue(api.callApi())
    }
}
