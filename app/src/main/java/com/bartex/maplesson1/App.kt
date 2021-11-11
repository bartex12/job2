package com.bartex.maplesson1

import android.app.Application
import com.bartex.maplesson1.model.room.Database

class App : Application() {

    companion object{
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Database.create(this)
    }
}