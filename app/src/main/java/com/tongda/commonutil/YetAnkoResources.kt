package com.tongda.commonutil

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View

/**
* Created by Zhou Jinlong on 2017-08-07.
*/
fun Int.toColor(context: Context): Int {
    return ContextCompat.getColor(context, this)
}

fun Context.toColor(color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun Int.toPixelSize(context: Context): Int {
    return context.resources.getDimensionPixelSize(this)
}

fun Context.toPixelSize(dimen: Int): Int {
    return this.resources.getDimensionPixelSize(dimen)
}

fun Int.toText(context: Context): String {
    return context.getString(this)
}

fun Context.toText(text: Int): String {
    return this.getString(text)
}

fun View?.show() {
    if (this != null && this.visibility != View.VISIBLE) {
        this.visibility = View.VISIBLE
    }
}

fun View?.hide() {
    if (this != null && this.visibility != View.INVISIBLE) {
        this.visibility = View.INVISIBLE
    }
}

fun View?.gone() {
    if (this != null && this.visibility != View.GONE) {
        this.visibility = View.GONE
    }
}

fun View?.isVisible(): Boolean {
    return this?.visibility == View.VISIBLE
}

fun View?.isGone(): Boolean {
    return this?.visibility == View.VISIBLE
}

fun View?.isInvisible(): Boolean {
    return this?.visibility == View.INVISIBLE
}

fun String.toVertical(): String {
    return this.replace('（', '(').replace('）', ')').replace('(', '︵').replace(')', '︶')
}
