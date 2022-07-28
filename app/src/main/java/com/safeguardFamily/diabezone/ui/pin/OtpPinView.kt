package com.safeguardFamily.diabezone.ui.pin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.safeguardFamily.diabezone.R
import kotlin.math.max

class OtpPinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), TextWatcher, View.OnFocusChangeListener,
    View.OnKeyListener {
    private val DENSITY = getContext().resources.displayMetrics.density

    private var mPinLength = 4
    private val editTextList: MutableList<EditText> = ArrayList()
    private var mPinWidth = 50
    private var mTextSize = 12
    private var mPinHeight = 50
    private var mSplitWidth = 20
    private var mCursorVisible = false
    private var mDelPressed = false

    @get:DrawableRes
    @DrawableRes
    var pinBackground = R.drawable.bg_pin
        private set
    private var mPassword = false
    private var mHint: String? = ""
    private var inputType = InputType.TEXT
    private var finalNumberPin = false
    private var mListener: PinViewEventListener? = null
    private var fromSetValue = false
    private var mForceKeyboard = true

    enum class InputType { TEXT, NUMBER }

    interface PinViewEventListener {
        fun onDataEntered(otpPinView: OtpPinView?, fromUser: Boolean)
    }

    var mClickListener: OnClickListener? = null
    var currentFocus: View? = null
    var filters = arrayOfNulls<InputFilter>(1)
    var params: LayoutParams? = null

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        removeAllViews()
        mPinHeight *= DENSITY.toInt()
        mPinWidth *= DENSITY.toInt()
        mSplitWidth *= DENSITY.toInt()
        setWillNotDraw(false)
        initAttributes(context, attrs, defStyleAttr)
        params = LayoutParams(mPinWidth, mPinHeight)
        orientation = HORIZONTAL
        createEditTexts()
        super.setOnClickListener {
            var focused = false
            for (editText in editTextList)
                if (editText.length() == 0) {
                    editText.requestFocus()
                    openKeyboard()
                    focused = true
                    break
                }
            if (!focused && editTextList.size > 0)
                editTextList[editTextList.size - 1].requestFocus()

            if (mClickListener != null) mClickListener!!.onClick(this@OtpPinView)

        }
        // Bring up the keyboard
        val firstEditText: View = editTextList.first()
        firstEditText.postDelayed({ openKeyboard() }, 200)
        updateEnabledState()
    }

    private fun createEditTexts() {
        removeAllViews()
        editTextList.clear()
        var editText: EditText

        for (i in 0 until mPinLength) {
            editText = EditText(context)
            editText.textSize = mTextSize.toFloat()
            editTextList.add(i, editText)
            this.addView(editText)
            generateOneEditText(editText, "" + i)
        }
        setTransformation()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs == null) return

        val array = context.obtainStyledAttributes(attrs, R.styleable.Pinview, defStyleAttr, 0)
        pinBackground = array.getResourceId(R.styleable.Pinview_pinBackground, pinBackground)
        mPinLength = array.getInt(R.styleable.Pinview_pinLength, mPinLength)
        mPinHeight = array.getDimension(R.styleable.Pinview_pinHeight, mPinHeight.toFloat()).toInt()
        mPinWidth = array.getDimension(R.styleable.Pinview_pinWidth, mPinWidth.toFloat()).toInt()
        mSplitWidth =
            array.getDimension(R.styleable.Pinview_splitWidth, mSplitWidth.toFloat()).toInt()
        mTextSize = array.getDimension(R.styleable.Pinview_textSize, mTextSize.toFloat()).toInt()
        mCursorVisible = array.getBoolean(R.styleable.Pinview_cursorVisible, mCursorVisible)
        mPassword = array.getBoolean(R.styleable.Pinview_password, mPassword)
        mForceKeyboard = array.getBoolean(R.styleable.Pinview_forceKeyboard, mForceKeyboard)
        mHint = array.getString(R.styleable.Pinview_hint)
        val its = InputType.values()
        inputType = its[array.getInt(R.styleable.Pinview_inputType, 0)]
        array.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun generateOneEditText(styleEditText: EditText, tag: String) {
        params!!.setMargins(mSplitWidth / 2, mSplitWidth / 2, mSplitWidth / 2, mSplitWidth / 2)
        filters[0] = InputFilter.LengthFilter(1)
        styleEditText.filters = filters
        styleEditText.layoutParams = params
        styleEditText.gravity = Gravity.CENTER
        styleEditText.isCursorVisible = mCursorVisible

        if (!mCursorVisible) {
            styleEditText.isClickable = false
            styleEditText.hint = mHint
            styleEditText.setOnTouchListener { _, _ -> // When back space is pressed it goes to delete mode and when u click on an edit Text it should get out of the delete mode
                mDelPressed = false
                false
            }
        }
        styleEditText.apply {
            setBackgroundResource(pinBackground)
            setPadding(0, 0, 0, 0)
            this.tag = tag
            inputType = keyboardInputType
            addTextChangedListener(this@OtpPinView)
            onFocusChangeListener = this@OtpPinView
            setOnKeyListener(this@OtpPinView)
        }
    }

    private val keyboardInputType: Int
        get() {
            return when (inputType) {
                InputType.NUMBER -> android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
                InputType.TEXT -> android.text.InputType.TYPE_CLASS_TEXT
            }
        }

    var value: String
        get() {
            val sb = StringBuilder()
            for (et in editTextList) sb.append(et.text.toString())
            return sb.toString()
        }
        set(value) {
            val regex = Regex("[0-9]*") // Allow empty string to clear the fields
            fromSetValue = true

            if (inputType == InputType.NUMBER && !value.matches(regex) || editTextList.isEmpty())
                return

            var lastTagHavingValue = -1
            for (i in editTextList.indices)
                if (value.length > i) {
                    lastTagHavingValue = i
                    editTextList[i].setText(value[i].toString())
                } else editTextList[i].setText("")

            if (mPinLength > 0) {
                currentFocus = editTextList[mPinLength - 1]
                if (lastTagHavingValue == mPinLength - 1) {
                    currentFocus = editTextList[mPinLength - 1]
                    if (inputType == InputType.NUMBER || mPassword) this.finalNumberPin = true
                    this.mListener?.onDataEntered(this, false)
                }
                currentFocus?.requestFocus()
            }
            fromSetValue = false
            updateEnabledState()
        }

    fun requestPinEntryFocus(): View {
        val currentTag = max(0, indexOfCurrentFocus)
        val currentEditText = editTextList.get(currentTag)
        currentEditText.requestFocus()
        openKeyboard()
        return currentEditText
    }

    private fun openKeyboard() {
        if (mForceKeyboard) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    fun clearValue() {
        value = ""
    }

    override fun onFocusChange(view: View, isFocused: Boolean) {
        if (isFocused && !mCursorVisible) {
            if (mDelPressed) {
                currentFocus = view
                mDelPressed = false
                return
            }
            for (editText in editTextList) if (editText.length() == 0) {
                if (editText !== view) editText.requestFocus() else currentFocus = view
                return
            }

            if (editTextList[editTextList.size - 1] !== view) {
                editTextList[editTextList.size - 1].requestFocus()
            } else currentFocus = view
        } else if (isFocused && mCursorVisible) currentFocus = view
        else view.clearFocus()
    }

    private fun setTransformation() {
        if (mPassword) for (editText in editTextList) {
            editText.removeTextChangedListener(this)
            editText.transformationMethod = PinTransformationMethod()
            editText.addTextChangedListener(this)
        } else for (editText in editTextList) {
            editText.removeTextChangedListener(this)
            editText.transformationMethod = null
            editText.addTextChangedListener(this)
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, start: Int, i1: Int, count: Int) {
        if (charSequence.length == 1 && currentFocus != null) {
            val currentTag = indexOfCurrentFocus
            if (currentTag < mPinLength - 1) {
                var delay: Long = 1
                if (mPassword) delay = 25
                postDelayed({
                    val nextEditText = editTextList.get(currentTag + 1)
                    nextEditText.isEnabled = true
                    nextEditText.requestFocus()
                }, delay)
            }

            if (currentTag == mPinLength - 1 && inputType == InputType.NUMBER || currentTag == mPinLength - 1 && mPassword) {
                finalNumberPin = true
            }
        } else if (charSequence.isEmpty()) {
            if (indexOfCurrentFocus < 0) return
            val currentTag = indexOfCurrentFocus
            this.mDelPressed = true

            //For the last cell of the non password text fields. Clear the text without changing the focus.
            if (!this.editTextList.get(currentTag).text.isNullOrEmpty())
                this.editTextList.get(currentTag).setText("")
        }

        this.editTextList.forEach { item ->
            if (item.text.isNotEmpty()) {
                val index = this.editTextList.indexOf(item) + 1
                if (!this.fromSetValue && index == mPinLength)
                    this.mListener?.onDataEntered(this, true)
            }
        }
        updateEnabledState()
    }

    private fun updateEnabledState() {
        val currentTag = max(0, indexOfCurrentFocus)
        for (index in editTextList.indices) {
            val editText = editTextList[index]
            editText.isEnabled = index <= currentTag
        }
    }

    override fun afterTextChanged(editable: Editable) {}

    override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
        if (keyEvent.action == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_DEL) {
            // Perform action on Del press
            val currentTag = indexOfCurrentFocus
            val currentEditText = editTextList[currentTag].text
            //Last tile of the number pad. Clear the edit text without changing the focus.
            if (inputType == InputType.NUMBER && currentTag == mPinLength - 1 && finalNumberPin ||
                mPassword && currentTag == mPinLength - 1 && finalNumberPin
            ) {
                if (!currentEditText.isNullOrEmpty()) this.editTextList[currentTag].setText("")
                finalNumberPin = false
            } else if (currentTag > 0) {
                mDelPressed = true
                if (currentEditText.isNullOrEmpty()) this.editTextList[currentTag - 1]
                    .requestFocus()
                this.editTextList[currentTag].setText("")
            } else {
                if (!currentEditText.isNullOrEmpty()) editTextList[currentTag].setText("")
            }
            return true
        }
        return false
    }

    private val indexOfCurrentFocus: Int
        get() = editTextList.indexOf(currentFocus)

    var splitWidth: Int
        get() = mSplitWidth
        set(splitWidth) {
            mSplitWidth = splitWidth
            val margin = splitWidth / 2
            params?.setMargins(margin, margin, margin, margin)
            this.editTextList.forEach { it.layoutParams = params }
        }

    var pinHeight: Int
        get() = mPinHeight
        set(pinHeight) {
            mPinHeight = pinHeight
            params?.height = pinHeight
            this.editTextList.forEach { it.layoutParams = params }
        }

    var pinWidth: Int
        get() = mPinWidth
        set(pinWidth) {
            mPinWidth = pinWidth
            params?.width = pinWidth
            this.editTextList.forEach { it.layoutParams = params }
        }

    var pinLength: Int
        get() = mPinLength
        set(pinLength) {
            mPinLength = pinLength
            createEditTexts()
        }

    var isPassword: Boolean
        get() = mPassword
        set(password) {
            mPassword = password
            setTransformation()
        }

    var hint: String?
        get() = mHint
        set(mHint) {
            this.mHint = mHint
            this.editTextList.forEach {
                it.hint = mHint
            }
        }

    fun setPinBackgroundRes(@DrawableRes res: Int) {
        pinBackground = res
        this.editTextList.forEach {
            it.setBackgroundResource(res)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    fun getInputType(): InputType {
        return inputType
    }

    fun setInputType(inputType: InputType) {
        this.inputType = inputType
        val keyInputType = keyboardInputType
        editTextList.forEach { it.inputType = keyInputType }
    }

    fun setPinViewEventListener(listener: PinViewEventListener?) {
        mListener = listener
    }

    fun showCursor(status: Boolean) {
        mCursorVisible = status
        this.editTextList.forEach { it.isCursorVisible = status }
    }

    fun setTextSize(textSize: Int) {
        mTextSize = textSize
        this.editTextList.forEach { it.textSize = mTextSize.toFloat() }
    }

    fun setCursorColor(@ColorInt color: Int) {
        this.editTextList.forEach {
            setCursorColor(it, color)
        }
    }

    fun setTextColor(@ColorInt color: Int) {
        this.editTextList.forEach {
            it.setTextColor(color)
        }
    }

    fun setCursorShape(@DrawableRes shape: Int) {
        editTextList.forEach {
            try {
                val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                field[it] = shape
            } catch (ignored: Exception) {
            }
        }

    }

    private fun setCursorColor(view: EditText, @ColorInt color: Int) {
        try {
            // Get the cursor resource id
            var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            val drawableResId = field.getInt(view)

            // Get the editor
            field = TextView::class.java.getDeclaredField("mEditor")
            field.isAccessible = true
            val editor = field[view]

            // Get the drawable and set a color filter
            val drawable = ContextCompat.getDrawable(view.context, drawableResId)
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            val drawables = arrayOf(drawable, drawable)

            // Set the drawables
            field = editor.javaClass.getDeclaredField("mCursorDrawable")
            field.isAccessible = true
            field[editor] = drawables
        } catch (ignored: Exception) {
        }
    }

    init {
        gravity = Gravity.CENTER
        init(context, attrs, defStyleAttr)
    }
}