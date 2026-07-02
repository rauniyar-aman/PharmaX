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

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun uploadImage(uri: Uri, context: Context, onSuccess: (String) -> Unit = {}) {
        _isUploading.value = true
        repo.uploadImage(uri, context) { success, result ->
            _isUploading.value = false
            if (success) {
                _imageUrl.value = result
                onSuccess(result)
            } else {
                _message.value = "Image upload failed: $result"
            }
        }
    }

    fun setImageUrl(url: String) {
        _imageUrl.value = url
    }
}
