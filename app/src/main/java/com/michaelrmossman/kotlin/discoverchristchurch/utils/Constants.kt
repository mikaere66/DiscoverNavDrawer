package com.michaelrmossman.kotlin.discoverchristchurch.utils

const val extRet = 1
const val ext1way = 0
const val baseRet = -1
const val base1way = -2
const val defaultNearest = -0.0

const val ALWAYS_GO_MAPS_PREF = "always_go_maps"
const val ALWAYS_SHOW_MARKERS_PREF = "always_show_markers"
const val ALWAYS_SHOW_POLYLINE_PREF = "always_show_polyline"

// A higher number means a higher zoom
const val CAMERA_DEFAULT_ZOOM: Float = 17F // BaseActivity Single Marker
const val CAMERA_LITE_LIST_ZOOM: Float = 12F // MapUtils
const val CAMERA_COMMUNITY_ZOOM: Float = 17F // CommunityBaseActivity
const val CAMERA_DOG_PARK_ZOOM: Float = 13F // DogParksActivity
// When zooming in on one of All start points
const val CAMERA_START_POINT_ZOOM: Float = 15F // RoutesBaseActivity
// A lower number means a higher zoom
const val CAMERA_SMALL_PADDING: Int = 360
const val CAMERA_MEDIUM_PADDING: Int = 720 // DogParks|Parks polygons
// const val CAMERA_LARGE_PADDING: Int = 1080 // Crashes on Galaxy S7

const val CAMERA_UPDATE_PADDING: Int = 180 // Test with 1 | 19 | 22 (was 170)

const val DEBUG_NAV_HOME_PREF = "navigation_home"
// 0,1,2,3 = R.id.communityFragment, R.id.areasFragment, R.id.listFragment, R.id.multiDayFragment,
// 4,5,6,7 = R.id.searchFragment, R.id.favouritesFragment, R.id.settingsFragment, R.id.legendFragment
const val DEBUG_NAV_HOME_DEFAULT = 0
const val DEBUG_ART_ITEM_ID = 0
const val DEBUG_BATTERY_RECYCLER_ID = 0
const val DEBUG_BIKE_TRACK_ID = 0
const val DEBUG_CH_CH_360_ID = 0
const val DEBUG_CONVENIENCE_ID = 0
const val DEBUG_DOG_PARK_ID = 0
const val DEBUG_FACILITY_ID = 0
const val DEBUG_FOUNTAIN_ID = 0
const val DEBUG_FREE_WI_FI_ID = 0
const val DEBUG_FRUIT_TYPE_ID = 0
const val DEBUG_HERITAGE_ID = 0
const val DEBUG_PARK_ID = 0
const val DEBUG_PLAY_ID = 0
// Test with 139(Basic) or 43(Ext) or 101(Linked)
const val DEBUG_ROUTE_ID = 0

// const val FAVE_ROUTES_PREF = "fave_routes"
const val FAVE_DATE_FORMAT = "EEE, d MMM h:mm" // + am/pm

const val FIRST_LAUNCH_PREF = "first_launch"

const val FRAGMENT_CH_CH_360_EXTRA = "fragment_ch_ch_360_extra"
const val FRAGMENT_CH_CH_360_TAG = "ChCh360ImageFragment"

const val FRAGMENT_DETAILED_EXTRA = "fragment_detailed_extra"

const val FRAGMENT_INFO_EXTRA = "fragment_info_extra"
const val FRAGMENT_INFO_TAG = "LegendInfo"

const val FRAGMENT_POSITION_EXTRA = "fragment_position"

const val FRUIT_SEASON_PREF = "fruit_season"

const val FULLSCREEN_IMAGE_ID_EXTRA = "image_id"
const val FULLSCREEN_IMAGE_LANDSCAPE_EXTRA = "image_landscape"
const val FULLSCREEN_IMAGE_NAME_EXTRA = "image_name"

const val INSTANCE_STATE_KEY_LAT = "lat"
const val INSTANCE_STATE_KEY_LON = "lon"
const val INSTANCE_STATE_KEY_ZOOM = "zoom"

const val ITEM_VIEW_TYPE_HEADER = Int.MAX_VALUE
const val ITEM_VIEW_TYPE_ITEM_00 = -1 // ChCh360
const val ITEM_VIEW_TYPE_ITEM_01 = -2 // Routes
const val ITEM_VIEW_TYPE_ITEM_02 = -3 // Linked Routes
const val ITEM_VIEW_TYPE_ITEM_1 = 1
const val ITEM_VIEW_TYPE_ITEM_2 = 2
const val ITEM_VIEW_TYPE_ITEM_3 = 3
const val ITEM_VIEW_TYPE_ITEM_4 = 4
const val ITEM_VIEW_TYPE_ITEM_5 = 5
const val ITEM_VIEW_TYPE_ITEM_6 = 6
const val ITEM_VIEW_TYPE_ITEM_7 = 7
const val ITEM_VIEW_TYPE_ITEM_8 = 8
const val ITEM_VIEW_TYPE_ITEM_9 = 9
const val ITEM_VIEW_TYPE_ITEM_10 = 10
const val ITEM_VIEW_TYPE_ITEM_11 = 11
const val ITEM_VIEW_TYPE_ITEM_12 = 12
const val ITEM_VIEW_TYPE_FOOTER = Int.MIN_VALUE

const val LIBRARY_FILTER_QUERY = "Library"

const val MAP_TYPE_PREF = "map_type"
const val MAP_TYPE_DEFAULT = 2 // Terrain

const val MULTI_DAY_COASTAL_ID = 1
const val MULTI_DAY_CRATER_ID = 2
const val MULTI_DAY_HEADS_ID = 3

const val MULTI_DAY_COASTAL_PATHWAY = "Coastal Pathway"
const val MULTI_DAY_CRATER_RIM = "Crater Rim"
const val MULTI_DAY_HEAD_TO_HEAD = "Head to Head"

const val NAV_INDEX_ID_EXTRA = "nav_index_id"

const val NO_PROMPT_ON_EXIT_PREF = "no_prompt_on_exit"

const val RANDOM_ROUTE_ID_PREF = "random_route_id"

const val POLYLINE_PATTERN_GAP_LENGTH = 2F
const val POLYLINE_LIGHT_STROKE_WIDTH = 10F
const val POLYLINE_MEDIUM_STROKE_WIDTH = 12.5F
const val POLYLINE_HEAVY_STROKE_WIDTH = 15F
const val POLYLINE_OBESE_STROKE_WIDTH = 17.5F

const val SCROLL_TO_THRESHOLD = 3

const val SHARED_PREFERENCES_KEY = "Prefs"
const val SHOW_FOOTER_PREF = "hide_community_footer"
const val SHOW_LOCATION_NEVER_PREF = "never_location"
const val SHOW_MY_LOCATION_PREF = "show_location"
const val SHOW_OPTIONAL_BUTTONS_PREF = "show_buttons"

const val SORT_BIKES_BY_PREF = "sort_bikes_by"
const val SORT_BIKES_ASC_PREF = "sort_bikes_asc"

const val SORT_LISTS_BY_PREF = "sort_lists_by"
const val SORT_ORDER_ASC_PREF = "sort_order_asc"

const val SHOW_STREET_VIEW_NAMES_PREF = "show_street_names"

const val REQUEST_CURRENT_LOCATION_PERMISSION = 123 // Nearest/WhereAmI
const val REQUEST_SHOW_MY_LOCATION_PERMISSION = 456 // Show My Location
