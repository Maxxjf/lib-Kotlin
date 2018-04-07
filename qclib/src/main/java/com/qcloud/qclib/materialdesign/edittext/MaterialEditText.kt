package com.qcloud.qclib.materialdesign.edittext

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.KeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.materialdesign.listener.OnErrorListener
import com.qcloud.qclib.materialdesign.listener.OnGetFocusListener
import com.qcloud.qclib.materialdesign.listener.OnLostFocusListener
import com.qcloud.qclib.utils.ApiReplaceUtil
import com.qcloud.qclib.utils.DensityUtil

/**
 * 类说明：自定义实现MaterialDesign风格输入
 * Author: Kuzan
 * Date: 2018/4/7 10:44.
 */
class MaterialEditText(
        private val mContext: Context,
        attrs: AttributeSet? = null) : FrameLayout(mContext, attrs) {

    /**提示*/
    private var mTvHint: TextView? = null
    private var mEditTextContent: FrameLayout? = null
    private var mEditTextLayout: FrameLayout? = null
    private var mIcon: ImageView? = null
    private var mCleanLayout: FrameLayout? = null
    private var mCleanIcon: ImageView? = null
    private var mTvError: TextView? = null
    private var mTvWordCount: TextView? = null
    var mEditText: EditText? = null

    /**输入属性配置*/
    private var mInputTextSize = -1f
    private var mInputColor = -1
    private var mInputIconId = -1
    private var mCleanIconId = -1
    private var mUnderlineColor = -1
    private var mCursorColor = -1

    /**提示属性配置*/
    private var mHintText: String? = ""
    private var mHintScale = 0.7f
    private var mHintColor = -1
    private var mHintScaleColor = -1

    /**错误提示配置*/
    private var mErrorSize = -1f
    private var mErrorColor = -1
    private var mErrorShow = false

    /**输入提示配置*/
    private var mWordCountColor = -1
    private var mMaxLength = 0
    private var mWordCountEnabled = true

    private var mExpand = true
    private var mMinEditTextHeight = 2
    private var mEditTextLayoutHeight: Int = 0
    private var mHintHeight: Int = 0

    private var ANIMATION_DURATION: Long = 100

    var onGetFocusListener: OnGetFocusListener? = null
    var onLostFocusListener: OnLostFocusListener? = null
    var onErrorListener: OnErrorListener? = null
    var onEditorActionListener: TextView.OnEditorActionListener? = null

    private val mOnFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            mCleanLayout?.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
            mEditText?.addTextChangedListener(mTextWatcher)
            handleEditTextLength()
        } else {
            if (TextUtils.isEmpty(text) && !mExpand) {
                reduceEditText()
            }
            mCleanLayout?.visibility = View.GONE

            onLostFocusListener?.onLostFocus()
            if (onErrorListener != null) {
                mTvError?.visibility = if (onErrorListener!!.onError(text)) View.VISIBLE else View.GONE
            }
            hideWordCount()
        }
    }

    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            onGetFocusListener?.beforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onGetFocusListener?.onTextChanged(s, start, before, count)
            if (onErrorListener != null) {
                mTvError?.visibility = if (onErrorListener!!.onError(s)) View.VISIBLE else View.GONE
            }
            handleEditTextLength()
        }

        override fun afterTextChanged(s: Editable) {
            if (TextUtils.isEmpty(text)) {
                mCleanLayout?.visibility = View.GONE
            } else {
                mCleanLayout?.visibility = View.VISIBLE
            }

            onGetFocusListener?.afterTextChanged(s)
        }
    }

    /**
     * @return 最大输入长度
     */
    private val editTextMaxLength: Int
        get() {
            var length = 0
            try {
                val filters = mEditText!!.filters
                for (filter in filters) {
                    val clazz = filter.javaClass
                    if (clazz.name == "android.text.InputFilter\$LengthFilter") {
                        val fields = clazz.declaredFields
                        for (f in fields) {
                            if (f.name == "mMax") {
                                f.isAccessible = true
                                length = f.get(filter) as Int
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return length
        }

    /**
     * @return 输入内容
     */
    val text: String
        get() = mEditText!!.text.toString().trim { it <= ' ' }

    init {
        mMinEditTextHeight = DensityUtil.dp2px(mContext, mMinEditTextHeight.toFloat())
        parseAttrs(attrs)
        initView()
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = mContext.obtainStyledAttributes(attrs, R.styleable.Material_EditStyle)
            try {
                mInputTextSize = a.getDimension(R.styleable.Material_EditStyle_inputTextSize,
                        resources.getDimension(R.dimen.micro_text_size))
                mInputColor = a.getColor(R.styleable.Material_EditStyle_inputTextColor,
                        ApiReplaceUtil.getColor(mContext, R.color.black))
                mInputIconId = a.getResourceId(R.styleable.Material_EditStyle_inputIcon, mInputIconId)
                mCleanIconId = a.getResourceId(R.styleable.Material_EditStyle_cleanIcon, mCleanIconId)
                mUnderlineColor = a.getColor(R.styleable.Material_EditStyle_underlineColor,
                        ApiReplaceUtil.getColor(mContext, R.color.colorDivider))
                mCursorColor = a.getColor(R.styleable.Material_EditStyle_cursorColor,
                        ApiReplaceUtil.getColor(mContext, R.color.colorDivider))
                mHintText = a.getString(R.styleable.Material_EditStyle_hint)
                mHintScale = a.getFloat(R.styleable.Material_EditStyle_hintScale, mHintScale)
                mHintColor = a.getColor(R.styleable.Material_EditStyle_hintColor,
                        ApiReplaceUtil.getColor(mContext, R.color.black))
                mHintScaleColor = a.getColor(R.styleable.Material_EditStyle_hintScaleColor, mHintScaleColor)
                mErrorSize = a.getDimension(R.styleable.Material_EditStyle_errorSize,
                        resources.getDimension(R.dimen.micro_text_size))
                mErrorColor = a.getColor(R.styleable.Material_EditStyle_errorColor,
                        ApiReplaceUtil.getColor(mContext, R.color.colorRed))
                mMaxLength = a.getInteger(R.styleable.Material_EditStyle_length, mMaxLength)
                mWordCountEnabled = a.getBoolean(R.styleable.Material_EditStyle_wordCountEnabled, mWordCountEnabled)
                mWordCountColor = a.getColor(R.styleable.Material_EditStyle_wordCountColor,
                        ApiReplaceUtil.getColor(mContext, R.color.black))
                ANIMATION_DURATION = a.getInt(R.styleable.Material_EditStyle_expandDuration, ANIMATION_DURATION.toInt()).toLong()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                a.recycle()
            }
        }
    }

    private fun initView() {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_material_edittext, this)
        mTvHint = view.findViewById(R.id.tv_material_hint)
        mEditTextContent = view.findViewById(R.id.fl_material_content)
        mEditTextLayout = view.findViewById(R.id.fl_material_edittext)
        mIcon = view.findViewById(R.id.iv_material_icon)
        mEditText = view.findViewById(R.id.edt_material)
        mCleanLayout = view.findViewById(R.id.fl_material_clean)
        mCleanIcon = view.findViewById(R.id.iv_material_clean)
        mTvError = findViewById(R.id.tv_material_error)
        mTvWordCount = findViewById(R.id.tv_material_wordcount)

        initConfigurations()

        // init editTextLayout height
        mHintHeight = getMeasureHeight(mTvHint)
        mEditTextLayoutHeight = getMeasureHeight(mEditTextLayout)
        mEditTextContent!!.layoutParams.height = (mHintHeight * 0.9 + mEditTextLayoutHeight).toInt()

        // init hint location
        val hintParams = mTvHint!!.layoutParams as FrameLayout.LayoutParams
        hintParams.topMargin = (mHintHeight * 0.7).toInt()
        hintParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        hintParams.height = FrameLayout.LayoutParams.MATCH_PARENT
        hintParams.gravity = Gravity.CENTER_VERTICAL
        mTvHint!!.layoutParams = hintParams
        mTvHint!!.gravity = Gravity.CENTER_VERTICAL

        // init edittext
        mEditText!!.background.setColorFilter(mUnderlineColor, PorterDuff.Mode.SRC_ATOP)
        cursorColor(mCursorColor)
        mEditText!!.onFocusChangeListener = mOnFocusChangeListener
        mEditText!!.setOnEditorActionListener(onEditorActionListener)

        // show no icon as default
        if (mInputIconId == -1) {
            mIcon!!.visibility = View.GONE
        }

        // show no error as default
        mTvError!!.visibility = View.GONE

        // set editText height to 3
        mEditTextLayout!!.layoutParams.height = mMinEditTextHeight
        mEditTextLayout!!.setBackgroundColor(mUnderlineColor)

        mTvHint?.setOnClickListener {
            if (mExpand) {
                expandEditText()
            } else {
                reduceEditText()
            }
        }

        //show no cleanIcon as default
        mCleanLayout!!.visibility = View.GONE
        mCleanLayout!!.setOnClickListener { mEditText!!.setText("") }
    }

    private fun initConfigurations() {
        inputSize(TypedValue.COMPLEX_UNIT_PX, mInputTextSize)
        inputColor(mInputColor)
        if (mInputIconId != -1) {
            setIcon(mInputIconId)
        }
        if (mCleanIconId != -1) {
            cleanIcon(mCleanIconId)
        }
        hint(mHintText ?: "")
        hintColor(mHintColor)
        hintScale(mHintScale)
        if (mHintScaleColor != -1) {
            hintScaleColor(mHintScaleColor)
        }
        errorColor(mErrorColor)
        errorSize(TypedValue.COMPLEX_UNIT_PX, mErrorSize)
        if (mMaxLength != 0) {
            maxLength(mMaxLength)
        }
        wordCountEnabled(mWordCountEnabled)
        if (mWordCountColor != -1) {
            wordCountColor(mWordCountColor)
        }
    }

    private fun handleEditTextLength() {
        mMaxLength = if (editTextMaxLength == 0) mMaxLength else editTextMaxLength
        if (mWordCountEnabled && !TextUtils.isEmpty(text) && mMaxLength != 0) {
            showWordCount(text.length, mMaxLength)
        } else {
            hideWordCount()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showWordCount(currentLength: Int, maxLength: Int) {
        mTvWordCount!!.text = currentLength.toString() + " / " + maxLength
        mTvWordCount!!.visibility = View.VISIBLE
    }

    private fun hideWordCount() {
        mTvWordCount!!.text = ""
        mTvWordCount!!.visibility = View.GONE
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun expandEditText() {
        val expandAnimator = ValueAnimator.ofInt(mMinEditTextHeight, mEditTextLayoutHeight)
        expandAnimator.addUpdateListener { va ->
            mEditTextLayout!!.layoutParams.height = va.animatedValue as Int
            mEditTextLayout!!.requestLayout()
        }

        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(mTvHint, "alpha", 0.6f),
                ObjectAnimator.ofFloat(mTvHint, "scaleX", mHintScale),
                ObjectAnimator.ofFloat(mTvHint, "scaleY", mHintScale),
                ObjectAnimator.ofFloat(mTvHint, "translationX",
                        -mTvHint!!.measuredWidth * (1 - mHintScale) / 2),
                ObjectAnimator.ofFloat(mTvHint, "translationY", -mHintHeight * 1.3f),
                ObjectAnimator.ofFloat(mIcon, "alpha", 1f),
                ObjectAnimator.ofFloat(mEditText, "alpha", 1f),
                expandAnimator
        )
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                mEditTextLayout?.setBackgroundColor(ApiReplaceUtil.getColor(mContext, R.color.transparent))
            }

            override fun onAnimationEnd(animation: Animator) {
                if (mHintScaleColor != -1) {
                    mTvHint?.setTextColor(mHintScaleColor)
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        set.duration = ANIMATION_DURATION
        set.start()
        mExpand = false
        mEditText?.requestFocus()

        //show softinput
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun reduceEditText() {
        val reduceAnimator = ValueAnimator.ofInt(mEditTextLayoutHeight, mMinEditTextHeight)
        reduceAnimator.addUpdateListener { va ->
            mEditTextLayout!!.layoutParams.height = va.animatedValue as Int
            mEditTextLayout!!.requestLayout()
        }

        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(mTvHint, "alpha", 1f),
                ObjectAnimator.ofFloat(mTvHint, "scaleX", 1f),
                ObjectAnimator.ofFloat(mTvHint, "scaleY", 1f),
                ObjectAnimator.ofFloat(mTvHint, "translationX", 0f),
                ObjectAnimator.ofFloat(mTvHint, "translationY", 0f),
                ObjectAnimator.ofFloat(mIcon, "alpha", 0f),
                ObjectAnimator.ofFloat(mEditText, "alpha", 0f),
                reduceAnimator
        )
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                mTvHint?.setTextColor(mHintColor)
            }

            override fun onAnimationEnd(animation: Animator) {
                mEditTextLayout?.setBackgroundColor(mUnderlineColor)
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        set.duration = ANIMATION_DURATION
        set.start()
        mExpand = true
        mEditText!!.clearFocus()

        //hide softinput
        (mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(mEditText!!.windowToken, 0)
    }

    private fun expandLayout(fromHeight: Int, targetHeight: Int, view: View): ValueAnimator {
        val expandAnimator = ValueAnimator.ofInt(fromHeight, targetHeight)
        expandAnimator.addUpdateListener { va ->
            view.layoutParams.height = va.animatedValue as Int
            view.requestLayout()
        }
        expandAnimator.duration = ANIMATION_DURATION
        expandAnimator.start()
        return expandAnimator
    }

    private fun getMeasureHeight(view: View?): Int {
        view!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return view.measuredHeight
    }

    /**
     * 设置图标
     *
     * @param resId
     */
    fun setIcon(resId: Int) {
        mInputIconId = resId
        mIcon?.setImageResource(mInputIconId)
        mIcon?.visibility = View.VISIBLE
        mIcon?.alpha = 0f
        if (mEditText != null) {
            mEditText!!.setPadding(
                    DensityUtil.dp2px(mContext, 40f),
                    mEditText!!.paddingTop,
                    mEditText!!.paddingRight,
                    mEditText!!.paddingBottom)
        }
    }

    /**
     * 设置清除图标
     * */
    fun cleanIcon(resId: Int) {
        mCleanIconId = resId
        mCleanIcon?.setImageResource(mCleanIconId)
    }

    /**
     * 设置光标的颜色
     * */
    fun cursorColor(color: Int) {
        try {
            mCursorColor = color
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(mEditText)
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(mEditText)
            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true

            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = ApiReplaceUtil.getDrawable(mContext, mCursorDrawableRes)
            drawables[1] = ApiReplaceUtil.getDrawable(mContext, mCursorDrawableRes)
            drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            fCursorDrawable.set(editor, drawables)
        } catch (ignored: Throwable) {
            ignored.printStackTrace()
        }
    }

    /**
     * 设置内容
     *
     * @param s
     * @return
     */
    fun inputText(s: String) {
        mEditText?.setText(s)
        mEditText?.setSelection(s.length)
    }

    /**
     * 设置输入字体大小
     *
     * @param size
     * @return
     */
    fun inputSize(size: Float) {
        mInputTextSize = size
        mEditText?.textSize = mInputTextSize
        mTvHint?.textSize = mInputTextSize
    }

    /**
     * 设置输入字体大小
     *
     * @param size
     * @return
     */
    fun inputSize(unit: Int, size: Float) {
        mInputTextSize = size
        mEditText?.setTextSize(unit, mInputTextSize)
        mTvHint?.setTextSize(unit, mInputTextSize)
    }

    /**
     * 设置输入字体颜色
     *
     * @param color
     * @return
     */
    fun inputColor(color: Int): MaterialEditText {
        mInputColor = color
        mEditText?.setTextColor(mInputColor)
        return this
    }

    /**
     * 设置输入提示
     *
     * @param hint
     * @return
     */
    fun hint(hint: String) {
        mTvHint?.text = hint
    }

    /**
     * 设置输入提示
     *
     * @param hintRes
     * @return
     */
    fun hint(@StringRes hintRes: Int) {
        mTvHint?.setText(hintRes)
    }

    /**
     * 设置输入提示颜色
     *
     * @param color
     * @return
     */
    fun hintColor(color: Int) {
        mHintColor = color
        mTvHint?.setTextColor(mHintColor)
    }

    /**
     * 设置提示的尺度
     *
     * @param scale
     * @return
     */
    fun hintScale(scale: Float) {
        mHintScale = scale
    }

    /**
     * 设置提示的尺度颜色
     *
     * @param color
     * @return
     */
    fun hintScaleColor(color: Int) {
        mHintScaleColor = color
    }

    /**
     * 设置下划线颜色
     *
     * @param color
     * @return
     */
    fun underlineColor(color: Int) {
        mUnderlineColor = color
        if (mEditText != null) {
            mEditText!!.background.setColorFilter(mUnderlineColor, PorterDuff.Mode.SRC_ATOP)
        }
        if (!mExpand) {
            mEditTextLayout?.setBackgroundColor(mUnderlineColor)
        }
    }

    /**
     * 设置错误提示
     *
     * @param error
     * @return
     */
    fun error(error: String) {
        mTvError?.text = error
    }

    /**
     * 设置错误提示字体大小
     *
     * @param size
     * @return
     */
    fun errorSize(size: Float) {
        mErrorSize = size
        mTvError?.textSize = mErrorSize
    }

    /**
     * 设置错误提示字体大小
     *
     * @param size
     * @return
     */
    fun errorSize(unit: Int, size: Float) {
        mErrorSize = size
        mTvError?.setTextSize(unit, mErrorSize)
    }

    /**
     * 设置错误提示字体颜色
     *
     * @param color
     * @return
     */
    fun errorColor(color: Int) {
        mErrorColor = color
        mTvError?.setTextColor(mErrorColor)
    }

    /**
     * 设置是否显示错误提示
     *
     * @param show
     * @return
     */
    fun errorShow(show: Boolean) {
        mErrorShow = show
        mTvError?.visibility = if (mErrorShow) View.VISIBLE else View.GONE
    }

    /**
     * 设置输入数量颜色
     *
     * @param color
     * @return
     */
    fun wordCountColor(@ColorInt color: Int) {
        mWordCountColor = color
        mTvWordCount?.setTextColor(mWordCountColor)
    }

    /**
     * 设置输入时长
     *
     * @param duration
     * @return
     */
    fun duration(duration: Long) {
        ANIMATION_DURATION = duration
    }

    /**
     * 设置输入类型
     * */
    fun inputType(type: Int) {
        mEditText?.inputType = type
    }

    /**
     * 设置总长度
     *
     * @param length
     * @return
     */
    fun maxLength(length: Int) {
        mMaxLength = length
        mEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(mMaxLength))
    }

    fun wordCountEnabled(lengthEnabled: Boolean) {
        mWordCountEnabled = lengthEnabled
        mTvWordCount?.visibility = if (mWordCountEnabled) View.VISIBLE else View.GONE
    }

    fun filters(inputFilters: Array<InputFilter>) {
        mEditText?.filters = inputFilters
    }

    fun keyListener(listener: KeyListener) {
        mEditText?.keyListener = listener
    }

    override fun onSaveInstanceState(): Parcelable? {
        val ss = MdEditTextSavedState(super.onSaveInstanceState())
        ss.inputTextSize = mInputTextSize
        ss.inputColor = mInputColor
        ss.inputIconId = mInputIconId
        ss.cleanIconId = mCleanIconId
        ss.underlineColor = mUnderlineColor
        ss.cursorColor = mCursorColor
        ss.hintText = mHintText
        ss.hintScale = mHintScale
        ss.hintColor = mHintColor
        ss.hintScaleColor = mHintScaleColor
        ss.errorSize = mErrorSize
        ss.errorColor = mErrorColor
        ss.wordCountColor = mWordCountColor
        ss.maxLength = mMaxLength
        ss.wordCountEnabled = mWordCountEnabled
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is MdEditTextSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state)
        mInputTextSize = state.inputTextSize!!
        mInputColor = state.inputColor!!
        mInputIconId = state.inputIconId!!
        mCleanIconId = state.cleanIconId!!
        mUnderlineColor = state.underlineColor!!
        mCursorColor = state.cursorColor!!
        mHintText = state.hintText
        mHintScale = state.hintScale!!
        mHintColor = state.hintColor!!
        mHintScaleColor = state.hintScaleColor!!
        mErrorSize = state.errorSize!!
        mErrorColor = state.errorColor!!
        mWordCountColor = state.wordCountColor!!
        mMaxLength = state.maxLength!!
        mWordCountEnabled = state.wordCountEnabled
    }
}