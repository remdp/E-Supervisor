package com.euromix.esupervisor.app.common.media

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.util.UUID

interface FileManager {

    /** Create file. */
    fun createFileExternalFilesDir(context: Context, format: String, environment: String): File {
        val storageDir: File = context.getExternalFilesDir(environment)
            ?: throw IOException("Error on getExternalFilesDir")
        return File.createTempFile(UUID.randomUUID().toString(), format, storageDir)
    }

    /** Create file. */
    fun createFileExternalStoragePublicDirectory(format: String, environment: String): File{
        val storageDir: File = Environment.getExternalStoragePublicDirectory(environment)
            ?: throw IOException("Error on getExternalStoragePublicDirectory")
        return File.createTempFile(UUID.randomUUID().toString(), format, storageDir)
    }
}