package com.bartex.maplesson1.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.maplesson1.R
import com.bartex.maplesson1.model.MarkerData
import com.bartex.maplesson1.view.adapters.MapRecyclerAdapter
import com.bartex.maplesson1.viewmodels.MarkersViewModel

class MarkersFragment:Fragment() {

    companion object{
        const val TAG = "33333"
    }
    lateinit var viewModelMarkers: MarkersViewModel
    private var adapter: MapRecyclerAdapter? = null
    private var listOfMarkers = listOf<MarkerData>()
    lateinit var navController: NavController
    private lateinit var rvMap: RecyclerView
    private lateinit  var emptyViewMarkers: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_of_marcers, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModelMarkers = ViewModelProvider(this).get(MarkersViewModel::class.java)
        rvMap = view.findViewById(R.id.rv_map)
        emptyViewMarkers = view.findViewById(R.id.empty_view_markers)

        viewModelMarkers.loadData().observe(viewLifecycleOwner, Observer { list->
            Log.d(TAG, "MapFragment onViewCreated вкладка со списком " )
           listOfMarkers = list.map { room->
                MarkerData(
                    id =room.id, latitude = room.latitude, longitude = room.longitude,
                    title = room.title, snippet = room.snippet
                )
            }
            renderData(listOfMarkers)
        })
        initAdapter()
    }

    private fun renderData(listOfMarkers: List<MarkerData>) {

        if(listOfMarkers.isEmpty()){
            rvMap.visibility = View.GONE
            emptyViewMarkers.visibility = View.VISIBLE
            emptyViewMarkers.text = getString(R.string.noData)
        }else{
            rvMap.visibility = View.VISIBLE
            emptyViewMarkers.visibility = View.GONE
            adapter?.listOfMarkers = listOfMarkers
        }
    }


    private fun  initAdapter(){
        rvMap.layoutManager = LinearLayoutManager(requireActivity())
        adapter =MapRecyclerAdapter()
        rvMap.adapter = adapter
    }

}