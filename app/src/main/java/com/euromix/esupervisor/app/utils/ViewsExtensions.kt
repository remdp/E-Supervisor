package com.euromix.esupervisor.app.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.screens.Status

fun View.setColorStatus(status: String) {
    backgroundTintList = context.colorStateList(Status.getColorStatus(status))
}

//fun View.setBackgroundTintList(@ColorRes color: Int) {
//    backgroundTintList = context.colorStateList(color)
//}

//fun ImageView.setColorTint(@ColorRes color: Int) {
//    setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
//}

fun Context.colorStateList(@ColorRes color: Int): ColorStateList {
    return ColorStateList.valueOf(ContextCompat.getColor(this, color))
}

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}
fun View.gone(){
    visibility = View.GONE
}

//fun RecyclerView.setDivider(
//    @DrawableRes drawableRes: Int,
//    isGridDecoration: Boolean = false
//) {
//    val drawable = ContextCompat.getDrawable(
//        this.context,
//        drawableRes
//    )
//    drawable?.let {
//        val divider =
//            if (!isGridDecoration)
//                DividerItemDecoration(
//                    this.context,
//                    DividerItemDecoration.VERTICAL
//                ).apply { setDrawable(it) }
//            else
//                GridDividerItemDecoration(this.context, GridDividerItemDecoration.ALL)
//                    .apply { setDrawable(it) }
//
//        addItemDecoration(divider)
//    }
//}