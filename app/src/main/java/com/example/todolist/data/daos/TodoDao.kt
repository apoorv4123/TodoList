package com.example.todolist.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.data.models.TodoModel

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTask(todoModel: TodoModel): Long

    // get the list of unfinished tasks
    @Query("Select * from TodoModel where isFinished != 0")
    fun getTask(): LiveData<List<TodoModel>>

    // finish task on right swipe
    @Query("Update TodoModel Set isFinished = 1 where id =:uid")
    fun finishTask(uid: Long)

    // delete task on left swipe
    @Query("Delete from TodoModel where id =:uid")
    fun deleteTask(uid: Long)

}