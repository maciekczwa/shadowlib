package pl.looksoft.shadowlib

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

fun <T : View> View.findShadowedView(@IdRes id: Int): T = findViewById<ShadowLayout>(id).getChild()

fun <T : View> Activity.findShadowedView(@IdRes id: Int): T = findViewById<ShadowLayout>(id).getChild()