package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.database.*
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentSearchBinding
import com.michaelrmossman.kotlin.discoverchristchurch.views.FlatRadioGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    override fun onResume() {
        super.onResume() // Tested: Required

        viewModel.setTitle(getString(R.string.app_name))
        viewModel.setSubtitle(getString(R.string.search_by))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            searchFragment = this@SearchFragment

            accessibilityTypeAccessible.setOnClickListener {
                accessibilitySearchButton.isEnabled = true
                resetGroups(accessibilityTypeRadioGroup)
            }
            accessibilityTypePram.setOnClickListener {
                accessibilitySearchButton.isEnabled = true
                resetGroups(accessibilityTypeRadioGroup)
            }
            accessibilityTypeEither.setOnClickListener {
                accessibilitySearchButton.isEnabled = true
                resetGroups(accessibilityTypeRadioGroup)
            }
            accessibilitySearchButton.setOnClickListener {
                goResults(
                    R.array.search_by_accessibility_types,
                    ACCESSIBLE_DB,
                    when {
                        accessibilityTypeAccessible.isChecked -> 0
                        accessibilityTypePram.isChecked -> 1
                        accessibilityTypeEither.isChecked -> 2
                        else -> -1
                    },
                    R.string.accessibility_search_by_text
                )
            }

            dogsTypeControlled.setOnClickListener {
                dogsSearchButton.isEnabled = true
                resetGroups(dogsTypeRadioGroup)
            }
            dogsTypeLeash.setOnClickListener {
                dogsSearchButton.isEnabled = true
                resetGroups(dogsTypeRadioGroup)
            }
            dogsTypeEither.setOnClickListener {
                dogsSearchButton.isEnabled = true
                resetGroups(dogsTypeRadioGroup)
            }
            dogsSearchButton.setOnClickListener {
                goResults(
                    R.array.search_by_dogs_types,
                    DOGS_DB,
                    when {
                        dogsTypeControlled.isChecked -> 0
                        dogsTypeLeash.isChecked -> 1
                        dogsTypeEither.isChecked -> 2
                        else -> -1
                    },
                    R.string.dogs_search_by_text
                )
            }

            pubTransportTypeYes.setOnClickListener {
                pubTransportSearchButton.isEnabled = true
                resetGroups(pubTransportTypeRadioGroup)
            }
            pubTransportSearchButton.setOnClickListener {
                goResults(
                    R.array.search_by_common_types,
                    TRANSPORT_DB,
                    0,
                    R.string.pub_transport_search_by_text
                )
            }

            returnJourneyTypeYes.setOnClickListener {
                returnJourneySearchButton.isEnabled = true
                resetGroups(returnJourneyTypeRadioGroup)
            }
            returnJourneyTypeNo.setOnClickListener {
                returnJourneySearchButton.isEnabled = true
                resetGroups(returnJourneyTypeRadioGroup)
            }
            returnJourneySearchButton.setOnClickListener {
                goResults(
                    R.array.search_by_common_types,
                    ROUND_DB,
                    when {
                        returnJourneyTypeYes.isChecked -> 0
                        returnJourneyTypeNo.isChecked -> 1
                        else -> -1
                    },
                    R.string.return_journey_search_by_text
                )
            }

            sharedUseTypeYes.setOnClickListener {
                sharedUseSearchButton.isEnabled = true
                resetGroups(sharedUseTypeRadioGroup)
            }
            sharedUseTypeNo.setOnClickListener {
                sharedUseSearchButton.isEnabled = true
                resetGroups(sharedUseTypeRadioGroup)
            }
            sharedUseSearchButton.setOnClickListener {
                goResults(
                    R.array.search_by_common_types,
                    SHARED_DB,
                    when {
                        sharedUseTypeYes.isChecked -> 0
                        sharedUseTypeNo.isChecked -> 1
                        else -> -1
                    },
                    R.string.shared_use_search_by_text
                )
            }
        }
    }

    private fun goResults(
        @ArrayRes arrayId: Int,
        featureType: String,
        selectedIndex: Int,
        @StringRes stringId: Int
    ) {
        val stringArray = resources.getStringArray(arrayId)
        val searchBy = stringArray[selectedIndex]
        val searchCat = getString(stringId)
        val subtitle = "$searchCat $searchBy"
        viewModel.setCurrentFeature(featureType)
        viewModel.setCurrentSearch(subtitle)
        viewModel.setCurrentSelection(selectedIndex)
        navigateTo(1905618056) // ResultsFragment
    }

    private fun resetGroups(radioGroup: FlatRadioGroup) {
        binding.apply {
            accessibilityTypeRadioGroup.apply {
                if (this.id != radioGroup.id) {
                    this.selectViewProgrammatically(-1)
                    accessibilitySearchButton.isEnabled = false
                }
            }

            dogsTypeRadioGroup.apply {
                if (this.id != radioGroup.id) {
                    this.selectViewProgrammatically(-1)
                    dogsSearchButton.isEnabled = false
                }
            }

            pubTransportTypeRadioGroup.apply {
                if (this.id != radioGroup.id) {
                    this.selectViewProgrammatically(-1)
                    pubTransportSearchButton.isEnabled = false
                }
            }

            returnJourneyTypeRadioGroup.apply {
                if (this.id != radioGroup.id) {
                    this.selectViewProgrammatically(-1)
                    returnJourneySearchButton.isEnabled = false
                }
            }

            sharedUseTypeRadioGroup.apply {
                if (this.id != radioGroup.id) {
                    this.selectViewProgrammatically(-1)
                    sharedUseSearchButton.isEnabled = false
                }
            }
        }
    }
}
