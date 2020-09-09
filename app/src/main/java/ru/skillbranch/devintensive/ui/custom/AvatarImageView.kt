package ru.skillbranch.devintensive.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.animation.doOnRepeat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpTiPx
import kotlin.math.max
import kotlin.math.truncate

class AvatarImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_SIZE = 40

        private val bgColor = arrayOf(
            Color.parseColor("#7BC862"),
            Color.parseColor("#E17076"),
            Color.parseColor("#FAA774"),
            Color.parseColor("#6EC9CB"),
            Color.parseColor("#65AADD"),
            Color.parseColor("#A695E7"),
            Color.parseColor("#EE7AAE"),
            Color.parseColor("#2196F3")
        )
    }

    @Px
    private var borderWidth: Float = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
    @ColorInt
    private var borderColor: Int = DEFAULT_BORDER_COLOR
    private var initials: String = "??"

    private val avatarPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val initialsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect: Rect = Rect()
    private val borderRect: Rect = Rect()
    private var size = 0
    private var isAvatarMode = true

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView)
            borderColor = ta.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = ta.getDimension(R.styleable.CircleImageView_cv_borderWidth, context.dpTiPx(DEFAULT_BORDER_WIDTH))
            initials = ta.getString(R.styleable.AvatarImageView_aiv_initials) ?: "??"
            ta.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
        setOnLongClickListener {
            handleLongClick()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(max(initSize, size), max(initSize, size))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }
        prepareShader(w, h)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)

        if(isAvatarMode) prepareShader(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        if(isAvatarMode) prepareShader(width, height)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)

        if(isAvatarMode) prepareShader(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable != null && isAvatarMode) {
                drawAvatar(canvas)
        } else {
            drawInitials(canvas)
        }
        val half =  (borderWidth / 2).toInt()
        borderRect.set(viewRect)
        borderRect.inset(half, half)
        canvas.drawOval(borderRect.toRectF(), borderPaint)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAvatarMode = isAvatarMode
        savedState.borderColor = borderColor
        savedState.borderWidth = borderWidth
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state)
            isAvatarMode = state.isAvatarMode
            borderColor = state.borderColor
            borderWidth = state.borderWidth
            with(borderPaint) {
                color = borderColor
                strokeWidth = borderWidth
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun setup() {
        with(borderPaint) {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        if (w == 0 || drawable == null) return

        val srcBitmap = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when(MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpTiPx(DEFAULT_SIZE).toInt()
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initialsPaint.color = initialsToColor(initials)
        canvas.drawOval(viewRect.toRectF(), initialsPaint)
        with(initialsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33F
        }

        val offsetY = (initialsPaint.descent() + initialsPaint.ascent() * 0.6F)
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initialsPaint)
    }

    private fun initialsToColor(letters: String): Int {
        val b = letters[0].toByte()
        val len = bgColor.size
        val d = b / len.toDouble()
        val index = ((d - truncate(d)) * len).toInt()
        return bgColor[index]
    }

    private fun handleLongClick(): Boolean {
        val va = ValueAnimator.ofInt(width, width*2).apply {
            duration = 300
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
        }

        va.addUpdateListener {
            size = it.animatedValue as Int
            requestLayout()
        }

        va.doOnRepeat {
            toggleMode()
        }

        va.start()
        return true
    }

    private fun toggleMode() {
        isAvatarMode = !isAvatarMode
        invalidate()
    }

    fun setBorderColor(@ColorInt color: Int) {
        borderColor = color
        borderPaint.color = color
        invalidate()
    }

    fun setBorderWidth(@Dimension width: Int) {
        borderWidth = context.dpTiPx(width)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    fun setInitials(initials: String) {
        this.initials = initials
        if (!isAvatarMode) invalidate()
    }

    private class SavedState: BaseSavedState, Parcelable {

        var isAvatarMode: Boolean = true
        var borderColor: Int = 0
        var borderWidth: Float = 0F

        constructor(superState: Parcelable?): super(superState)

        constructor(parcel: Parcel): super(parcel) {
            isAvatarMode = parcel.readInt() == 1
            borderColor = parcel.readInt()
            borderWidth = parcel.readFloat()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeInt(if (isAvatarMode) 1 else 0)
            parcel.writeInt(borderColor)
            parcel.writeFloat(borderWidth)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

}