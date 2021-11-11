package com.bartex.maplesson1.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.maplesson1.IRoomCash
import com.bartex.maplesson1.RoomCash
import com.bartex.maplesson1.model.MarkerData
import com.bartex.maplesson1.model.room.Database
import com.bartex.maplesson1.model.room.RoomMarkerData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single

class MarkersViewModel(
    private val roomCash: IRoomCash = RoomCash(Database.getInstance())
): ViewModel() {

    //метод для получения списка маркеров
    fun loadData(): LiveData<List<RoomMarkerData>> {
        return  roomCash.loadData()
    }

    fun addMarkerInRoom(markerData:MarkerData) {
        roomCash.addMarkerInRoom(markerData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idNotEmpty->
                if (idNotEmpty) {
                    Log.d(TAG, "Записано: ")
                }else{
                    Log.d(TAG, "Не записано")
                }
            }, {error->
                Log.d(TAG, "${error.message}")
            })
    }

    fun updateMarkerInRoom(markerData:MarkerData){
        roomCash.updateMarkerInRoom(markerData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ idNotEmpty->
                if (idNotEmpty) {
                    Log.d(TAG, "Обновлено: ")
                }else{
                    Log.d(TAG, "Не обновлено")
                }
            }, {error->
                Log.d(TAG, "${error.message}")
            })
    }

    companion object{
        const val TAG = "33333"
    }
}