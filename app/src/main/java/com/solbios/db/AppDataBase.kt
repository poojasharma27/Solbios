package com.solbios.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solbios.db.entities.SearchData
import com.solbios.db.entities.SearchEntityRoot

@Database(entities = arrayOf(SearchData::class),version = 2)
abstract class  AppDataBase : RoomDatabase() {

    abstract fun searchDetailsDao() : SearchDetailsDao


    companion object {

        @Volatile private var instance: AppDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context?)= instance ?: synchronized(LOCK){
            instance ?: context?.let {
                buildDatabase(it).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDataBase::class.java, "todo-list.db").allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}