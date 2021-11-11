package com.bartex.maplesson1.model.room

import androidx.lifecycle.LiveData
import androidx.room.*

/*
*  стандартные CRUD разных вариаций для создания, чтения, обновления и удаления данных, а также
* возможность поискать пользователя по логину. С помощью встроенного шаблонизатора содержимое
*  аргумента login функции findByLogin подставится вместо :login в запрос. В функциях insert
* с помощью аргумента аннотации onConflict мы указываем, что при возникновении конфликта
* по первичному ключу необходимо заменить старое значение новым*/
@Dao
interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(roomMarkerData: RoomMarkerData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg roomMarkerData: RoomMarkerData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(roomMarkerData:List<RoomMarkerData>)

    @Delete
    fun delete(roomMarkerData: RoomMarkerData)

    @Delete
    fun delete(vararg roomMarkerData: RoomMarkerData)

    @Delete
    fun delete(roomMarkerData:List<RoomMarkerData>)

    @Update
    fun update(roomMarkerData: RoomMarkerData)
    @Update
    fun update(vararg roomMarkerData: RoomMarkerData)
    @Update
    fun update(roomMarkerData:List<RoomMarkerData>)

    @Query("SELECT * FROM RoomMarkerData")
    fun getAllMarkers():List<RoomMarkerData>

    @Query("SELECT * FROM RoomMarkerData WHERE id = :id")
    fun getMarkerDyId(id:String):RoomMarkerData

    @Query("SELECT*FROM RoomMarkerData ")
    fun getAllMarkersLive(): LiveData<List<RoomMarkerData>>
}