package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val  DEFAULT_BORDER_WIDTH = 2
        private val SCALE_TYPE = ScaleType.CENTER_CROP
    }

    @Px
    private var borderWidth: Float = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
    @ColorInt
    private var borderColor: Int = DEFAULT_BORDER_COLOR
    @ColorInt
    private var initialsBackground: Int = Color.TRANSPARENT

    private var isInitialSet: Boolean = false
    private var initials: String = "?"

    private val avatarPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val initialsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect: Rect = Rect()
    private val borderRect: Rect = Rect()

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density)
            initialsBackground = a.getColor(R.styleable.CircleImageView_cv_colorBackground, Color.TRANSPARENT)
            a.recycle()
        }
        scaleType = SCALE_TYPE
        setup()
    }

    fun setInitials(i: String) {
        initials = i
        isInitialSet = true
        invalidate()
    }

    private fun setup() {
        with(borderPaint) {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }
        if (drawable == null) {
            isInitialSet = true
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        if (w == 0 || drawable == null) return
        val srcBitmap = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
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
        borderRect.set(viewRect)
        val half = (borderWidth / 2).toInt()
        borderRect.inset(half, half)
        prepareShader(w, h)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        isInitialSet = false
        prepareShader(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        isInitialSet = false
        prepareShader(width, height)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        isInitialSet = false
        prepareShader(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInitialSet && drawable != null) {
            drawAvatar(canvas)
        } else {
            drawInitials(canvas)
        }
        canvas.drawOval(borderRect.toRectF(), borderPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initialsPaint.color = initialsBackground
        canvas.drawOval(viewRect.toRectF(), initialsPaint)
        with(initialsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.5F
        }
        val offsetY = (initialsPaint.descent() + initialsPaint.ascent() * 0.6F)
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initialsPaint)
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

}