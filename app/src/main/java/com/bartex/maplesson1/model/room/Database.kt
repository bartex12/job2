package com.bartex.maplesson1.model.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


/*
* Перечисляем сущности в аннотации, наследуемся от RoomDatabase и перечисляем DAO в виде
*  абстрактных полей.
*/
@androidx.room.Database(entities = [RoomMarkerData::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract val markerDao: MarkerDao

    companion object{
        private const val DB_NAME = "databaseMarkers.db"
        private var instance:Database? = null

        fun getInstance() = instance ?:throw  RuntimeException(
            " Database  has  not  been  created.  Please  call  create(context)"
        )

        fun create(context:Context?){
            if (instance == null){
                instance = Room.databaseBuilder(context!!, Database::class.java, DB_NAME ).build()
            }
        }
    }
}