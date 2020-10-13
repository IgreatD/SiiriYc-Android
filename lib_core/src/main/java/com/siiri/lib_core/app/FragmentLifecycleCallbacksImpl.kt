package com.siiri.lib_core.app

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

class FragmentLifecycleCallbacksImpl :
    FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentAttached(
        fm: FragmentManager,
        f: Fragment,
        context: Context
    ) {
        Timber.i("$f - onFragmentAttached")
    }

    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        Timber.i("$f - onFragmentCreated")
        f.retainInstance = true
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        Timber.i("$f - onFragmentViewCreated")
    }

    override fun onFragmentActivityCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        Timber.i("$f - onFragmentActivityCreated")
    }

    override fun onFragmentStarted(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentStarted")
    }

    override fun onFragmentResumed(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentResumed")
    }

    override fun onFragmentPaused(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentPaused")
    }

    override fun onFragmentStopped(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentStopped")
    }

    override fun onFragmentSaveInstanceState(
        fm: FragmentManager,
        f: Fragment,
        outState: Bundle
    ) {
        Timber.i("$f - onFragmentSaveInstanceState")
    }

    override fun onFragmentViewDestroyed(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentViewDestroyed")
    }

    override fun onFragmentDestroyed(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentDestroyed")
    }

    override fun onFragmentDetached(
        fm: FragmentManager,
        f: Fragment
    ) {
        Timber.i("$f - onFragmentDetached")
    }
}