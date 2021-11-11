package com.bartex.maplesson1.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/*будем использовать отдельный класс RoomMarkerData для работы с базой, чтобы не
 вносить изменений в существующие сущности во избежание создания зависимости логики от Room
 RoomMarkerData будет представлять класс MakerData*/

@Entity
class RoomMarkerData (
        @PrimaryKey val id: String = "-1",
        val latitude:Double = 0.0,
        val longitude:Double = 0.0,
        val title:String = "",
        val snippet:String = "",
        val isMyPlace :String = "0"
)