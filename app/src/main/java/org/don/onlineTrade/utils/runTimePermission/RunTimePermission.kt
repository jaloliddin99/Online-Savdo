package org.don.onlineTrade.utils.runTimePermission

import android.Manifest
import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class RunTimePermission {



    fun permissionForGallery(listener: GalleryPermission, context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,

                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        listener.onGalleryGranted()
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken?
                    ) {
                        listener.onGalleryDenied()
                    }
                }).check()
        }else{
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        listener.onGalleryGranted()
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken?
                    ) {
                        listener.onGalleryDenied()
                    }
                }).check()
        }
    }


    interface GalleryPermission{
        fun onGalleryGranted()
        fun onGalleryDenied()
    }

    fun permissionListForCamera(onRunTimePermissionListener: OnRunTimePermissionListener, context: Context) {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    onRunTimePermissionListener.onPermissionGranted()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    onRunTimePermissionListener.onPermissionDenied()
                }
            }).check()
    }

    fun permissionList(onRunTimePermissionListener: OnRunTimePermissionListener, context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.POST_NOTIFICATIONS
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        onRunTimePermissionListener.onPermissionGranted()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken?
                    ) {
                        onRunTimePermissionListener.onPermissionDenied()
                    }
                }).check()
        }
    }

}