package com.bartex.maplesson1.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bartex.maplesson1.model.Constants
import com.bartex.maplesson1.R
import com.bartex.maplesson1.model.MarkerData
import com.bartex.maplesson1.viewmodels.MarkersViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapFragment:Fragment(), OnMapReadyCallback {

    companion object{
        const val DIALOG_FRAGMENT = "DIALOG_FRAGMENT"
        const val TAG = "33333"
    }
    private lateinit var googleMap: GoogleMap
    private val mLocManager by lazy{
        requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private var isHere = false
    private var lat = 0.0 //широта
    private var lon = 0.0 // долгота
    var target: LatLng = LatLng(0.0, 0.0) //координаты целевой точки
    private lateinit var navController: NavController
    private var listOfMarkers = listOf<MarkerData>()
    lateinit var viewModelMarkers: MarkersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?. let {
            lat = it.getDouble(Constants.LAT)
            lon = it.getDouble(Constants.LON)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModelMarkers = ViewModelProvider(this).get(MarkersViewModel::class.java)

        // грузим Google Map object
        val mapFragment =  childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        googleMap.isMyLocationEnabled = true
        //не показывать кнопку моё место - будет своя
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        // кнопка управления масштабом
        googleMap.uiSettings.isZoomControlsEnabled = true
        //выбираем вид со спутника
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        viewModelMarkers.loadData().observe(viewLifecycleOwner, Observer { list->
            Log.d(MarkersFragment.TAG, "MapFragment onViewCreated вкладка со списком " )
            listOfMarkers = list.map { room->
                MarkerData(
                    id =room.id, latitude = room.latitude, longitude = room.longitude,
                    title = room.title, snippet = room.snippet
                )
            }
            renderData(listOfMarkers)
        })

        arguments?. let {
            //получаем место из аргументов
            lat = it.getDouble(Constants.LAT)
            lon = it.getDouble(Constants.LON)
            target = LatLng(lat, lon)
            moveCamera(target, 5f)
        }
            googleMap.setOnMapClickListener {
                //запоминаем
                lat = it.latitude
                lon = it.longitude
               val bundle = bundleOf(Constants.LAT to lat, Constants.LON to lon) //так проще
                  navController.navigate(R.id.markerDialog, bundle)
            }
    }

    //если список пуст - определяем местоположение, ставим точку на карте, пишем в базу,перемещаемся в точку
    //если список не пуст -
    private fun renderData(listOfMarkers: List<MarkerData>) {
        //получаем место расположения устройства
        target = getMyLatLon()

        if (listOfMarkers.isEmpty()){
            //запоминаем для передачи в диалог
           // lat = target.latitude
            //lon = target.longitude
            //ставим целевую точку на карте
            val marker =  googleMap.addMarker(
                MarkerOptions()
                    .position(target) //координаты
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) //цвет
                    .snippet("Моё местоположение")
                    .title("Я здесь") //заголовок
            )
            //пишем в базу
            viewModelMarkers.addMarkerInRoom(MarkerData(id = UUID.randomUUID().toString(),
                latitude = target.latitude, longitude = target.longitude,
                title = "Я здесь", snippet ="Моё местоположение", isMyPlace = "1" ))

            //перемещаемся в точку расположения устройства с zoom = 5
            moveCamera(target, 5f)

            Log.d(TAG, "onMapReady:  ${marker?.position?.latitude} " +
                    " ${marker?.position?.longitude} ${marker?.title} ${marker?.snippet}")
        }else{
            val markers = arrayOfNulls<MarkerOptions>(listOfMarkers.size)
            for (i in listOfMarkers.indices) {
                markers[i] = MarkerOptions()
                    .position(LatLng(listOfMarkers[i].latitude, listOfMarkers[i].longitude ))
                    .snippet(listOfMarkers[i].snippet)
                    .title(listOfMarkers[i].title)
                markers[i]?.let {
                    googleMap.addMarker(it)
                }
            }
            target = LatLng(listOfMarkers[0].latitude, listOfMarkers[0].longitude)
            moveCamera(target, 5f)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_map_mode_satellite -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }
            R.id.menu_map_mode_terrain -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
            R.id.menu_map_mode_default -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.menu_map_location -> {
//                viewModelMarkers.updateMarkerInRoom(MarkerData(id = UUID.randomUUID().toString(),
//                    latitude = target.latitude, longitude = target.longitude,
//                    title = "Я здесь", snippet ="Моё местоположение", isMyPlace = "1" ))
                moveCameraToMyLocation()
            }
            R.id.menu_map_list ->{
                navController.navigate(R.id.listOfMarkersFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMyLatLon(): LatLng {
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