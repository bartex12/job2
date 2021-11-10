package com.bartex.maplesson1

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.gms.maps.model.LatLng

class MarkerDialog : AppCompatDialogFragment(){

    private var  latitude = 0.0
    private var  longitude = 0.0

    companion object{
        const val LAT = "LAT"
        const val LON = "LON"

      fun   newInstance(latLon: LatLng): MarkerDialog{
          val lat = latLon.latitude
          val lon = latLon.longitude
          val dialogFragment = MarkerDialog()
          val bundle = Bundle()
          bundle.putDouble(LAT, lat)
          bundle.putDouble(LON, lon)
          dialogFragment.arguments = bundle
          return dialogFragment
      }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?. let{
            latitude = it.getDouble(LAT)
            longitude = it.getDouble(LON)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //val view = requireActivity().layoutInflater.inflate(R.layout.dialog_marker, null)
        val builder = AlertDialog.Builder(requireActivity())
       // builder.setView(view)
        builder.setTitle("Установить маркер?")
        builder.setPositiveButton("Да") {dialog, _ ->
            Toast.makeText(requireActivity(), "Установить маркер", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), MarkerActivity::class.java)
            intent.putExtra(LAT, latitude)
            intent.putExtra(LON, longitude)
            startActivity(intent)
        }
        builder.setNegativeButton("Нет"){ dialog, _ -> dialog.dismiss()}
        return builder.create()
    }
}