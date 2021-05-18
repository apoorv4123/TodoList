package com.example.todolist.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todolist.R
import kotlinx.android.synthetic.main.activity_spinner.*
import kotlinx.android.synthetic.main.activity_spinner.view.*

const val KEY_1 = "Category"

class SpinnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)

        btnSaveCategory.setOnClickListener {
            val i = Intent(this, TaskActivity::class.java)
            i.putExtra(KEY_1, categoryInplay.etCategory.text.toString())
            startActivity(i)
            finish()
        }

    }
}