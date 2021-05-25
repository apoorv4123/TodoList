package com.example.todolist.ui.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import com.example.todolist.data.database.AppDatabase
import com.example.todolist.data.models.TodoModel
import com.example.todolist.ui.ReminderBroadcast
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "todo.db"

class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar: Calendar // This object holds the value of date, month & year

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener // This interface will be invoked when
    // you show the dialog. When you click on any value in the dialog, this listener
    // will be called and the values inside this listener will be updated

    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener// For timePickerDialog

    var finalDate =
        0L// These variables will help in saving data to the db. They need to be updated too
    var finalTime = 0L

    private val labels = arrayListOf("Personal", "Business", "Insurance", "Banking", "Shopping")

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
        setContentView(R.layout.activity_task)

        // For opening up the dialog, set the click of the date edit text
        // We have to handle multiple clicks, so we'll be implementing the interface of View.OnClickListener
        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        // set up notification channel
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    "first",
                    "default",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        setUpSpinner()

        // Set up back button in toolbar
        toolbarAddTask.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbarAddTask.setNavigationOnClickListener {
            finish()
        }
    }

    lateinit var adapter: ArrayAdapter<String>

    private fun setUpSpinner() {
        adapter =
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

    private var category = ""
    private var title = ""
    private var description = ""

    private fun saveTodo() {
        category = spinnerCategory.selectedItem.toString()
        title = titleInpLay.editText?.text.toString()
        description = taskInpLay.editText?.text.toString()

        if (category == "" || title == "" || description == "" || finalDate == 0L || finalTime == 0L) {
            Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
        }

        if (category != "" && title != "" && description != "" && finalDate != 0L && finalTime != 0L) {
            GlobalScope.launch(Dispatchers.Main) {
                val id = withContext(Dispatchers.IO) {
                    return@withContext db.todoDao()
                        .insertTask(TodoModel(title, description, category, finalDate, finalTime))
                }

                scheduleNotification()
                finish()// to finish the activity after clicking  on SAVE TASK button
            }
        }
    }

    private fun addCategoryToSpinner() {
//        val adapter =
//            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)
//        val i = Intent(this, SpinnerActivity::class.java)
//        startActivity(i)
//        val category = intent.getStringExtra(KEY_1)
//        labels.add(category!!)
//        labels.sort()
//        spinnerCategory.adapter = adapter
        Toast.makeText(this, "Add new Category", Toast.LENGTH_SHORT).show()
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
        finalTime = myCalendar.time.time
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
        finalDate = myCalendar.time.time
        dateEdt.setText(sdf.format(myCalendar.time))

        timeInptLay.visibility =
            View.VISIBLE// We saw that only after setting date, we'll be able to set time
    }

    private fun scheduleNotification() {
        val intent = Intent(this, ReminderBroadcast::class.java)

        val pi = PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val reminderNotification = NotificationCompat.Builder(this, "first")
            .setContentTitle(category)
            .setContentText(description)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

//        nm.notify(1, reminderNotification)

        Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show()
    }

    fun addNewCategory(view: View) {
        val editText = EditText(this)
        editText.hint = "Please enter a new category here"
        val newCategoryAlertdialog = AlertDialog.Builder(this)
            .setTitle("New Category:")
            .setView(editText)
            .setPositiveButton("ADD") { dialogInterface, which ->
                if (editText.text.toString() != "") {
                    val category = editText.text.toString()
                    labels.add(category)
                    labels.sort()
                    adapter.notifyDataSetChanged()
                    spinnerCategory.adapter = adapter
                    Toast.makeText(this, "Category Added!", Toast.LENGTH_SHORT).show()
                }
            }
            .setCancelable(true)
            .create()
        newCategoryAlertdialog.show()
    }
}