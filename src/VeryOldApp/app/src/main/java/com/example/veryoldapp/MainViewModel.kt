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

    private val _contents = MutableLiveData<List<String>>()
    val contents: LiveData<List<String>>
        get() = _contents

    fun updateText() {
        _text.postValue(api.callApi())
    }

    fun playContents1() {
        _contents.postValue( listOf("BigBuckBunny.mp4", "ForBiggerBlazes.mp4"))
    }

    fun playContents2() {
        _contents.postValue( listOf("SubaruOutbackOnStreetAndDirt.mp4", "WeAreGoingOnBullrun.mp4"))
    }
}
