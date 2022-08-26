package uz.mahmudxon.textview

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import uz.mahmudxon.textview.WaveTextView
import uz.mahmudxon.textview.WaveTextView.AnimationSetupCallback
import android.content.res.ColorStateList
import android.graphics.*
import uz.mahmudxon.textview.R
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat

class WaveTextView : AppCompatTextView {
    interface AnimationSetupCallback {
        fun onSetupAnimation(WaveTextView: WaveTextView?)
    }

    // callback fired at first onSizeChanged
    var animationSetupCallback: AnimationSetupCallback? = null

    // wave shader coordinates
    private var maskX = 0f
    private var maskY = 0f

    // if true, the shader will display the wave
    var isSinking = false

    // true after the first onSizeChanged
    var isSetUp = false
        private set

    // shader containing a repeated wave
    private var shader: BitmapShader? = null

    // shader matrix
    private var shaderMatrix: Matrix? = null

    // wave drawable
    private var wave: Drawable? = null

    // (getHeight() - waveHeight) / 2
    private var offsetY = 0f

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        shaderMatrix = Matrix()
    }

    fun getMaskX(): Float {
        return maskX
    }

    fun setMaskX(maskX: Float) {
        this.maskX = maskX
        invalidate()
    }

    fun getMaskY(): Float {
        return maskY
    }

    fun setMaskY(maskY: Float) {
        this.maskY = maskY
        invalidate()
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        createShader()
    }

    override fun setTextColor(colors: ColorStateList) {
        super.setTextColor(colors)
        createShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createShader()
        if (!isSetUp) {
            isSetUp = true
            if (animationSetupCallback != null) {
                animationSetupCallback!!.onSetupAnimation(this@WaveTextView)
            }
        }
    }

    /**
     * Create the shader
     * draw the wave with current color for a background
     * repeat the bitmap horizontally, and clamp colors vertically
     */
    private fun createShader() {
        if (wave == null) {
            wave =   ContextCompat.getDrawable(context, R.drawable.wave)
        }
        val waveW = wave!!.intrinsicWidth
        val waveH = wave!!.intrinsicHeight
        val b = Bitmap.createBitmap(waveW, waveH, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.drawColor(currentTextColor)
        wave!!.setBounds(0, 0, waveW, waveH)
        wave!!.draw(c)
        shader = BitmapShader(b, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        paint.shader = shader
        offsetY = ((height - waveH) / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {

        // modify text paint shader according to sinking state
        if (isSinking && shader != null) {

            // first call after sinking, assign it to our paint
            if (paint.shader == null) {
                paint.shader = shader
            }

            // translate shader accordingly to maskX maskY positions
            // maskY is affected by the offset to vertically center the wave
            shaderMatrix!!.setTranslate(maskX, maskY + offsetY)

            // assign matrix to invalidate the shader
            shader!!.setLocalMatrix(shaderMatrix)
        } else {
            paint.shader = null
        }
        super.onDraw(canvas)
    }
}