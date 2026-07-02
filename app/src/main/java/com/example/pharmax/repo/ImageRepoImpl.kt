package com.example.pharmax.repo

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.pharmax.BuildConfig

class ImageRepoImpl : ImageRepo {

    override fun uploadImage(imageUri: Uri, context: Context, callback: (Boolean, String) -> Unit) {
        MediaManager.get().upload(imageUri)
            .unsigned(BuildConfig.CLOUDINARY_UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    callback(true, url)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    callback(false, error.description)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch(context)
    }
}
