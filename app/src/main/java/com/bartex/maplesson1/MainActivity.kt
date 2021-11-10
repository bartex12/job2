package com.bartex.maplesson1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback  {

    companion object{
        const val DIALOG_FRAGMENT = "DIALOG_FRAGMENT"
    }
    private lateinit var googleMap: GoogleMap
    private val mLocManager by lazy{
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
//    //так как примитивы
//    private var lat by Delegates.notNull<Double>()
//    private var lon by Delegates.notNull<Double>()
    private var isHere = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        // грузим Google Map object
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        //получаем разрешения на определение местоположения
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(
                    this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ), 100)
        } else {
            //
            googleMap.isMyLocationEnabled = true
            //не показывать кнопку моё место
            //googleMap.uiSettings.isMyLocationButtonEnabled = false
            // кнопка управления масштабом
            googleMap.uiSettings.isZoomControlsEnabled = true
            //выбираем вид со спутника
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
           //получаем место расположения устройства
            val target: LatLng = getMyLatLon()
            //ставим целевую точку на карте
            googleMap.addMarker(MarkerOptions()
                .position(target)
                .title("Я здесь"))
            //перемещаемся в точку расположения устройства с zoom = 5
            moveCamera(target, 5f)

            googleMap.setOnMapClickListener {
                //Toast.makeText(this, "$it", Toast.LENGTH_LONG ).show()
                MarkerDialog.newInstance(it).show(supportFragmentManager, DIALOG_FRAGMENT)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
         permissions: Array<String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            val permissionsGranted =
                    (grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (permissionsGranted) recreate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_map_mode_satellite -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return  true
            }
            R.id.menu_map_mode_terrain ->{
                googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
            R.id.menu_map_mode_default ->{
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.menu_map_location  ->{
                moveCameraToMyLocation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMyLatLon(): LatLng {
        var target: LatLng = LatLng(0.0, 0.0)
        if (googleMap.isMyLocationEnabled) {
            //получаем последнее известное местоположение
            @SuppressLint("MissingPermission")
            val loc = mLocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            loc?.let {
                //создаём объект LatLng для Maps
                target = LatLng(loc.latitude, loc.longitude)
            }
        }
        return target
    }

    //перемещаем камеру в заданную точку с анимацией
    private fun moveCamera(target: LatLng?, zoom: Float) {
        if (target == null || zoom < 1) return
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, zoom))
    }

    //приближение / удаление   zoom = 14f / 5f
    private fun moveCameraToMyLocation(){
        val target: LatLng = getMyLatLon()
        if (isHere){
            moveCamera(target, 5f)
        }else{
            moveCamera(target, 14f)
        }
        isHere = !isHere
    }
}