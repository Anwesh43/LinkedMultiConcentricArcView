package com.anwesh.uiprojects.multiconcentricarcview

/**
 * Created by anweshmishra on 22/02/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodeColors : Array<String> = arrayOf("#3F51B5", "#4CAF50", "#f44336", "#0D47A1", "#FFB300")
val arcs : Int = 5
val scGap : Float = 0.02f / arcs
val strokeFactor : Int = 90
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawConcentricArc(i : Int, scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, arcs)
    val gap : Float = size / arcs
    val deg : Float = 360f / arcs
    save()
    rotate(deg * i)
    drawArc(RectF(-gap / 2, -gap / 2, gap / 2, gap / 2), 0f, deg * sf, false, paint)
    restore()
}

fun Canvas.drawMultiConcentricArcs(scale : Float, size : Float, paint : Paint) {
    for (j in 0..(arcs - 1)) {
        drawConcentricArc(j, scale, size, paint)
    }
}

fun Canvas.drawMCANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = Math.min(w, h)
    save()
    translate(w / 2, h  / 2)
    drawMultiConcentricArcs(scale, size, paint)
    restore()
}

class MultiConcentricArcView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class MCANode(var i : Int, val state : State = State()) {

        private var next : MCANode? = null
        private var prev : MCANode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodeColors.size - 1) {
                next = MCANode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawMCANode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MCANode {
            var curr : MCANode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}