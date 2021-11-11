package com.bartex.maplesson1.model

//UUID - неизменный универсальный уникальный идентификатор (UUID).
// UUID представляет собой 128-битное значение.
data class MarkerData(
        val id: String = "-1",
        val latitude:Double = 0.0,
        val longitude:Double = 0.0,
        val title:String = "Новая точка",
        val snippet:String = "Без описания",
        val isMyPlace :String = "0"
)
