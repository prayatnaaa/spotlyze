package com.bangkit.spotlyze.ui.camera

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.spotlyze.data.remote.response.ClassifySkinResponse
import com.bangkit.spotlyze.data.repository.SkinRepository
import com.bangkit.spotlyze.data.source.Result
import kotlinx.coroutines.launch

class CameraViewModel(private val repository: SkinRepository) : ViewModel() {

    private var _result = MutableLiveData<Result<ClassifySkinResponse>>()
    val result: LiveData<Result<ClassifySkinResponse>> = _result


    fun classifySkin(recommendation: String, imageUri: Uri, context: Context) {
        _result.value = Result.Loading
        viewModelScope.launch {
            _result.value = repository.classifySkin(recommendation, imageUri, context)
        }
    }
}