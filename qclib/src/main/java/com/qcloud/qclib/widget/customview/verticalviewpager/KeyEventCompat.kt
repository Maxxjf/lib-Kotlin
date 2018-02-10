package com.qcloud.qclib.widget.customview.verticalviewpager

import android.view.KeyEvent
import android.view.View

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/20 17:44.
 */
object KeyEventCompat {

    /**
     * Select the correct implementation to use for the current platform.
     */
    internal val IMPL: KeyEventVersionImpl

    /**
     * Interface for the full API.
     */
    internal interface KeyEventVersionImpl {
        fun normalizeMetaState(metaState: Int): Int
        fun metaStateHasModifiers(metaState: Int, modifiers: Int): Boolean
        fun metaStateHasNoModifiers(metaState: Int): Boolean
        fun isCtrlPressed(event: KeyEvent): Boolean
    }

    /**
     * Interface implementation that doesn't use anything about v4 APIs.
     */
    internal open class BaseKeyEventVersionImpl : KeyEventVersionImpl {

        override fun normalizeMetaState(metaState: Int): Int {
            var newMetaState = metaState
            if (newMetaState and (KeyEvent.META_SHIFT_LEFT_ON or KeyEvent.META_SHIFT_RIGHT_ON) != 0) {
                newMetaState = newMetaState or KeyEvent.META_SHIFT_ON
            }
            if (metaState and (KeyEvent.META_ALT_LEFT_ON or KeyEvent.META_ALT_RIGHT_ON) != 0) {
                newMetaState = newMetaState or KeyEvent.META_ALT_ON
            }
            return newMetaState and META_ALL_MASK
        }

        override fun metaStateHasModifiers(metaState: Int, modifiers: Int): Boolean {
            var newMetaState = metaState
            newMetaState = normalizeMetaState(newMetaState) and META_MODIFIER_MASK
            newMetaState = metaStateFilterDirectionalModifiers(newMetaState, modifiers,
                    KeyEvent.META_SHIFT_ON, KeyEvent.META_SHIFT_LEFT_ON, KeyEvent.META_SHIFT_RIGHT_ON)
            newMetaState = metaStateFilterDirectionalModifiers(newMetaState, modifiers,
                    KeyEvent.META_ALT_ON, KeyEvent.META_ALT_LEFT_ON, KeyEvent.META_ALT_RIGHT_ON)
            return newMetaState == modifiers
        }

        override fun metaStateHasNoModifiers(metaState: Int): Boolean {
            return normalizeMetaState(metaState) and META_MODIFIER_MASK == 0
        }

        override fun isCtrlPressed(event: KeyEvent): Boolean {
            return false
        }

        companion object {
            private val META_MODIFIER_MASK = (
                    KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON or KeyEvent.META_SHIFT_RIGHT_ON
                            or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON or KeyEvent.META_ALT_RIGHT_ON
                            or KeyEvent.META_SYM_ON)

            // Mask of all lock key meta states.
            private val META_ALL_MASK = META_MODIFIER_MASK

            private fun metaStateFilterDirectionalModifiers(metaState: Int,
                                                            modifiers: Int, basic: Int, left: Int, right: Int): Int {
                val wantBasic = modifiers and basic != 0
                val directional = left or right
                val wantLeftOrRight = modifiers and directional != 0

                if (wantBasic) {
                    if (wantLeftOrRight) {
                        throw IllegalArgumentException("bad arguments")
                    }
                    return metaState and directional.inv()
                } else return if (wantLeftOrRight) {
                    metaState and basic.inv()
                } else {
                    metaState
                }
            }
        }
    }

    /**
     * Interface implementation for devices with at least v11 APIs.
     */
    internal class HoneycombKeyEventVersionImpl : BaseKeyEventVersionImpl() {
        override fun normalizeMetaState(metaState: Int): Int {
            return KeyEventCompatHoneycomb.normalizeMetaState(metaState)
        }

        override fun metaStateHasModifiers(metaState: Int, modifiers: Int): Boolean {
            return KeyEventCompatHoneycomb.metaStateHasModifiers(metaState, modifiers)
        }

        override fun metaStateHasNoModifiers(metaState: Int): Boolean {
            return KeyEventCompatHoneycomb.metaStateHasNoModifiers(metaState)
        }

        override fun isCtrlPressed(event: KeyEvent): Boolean {
            return KeyEventCompatHoneycomb.isCtrlPressed(event)
        }
    }

    init {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            IMPL = HoneycombKeyEventVersionImpl()
        } else {
            IMPL = BaseKeyEventVersionImpl()
        }
    }

    // -------------------------------------------------------------------

    fun normalizeMetaState(metaState: Int): Int {
        return IMPL.normalizeMetaState(metaState)
    }

    fun metaStateHasModifiers(metaState: Int, modifiers: Int): Boolean {
        return IMPL.metaStateHasModifiers(metaState, modifiers)
    }

    fun metaStateHasNoModifiers(metaState: Int): Boolean {
        return IMPL.metaStateHasNoModifiers(metaState)
    }

    fun hasModifiers(event: KeyEvent, modifiers: Int): Boolean {
        return IMPL.metaStateHasModifiers(event.metaState, modifiers)
    }

    fun hasNoModifiers(event: KeyEvent): Boolean {
        return IMPL.metaStateHasNoModifiers(event.metaState)
    }


    @Deprecated("Call {@link KeyEvent#startTracking()} directly. This method will be removed in a\n" +
            "      future release.",
            ReplaceWith("event.startTracking()"))
    fun startTracking(event: KeyEvent) {
        event.startTracking()
    }


    @Deprecated("Call {@link KeyEvent#isTracking()} directly. This method will be removed in a\n" +
            "      future release.",
            ReplaceWith("event.isTracking"))
    fun isTracking(event: KeyEvent): Boolean {
        return event.isTracking
    }


    @Deprecated("Call {@link View#getKeyDispatcherState()} directly. This method will be removed\n" +
            "      in a future release.",
            ReplaceWith("view.keyDispatcherState"))
    fun getKeyDispatcherState(view: View): Any {
        return view.keyDispatcherState
    }


    @Deprecated("Call\n" +
            "      {@link KeyEvent#dispatch(KeyEvent.Callback, KeyEvent.DispatcherState, Object)} directly.\n" +
            "      This method will be removed in a future release.",
            ReplaceWith("event.dispatch(receiver, state as KeyEvent.DispatcherState, target)", "android.view.KeyEvent"))
    fun dispatch(event: KeyEvent, receiver: KeyEvent.Callback, state: Any,
                 target: Any): Boolean {
        return event.dispatch(receiver, state as KeyEvent.DispatcherState, target)
    }

    fun isCtrlPressed(event: KeyEvent): Boolean {
        return IMPL.isCtrlPressed(event)
    }
}