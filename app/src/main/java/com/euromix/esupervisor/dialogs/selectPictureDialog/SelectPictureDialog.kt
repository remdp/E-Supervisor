package com.euromix.esupervisor.dialogs.selectPictureDialog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.REQUEST_CODE_CHOOSE_FILE_PERMISSION
import com.euromix.esupervisor.app.Const.TAKE_PHOTO
import com.euromix.esupervisor.app.common.media.PhotoManager
import com.euromix.esupervisor.app.utils.PermissionHelper
import com.euromix.esupervisor.app.utils.PermissionManager
import com.euromix.esupervisor.app.utils.permissionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class SelectPictureDialog : BottomSheetDialogFragment() {

    private lateinit var photoManager: PhotoManager
    private lateinit var permissionManager: PermissionManager

    private val selectPictureViewModel by activityViewModels<SelectPictureViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = permissionManager()
        photoManager = PhotoManager(requireContext(), this) { photoUri ->
            selectPictureViewModel.viewModelScope.launch {
                photoManager.compressPhoto(photoUri)
                selectPictureViewModel.selectPicture(photoUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.select_picture_dialog_fragment, container, false)
        setupUI(view)
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestResult(requestCode, permissions, grantResults)
    }

    private fun setupUI(view: View) {
        view.findViewById<ConstraintLayout>(R.id.gallery).setOnClickListener {
            permissionManager.request(
                permissions = listOf(PermissionHelper.getImagePermission()),
                onAccepted = { photoManager.takePhotoFromGallery(REQUEST_CODE_CHOOSE_FILE_PERMISSION) },
                onDenied = { }
            )
        }
        view.findViewById<ConstraintLayout>(R.id.camera).setOnClickListener {
            permissionManager.request(
                permissions = listOf(
                    Manifest.permission.CAMERA,
                    PermissionHelper.getImagePermission()
                ) + PermissionHelper.getWriteStoragePermissionList(),
                onAccepted = { photoManager.takePhoto(TAKE_PHOTO) },
                onDenied = { }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_CHOOSE_FILE_PERMISSION -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { uri ->
                    photoManager.setPhotoUri(uri)
                    dismiss()
                }
            }
            TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                photoManager.onPhotoTakeResult()
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(): SelectPictureDialog = SelectPictureDialog()
    }
}