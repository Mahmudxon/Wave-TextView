package uz.mahmudxon.textview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import uz.mahmudxon.textview.WaveTextView.AnimationSetupCallback

class Wave {
    private var animatorSet: AnimatorSet? = null
    var animatorListener: Animator.AnimatorListener? = null
    fun start(textView: WaveTextView, maskXDuration: Long = 1000, maskYDuration: Long = 10000) {
        val animate = Runnable {
            textView.isSinking = true

            // horizontal animation. 200 = wave.png width
            val maskXAnimator = ObjectAnimator.ofFloat(textView, "maskX", 0f, 200f)
            maskXAnimator.repeatCount = ValueAnimator.INFINITE
            maskXAnimator.duration = maskXDuration
            maskXAnimator.startDelay = 0
            val h = textView.height

            // vertical animation
            // maskY = 0 -> wave vertically centered
            // repeat mode REVERSE to go back and forth
            val maskYAnimator =
                ObjectAnimator.ofFloat(textView, "maskY", (h / 2).toFloat(), (-h / 2).toFloat())
            maskYAnimator.repeatCount = ValueAnimator.INFINITE
            maskYAnimator.repeatMode = ValueAnimator.REVERSE
            maskYAnimator.duration = maskYDuration
            maskYAnimator.startDelay = 0

            // now play both animations together
            animatorSet = AnimatorSet()
            animatorSet!!.playTogether(maskXAnimator, maskYAnimator)
            animatorSet!!.interpolator = LinearInterpolator()
            animatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    textView.isSinking = false
                    textView.postInvalidateOnAnimation()
                    animatorSet = null
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            if (animatorListener != null) {
                animatorSet!!.addListener(animatorListener)
            }
            animatorSet!!.start()
        }
        if (!textView.isSetUp) {
            textView.animationSetupCallback = object : AnimationSetupCallback {
                override fun onSetupAnimation(target: WaveTextView?) {
                    animate.run()
                }
            }
        } else {
            animate.run()
        }
    }

    fun cancel() {
        if (animatorSet != null) {
            animatorSet!!.cancel()
        }
    }
}