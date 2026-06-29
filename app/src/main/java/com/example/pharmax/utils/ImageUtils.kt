package com.example.pharmax.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

object ImageUtils {

    fun getFileExtension(uri: Uri, context: Context): String? {
        return context.contentResolver.getType(uri)?.let {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
        }
    }
}
