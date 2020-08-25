package com.example.minipaint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat



private const val  STROKE_WIDTH = 12f //has to be float

class MyCanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //caching what has been drawn before
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    //class vraiable for background color
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    var drawColor = ResourcesCompat.getColor(resources,R.color.colorPaint, null)

    //to store the path that has been drawn
    private var path = Path()

    //coordinates of touch
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    //latest touch coordinates
    private var currentX = 0f
    private var currentY = 0f

    //frame in the view
    private lateinit var frame : Rect

    //touch tolerance -
    // scaledTouchSlop returns the distance in pixels a touch can wander before the system thinks the user is scrolling.
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //set up paint with which to draw
    private val paint = Paint().apply {
        color = drawColor
        //smooths out edges
        isAntiAlias = true
        //affects how color with hi-precision than the device are drawnn
        isDither = true
        style = Paint.Style.STROKE //default : FILL
        strokeJoin = Paint.Join.ROUND //default : MITER
        strokeCap = Paint.Cap.ROUND // default : BUTT
        strokeWidth = STROKE_WIDTH //default : Hairline-width (really thin)

    }

    fun changeStrokeColor(color: Int){
        paint.color = color
    }

    //called by android system whenever a view changes
    //a new bitmap and canvas is created
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //if we already have bitmap --> recycle it ---> avoid memory leak
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        //create a bitmap with new w and h
        //third argument is bitmap color config
        // AR_8888 stores each color in 4 byte
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        //fill canvas with color
        extraCanvas.drawColor(backgroundColor)
        //calculate a rectangular frame around the picture
        val inset = 40
        frame = Rect(inset, inset, w - inset, h - inset)
    }

    //draw contents of cached extraBitmap
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //drawing a bitmap
        canvas.drawBitmap(extraBitmap, 0f, 0f,null)
        //drawing a rect
        canvas.drawRect(frame, paint)
    }

    //called whenever user touches the view
    //to cache x,y coordinates of the passed in touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> touchStart() //on press
            MotionEvent.ACTION_MOVE -> touchMove() //on drag
            MotionEvent.ACTION_UP -> touchUp()// on release
        }

        return true
    }

    //when user first touches the screen
    private fun touchStart(){
        path.reset()
        //to the first touch point
        path.moveTo(motionTouchEventX, motionTouchEventY)
        //update the current coordinates
        currentX = motionTouchEventX
        currentY = motionTouchEventY

    }

    /**
     * Using a path, there is no need to draw every pixel and each time request a refresh of the display.
     * Instead, you can (and will) interpolate a path between points for much better performance.
     *If the finger has barely moved, there is no need to draw.
     *If the finger has moved less than the touchTolerance distance, don't draw.
     */
    private fun touchMove() {
        //to create a curve between 2 point
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if(dx >= touchTolerance || dy >= touchTolerance){
            //QuadTo() adds a quadratic bezier from the last point
            //approaching control point (x1,y1) and ending at (x2,y2)
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX)/2, (motionTouchEventY + currentY)/2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            //draw the path in the extra bitmap to cache it
            extraCanvas.drawPath(path, paint)

            //redraw the screen
            invalidate()
        }
    }

    private fun touchUp() {
        //reset the path so it doesnt get drawn again
        path.reset()
    }
}