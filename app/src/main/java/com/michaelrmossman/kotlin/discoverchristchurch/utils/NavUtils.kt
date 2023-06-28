package com.michaelrmossman.kotlin.discoverchristchurch.utils

import androidx.navigation.NavController
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getAppContext
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast

/**
 * Helper functions used throughout the app, relating directly to Navigation
 */
object NavUtils {

    fun navigateTo(
        index: Int, navController: NavController, round: Int = Int.MIN_VALUE
    ) {
        /* The index is derived from the first two letters of
           the source name and the first letter of it's class
           type, except just one letter, A or F; then the same
           for the destination. Be aware however, that this
           system is limited to a source name of e.g. uzy,
           considering that Int.MAX_VALUE = 2,147,483,647 */
        val destinationId: Int = when (index) {

            -2 -> { // Home (popUpTo for both)
                R.id.action_routesFragment_to_communityFragment
            }
            -1 -> R.id.action_placesFragment_to_communityFragment

            /* This first lot should have preceding 0. Note: obviously
               the system went out the window with chCh360* but
               chCh360, chCh361, chCh362 use 0, 1, 2 respectively */
            118818156 -> R.id.action_areasFragment_to_placesFragment

            201602011 -> R.id.action_batteryRecyclersFragment_to_batteryRecyclersActivity

            209602041 -> R.id.action_bikeTracksFragment_to_bikeDetailsActivity
            209602091 -> R.id.action_bikeTracksFragment_to_bikeTracksActivity

            308603080 -> { // Details viewPager
                R.id.action_chCh360ListFragment_to_chCh360Activity
            }
            308603081 -> { // Single chCh360 map
                R.id.action_chCh360ListFragment_to_chCh361Activity
            }
            308603082 -> { // Overview map (from menu)
                R.id.action_chCh360ListFragment_to_chCh362Activity
            }

//                315602011 -> {
//                    R.id.action_communityFragment_to_batteryRecyclersActivity
//                }
            315602016 -> {
                R.id.action_communityFragment_to_batteryRecyclersFragment
            }
//                315602041 -> {
//                    R.id.action_communityFragment_to_bikeTracksActivity
//                }
            315602046 -> {
                R.id.action_communityFragment_to_bikeTracksFragment
            }
//                315616031 -> {
//                    R.id.action_communityFragment_to_conveniencesActivity
//                }
            315616036 -> {
                R.id.action_communityFragment_to_conveniencesFragment
            }
//                315604141 -> {
//                    R.id.action_communityFragment_to_dogParksActivity
//                }
            315604146 -> {
                R.id.action_communityFragment_to_dogParksFragment
            }
//                315606011 -> {
//                    R.id.action_communityFragment_to_facilitiesActivity
//                }
            315606016 -> {
                R.id.action_communityFragment_to_facilitiesFragment
            }
//                315606151 -> {
//                    R.id.action_communityFragment_to_fountainsActivity
//                }
            315606156 -> {
                R.id.action_communityFragment_to_fountainsFragment
            }
//                315606231 -> {
//                    R.id.action_communityFragment_to_freeWiFiActivity
//                }
            315606236 -> {
                R.id.action_communityFragment_to_freeWiFiFragment
            }
//                315606181 -> {
//                    R.id.action_communityFragment_to_fruitTreesActivity
//                }
            315606186 -> {
                R.id.action_communityFragment_to_fruitTypesFragment
            }
//                315608051 -> {
//                    R.id.action_communityFragment_to_heritageSitesActivity
//                }
            315608056 -> {
                R.id.action_communityFragment_to_heritageSitesFragment
            }
//                315616011 -> {
//                    R.id.action_communityFragment_to_parksActivity
//                }
            315616016 -> {
                R.id.action_communityFragment_to_parksFragment
            }
//                315619011 -> {
//                    R.id.action_communityFragment_to_streetArtActivity
//                }
            315619016 -> {
                R.id.action_communityFragment_to_streetArtFragment
            }
//                315621181 -> {
//                    R.id.action_communityFragment_to_urbanPlayActivity
//                }
            315621186 -> {
                R.id.action_communityFragment_to_urbanPlayFragment
            }

            315603151 -> {
                R.id.action_conveniencesFragment_to_conveniencesActivity
            }

            412604051 -> R.id.action_dogLinksFragment_to_detailsActivity
            412602011 -> {
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_dogLinksFragment_to_basicActivity
                    }
                    else -> R.id.action_dogLinksFragment_to_extendedActivity
                }
            }

            415604051 -> R.id.action_dogParksFragment_to_detailsActivity
            415604151 -> R.id.action_dogParksFragment_to_dogDetailsActivity
            415604126 -> R.id.action_dogParksFragment_to_dogLinksFragment
            415604161 -> R.id.action_dogParksFragment_to_dogParksActivity

            601606011 -> R.id.action_facilitiesFragment_to_facilitiesActivity

            601602181 -> R.id.action_favouritesFragment_to_batteryRecyclersActivity

