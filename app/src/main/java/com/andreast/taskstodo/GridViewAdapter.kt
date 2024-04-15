package com.andreast.taskstodo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

internal class GridViewAdapter(
    private val gridItems: List<GridViewItem>,
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var gridItem: TextView

    override fun getCount(): Int {
        return gridItems.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertViewCopy = convertView

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertViewCopy == null) {
            convertViewCopy = layoutInflater!!.inflate(R.layout.todo_gridview_item, null)
        }

        gridItem = convertViewCopy!!.findViewById(R.id.idGridViewItem)
        gridItem.setText(gridItems.get(position).name)

        return convertViewCopy
    }
}
