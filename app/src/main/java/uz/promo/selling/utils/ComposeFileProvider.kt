package uz.promo.selling.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import uz.promo.selling.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComposeFileProvider : FileProvider(
    R.xml.file_paths
){
    companion object {

        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val file = File.createTempFile(
                imageFileName,
                ".jpg",
                directory
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}