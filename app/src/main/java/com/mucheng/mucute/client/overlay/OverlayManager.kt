package com.mucheng.mucute.client.overlay

import android.content.Context
import android.view.WindowManager
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mucheng.mucute.client.game.ModuleManager
import com.mucheng.mucute.client.ui.theme.MuCuteClientTheme
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
object OverlayManager {

    private val overlayWindows = ArrayList<OverlayWindow>()

    private var contextWeakRef: WeakReference<Context> = WeakReference(null)

    val currentContext: Context?
        get() = contextWeakRef.get()

    var isShowing = false
        private set

    init {
        with(overlayWindows) {
            add(OverlayButton())
            addAll(ModuleManager
                .modules
                .filter { it.isShortcutDisplayed }
                .map { it.overlayShortcutButton })
        }
    }

    fun showOverlayWindow(overlayWindow: OverlayWindow) {
        overlayWindows.add(overlayWindow)

        val context = currentContext
        if (isShowing && context != null) {
            showOverlayWindow(context, overlayWindow)
        }
    }

    fun dismissOverlayWindow(overlayWindow: OverlayWindow) {
        overlayWindows.remove(overlayWindow)

        val context = currentContext
        if (isShowing && context != null) {
            dismissOverlayWindow(context, overlayWindow)
        }
    }

    fun show(context: Context) {
        this.contextWeakRef = WeakReference(context)

        overlayWindows.forEach {
            showOverlayWindow(context, it)
        }

        isShowing = true
    }

    fun dismiss() {
        val context = currentContext
        if (context != null) {
            overlayWindows.forEach {
                dismissOverlayWindow(context, it)
            }
            isShowing = false
        }
    }

    private fun showOverlayWindow(context: Context, overlayWindow: OverlayWindow) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = overlayWindow.layoutParams
        val composeView = overlayWindow.composeView
        composeView.setContent {
            MuCuteClientTheme {
                overlayWindow.Content()
            }
        }
        val lifecycleOwner = overlayWindow.lifecycleOwner
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = overlayWindow.viewModelStore
        })
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.compositionContext = overlayWindow.recomposer
        if (overlayWindow.firstRun) {
            overlayWindow.composeScope.launch {
                overlayWindow.recomposer.runRecomposeAndApplyChanges()
            }
            overlayWindow.firstRun = false
        }

        try {
            windowManager.addView(composeView, layoutParams)
        } catch (_: Exception) {

        }
    }

    private fun dismissOverlayWindow(context: Context, overlayWindow: OverlayWindow) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val composeView = overlayWindow.composeView

        try {
            windowManager.removeView(composeView)
        } catch (_: Exception) {

        }
    }

}