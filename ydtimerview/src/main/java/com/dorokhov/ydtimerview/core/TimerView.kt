package com.dorokhov.ydtimerview.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getColorOrThrow
import com.dorokhov.ydtimerview.R
import com.dorokhov.ydtimerview.listeners.TimerListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val STANDARD_WIDTH = 50
const val STANDARD_INDICATOR_RADIUS = 4
const val STANDARD_INDICATION_STROKE = 5f
const val STANDARD_COLOR_STRING = "#000000"
const val STANDARD_COLOR_MASK = "#778331"

@SuppressLint("ResourceAsColor")
class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ITimerView {

    private val timerDisposable = CompositeDisposable()
    private var listener: TimerListener? = null

    private val scaleDp = resources.displayMetrics.density
    private val scaleSp = resources.displayMetrics.scaledDensity
    var widthTimer = STANDARD_WIDTH * scaleDp


    var indicatorCircleRadius = STANDARD_INDICATOR_RADIUS * scaleDp
    var indicationStrokeWidth: Float = STANDARD_INDICATION_STROKE * scaleDp
    var indicationColor: Int? = null
    var indicatorColor: Int? = null
    var textColor: Int? = null
    var reverseIndication: Boolean = false
    var drawSeparatorsLines: Boolean = false
    var drawMainCircle: Boolean = true
    var showTime: Boolean = false

    var centerX: Float? = 0f
    var centerY: Float? = 0f

    var fullTime: Long = 120000
    var currentTime: Long = 0

    /*    val points = ArrayList<PointF>()
        val controlPoint1 = ArrayList<PointF>()
        val controlPoint2 = ArrayList<PointF>()*/

    /*   var paintWave: Paint = Paint(ANTI_ALIAS_FLAG).apply {
           isAntiAlias = true
           strokeWidth = 2f
           color = Color.parseColor(STANDARD_COLOR_STRING)
           style = Paint.Style.STROKE
           xfermode = (PorterDuffXfermode(PorterDuff.Mode.MULTIPLY))
    }*/

