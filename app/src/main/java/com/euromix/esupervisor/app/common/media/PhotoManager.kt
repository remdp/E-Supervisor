package com.euromix.esupervisor.app.common.media

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.BITMAP_50_COMPRESSION_QUALITY
import com.euromix.esupervisor.app.Const.FILE_PROVIDER_PREFIX
import com.euromix.esupervisor.app.Const.JPG_PREFIX
import com.euromix.esupervisor.app.common.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PhotoManager(
    private val context: Context,
    private val fragment: Fragment,
    private val onPhotoTaken: Consumer<Uri>
) : FileManager {

    private var photoUri: Uri? = null

    fun takePhoto(requestCode: Int) {
        val imageFile = try {
            createFileExternalFilesDir(context, JPG_PREFIX, Environment.DIRECTORY_DCIM)
        } catch (ex: IOException) {
        //    Timber.w(ex)
            null
        }

        photoUri = imageFile?.let { Uri.fromFile(it) }

        imageFile?.let { file ->
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName.plus(FILE_PROVIDER_PREFIX),
                file
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            fragment.startActivityForResult(Intent.createChooser(intent, null), requestCode)
        } ?: Toast.makeText(context, R.string.media_store_error, Toast.LENGTH_SHORT).show()
    }

    @Suppress("DEPRECATION")
    fun takePhotoFromGallery(requestCode: Int) {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        fragment.startActivityForResult(Intent.createChooser(intent, null), requestCode)
    }

    fun setPhotoUri(uri: Uri) {
        photoUri = uri
        photoUri?.let(onPhotoTaken)
    }

    fun onPhotoTakeResult() {
        photoUri?.let { uri ->
            onPhotoTaken(uri)
        }
    }

    suspend fun compressPhoto(uri: Uri) = withContext(Dispatchers.Default) {
        val bitmap = decodeSampledBitmapFromUri(uri)
        uri.path?.let { path ->
            val fileToSave = File(path)
            FileOutputStream(fileToSave).use { out ->
                bitmap?.compress(
                    Bitmap.CompressFormat.JPEG,
                    BITMAP_50_COMPRESSION_QUALITY,
                    out
                )
            }
        }

    }

    private fun decodeSampledBitmapFromUri(uri: Uri): Bitmap? {
        val stream = BufferedInputStream(context.contentResolver.openInputStream(uri))
        val options = BitmapFactory.Options()
        return stream.use {
            stream.mark(stream.available())
            BitmapFactory.decodeStream(stream, null, options)

            Glide.with(context)
                .asBitmap()
                .load(uri)
                .submit()
                .get()
        }
    }
}