package com.bartex.maplesson1

import androidx.lifecycle.LiveData
import com.bartex.maplesson1.model.MarkerData
import com.bartex.maplesson1.model.room.Database
import com.bartex.maplesson1.model.room.RoomMarkerData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RoomCash(val db:Database):IRoomCash {
    override fun loadData(): LiveData<List<RoomMarkerData>> {
        return db.markerDao.getAllMarkersLive()
    }

    override fun addMarkerInRoom(markerData: MarkerData) : Single<Boolean> =
        Single.fromCallable {
            val roomMarkerData = RoomMarkerData(id = markerData.id, latitude = markerData.latitude,
                longitude = markerData.longitude, title = markerData.title,
                snippet = markerData.snippet, isMyPlace = markerData.isMyPlace )
            db.markerDao.insert( roomMarkerData)
            val result:String =  db.markerDao.getMarkerDyId(roomMarkerData.id).id
            result.isNotEmpty()  //length > 0
        }.subscribeOn(Schedulers.io())

    override fun updateMarkerInRoom(markerData: MarkerData): Single<Boolean> =
        Single.fromCallable {
            val roomMarkerData = RoomMarkerData(id = markerData.id, latitude = markerData.latitude,
                longitude = markerData.longitude, title = markerData.title,
                snippet = markerData.snippet, isMyPlace = markerData.isMyPlace )
            db.markerDao.insert( roomMarkerData)
            val result:String =  db.markerDao.getMarkerDyId(roomMarkerData.id).id
            result.isNotEmpty()  //length > 0
        }.subscribeOn(Schedulers.io())

}