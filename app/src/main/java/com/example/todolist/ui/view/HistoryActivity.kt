package com.example.todolist.ui.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todolist.R
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolBarSettings

class HistoryActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        toolBarHistory.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolBarHistory.setNavigationIconTint(R.color.darkBlue)
        toolBarHistory.setNavigationOnClickListener {
            finish()
        }
    }
}