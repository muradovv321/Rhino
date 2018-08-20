/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.jetbrains.anko.bundleOf


/**
 * Class holding static methods used by activities
 *
 * @author Orkhan Gasimli on 25.12.2017.
 */

/**
 * Helper method to determine visible fragment
 *
 * @param resId             resource id of container
 * @return                  return the currently visible fragment
 */
fun AppCompatActivity.currentFragmentOf(@IdRes resId: Int): Fragment? {
    return this.supportFragmentManager.findFragmentById(resId)
}

/**
 * Helper method to replace fragments
 *
 * @param resId             resource id of container
 * @param addToBackStack    boolean indicating the necessity to add the fragment to back-stack
 * @param params            additional data that should be passed into Fragment
 * @param tag               the tag of the fragment
 */
inline fun <reified T : Fragment> AppCompatActivity.replaceFragment(
        @IdRes resId: Int,
        addToBackStack: Boolean = true,
        vararg params: Pair<String, Any>,
        tag: String? = T::class.java.simpleName) {

    Handler().postDelayed({
        val ft = this.supportFragmentManager.beginTransaction()
        // TODO: below line sometimes causes the memory leak
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        val fragment = instanceOf<T>(*params)
        if (tag == null) {
            ft.replace(resId, fragment)
        } else {
            ft.replace(resId, fragment, tag)
        }
        if (addToBackStack) ft.addToBackStack(fragment.tag)
        ft.commitAllowingStateLoss()
    }, 300)
}

/**
 * Pass arguments to a Fragment without the hassle of
 * creating a static newInstance() method for every Fragment.
 *
 * Usage: instanceOf<MyFragment>("foo" to true, "bar" to 0)
 *
 * @param params            the parameters to be passed into the fragment
 * @return                  returns an instance of fragment
 */
inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>): T =
        T::class.java.newInstance().apply {
            arguments = bundleOf(*params)
        }

/**
 * Helper method to show dialog fragments
 *
 * @param tag               the tag of the fragment
 * @param requestCode       Optional request code, for calling back with {@link #onActivityResult(int, int, Intent)}.
 */
inline fun <reified T : DialogFragment>
        Fragment.showDialogFragment(tag: String, requestCode: Int = 0,
                                    vararg params: Pair<String, Any>) {
    val ft = this.activity?.supportFragmentManager?.beginTransaction()
    ft?.also {
        val dialogFragment = instanceOfDialogFragment<T>(*params)
        dialogFragment.setTargetFragment(this, requestCode)
        ft.add(dialogFragment, tag)
        ft.commitAllowingStateLoss()
    }
}

/**
 * Pass arguments to a DialogFragment without the hassle of
 * creating a static newInstance() method for every DialogFragment.
 *
 * Usage: instanceOfDialog<DialogFragment>("foo" to true, "bar" to 0)
 *
 * @param params            the parameters to be passed into the fragment
 * @return                  returns an instance of DialogFragment
 */
inline fun <reified T : DialogFragment> instanceOfDialogFragment(vararg params: Pair<String, Any>):
        T = T::class.java.newInstance().apply {
    arguments = bundleOf(*params)
}

/**
 * Helper & extension functions for loading images
 *
 * @param intent            {@link Intent} object required for starting an Activity
 * @param sharedElement     shared View element
 * @param sharedElementName name of the shared element
 */
fun Activity.launchActivity(intent: Intent, sharedElement: View? = null,
                            sharedElementName: String? = null) {
    val options = if (sharedElement == null || sharedElementName == null) {
        ActivityOptionsCompat.makeSceneTransitionAnimation(this)
    } else {
        ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement, sharedElementName)
    }
    startActivity(intent, options.toBundle())
}

/**
 * Extension function to change statusbar color
 *
 * @param color             color of the statusbar
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = color
    }
}
