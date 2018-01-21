package com.rhythm7.opengldemo

import android.util.Log

/**
 * Created by Jaminchanks on 2018-01-21.
 */
object Logger {
    val DEBUG_MODE = true

    fun e(tag: String, msg: String) {
        if (DEBUG_MODE) Log.e(tag, msg)
    }

    fun i(tag: String, msg: String) {
        if (DEBUG_MODE) Log.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        if (DEBUG_MODE) Log.w(tag, msg)
    }

}