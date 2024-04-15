package com.andreast.taskstodo

import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var gridView: GridView
    private lateinit var gridViewItems: List<GridViewItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_gridview)

        gridView = findViewById(R.id.idGridView)
        gridViewItems = ArrayList()

        for (i in 1..10) {
            gridViewItems = gridViewItems + GridViewItem("Item $i")
        }

        val adapter = GridViewAdapter(gridViewItems, this@MainActivity)
        gridView.adapter = adapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(
                applicationContext, gridViewItems[position].name + " selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}