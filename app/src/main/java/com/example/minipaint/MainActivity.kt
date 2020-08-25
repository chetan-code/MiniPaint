package com.example.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.res.ResourcesCompat
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape


class MainActivity : AppCompatActivity() {

    lateinit var strokeColorButton: ImageButton
    lateinit var canvasView : MyCanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val myCanvasView = MyCanvasView(this)
        //fill the fullscreen with view
        //myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        //myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        //setContentView(myCanvasView)

        canvasView = findViewById(R.id.canvasView)
        strokeColorButton = findViewById(R.id.strokeColorButton)
        strokeColorButton.setOnClickListener {
            //open color picker
            ColorPickerDialog
                .Builder(this)        			// Pass Activity Instance
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor(ResourcesCompat.getColor(resources, R.color.colorPaint, null))// Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection
                    canvasView.changeStrokeColor(color)
                }
                .show()
        }
    }
}