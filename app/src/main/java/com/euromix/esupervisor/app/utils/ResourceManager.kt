package com.euromix.esupervisor.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources

interface ResourceManager {

    fun getString(@StringRes id: Int): String

    fun getString(@StringRes id: Int, vararg formatArgs: Any): String

    @ColorInt
    fun getColor(@ColorRes id: Int): Int

    fun getDrawable(@DrawableRes id: Int): Drawable?

    fun getBitmapFromDrawableRes(@DrawableRes resourceId: Int): Bitmap?
}

class ResourceManagerImpl(private val context: Context) : ResourceManager {

    override fun getString(id: Int) = context.getString(id)

    override fun getString(id: Int, vararg formatArgs: Any) = context.getString(id, *formatArgs)

    override fun getColor(id: Int) = context.getColor(id)

    override fun getDrawable(id: Int): Drawable? = context.getDrawable(id)

    override fun getBitmapFromDrawableRes(resourceId: Int): Bitmap? = convertDrawableToBitmap(
        AppCompatResources.getDrawable(context, resourceId)
    )

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}