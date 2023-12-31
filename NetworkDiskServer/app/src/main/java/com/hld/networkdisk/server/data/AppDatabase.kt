package com.hld.networkdisk.server.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PreviewDao.Bean::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun previewDao(): PreviewDao
    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context.applicationContext).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context): AppDatabase {
            val path = context.getExternalFilesDir("database")?.absolutePath?:""
            return Room.databaseBuilder(context, AppDatabase::class.java, "${path}/${DATABASE_NAME}").build()
        }
    }
}