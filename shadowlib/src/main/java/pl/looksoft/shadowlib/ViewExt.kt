package pl.looksoft.shadowlib

import android.support.annotation.IdRes
import android.view.View

fun <T : View> View.findShadowedView(@IdRes id: Int): T = findViewById<ShadowLayout>(id).getChild()