package com.example.todolist.ui.view

import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.database.AppDatabase
import com.example.todolist.data.models.TodoModel
import com.example.todolist.ui.adapter.TodoAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val list = arrayListOf<TodoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy {
//        Room.databaseBuilder(
//            this,
//            AppDatabase::class.java,
//            DB_NAME
//        ).build()
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)

        rvTodos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        initSwipe()

        db.todoDao().getTask().observe(this, {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            } else {
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })

    }

    fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,// 0 because we want to drag(swipe)
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT// There are UP & DOWN too
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false// false because we don't want to move. We just want to swipe

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Fetch the position in recyclerView where swipe action is performed
                val position = viewHolder.adapterPosition

                // Check the direction in which swipe action is being performed
                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.getItemId(position))
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,//Canvas is used to draw something on the view
                recyclerView: RecyclerView, // the recyclerView on which we've attached this swipe callback
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,// direction
                dY: Float,// direction
                actionState: Int, // current action that is being performed on swipe
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView =
                        viewHolder.itemView//get the view(todo_item) of the holder that is being swiped
                    // Now, we'll draw in this itemView
                    // Have a look in mipmap for the icons of check & delete

                    val paint = Paint()
                    val icon: Bitmap

                    // Check whether swipe direction is positive or negative
                    if (dX > 0) { // Positive, Rightwards swipe
                        // draw the green color and display the white check image
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_check)
                        paint.color = Color.parseColor("#388E3C")

                        // draw rectangle in the canvas
                        canvas.drawRect(
                            itemView.left.toFloat(),// left parameter
                            itemView.top.toFloat(),// top parameter
                            itemView.left.toFloat() + dX,// right parameter (left parameter+ amount swiped till now)
                            itemView.bottom.toFloat(), // bottom parameter
                            paint
                        )

                        // set the icon
                        canvas.drawBitmap(
                            icon,// icon object, now give the values for where i want to put the icon
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )

                    } else {
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete)

                        paint.color = Color.parseColor("#D32F2F")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX

                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rvTodos)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Code for Search view will be inside this function rather than onClick()
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView// convert the item to searchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displayTodo()
                return true// return true for this functionality to work
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayTodo()
                return true// return true for this functionality to work
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // This function will be called when you submit something in searchView
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // This function will be called as soon as you write something in searchView
                if (!newText.isNullOrEmpty()) {
                    displayTodo(newText)
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun displayTodo(newText: String = "") {
        // Query to db
        db.todoDao().getTask().observe(this, Observer {
            if (it.isNotEmpty()) {
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(newText, true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    // This function is similar to onClickListener. This function will be invoked when 3 dots in toolBar
    // is clicked. When any item is clicked, we get its item id
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // We get this function by setting onClick attribute in FloatingActionButton
    fun openNewTask(view: View) {
        startActivity(Intent(this, TaskActivity::class.java))
    }
}