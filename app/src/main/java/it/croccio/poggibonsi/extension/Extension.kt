package it.croccio.poggibonsi.extension

import android.support.v4.app.Fragment

fun Fragment.intActivity(): IntActivity {
    return activity as IntActivity
}
