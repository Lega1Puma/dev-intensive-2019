package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
//import android.graphics.Rect
import android.view.inputmethod.InputMethodManager
//import kotlinx.android.synthetic.main.activity_profile.*

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

//fun Activity.isKeyboardOpen():Boolean {
//    val r = Rect()
//    val rootView = et_message.rootView
//    rootView.getWindowVisibleDisplayFrame(r)
//    val height = rootView.measuredHeight
//    val diff = height - r.bottom - r.top * 2
//    return diff > 200
//}
//
//fun Activity.isKeyboardClosed():Boolean {
//    val r = Rect()
//    val rootView = et_message.rootView
//    rootView.getWindowVisibleDisplayFrame(r)
//    val height = rootView.measuredHeight
//    val diff = height - r.bottom - r.top * 2
//    return diff < 200
//}
