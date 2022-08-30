package com.safeguardFamily.diabezone.common

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

object RealPathUtil {

    fun getRealPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId: String
            val split: Array<String>
            val type: String
            if (isExternalStorageDocument(uri)) {
                docId = DocumentsContract.getDocumentId(uri)
                split = docId.split(":".toRegex()).toTypedArray()
                type = split[0]
                if ("primary".equals(type, ignoreCase = true))
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]

            } else {
                if (isDownloadsDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(docId)
                    )
                    return getDataColumn(
                        context,
                        contentUri,
                        null as String?,
                        null as Array<String?>?
                    )
                }
                if (isMediaDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri)
                    split = docId.split(":".toRegex()).toTypedArray()
                    type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf<String?>(split[1])
                    return getDataColumn(context, contentUri, "_id=?", selectionArgs)
                }
            }
        } else {
            if ("content".equals(uri.scheme, ignoreCase = true))
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment
                else getDataColumn(
                    context,
                    uri,
                    null as String?,
                    null as Array<String?>?
                )
            if ("file".equals(uri.scheme, ignoreCase = true)) return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val projection = arrayOf("_data")
        val var8: String
        try {
            cursor = context.contentResolver.query(
                uri!!,
                projection,
                selection,
                selectionArgs,
                null as String?
            )
            if (cursor == null || !cursor.moveToFirst()) return null
            val index = cursor.getColumnIndexOrThrow("_data")
            var8 = cursor.getString(index)
        } finally {
            cursor?.close()
        }
        return var8
    }

    private fun isExternalStorageDocument(uri: Uri) =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri) =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri) =
        "com.android.providers.media.documents" == uri.authority

    private fun isGooglePhotosUri(uri: Uri) =
        "com.google.android.apps.photos.content" == uri.authority
}