    private val circleTimerPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(STANDARD_COLOR_STRING)
        style = Paint.Style.STROKE
    }

    /*
    private val circleMaskPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor(STANDARD_COLOR_MASK)
    }
    */

    /*отвечает за кружочек которые бежит до конца*/
    private val indicatorTimePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    /*отвечает за заполнение циферблаата*/
    private val completedZonePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 3 * scaleDp
    }

    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 22 * scaleSp
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimerView,
            0, 0
        ).apply {
            try {
                indicatorCircleRadius = getDimension(
                    R.styleable.TimerView_radiusIndicator,
                    STANDARD_INDICATOR_RADIUS * scaleDp
                )
                drawMainCircle = getBoolean(R.styleable.TimerView_drawMainCircle, false)
                indicatorColor = getColorOrThrow(R.styleable.TimerView_indicatorColor)
                textColor = getColorOrThrow(R.styleable.TimerView_textColor)
                indicationColor =
                    getColorOrThrow(R.styleable.TimerView_indicationZoneColor)
                reverseIndication = getBoolean(R.styleable.TimerView_reverseCompletedZone, false)
                drawSeparatorsLines = getBoolean(R.styleable.TimerView_drawSeparatorLines, false)
                indicationStrokeWidth = getDimension(
                    R.styleable.TimerView_widthCompletedZone,
                    STANDARD_INDICATION_STROKE
                )
                showTime = getBoolean(R.styleable.TimerView_showTime, false)
                setPaintColorFromAttributes()
                setPaintWidthFromAttributes()
            } finally {
                recycle()
            }
        }
    }

    private fun setPaintColorFromAttributes() {
        indicatorTimePaint.color = indicatorColor!!
        completedZonePaint.color = indicationColor!!
        textPaint.color = textColor!!
    }

    private fun setPaintWidthFromAttributes() {
        indicatorTimePaint.strokeWidth = indicationStrokeWidth
    }

    fun setTimerListener(listener: TimerListener) {
        this.listener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minw = (paddingLeft + paddingRight + widthTimer + indicatorCircleRadius * 2).toInt()
        val w = resolveSizeAndState(minw, widthMeasureSpec, 0)

        val minh = (paddingTop + paddingBottom + widthTimer + indicatorCircleRadius * 2).toInt()
        val h = resolveSizeAndState(minh, heightMeasureSpec, 0)
        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            if (drawMainCircle)
                drawCircleTimer(this)

            if (drawSeparatorsLines)
                drawLinesIndicators(this)
            drawTime(this)
            drawCompletedZone(this, reverseIndication)
            drawTimeIndicator(this, currentTime, fullTime)
            // drawTheFirstWave(this)
            // calculateZoneForDrawing(this)
        }
    }

    override fun setTime(ms: Long) {
        if (ms >= fullTime) {
            listener?.onTimerComplete()
            stopTimer()
        }
        currentTime = ms
        invalidate()
    }

    override fun startTimer() {
        timerDisposable.add(Observable.interval(0, 100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .timeInterval()
            .doOnDispose { setTime(0) }
            .subscribe {
                setTime(it.value() * 100)
            })
    }

    override fun stopTimer() {
        currentTime = 0
        timerDisposable.dispose()
        invalidate()
    }

    override fun resetTimer() {
        setTime(0)
    }

    private fun drawTimeIndicator(canvas: Canvas, currentTime: Long, fullTime: Long) {
        val percentage = (currentTime.toFloat() / fullTime.toFloat())
        val rad = -percentage * PI * 2
        val radius = if (width < height) {
            ((width / 2) - (indicatorCircleRadius / 2))
        } else {
            ((height / 2) - (indicatorCircleRadius / 2))
        }
        val cx = (width / 2) - radius * sin(rad)
        val cy = (height / 2) - radius * cos(rad)
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), indicatorCircleRadius, indicatorTimePaint)
    }

    private fun drawCircleTimer(canvas: Canvas) {
        val cx = (width / 2).toFloat()
        val cy = (height / 2).toFloat()
        val radius = if (width < height) {
            ((width / 2) - (indicatorCircleRadius / 2))
        } else {
            ((height / 2) - (indicatorCircleRadius / 2))
        }
        canvas.drawCircle(cx, cy, radius, circleTimerPaint)
    }

    private fun drawLinesIndicators(canvas: Canvas) {
        centerY = (height / 2).toFloat()
        centerX = (width / 2).toFloat()

        for (lineIndex in 0..11) {
            val rad = (lineIndex.toFloat() / 12) * 2 * PI
            val minSide = if (width < height)
                width / 2
            else
                height / 2

            val smallRadius = minSide - 0.02 * minSide * scaleDp
            val bigRadius = minSide - 0.05 * minSide * scaleDp

            val startX = centerX!! + smallRadius * sin(rad)
            val endX = centerX!! + bigRadius * sin(rad)

            val startY = centerY!! + smallRadius * cos(rad)
            val endY = centerY!! + bigRadius * cos(rad)

            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                endX.toFloat(),
                endY.toFloat(),
                circleTimerPaint
            )
        }
    }

    private fun drawTime(canvas: Canvas) {
        val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
        val textW = textPaint.measureText(sdf.format(Date(currentTime)))
        canvas.drawText(
            sdf.format(Date(currentTime)),
            (width - textW) / 2f,
            height / 2f + textPaint.textSize / 2,
            textPaint
        )
    }

    private fun drawCompletedZone(canvas: Canvas, reverseIndication: Boolean) {
        val radius = if (width < height) {
            ((width / 2) - (indicatorCircleRadius / 2))
        } else {
            ((height / 2) - (indicatorCircleRadius / 2))
        }

        val reverse: Float = if (reverseIndication) -1f else 1f
        val sweepAngle = if (reverseIndication) {
            (360f - (currentTime.toFloat() / fullTime.toFloat()) * 360f)
        } else {
            (currentTime.toFloat() / fullTime.toFloat()) * 360f
        }

        canvas.drawArc(
            RectF(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius
            ),
            -90f,
            sweepAngle * reverse,
            false,
            completedZonePaint
        )
    }

    /*  private fun drawSeconds(canvas: Canvas) {
          textPaint.alpha = 255 - (255 - currentTime.toInt() % 1000 - 745)
          canvas.drawText("1", width / 2f, height / 2f, textPaint)
      }*/
    /*

    private fun calculatePoints() {
        // задает текущую высоту относительно времени, получается, что линия поднимется, в зависимости
        // от времени
        val currentHeight = height - (currentTime.toFloat() / fullTime) * height
        val xStorm = cos(currentHeight % 50) * 3 * scaleDp
        val bottomY = 8 * scaleDp
        val someData = ArrayList<Int>()
        someData.add(0)
        someData.add(20)
        someData.add(0)
        someData.add(20)
        someData.add(0)
        var xDiff = (width.toFloat() / (someData.size - 1))
        val maxData = someData.max()!!
        points.clear()
        for (i in someData.indices) {
            val y = currentHeight + bottomY - (someData[i] / maxData * bottomY)
            points.add(PointF(xDiff * i + xStorm, y))
        }
    }

    private fun calculateConnectionPointsForBezierCurve() {
        try {
            calculatePoints()
            controlPoint1.clear()
            controlPoint2.clear()
            for (i in 1 until points.size) {
                controlPoint1.add(PointF((points[i].x + points[i - 1].x) / 2, points[i - 1].y))
                controlPoint2.add(PointF((points[i].x + points[i - 1].x) / 2, points[i].y))
            }
        } catch (e: Exception) {

        }
    }

    // draws the curve
    private fun drawTheFirstWave(canvas: Canvas) {
        try {
            path = Path()
            calculateConnectionPointsForBezierCurve()
            if (points.isEmpty() && controlPoint1.isEmpty() && controlPoint2.isEmpty()) {
                return
            }
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                path.cubicTo(
                    controlPoint1[i - 1].x,
                    controlPoint1[i - 1].y,
                    controlPoint2[i - 1].x,
                    controlPoint2[i - 1].y,
                    points[i].x,
                    points[i].y
                )
            }
            drawMaskForWave(canvas)
            canvas.drawPath(path, paintWave)
        } catch (e: Exception) {

        }
    }

    private fun drawMaskForWave(canvas: Canvas) {
        val cx = (width / 2).toFloat()
        val cy = (height / 2).toFloat()
        val radius = if (width < height) {
            ((width / 2) - (indicatorCircleRadius / 2))
        } else {
            ((height / 2) - (indicatorCircleRadius / 2))
        }
        canvas.drawCircle(cx, cy, radius, circleMaskPaint)
    }*/

    override fun onDetachedFromWindow() {
        timerDisposable.clear()
        super.onDetachedFromWindow()
    }
}

fun Paint.setColor(res: Int, context: Context) {
    this.color = context.resources.getColor(res, null)
}