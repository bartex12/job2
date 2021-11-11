package com.bartex.maplesson1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bartex.maplesson1.R
import com.bartex.maplesson1.model.MarkerData

class MapRecyclerAdapter(
    private val onMarkerClickListener:OnMarkerClickListener
): RecyclerView.Adapter<MapRecyclerAdapter.ViewHolder>() {

    //так сделано чтобы передавать список в адаптер без конструктора
    // - через присвоение полю значения
    var listOfMarkers:List<MarkerData> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    interface  OnMarkerClickListener{
        fun onMarkerClick(markerData:MarkerData)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MapRecyclerAdapter.ViewHolder {
        val view:View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_of_markers,parent, false )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind(listOfMarkers[position])
    }

    override fun getItemCount(): Int {
       return listOfMarkers.size
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val inputTitle: TextView = view.findViewById(R.id.tv_title)
        private val inputLat: TextView = view.findViewById(R.id.tv_latitude)
        private val inputLon: TextView = view.findViewById(R.id.tv_longitude)
        private val inputSnippet: TextView = view.findViewById(R.id.tv_snippet)
        private val  markerCard: LinearLayout = view.findViewById(R.id. marker_card)


        fun bind(marker:MarkerData){

            inputTitle.text = marker.title
            inputLat.text = marker.latitude.toString()
            inputLon.text = marker.longitude.toString()
            inputSnippet.text = marker.snippet

            markerCard.setOnClickListener {
                onMarkerClickListener.onMarkerClick(marker)
            }
        }
    }
}