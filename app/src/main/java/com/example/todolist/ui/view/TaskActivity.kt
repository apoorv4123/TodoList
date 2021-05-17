package com.example.todolist.ui.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.room.Room
import com.example.todolist.R
import com.example.todolist.data.database.AppDatabase
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todo.*
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "todo.db"

class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar: Calendar // This object holds the value of date, month & year

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener // This interface will be invoked when
    // you show the dialog. When you click on any value in the dialog, this listener
    // will be called and the values inside this listener will be updated

    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener// For timePickerDialog

    private val labels = arrayListOf("Personal", "Business", "Insurance", "Banking", "Shopping")

    val db by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // For opening up the dialog, set the click of the date edit text
        // We have to handle multiple clicks, so we'll be implementing the interface of View.OnClickListener
        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)

        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)
        labels.sort()
        spinnerCategory.adapter = adapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dateEdt -> {
                // setup myCalender & dateSetListener and open up the dialog. And, When you open up the dialog,
                // make sure that you're setting the default date as the minimum date on which a task can
                // be scheduled, i.e. current date. You cannot schedule a task on a previous date.
                setListener()
            }

            R.id.timeEdt -> {
                // On clicking timeEdt, open the timePickerDialog
                setTimeListener()
            }

            R.id.imgAddCategory -> {
                // on clicking this, we can add custom category to spinner
                addCategoryToSpinner()
            }

            R.id.saveBtn -> {
                saveTodo()
            }

        }
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()

//        if(category != null && title != null && description != null){
//            Toast.makeText(this, "Fill the vacant values", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun addCategoryToSpinner() {
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)
        val i = Intent(this, SpinnerActivity::class.java)
        startActivity(i)
        val category = intent.getStringExtra(KEY_1)
        labels.add(category!!)
        labels.sort()
        spinnerCategory.adapter = adapter
    }

    private fun setTimeListener() {
        // set up calender object
        myCalendar = Calendar.getInstance()

        // setup listener
        timeSetListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->// These are the parameters which are selected from the dialog
                // Whenever above parameters are selected, update the calender value
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTime()// Whenever you update any value in TimePickerDialog(set any value), you have to update the input layout as well. This is invoked only when you select any value
            }

        // Open up TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            timeSetListener, // The listener which will be invoked when dialog performs any action(operation)
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),
            false // If you want clock in 24 hours system then true, if in 12 hour system then false
        )

        // Set present time as minimum time
        timePickerDialog.show()
    }

    private fun updateTime() {
        // 23:13 pm
        val myformat = "h:mm a" //

        val sdf = SimpleDateFormat(myformat)
        timeEdt.setText(sdf.format(myCalendar.time))
    }

    private fun setListener() {
        // set up calender object
        myCalendar = Calendar.getInstance()

        // setup listener
        dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->// These are the parameters which are selected from the dialog
                // Whenever above parameters are selected, update the calender value
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()// Whenever you update any value in datePickerDialog(set any value), you have to update the input layout as well. Invoked only when you select any value
            }

        // Open up datePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener, // The listener which will be invoked when dialog performs any action(operation)
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set present date as minimum date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        // Mon, 17 May 2021
        val myformat = "EEE, d MMM yyyy" // "EEE, d MM yyyy" -> Mon, 5 05 2021

        val sdf = SimpleDateFormat(myformat)
        dateEdt.setText(sdf.format(myCalendar.time))

        timeInptLay.visibility =
            View.VISIBLE// We saw that only after setting date, we'll be able to set time
    }

}