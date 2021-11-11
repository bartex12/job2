package com.bartex.maplesson1.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.bartex.maplesson1.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // сначала получаем разрешения на определение местоположения
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100)
        }
        setContentView(R.layout.activity_main)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_maps)
        //поддержка экшенбара
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        //текстовое поле в тулбаре
        with(toolbar.findViewById<TextView>(R.id.maps_title)){
            textSize = 16f
            setTextColor(Color.WHITE)
            text = context.getString(R.string.app_name)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
         permissions: Array<String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            val permissionsGranted =
                    (grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (permissionsGranted) {
                recreate() //если разрешения получены- перезагрузка
            }
        }
    }
}