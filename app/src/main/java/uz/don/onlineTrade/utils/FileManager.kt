package uz.don.onlineTrade.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileManager {

    // Extension function to get a File from a content URI
    fun ContentResolver.getFileFromUri(uri: Uri, context: Context): File? {
        var inputStream: InputStream? = null
        var file: File? = null
        try {
            inputStream = this.openInputStream(uri)
            if (inputStream != null) {
                file = createFileFromInputStream(inputStream, uri, this, context)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return file
    }

    // Function to create a File from an InputStream
    private fun createFileFromInputStream(inputStream: InputStream, uri: Uri, contentResolver: ContentResolver, context: Context): File? {
        var file: File? = null
        try {
            val displayName: String? = getDisplayName(uri, contentResolver)
            val extension: String? = getExtension(contentResolver.getType(uri))

            if (displayName != null && extension != null) {
                val outputDir: File = getOutputDirectory(context)
                file = File(outputDir, "$displayName.$extension")

                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    // Function to get the display name of the file from a content URI
    // Function to get the display name of the file from a content URI
    private fun getDisplayName(uri: Uri, contentResolver: ContentResolver): String? {
        var displayName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val displayNameIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
            if (displayNameIndex != -1 && it.moveToFirst()) {
                displayName = it.getString(displayNameIndex)
            }
        }
        return displayName
    }

    // Function to get the file extension from a MIME type
    private fun getExtension(mimeType: String?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    // Function to get the output directory for saving the file
    // Function to get the output directory for saving the file
    private fun getOutputDirectory(context: Context): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: context.filesDir
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }
    }
}