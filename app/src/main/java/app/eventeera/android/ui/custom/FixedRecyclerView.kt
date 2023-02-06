package app.eventeera.android.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import app.eventeera.android.util.toPx

class FixedRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val h = MeasureSpec.makeMeasureSpec((150).toPx, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, h)
    }
}