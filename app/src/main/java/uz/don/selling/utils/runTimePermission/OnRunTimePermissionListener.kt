package uz.don.selling.utils.runTimePermission

interface OnRunTimePermissionListener {

    //onPermission Granted...
    fun onPermissionGranted()

    //onPermissionDenied
    fun onPermissionDenied()
}