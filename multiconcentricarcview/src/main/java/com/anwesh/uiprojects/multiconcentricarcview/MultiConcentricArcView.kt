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
