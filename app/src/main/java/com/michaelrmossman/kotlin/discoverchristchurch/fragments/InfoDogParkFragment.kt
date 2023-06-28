package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoDogParkBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3

// Called from DogParksActivity. Shows minimal info re individual dog environments
class InfoDogParkFragment: InfoBaseFragment<FragmentInfoDogParkBinding>(R.layout.fragment_info_dog_park) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            checkBox = infoFavourite
            discoverViewModel = viewModel
            infoFragment = this@InfoDogParkFragment
        }

        // This forces the BottomSheet to appear at max height, since it's so small
        expandCollapseDialog(false)
    }

    fun showStreetView() {
        viewModel.setShowStreetView(true)
    }

    fun toggleFavourite(checkBox: CheckBox, itemId: Long) {
        toggleFave( // Called from xml
            checkBox.isChecked, itemId,
            ITEM_VIEW_TYPE_ITEM_3,true
        )
    }
}
