package com.nobos.moneymap.formating

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewBorder(
    private val borderSize: Float,
    borderColor: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + borderSize / 2
        val right = parent.width - parent.paddingRight - borderSize / 2

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin + borderSize / 2
            val bottom = top + borderSize

            c.drawRect(left, top, right, bottom, paint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, borderSize.toInt())
    }
}
