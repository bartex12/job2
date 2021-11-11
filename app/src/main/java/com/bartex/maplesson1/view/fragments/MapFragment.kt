package com.bartex.maplesson1.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
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

    private lateinit var googleMap: GoogleMap
    private val mLocManager by lazy{
        requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private var isHere = false
    private var target: LatLng = LatLng(0.0, 0.0) //координаты целевой точки
    private lateinit var navController: NavController
    private var listOfMarkers = listOf<MarkerData>()
    lateinit var viewModelMarkers: MarkersViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initGoogleMap()
        setHasOptionsMenu(true)
    }

    private fun initViews(view: View) {
        navController = Navigation.findNavController(view)
        viewModelMarkers = ViewModelProvider(this).get(MarkersViewModel::class.java)
    }

    // грузим Google Map object
    private fun initGoogleMap() {
        val mapFragment =  childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //колбэк когда карта готова
    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100)
        }else{
            //эта строка требовала разрешений
            googleMap.isMyLocationEnabled = true

            initMapPArams()
            getListOfMarkers()
            //getArgumentsWithData()
            initOnMapListener()
        }
    }

    private fun initMapPArams() {
        //не показывать кнопку моё место - будет своя
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        // кнопка управления масштабом
        googleMap.uiSettings.isZoomControlsEnabled = true
        //выбираем обычный вид  карты
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    private fun getListOfMarkers() {
        viewModelMarkers.loadData().observe(viewLifecycleOwner, Observer { list->
            listOfMarkers = list.map { room->
                MarkerData(
                    id =room.id, latitude = room.latitude, longitude = room.longitude,
                    title = room.title, snippet = room.snippet
                )
            }
            renderData(listOfMarkers)
        })
    }

    private fun initOnMapListener() {
        googleMap.setOnMapClickListener {
            val bundle = bundleOf(Constants.LAT to it.latitude, Constants.LON to it.longitude) //так проще
            navController.navigate(R.id.markerDialog, bundle)
        }
    }

    private fun renderData(listOfMarkers: List<MarkerData>) {
        //получаем место расположения устройства
        target = getMyLatLon()
        //если список пуст - определяем местоположение, ставим точку на карте,
        // пишем в базу,перемещаемся   в точку
        if (listOfMarkers.isEmpty()){
            //ставим целевую точку на карте
            googleMap.addMarker(
                MarkerOptions()
                    .position(target) //координаты
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) //цвет
                    .snippet(getString(R.string.menu_map_description))
                    .title(getString(R.string.menu_map_location)) //заголовок
            )
            //пишем в базу
            viewModelMarkers.addMarkerInRoom(MarkerData(id = UUID.randomUUID().toString(),
                latitude = target.latitude, longitude = target.longitude,
                title = getString(R.string.menu_map_location),
                snippet =getString(R.string.menu_map_description), isMyPlace = "1" ))
            //перемещаемся в точку расположения устройства с zoom = 5
            moveCamera(target, 5f)

            //если список не пуст -ставим на карте все маркеры и идём в точку первого маркера
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
            if(arguments == null){
                target = LatLng(listOfMarkers[0].latitude, listOfMarkers[0].longitude)
                moveCamera(target, 5f)
            }else{
                //получаем место из аргументов
                target = LatLng(
                    requireArguments().getDouble(Constants.LAT),
                    requireArguments().getDouble(Constants.LON))
                moveCamera(target, 5f)
            }
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
                moveCameraToMyLocation()
            }
            R.id.menu_map_list ->{
                navController.popBackStack()
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