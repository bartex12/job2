package com.bartex.maplesson1.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bartex.maplesson1.model.Constants
import com.bartex.maplesson1.R
import com.bartex.maplesson1.model.MarkerData
import com.bartex.maplesson1.viewmodels.MarkersViewModel
import java.util.*

class NewMarkerFragment:Fragment() {

    private lateinit var inputLat:EditText
    private lateinit var inpuLon:EditText
    private lateinit var inpuTitle:EditText
    private lateinit var inpuSnippet:EditText
    private lateinit var btnSave:Button
    private var  latitude = 0.0
    private var  longitude = 0.0
    lateinit var markersViewModel: MarkersViewModel
    lateinit var navController:NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_marker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        initViews(view)
        getArgumentsWithData()
        initButtonSaveListener()
    }

    private fun initViews(view: View) {
        inputLat = view.findViewById(R.id.editTextLat)
        inpuLon= view.findViewById(R.id.editTextLon)
        inpuTitle= view.findViewById(R.id.editTextTitle)
        inpuSnippet= view.findViewById(R.id.editTextSnippet)
        btnSave = view.findViewById(R.id.button2)
        markersViewModel = ViewModelProvider(this).get(MarkersViewModel::class.java)
    }

    private fun getArgumentsWithData() {
        arguments?. let{
            latitude = it.getDouble(Constants.LAT)
            longitude = it.getDouble(Constants.LON)
        }
        inputLat.setText(latitude.toString())
        inpuLon.setText(longitude.toString())
    }

    private fun initButtonSaveListener() {
        btnSave.setOnClickListener {
            val lat = inputLat.text.toString().toDouble()
            val lon  = inpuLon.text.toString().toDouble()
            val tit  = inpuTitle.text.toString()
            val snip  = inpuSnippet.text.toString()

            markersViewModel.addMarkerInRoom(MarkerData(id = UUID.randomUUID().toString(),
                latitude = lat, longitude = lon,title = tit, snippet = snip ))
            navController.popBackStack()
            navController.navigate(R.id.mapFragment)
        }
    }
}