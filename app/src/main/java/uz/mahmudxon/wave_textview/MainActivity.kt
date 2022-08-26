package uz.mahmudxon.wave_textview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.mahmudxon.textview.Wave
import uz.mahmudxon.textview.WaveTextView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv: WaveTextView = findViewById(R.id.my_text_view)
        // set fancy typeface
        tv.typeface = Typefaces.get(this, "Satisfy-Regular.ttf")

        // start animation
        Wave().start(tv)
    }
}