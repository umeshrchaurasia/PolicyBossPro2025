/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.policyboss.demoandroidapp.Utility.ExtensionFun

import android.content.Context
import android.view.View
import android.widget.Toast


import com.google.android.material.snackbar.Snackbar



import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**************** For Snackbar *********************************************/
//fun View.showSnackbar(msgId: Int, length: Int) {
//    showSnackbar(context.getString(msgId), length)
//}
//
//fun View.showSnackbar(msg: String, length: Int) {
//    showSnackbar(msg, length, null, {})
//}
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showSnackbar(
        msgId: Int,
        length: Int,
        actionMessageId: Int,
        action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
}
//*********** Snackbar with Action ****************

//    layout.showSnackbar(
//    "Required for PolicyBoss Pro to get Location",
//    Snackbar.LENGTH_INDEFINITE,
//    "ALLOW"){
//
//       print("Action_
//
//    }
/**************** For AlertDialog with Action *********************************************/

 fun View.toast(context: Context ,text: String) = Toast.makeText( context, text, Toast.LENGTH_SHORT).show()



//****************************************************************************
//Mark : Handle Default Safe Area
//****************************************************************************/
fun View.applySystemBarInsetsPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updatePadding(
            left = systemBars.left,
            top = systemBars.top,
            right = systemBars.right,
            bottom = systemBars.bottom
        )
        insets // do not consume so children can also react if needed
    }
    requestApplyInsets()
}

//Mark : applyBottomSystemBarPadding If you only care about the bottom inset (e.g., navigation bar) and do not want top padding, you can:

/*
Bad Use Cases ❌
Screens with a DrawerLayout: As you discovered, this is the classic example of where it fails.

Screens with a CoordinatorLayout, FloatingActionButton (FAB), and BottomAppBar:
 Each of these Material components needs to handle insets differently to position
 itself correctly relative to the screen edges and each other.

Any screen with an immersive background: If you want a background
 map or image to extend edge-to-edge, but the UI controls on top of it need padding,
 a single generic function on the root won't work.
 */
fun View.applyBottomSystemBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        v.updatePadding(bottom = navBar.bottom)
        insets
    }
    requestApplyInsets()
}

fun View.applySystemBarInsetsPadding(
    applyTop: Boolean = true,
    applyBottom: Boolean = true
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updatePadding(
            top = if (applyTop) systemBars.top else paddingTop,
            bottom = if (applyBottom) systemBars.bottom else paddingBottom,
            left = systemBars.left,
            right = systemBars.right
        )
        insets
    }
    requestApplyInsets()
}





