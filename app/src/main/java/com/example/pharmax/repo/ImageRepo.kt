package com.example.pharmax.repo

import android.content.Context
import android.net.Uri

interface ImageRepo {
    fun uploadImage(imageUri: Uri, context: Context, callback: (Boolean, String) -> Unit)
}
