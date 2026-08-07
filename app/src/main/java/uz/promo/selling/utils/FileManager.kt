package uz.promo.selling.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

object FileManager {

    private const val TAG = "FileManager"

    // Extension function to get a File from a content URI. Never throws — a
    // URI that can't be read (revoked grant, dead provider, odd metadata)
    // returns null so callers can skip it.
    fun ContentResolver.getFileFromUri(uri: Uri, context: Context): File? {
        return try {
            openInputStream(uri)?.use { inputStream ->
                // Some providers return no display name or MIME type, and
                // display names can contain characters that are invalid in file
                // names — a generated unique name avoids that whole class of
                // failures (and collisions between same-named picked images).
                val extension = getExtension(getType(uri)) ?: "jpg"
                val file = File.createTempFile("upload_", ".$extension", getOutputDirectory(context))
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                file
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not read image from uri: $uri", e)
            null
        }
    }

    // Function to get the file extension from a MIME type
    private fun getExtension(mimeType: String?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    // Upload copies go to the app cache: the system can reclaim it, it isn't
    // user-visible, and leftovers don't inflate the app's reported storage the
    // way the previous external Pictures directory did.
    private fun getOutputDirectory(context: Context): File {
        return File(context.cacheDir, "uploads").apply { mkdirs() }
    }
}
