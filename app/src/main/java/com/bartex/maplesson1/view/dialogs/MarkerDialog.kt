package com.bartex.maplesson1.view.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.bartex.maplesson1.model.Constants
import com.bartex.maplesson1.R


class MarkerDialog : AppCompatDialogFragment(){

    private var  latitude = 0.0
    private var  longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?. let{
            latitude = it.getDouble(Constants.LAT)
            longitude = it.getDouble(Constants.LON)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Установить маркер в этом месте?")
        builder.setPositiveButton("Да") { _, _ ->
            val navController = Navigation.findNavController(requireParentFragment().requireView())
            val bundle = bundleOf(Constants.LAT to latitude, Constants.LON to longitude  )
            navController.navigate(R.id.newMarkerFragment, bundle)
        }
        builder.setNegativeButton("Нет"){ dialog, _ -> dialog.dismiss()}
        return builder.create()
    }
}