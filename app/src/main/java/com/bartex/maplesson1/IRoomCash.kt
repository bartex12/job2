package com.bartex.maplesson1

import androidx.lifecycle.LiveData
import com.bartex.maplesson1.model.MarkerData
import com.bartex.maplesson1.model.room.RoomMarkerData
import io.reactivex.rxjava3.core.Single

interface IRoomCash {
    fun loadData(): LiveData<List<RoomMarkerData>>
    fun addMarkerInRoom(markerData: MarkerData): Single<Boolean>
    fun updateMarkerInRoom(markerData: MarkerData): Single<Boolean>
}