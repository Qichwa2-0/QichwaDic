package com.ocram.qichwadic.features.search.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat

import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * See https://medium.com/@beraldofilippo/android-coordinatorlayout-scroll-aware-fab-f0c6264a5ed1
 */
class ScrollAwareFABBehavior(context: Context, attr: AttributeSet) : FloatingActionButton.Behavior() {

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, nestedScrollAxes: Int, viewType: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, viewType)
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        if (child.visibility == View.VISIBLE) {
            child.hide()
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        child.show()
    }
}
