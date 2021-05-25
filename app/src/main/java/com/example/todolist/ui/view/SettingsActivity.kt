package com.example.todolist.ui.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todolist.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolBarSettings.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolBarSettings.setNavigationIconTint(R.color.black)
        toolBarSettings.setNavigationOnClickListener {
            finish()
        }
    }
}