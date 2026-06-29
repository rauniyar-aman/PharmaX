package com.example.pharmax.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.pharmax.repo.ImageRepo
import com.example.pharmax.repo.ImageRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImageViewModel(private val repo: ImageRepo = ImageRepoImpl()) : ViewModel() {

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> = _imageUrl.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    fun uploadImage(uri: Uri, context: Context, onSuccess: (String) -> Unit = {}) {
        _isUploading.value = true
        repo.uploadImage(uri, context) { success, url ->
            _isUploading.value = false
            if (success) {
                _imageUrl.value = url
                onSuccess(url)
            }
        }
    }

    fun setImageUrl(url: String) {
        _imageUrl.value = url
    }
}
