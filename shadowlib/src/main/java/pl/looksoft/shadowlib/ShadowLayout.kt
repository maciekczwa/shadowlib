package pl.looksoft.shadowlib

/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by GIGAMOLE on 13.04.2016.
 */
class ShadowLayout : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
    }


    companion object {
        // Shadow bounds values
        private val MAX_ALPHA = 255
        private val MIN_RADIUS = 0.1f
    }

    // Shadow paint
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        isFilterBitmap = true
    }

    // Shadow bitmap and canvas
    private var shadowBitmap: Bitmap? = null
    private val shadowCanvas = Canvas()
    // View bounds
    private val viewBounds = Rect()
    // Check whether need to redraw shadow
    private var invalidateShadow = true

    var blockLayoutParams = false

    // Detect if shadow is visible
    var isShadowed: Boolean = true
        set(isShadowed) {
            field = isShadowed
            postInvalidate()
        }

    // Shadow variables
    var shadowColor: Int = 0
        set(shadowColor) {
            field = shadowColor
            mShadowAlpha = Color.alpha(shadowColor)
            resetShadow()
        }
    private var mShadowAlpha: Int = 0


    var shadowBlur: Float = 0.toFloat()
        set(shadowRadius) {
            field = Math.max(MIN_RADIUS, shadowRadius)
            if (isInEditMode) return
            mPaint.maskFilter = BlurMaskFilter(this.shadowBlur, BlurMaskFilter.Blur.NORMAL)
            resetShadow()
        }
    var shadowSpread: Float = 0f
        set(shadowDistance) {
            field = shadowDistance
            resetShadow()
        }
    var shadowDx: Float = 0f
        set(shadowDx) {
            field = shadowDx
            resetShadow()
        }
    var shadowDy: Float = 0f
        set(shadowDy) {
            field = shadowDy
            resetShadow()
        }

    init {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, mPaint)
    }

    fun initAttrs(attrs: AttributeSet?) {
        // Retrieve attributes from xml
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ShadowLayout)
            try {
                isShadowed = typedArray.getBoolean(R.styleable.ShadowLayout_isShadowed, true)
                shadowBlur = typedArray.getDimension(R.styleable.ShadowLayout_shadowLayoutBlur, 0f)
                shadowSpread = typedArray.getDimension(R.styleable.ShadowLayout_shadowLayoutSpread, 0f)
                shadowColor = typedArray.getColor(R.styleable.ShadowLayout_shadowLayoutColor, 0)
                shadowDx = typedArray.getDimension(R.styleable.ShadowLayout_shadowLayoutDx, 0f)
                shadowDy = typedArray.getDimension(R.styleable.ShadowLayout_shadowLayoutDy, 0f)
            } finally {
                typedArray.recycle()
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Clear shadow bitmap
        if (shadowBitmap != null) {
            shadowBitmap!!.recycle()
            shadowBitmap = null
        }
    }

    // Reset shadow layer
    private fun resetShadow() {
        // Set padding for shadow bitmap
        val padding = (shadowSpread + shadowBlur)
        setPadding((padding - shadowDx).toInt(), (padding - shadowDy).toInt(), (padding + shadowDx).toInt(), (padding + shadowDy).toInt())
        requestLayout()
    }

    private fun adjustShadowAlpha(adjust: Boolean): Int {
        return Color.argb(
                if (adjust) MAX_ALPHA else mShadowAlpha,
                Color.red(shadowColor),
                Color.green(shadowColor),
                Color.blue(shadowColor)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Set ShadowLayout bounds
        viewBounds.set(0, 0, measuredWidth, measuredHeight)
    }

    override fun requestLayout() {
        // Redraw shadow
        invalidateShadow = true
        super.requestLayout()
    }

    override fun dispatchDraw(canvas: Canvas) {
        // If is not shadowed, skip
        if (isShadowed) {
            // If need to redraw shadow
            if (invalidateShadow) {
                // If bounds is zero
                if (viewBounds.width() != 0 && viewBounds.height() != 0) {
                    // Reset bitmap to bounds
                    shadowBitmap = Bitmap.createBitmap(viewBounds.width(), viewBounds.height(), Bitmap.Config.ARGB_8888)
                    // Canvas reset
                    shadowCanvas.setBitmap(shadowBitmap)

                    // We just redraw
                    invalidateShadow = false
                    // Main feature of this lib. We create the local copy of all content, so now
                    // we can draw bitmap as a bottom layer of natural canvas.
                    // We draw shadow like blur effect on bitmap, cause of setShadowLayer() method of
                    // paint does`t draw shadow, it draw another copy of bitmap
                    super.dispatchDraw(shadowCanvas)

                    // Get the alpha bounds of bitmap
                    val extractedAlpha = shadowBitmap!!.extractAlpha()
                    // Clear past content content to draw shadow
                    shadowCanvas.drawColor(0, PorterDuff.Mode.CLEAR)

                    // Draw extracted alpha bounds of our local canvas
                    mPaint.color = adjustShadowAlpha(false)
                    shadowCanvas.drawBitmap(extractedAlpha, shadowDx, shadowDy, mPaint)

                    // Recycle and clear extracted alpha
                    extractedAlpha.recycle()
                } else {
                    // Create placeholder bitmap when size is zero and wait until new size coming up
                    shadowBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
                }
            }

            // Reset alpha to draw child with full alpha
            mPaint.color = adjustShadowAlpha(true)
            // Draw shadow bitmap
            if (shadowBitmap != null && !shadowBitmap!!.isRecycled)
                canvas.drawBitmap(shadowBitmap!!, 0.0f, 0.0f, mPaint)
        }

        // Draw child`s
        super.dispatchDraw(canvas)
    }

    fun <T> getChild(): T {
        return getChildAt(0) as T
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        LOGGER.log("sl setLayoutParams: ${params?.width} ${params?.height}")
        if(!blockLayoutParams) {
            super.setLayoutParams(params)
        }
    }
}