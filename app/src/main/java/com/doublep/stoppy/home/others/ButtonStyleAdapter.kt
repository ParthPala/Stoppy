package com.doublep.stoppy.home.others

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.doublep.stoppy.R
import com.doublep.stoppy.data.ButtonStyleItem

class ButtonStyleAdapter(
    private val context: Context,
    private val styles: List<ButtonStyleItem>
) : BaseAdapter() {

    override fun getCount(): Int = styles.size

    override fun getItem(position: Int): Any = styles[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_button_style, parent, false)
        val style = styles[position]

        val preview = view.findViewById<View>(R.id.preview)
        val title = view.findViewById<TextView>(R.id.title)

        preview.background = ContextCompat.getDrawable(context, style.previewDrawableId)
        title.text = style.name

        return view
    }
}