            601602041 -> R.id.action_favouritesFragment_to_bikeDetailsActivity
            601602201 -> R.id.action_favouritesFragment_to_bikeTracksActivity

            601603080 -> { // Details viewPager
                R.id.action_favouritesFragment_to_chCh360Activity
            }
            601603081 -> { // Single chCh360 map
                R.id.action_favouritesFragment_to_chCh361Activity
            }

            601616031 -> R.id.action_favouritesFragment_to_conveniencesActivity

            601604051 -> R.id.action_favouritesFragment_to_detailsActivity
            601504151 -> R.id.action_favouritesFragment_to_dogDetailsActivity
            601504126 -> R.id.action_favouritesFragment_to_dogLinksFragment
            601504161 -> R.id.action_favouritesFragment_to_dogParksActivity
            601506011 -> R.id.action_favouritesFragment_to_facilitiesActivity
            601506151 -> R.id.action_favouritesFragment_to_fountainsActivity
            601506231 -> R.id.action_favouritesFragment_to_freeWiFiActivity
            601506181 -> R.id.action_favouritesFragment_to_fruitTreesActivity
            601508051 -> R.id.action_favouritesFragment_to_heritageSitesActivity
            601516011 -> R.id.action_favouritesFragment_to_parksActivity
            601519041 -> R.id.action_favouritesFragment_to_streetDetailsActivity
            601519011 -> R.id.action_favouritesFragment_to_streetArtActivity
            601521181 -> R.id.action_favouritesFragment_to_urbanPlayActivity
            601602011 -> {
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_favouritesFragment_to_basicActivity
                    }
                    else -> R.id.action_favouritesFragment_to_extendedActivity
                }
            }

            615606151 -> R.id.action_fountainsFragment_to_fountainsActivity

            623606231 -> R.id.action_freeWiFiFragment_to_freeWiFiActivity

            618606181 -> R.id.action_fruitTypesFragment_to_fruitTreesActivity

            805608051 -> R.id.action_heritageSitesFragment_to_heritageSitesActivity

            1209604051 -> R.id.action_listFragment_to_detailsActivity
            1209602011 -> {
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_listFragment_to_basicActivity
                    }
                    else -> R.id.action_listFragment_to_extendedActivity
                }
            }

            // Overview maps for ChCh360 | Crater Rim | Head to Head
            1304603086 -> R.id.action_multiDayFragment_to_chCh360ListFragment
            1304603081 -> R.id.action_multiDayFragment_to_chCh362Activity
            1304613211 -> R.id.action_multiDayFragment_to_multiDayActivity
            /* Routes based on _routeFilterTerm ... either
               CRATER_RIM_WALKWAYS or HEAD_TO_HEAD_WALKWAY */
            1304613216 -> R.id.action_multiDayFragment_to_multiRouteFragment

            1321613211 -> { // Crater Rim|Head to Head
                R.id.action_multiRouteFragment_to_multiDayActivity
            }
            1321604051 -> R.id.action_multiRouteFragment_to_detailsActivity
            1321602011 -> {
                /* All the Crater Rim routes are basic, and so are the
                   Head to Head ones EXCEPT ONE: Lyttelton Township */
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_multiRouteFragment_to_basicActivity
                    }
                    else -> R.id.action_multiRouteFragment_to_extendedActivity
                }
            }

            1601616011 -> R.id.action_parksFragment_to_parksActivity

            1612618156 -> R.id.action_placesFragment_to_routesFragment

            1805604051 -> R.id.action_resultsFragment_to_detailsActivity
            1805602011 -> {
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_resultsFragment_to_basicActivity
                    }
                    else -> R.id.action_resultsFragment_to_extendedActivity
                }
            }

            1815601186 -> R.id.action_routesFragment_to_areasFragment
            1815604051 -> R.id.action_routesFragment_to_detailsActivity
            1815602011 -> {
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_routesFragment_to_basicActivity
                    }
                    else -> R.id.action_routesFragment_to_extendedActivity
                }
            }

            1905618056 -> { // Search by feature
                R.id.action_searchFragment_to_resultsFragment
            }

            1905604051 -> { // By id, or random
                R.id.action_settingsFragment_to_detailsActivity
            }
            1905602011 -> {
                when (round) {
                    in base1way..baseRet -> {
                        R.id.action_settingsFragment_to_basicActivity
                    }
                    else -> R.id.action_settingsFragment_to_extendedActivity
                }
            }

            1901619011 -> {
                R.id.action_streetArtFragment_to_streetArtActivity
            }
            1901619041 -> {
                R.id.action_streetArtFragment_to_streetDetailsActivity
            }

            2118621181 -> {
                R.id.action_urbanPlayFragment_to_urbanPlayActivity
            }

            else -> 0
        }

        when (index ) {
            0 -> fancyToast(getAppContext(),3, R.string.no_nav_id)
            else -> navController.navigate(destinationId)
        }
    }
}
