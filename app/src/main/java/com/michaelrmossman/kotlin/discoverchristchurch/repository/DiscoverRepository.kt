package com.michaelrmossman.kotlin.discoverchristchurch.repository

import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.*
import com.michaelrmossman.kotlin.discoverchristchurch.enums.SortOrder
import com.michaelrmossman.kotlin.discoverchristchurch.repository.AreaHelpers.getAreasWithPlacesAndRoutes
import com.michaelrmossman.kotlin.discoverchristchurch.repository.BatteryRecyclerHelpers.getBatteryRecyclersQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.BikeTrackHelpers.getBikeTrackKtWithFave
import com.michaelrmossman.kotlin.discoverchristchurch.repository.BikeTrackHelpers.getBikeTracksKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.BikeTrackHelpers.getBikeTracksKtWithFave
import com.michaelrmossman.kotlin.discoverchristchurch.repository.BikeTrackHelpers.getBikeTracksQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.ChCh360Helpers.getChCh360Query
import com.michaelrmossman.kotlin.discoverchristchurch.repository.ConvenienceHelpers.getConveniencesQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.DogParkHelpers.getDogParkTypesKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.DogParkHelpers.getDogParksBy
import com.michaelrmossman.kotlin.discoverchristchurch.repository.DogParkHelpers.getDogParksKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.DogParkHelpers.getDogParksFullKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.DogParkHelpers.getDogParksQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FacilityHelpers.getFacilitiesBy
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FacilityHelpers.getFacilitiesQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FacilityHelpers.getFacilityTypesKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FavouriteHelpers.toggleFavourite
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FountainHelpers.getFountainsQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FreeWiFiHelpers.getFreeWiFiLocationsQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FruitTreeHelpers.getFruitCategoriesKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FruitTreeHelpers.getFruitCatsWithTypes
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FruitTreeHelpers.getFruitTreesQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.FruitTreeHelpers.getFruitTypeQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.HeritageSiteHelpers.getHeritageSitesFullKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.HeritageSiteHelpers.getHeritageSitesQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.HeritageSiteHelpers.getHeritageTypesKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.ParkHelpers.getParkTypesKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.ParkHelpers.getParksBy
import com.michaelrmossman.kotlin.discoverchristchurch.repository.ParkHelpers.getParksQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.PlaceHelpers.getPlacesWithRoutes
import com.michaelrmossman.kotlin.discoverchristchurch.repository.RouteHelpers.getMultiLinkedRoutesKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.RouteHelpers.getRoutesByFeature
import com.michaelrmossman.kotlin.discoverchristchurch.repository.RouteHelpers.getRoutesQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.StreetArtHelpers.getStreetArtFullKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.StreetArtHelpers.getStreetArtQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.UrbanPlayHelpers.getUrbanPlayQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.WaypointHelpers.getAPsAsWaypointsWithStartWaypoint
import com.michaelrmossman.kotlin.discoverchristchurch.repository.WaypointHelpers.getChCh360ItemsAsWaypointsKt
import com.michaelrmossman.kotlin.discoverchristchurch.repository.WaypointHelpers.getRoutesListAsWaypoints
import com.michaelrmossman.kotlin.discoverchristchurch.repository.WaypointHelpers.getWaypointsWithStartWaypoint
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getEmptyRouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getResources
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepository @Inject constructor(
    private val dao: DiscoverDao
) {

    private val _allListsSortedBy =
        MutableStateFlow(sharedPrefs.getInt(SORT_LISTS_BY_PREF, 0))
    fun setSortListsBy(index: Int) {
        _allListsSortedBy.value = index
    }

    private val _allListsSortOrder =
        MutableStateFlow(
            when (sharedPrefs.getBoolean(SORT_ORDER_ASC_PREF, true)) {
                true -> SortOrder.ASC
                else -> SortOrder.DESC
            }
        )
    fun setSortOrder(ascending: Boolean) {
        _allListsSortOrder.value =
            when (ascending) {
                true -> SortOrder.ASC
                else -> SortOrder.DESC
            }
    }

    suspend fun getAccessPointsListByChCh360Id(
        id: Long
    ) : List<WaypointKt> {
        val accessPoints = dao.getChCh360AccessPointsById(id)
        return getAPsAsWaypointsWithStartWaypoint(accessPoints, dao, id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val areas: Flow<List<AreaKt>> =
        _allListsSortedBy.flatMapLatest { sortBy ->
            _allListsSortOrder.flatMapLatest { sortOrder ->
                dao.getAreasFlow().map { areas ->
                    getAreasWithPlacesAndRoutes(
                        areas, dao, sortBy, sortOrder
                    )
                }
            }
        }
    suspend fun getAreaById(id: Long): Area =
        dao.getAreaById(id)

    // BATTERY RECYCLERS

    val batteryRecyclers: Flow<List<BatteryRecyclerKt>> =
        dao.getBatteryRecyclersFlow(getBatteryRecyclersQuery(0L))
    suspend fun getBatteryRecyclerById(id: Long): BatteryRecyclerKt =
        dao.getBatteryRecyclerById(getBatteryRecyclersQuery(id))
    suspend fun getBatteryRecyclersList(): List<BatteryRecyclerKt> =
        dao.getBatteryRecyclersList(getBatteryRecyclersQuery(0L))

    // BIKE TRACKS

    private val _bikeListsSortedBy =
        MutableStateFlow(sharedPrefs.getInt(SORT_BIKES_BY_PREF, 0))
    fun setSortBikesBy(index: Int) {
        _bikeListsSortedBy.value = index
    }

    private val _bikeListsSortOrder =
        MutableStateFlow(
            when (sharedPrefs.getBoolean(SORT_BIKES_ASC_PREF, true)) {
                true -> SortOrder.ASC
                else -> SortOrder.DESC
            }
        )
    fun setSortBikes(ascending: Boolean) {
        _bikeListsSortOrder.value =
            when (ascending) {
                true -> SortOrder.ASC
                else -> SortOrder.DESC
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bikeTracks: Flow<List<BikeTrackKt>> =
        _bikeListsSortedBy.flatMapLatest { sortBy ->
            _bikeListsSortOrder.flatMapLatest { sortOrder ->
                dao.getBikeTracksFlow(
                    getBikeTracksQuery(0L, sortBy, sortOrder)
                ).map { bikeTracks ->
                        getBikeTracksKtWithFave(bikeTracks)
                }
            }
        }
    suspend fun getBikeCoordsByTrackId(
        id: Long
    ) : List<BikeCoordsKt> =
        dao.getBikeCoordsByTrackId(id)
    suspend fun getBikeTrackById(id: Long): BikeTrackKt {
        val bikeTrack = dao.getBikeTrackById(
            getBikeTracksQuery(id,0, SortOrder.NONE)
        )
        return getBikeTrackKtWithFave(bikeTrack,1,1)
    }
    private var _bikeTrackItems: List<BikeTrackKt> = listOf()
    val bikeTrackItems: List<BikeTrackKt>
        get() = _bikeTrackItems
    suspend fun getBikeTrackItems(
        /* Persist for BikeDetails activity/fragment.
           Has an observer for favourites checkbox */
        persist: Boolean
    ) : List<BikeTrackKt> =
        when (persist) {
            true -> {
                val items = dao.getBikeTrackItems(
                    getBikeTracksQuery(0L,0, SortOrder.NONE)
                )
                setBikeTrackItems(items)
                items
            }
            else -> {
                val items = dao.getBikeTracksList()
                getBikeTracksKt(items)
            }
        }
    fun setBikeTrackItems(items: List<BikeTrackKt>) {
        _bikeTrackItems = items
    }

    // CHRISTCHURCH 360

    val chCh360ItemsFlow: Flow<List<ChCh360Kt>> =
        dao.getChCh360ItemsFlow(getChCh360Query(0L))
    suspend fun getChCh360ItemById(id: Long): ChCh360Kt =
        dao.getChCh360KtItemById( // Note diff name
            getChCh360Query(id)
        )

    private var _chCh360ItemId: Long =
        (DEBUG_CH_CH_360_ID).toLong()
    val chCh360ItemId: Long
        get() = _chCh360ItemId
    fun setChCh360ItemId(id: Long) {
        _chCh360ItemId = id
    }
    private var _chCh360Items: List<ChCh360Kt> = listOf()
    val chCh360Items: List<ChCh360Kt>
        get() = _chCh360Items

    suspend fun getChCh360Items(persist: Boolean): List<ChCh360Kt> {
        val items = dao.getChCh360ItemsList(getChCh360Query(0L))
        if (persist) {
            setChCh360Items(items)
        }
        return items
    }

    fun setChCh360Items(items: List<ChCh360Kt>) {
        _chCh360Items = items
    }
    fun getChCh360ItemsAsWaypoints(
        items: List<ChCh360Kt>
    ) : List<WaypointKt> = // Note diff name
        getChCh360ItemsAsWaypointsKt(items)
    suspend fun getChCh360ItemsCount(): Int =
        dao.getChCh360ItemsCount()
    suspend fun getChCh360CoordsListByLegId(
        id: Long, track: Int
    ) : List<ChCh360CoordsKt> =
        dao.getChCh360CoordsListByLegId(id, track)

    // COMMUNITY ITEMS

    val communityItems: Flow<List<CommunityItem>> =
        dao.getCommunityItemsFlow()

    // CONVENIENCES

    val conveniences: Flow<List<ConvenienceKt>> =
        dao.getConveniencesFlow(getConveniencesQuery(0L))
    suspend fun getConvenienceById(
        id: Long
    ) : ConvenienceKt =
        dao.getConvenienceById(getConveniencesQuery(id))
    suspend fun getConveniencesCount(): Int =
        dao.getConveniencesCount()
    suspend fun getConveniencesList(): List<ConvenienceKt> =
        dao.getConveniencesList(getConveniencesQuery(0L))
    suspend fun getConvenienceTypesList(): List<ConvenienceType> =
        dao.getConvenienceTypesList()
    suspend fun updateConvenienceTypesSelected(
        types: List<ConvenienceType>
    ) : Int = dao.updateConvenienceTypesSelected(types)

    // END CONVENIENCES

    private var _currentAreaId: Long = (0).toLong()
    val currentAreaId: Long
        get() = _currentAreaId
    fun setCurrentAreaId(id: Long) {
        _currentAreaId = id
    }

    private var _currentBatteryRecyclerId: Long =
        (DEBUG_BATTERY_RECYCLER_ID).toLong()
    val currentBatteryRecyclerId: Long
        get() = _currentBatteryRecyclerId
    fun setCurrentBatteryRecyclerId(id: Long) {
        _currentBatteryRecyclerId = id
    }

    private var _currentBikeTrackId: Long =
        (DEBUG_BIKE_TRACK_ID).toLong()
    val currentBikeTrackId: Long
        get() = _currentBikeTrackId
    fun setCurrentBikeTrackId(id: Long) {
        _currentBikeTrackId = id
    }

    private var _currentConvenienceId: Long =
        (DEBUG_CONVENIENCE_ID).toLong()
    val currentConvenienceId: Long
        get() = _currentConvenienceId
    fun setCurrentConvenienceId(id: Long) {
        _currentConvenienceId = id
    }

    private var _currentCoords = Coords()
    val currentCoords: Coords
        get() = _currentCoords
    fun setCurrentCoords(coords: Coords) {
        _currentCoords = coords
    }

    private var _currentDogParkId: Long =
        (DEBUG_DOG_PARK_ID).toLong()
    val currentDogParkId: Long
        get() = _currentDogParkId
    fun setCurrentDogParkId(id: Long) {
        _currentDogParkId = id
    }

    private var _currentFacilityId: Long =
        (DEBUG_PARK_ID).toLong()
    val currentFacilityId: Long
        get() = _currentFacilityId
    fun setCurrentFacilityId(id: Long) {
        _currentFacilityId = id
    }

    private var _currentFeature: String = String()
    fun setCurrentFeature(feature: String) {
        _currentFeature = feature
    }

    private var _currentFountainId: Long =
        (DEBUG_FOUNTAIN_ID).toLong()
    val currentFountainId: Long
        get() = _currentFountainId
    fun setCurrentFountainId(id: Long) {
        _currentFountainId = id
    }

    private var _currentFreeWiFiId: Long =
        (DEBUG_FREE_WI_FI_ID).toLong()
    val currentFreeWiFiId: Long
        get() = _currentFreeWiFiId
    fun setCurrentFreeWiFiId(id: Long) {
        _currentFreeWiFiId = id
    }

    private var _currentFruitTypeId: Long =
        (DEBUG_FRUIT_TYPE_ID).toLong()
    val currentFruitTypeId: Long
        get() = _currentFruitTypeId
    fun setCurrentFruitTypeId(id: Long) {
        _currentFruitTypeId = id
    }

    private var _currentHeritageSiteId: Long =
        (DEBUG_HERITAGE_ID).toLong()
    val currentHeritageSiteId: Long
        get() = _currentHeritageSiteId
    fun setCurrentHeritageSiteId(id: Long) {
        _currentHeritageSiteId = id
    }

    private var _currentParkId: Long =
        (DEBUG_FACILITY_ID).toLong()
    val currentParkId: Long
        get() = _currentParkId
    fun setCurrentParkId(id: Long) {
        _currentParkId = id
    }

    private var _currentPlaceId: Long = (0).toLong()
    val currentPlaceId: Long
        get() = _currentPlaceId
    fun setCurrentPlaceId(id: Long) {
        _currentPlaceId = id
    }

    private var _currentRouteId: Long =
        (DEBUG_ROUTE_ID).toLong()
    val currentRouteId: Long
        get() = _currentRouteId
    fun setCurrentRouteId(id: Long) {
        _currentRouteId = id
    }

    /* These two relate to Search by
       Feature, not routesSearchedBy */
    private var _currentSearch: String = String()
    val currentSearch: String
        get() = _currentSearch
    fun setCurrentSearch(search: String) {
        _currentSearch = search
    }
    private var _currentSelection: Int = -1
    fun setCurrentSelection(selection: Int) {
        _currentSelection = selection
    }

    private var _currentStreetArtId: Long =
        (DEBUG_ART_ITEM_ID).toLong()
    val currentStreetArtId: Long
        get() = _currentStreetArtId
    fun setCurrentStreetArtId(id: Long) {
        _currentStreetArtId = id
    }

    private var _currentUrbanPlayId: Long =
        (DEBUG_PLAY_ID).toLong()
    val currentUrbanPlayId: Long
        get() = _currentUrbanPlayId
    fun setCurrentUrbanPlayId(id: Long) {
        _currentUrbanPlayId = id
    }

    // DOG PARKS

    val dogParks: Flow<List<DogParkKt>> =
        dao.getDogParksFlow(getDogParksQuery(0L,null))
   val dogParksKt: Flow<List<DogParkKt>> =
        dao.getDogParksFlow(getDogParksQuery(0L,null)).map { parks ->
            getDogParksFullKt(parks)
        }
    suspend fun getDogParkById(id: Long): DogParkKt =
        dao.getDogParkById(getDogParksQuery(id,null))
    suspend fun getDogParksCount(): Int =
        dao.getDogParksCount()
    private var _dogParksList: List<DogParkKt> = listOf()
    val dogParksList: List<DogParkKt>
        get() = _dogParksList
    suspend fun getDogParksList(): List<DogParkKt> {
        val dogParkTypes = listOf(
            dao.getDogParkTypeById(8), // Dog Exercise Area
            dao.getDogParkTypeById(9) // Dog Park
        )
        val dogParksKt = getDogParksKt(dao, dogParkTypes)
        setDogParksList(dogParksKt)
        return dogParksKt
    }
    fun setDogParksList(list: List<DogParkKt>) {
        _dogParksList = list
    }
    suspend fun getDogParkTypes(): List<DogTypeKt> {
        val dogParkTypes = dao.getDogParkTypes()
        return getDogParkTypesKt(dao, dogParkTypes)
    }

    // Must come before dogParksFilteredBy, hence the name used
    private var _dogParkFilterTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val dogParkFilterTerm: StateFlow<String?>
        get() = _dogParkFilterTerm
    fun setDogParkFilterTerm(filterTerm: String?) {
        _dogParkFilterTerm.value = filterTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val dogParksFilteredBy: Flow<List<DogParkKt>> =
        _dogParkFilterTerm.flatMapLatest { filterTerm ->
            getDogParksBy(dao, filterTerm)
        }

    suspend fun getEnvironmentHoleListById(
        id: Long
    ) : List<HoleKt> =
        dao.getEnvironmentHoleListById(id)
    suspend fun getEnvironmentListByDogParkId(
        id: Long
    ) : List<DogEnvironmentKt> =
        dao.getEnvironmentListByDogParkId(id)
    suspend fun updateDogTypesSelected(
        types: List<DogType>
    ) : Int = dao.updateDogTypesSelected(types)

    // DRINK FOUNTAINS

    @OptIn(ExperimentalCoroutinesApi::class)
    val drinkFountains: Flow<List<DrinkFountainKt>> =
        dao.getFountainTypesFlow().flatMapLatest { types ->
            dao.getFountainsFlow(getFountainsQuery(0L, types))
        }
    suspend fun getFountainById(id: Long): DrinkFountainKt =
        dao.getFountainById(getFountainsQuery(id, listOf()))
    suspend fun getFountainsCount(): Int =
        dao.getFountainsCount()
    suspend fun getFountainsList(): List<DrinkFountainKt> {
        val types = dao.getFountainTypesSelected()
        return dao.getFountainsList(getFountainsQuery(0L, types))
    }
    suspend fun getFountainTypesList(): List<DrinkType> =
        dao.getFountainTypesList()
    suspend fun updateDrinkTypesSelected(
        types: List<DrinkType>
    ) : Int = dao.updateDrinkTypesSelected(types)

    // FACILITIES

    // Must come before facilitiesFilteredBy, hence the name used
    private var _facilityFilterTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val facilityFilterTerm: StateFlow<String?>
        get() = _facilityFilterTerm
    fun setFacilityFilterTerm(filterTerm: String?) {
        _facilityFilterTerm.value = filterTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val facilitiesFilteredBy: Flow<List<FacilityKt>> =
        _facilityFilterTerm.flatMapLatest { filterTerm ->
            getFacilitiesBy(dao, filterTerm)
        }

    // Must come before facilitiesSearchedBy, hence the name used
    private var _facilitySearchTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val facilitySearchTerm: StateFlow<String?>
        get() = _facilitySearchTerm
    fun setFacilitySearchTerm(searchTerm: String?) {
        _facilitySearchTerm.value = searchTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val facilitiesSearchedBy: Flow<List<FacilityKt>> =
        _facilitySearchTerm.flatMapLatest { searchTerm ->
            getFacilitiesBy(dao, searchTerm)
        }
    suspend fun getFacilityById(id: Long): FacilityKt =
        dao.getFacilityById(getFacilitiesQuery(id,null))
    suspend fun getFacilitiesCount(): Int =
        dao.getFacilitiesCount()
    suspend fun getFacilitiesList(): List<FacilityKt> = // -1L means NO sort
        dao.getFacilitiesList(getFacilitiesQuery(-1L,null))
    suspend fun getFacilityTypesSelectedCount() : Int =
        dao.getFacilityTypesSelectedCount()
    suspend fun getFacilityTypeSelected(): FacilityType =
        dao.getFacilityTypeSelected() // Limit 1
    suspend fun getFacilityTypes(): List<FacilityTypeKt> {
        val facilityTypes = dao.getFacilityTypes()
        return getFacilityTypesKt(dao, facilityTypes)
    }
    suspend fun updateFacilityTypesSelected(
        types: List<FacilityType>
    ) : Int = dao.updateFacilityTypesSelected(types)

    // FAVOURITES

    val faves: Flow<List<Favourite>> =
        dao.getFavouritesFlow()
    val favouritesCount: Flow<Int> =
        dao.getFavouritesCountFlow()
    suspend fun nukeFavouritesTableAsync(): Deferred<Int> =
        CoroutineScope(Dispatchers.IO).async {
            dao.nukeFavouritesTable()
        }

    // FREE WIFI

    val freeWiFiLocations: Flow<List<FreeWiFiKt>> =
        dao.getFreeWiFiLocationsFlow(getFreeWiFiLocationsQuery(0L,true))
    suspend fun getFreeWiFiLocationById(id: Long): FreeWiFiKt =
        dao.getFreeWiFiLocationById(getFreeWiFiLocationsQuery(id,false))
    suspend fun getFreeWiFiLocations(): List<FreeWiFiKt> =
        dao.getFreeWiFiLocationsList(getFreeWiFiLocationsQuery(0L,false))

    // FRUIT TREES

    val fruitTypes: Flow<List<FruitTypeKt>> =
        dao.getFruitCatsFlow().map { cats ->
            getFruitCatsWithTypes(cats, dao)
        }
    suspend fun getFruitCatsList(): List<FruitCat> =
        dao.getFruitCatsList()
    suspend fun getFruitCatsListKt(): List<FruitCatKt> {
        val fruitCats = dao.getFruitCatsAll()
        return getFruitCategoriesKt(dao, fruitCats)
    }
    suspend fun getFruitTreesByTypeId(
        id: Long
    ) : List<FruitTreeKt> =
        dao.getFruitTreesByTypeId(getFruitTreesQuery(id))
    suspend fun getFruitTypeById(id: Long): FruitTypeKt {
        val type = dao.getFruitTypeById(id)
        val cat = dao.getFruitCatById(type.categoryId)
        return dao.getFruitTypeByCatId(
            getFruitTypeQuery(
                cat.id, type.id
            )
        )
    }
    suspend fun getFruitTypesByCatId(
        id: Long
    ) : List<FruitType> =
        dao.getFruitTypesByCatId(id)
    suspend fun getFruitTypesCount(): Int =
        dao.getFruitTypesCount()
    suspend fun updateFruitCatsSelected(
        types: List<FruitCat>
    ) : Int = dao.updateFruitCatsSelected(types)

    // END FRUIT TREES

    // HERITAGE SITES

    val heritageSites: Flow<List<HeritageSiteKt>> =
        dao.getHeritageSitesFlow(getHeritageSitesQuery(0L))
    val heritageSitesKt: Flow<List<HeritageSiteKt>> =
        dao.getHeritageSitesFlow(getHeritageSitesQuery(0L)).map { sites ->
            getHeritageSitesFullKt(sites)
        }
    suspend fun getHeritageSiteById(id: Long): HeritageSiteKt =
        dao.getHeritageSiteById(getHeritageSitesQuery(id))
    suspend fun getHeritageSitesCount(): Int =
        dao.getHeritageSitesCount()
    suspend fun getHeritageTypes(): List<HeritageTypeKt> {
        val heritageTypes = dao.getHeritageTypes()
        return getHeritageTypesKt(dao, heritageTypes)
    }
    suspend fun getHeritageTypesSelectedCount() : Int =
            dao.getHeritageTypesSelectedCount()
    suspend fun getHeritageTypeSelected(): FacilityType =
        dao.getHeritageTypeSelected() // Limit 1
    suspend fun updateHeritageTypesSelected(
        types: List<HeritageType>
    ) : Int = dao.updateHeritageTypesSelected(types)

    // END HERITAGE SITES

    private val _isNavDrawerOpen = MutableStateFlow(false)
    val isNavDrawerOpen: StateFlow<Boolean>
        get() = _isNavDrawerOpen
    fun setIsNavDrawerOpen(isOpen: Boolean) {
        _isNavDrawerOpen.value = isOpen
    }

    private var _linkedRoute: MutableStateFlow<RouteKt> =
        MutableStateFlow(getEmptyRouteKt())
    val linkedRoute: StateFlow<RouteKt>
        get() = _linkedRoute
    fun setLinkedRouteKt(route: RouteKt) {
        _linkedRoute.value = route
    }

    /* Represents the Coastal Pathway, Crater Rim, &
       Head to Head route IDs, as well as any linked
       routes from DogParksFragment. Also limits list
       to adapter.currentList when going from either
       Results|Routes fragments to DetailsActivity */
    private var _multiRouteIds: List<Long> = listOf()
    val multiRouteIds: List<Long>
        get() = _multiRouteIds
    fun setMultiRouteIds(ids: List<Long>) {
        _multiRouteIds = ids
    }
    /* Int.MIN_VALUE = DogParksFragment linked routes
       0 = RoutesFragment list (Areas|Places|Routes)
       1,2,3 = CoastalPath, CraterRim, HeadToHead */
    private var _multiRouteIndex = Int.MAX_VALUE
    val multiRouteIndex: Int
        get() = _multiRouteIndex
    fun setMultiRouteIndex(index: Int) {
        _multiRouteIndex = index
    }
    suspend fun getMultiLinkedRoutesKt(): List<RouteKt> {
        val routes = getMultiLinkedRoutesKt(dao, multiRouteIds)
        setRoutesKtList(routes) // DetailsFragment
        return routes
    }

    private val _overflowIconIndex = MutableStateFlow(0)
    val overflowIconIndex: StateFlow<Int>
        get() = _overflowIconIndex
    fun setOverflowIconIndex(index: Int) {
        _overflowIconIndex.value = index
    }

    // PARKS

    // Must come before parksFilteredBy, hence the name used
    private var _parkFilterTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val parkFilterTerm: StateFlow<String?>
        get() = _parkFilterTerm
    fun setParkFilterTerm(filterTerm: String?) {
        _parkFilterTerm.value = filterTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val parksFilteredBy: Flow<List<ParkKt>> =
        _parkFilterTerm.flatMapLatest { filterTerm ->
            getParksBy(dao, filterTerm)
        }

    // Must come before parksSearchedBy, hence the name used
    private var _parkSearchTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val parkSearchTerm: StateFlow<String?>
        get() = _parkSearchTerm
    fun setParkSearchTerm(searchTerm: String?) {
        _parkSearchTerm.value = searchTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val parksSearchedBy: Flow<List<ParkKt>> =
        _parkSearchTerm.flatMapLatest { searchTerm ->
            getParksBy(dao, searchTerm)
        }

    suspend fun getParkById(id: Long): ParkKt =
        dao.getParkById(getParksQuery(id,null,false))
    suspend fun getParksCount(): Int =
        dao.getParksCount()
    suspend fun getParksList(): List<ParkKt> =
        dao.getParksList(getParksQuery(0L,null,false))
    suspend fun getParkTypesNotSelectedCount() : Int =
            dao.getParkTypesNotSelectedCount() // Note: NOT
    suspend fun getParkTypes(): List<ParkTypeKt> {
        val parkTypes = dao.getParkTypes()
        return getParkTypesKt(dao, parkTypes)
    }
    suspend fun updateParkTypesSelected(
        types: List<ParkType>
    ) : Int = dao.updateParkTypesSelected(types)
    suspend fun getEnvironmentListByParkId(
        id: Long
    ) : List<ParkEnvironmentKt> =
        dao.getEnvironmentListByParkId(id)

    // END PARKS

    @OptIn(ExperimentalCoroutinesApi::class)
    val places: Flow<List<PlaceKt>> =
        _allListsSortedBy.flatMapLatest { sortBy ->
            _allListsSortOrder.flatMapLatest { sortOrder ->
                dao.getPlacesFlowByAreaId(_currentAreaId).map { places ->
                    getPlacesWithRoutes(dao, places, sortBy, sortOrder)
                }
            }
        }
    suspend fun getPlaceById(id: Long): Place =
        dao.getPlaceById(id)
    suspend fun getPlacesCount(): Int =
        dao.getPlacesCount()

    private var _resetFlippedViews =
        MutableStateFlow(false)
    val resetFlippedViews: StateFlow<Boolean>
        get() = _resetFlippedViews
    fun setResetFlippedViews(reset: Boolean) {
        _resetFlippedViews.value = reset
    }

    // SINGLE ROUTES
    suspend fun getRouteById(id: Long): Route =
        dao.getRouteById(id) // Just for title in BasicActivity
    suspend fun getRouteKtById(id: Long): RouteKt =
        dao.getRouteKtById(getRoutesQuery(id,null))

    // ROUTES LISTS
    @OptIn(ExperimentalCoroutinesApi::class)
    val routes: Flow<List<RouteKt>> =
        _allListsSortedBy.flatMapLatest { sortBy ->
            _allListsSortOrder.flatMapLatest { sortOrder ->
                dao.getRoutesKtFlow(
                    getRoutesQuery(
                        currentPlaceId.unaryMinus(),
                        null, sortBy, sortOrder
                    )
                )
            }
        }

    suspend fun getRoutesCount(): Int =
        dao.getRoutesCount()
    suspend fun getRoutesCountByAreaId(id: Long): Int =
        dao.getRoutesCountByAreaId(id)

    private var _routesKtList: List<RouteKt> = listOf()
    val routesKtList: List<RouteKt>
        get() = _routesKtList
    fun setRoutesKtList(routes: List<RouteKt>) {
        _routesKtList = routes
    }
    // MultiDay|MultiRoutes fragments, just for IDs
    suspend fun getRoutesList(): List<Route> =
        dao.getRoutesList()

    private var _routesMenuVisible: Boolean = true
    val routesMenuVisible: Boolean
        get() = _routesMenuVisible
    fun setRoutesMenuVisible(visible: Boolean) {
        _routesMenuVisible = visible
    }

    // Must come before routesFilteredBy, hence the name used
    private var _routeFilterTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val routeFilterTerm: StateFlow<String?>
        get() = _routeFilterTerm
    fun setRouteFilterTerm(filterTerm: String?) {
        _routeFilterTerm.value = filterTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val routesFilteredBy: Flow<List<RouteKt>> =
        _routeFilterTerm.flatMapLatest { filterTerm ->
            dao.getRoutesKtFlow(
                getRoutesQuery(
                    0L, filterTerm,0, SortOrder.ASC
                )
            )
        }

    // Must come before routesSearchedBy, hence the name used
    private var _routeSearchTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val routeSearchTerm: StateFlow<String?>
        get() = _routeSearchTerm
    fun setRouteSearchTerm(searchTerm: String?) {
        _routeSearchTerm.value = searchTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val routesSearchedBy: Flow<List<RouteKt>> =
        _routeSearchTerm.flatMapLatest { searchTerm ->
            dao.getRoutesKtFlow(
                getRoutesQuery(0L, searchTerm,0)
            )
        }

    suspend fun getRoutesByFeature(): List<RouteKt> =
        getRoutesByFeature(_currentFeature, _currentSelection, dao)

    suspend fun getRoutesKtList(sort: Boolean): List<RouteKt> {
        val routes = dao.getRoutesKtList(
            getRoutesQuery(
                0L,null,
                when (sort) {
                    true -> _allListsSortedBy.value
                    else -> -1
                },
                when (sort) {
                    true -> _allListsSortOrder.value
                    else -> SortOrder.NONE
                }
            )
        )
        if (sort) { // No sort reqd for NearbyFragment
            setRoutesKtList(routes) // DetailsFragment
        }
        return routes
    }

    suspend fun getRoutesListAsWaypoints(): List<WaypointKt> {
        val routes = when (multiRouteIndex) {
            in MULTI_DAY_COASTAL_ID..MULTI_DAY_HEADS_ID -> {
                val routesList = mutableListOf<Route>()
                // Sort the multiRouteIds numerically
                val routeIds = multiRouteIds.sorted()
                routeIds.forEach { routeId ->
                    val route = dao.getRouteById(routeId)
                    routesList.add(route)
                }
                routesList
            }
            else -> dao.getRoutesListSorted() // 0 or Int.MAX_VALUE
        }
        return getRoutesListAsWaypoints(routes)
    }

    // STREET ART

    val streetArtKt: Flow<List<StreetArtKt>> =
        dao.getStreetArtFlow(
            getStreetArtQuery(0L,null)
        ).map { items ->
            getStreetArtFullKt(items)
        }

    private var _streetArtItems: List<StreetArtKt> = listOf()
    val streetArtItems: List<StreetArtKt>
        get() = _streetArtItems
    suspend fun getStreetArtItems(
    ) : List<StreetArtKt> {
        // Specify the ID because it might be random (-1)
        val items = dao.getStreetArtList(
            getStreetArtQuery(currentStreetArtId,null)
        )
        setStreetArtItems(items)
        return items
    }
    fun setStreetArtItems(items: List<StreetArtKt>) {
        _streetArtItems = items
    }
    // Must come before streetArtSearchedBy, hence the name used
    private var _streetArtSearchTerm: MutableStateFlow<String?> =
        MutableStateFlow(String())
    val streetArtSearchTerm: StateFlow<String?>
        get() = _streetArtSearchTerm
    fun setStreetArtSearchTerm(searchTerm: String?) {
        _streetArtSearchTerm.value = searchTerm
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val streetArtSearchedBy: Flow<List<StreetArtKt>> =
        _streetArtSearchTerm.flatMapLatest { searchTerm ->
            dao.getStreetArtFlow(
                getStreetArtQuery(0L, searchTerm)
            )
        }
    suspend fun getStreetArtCount(): Int =
        dao.getStreetArtCount()
    suspend fun getStreetArtById(id: Long): StreetArtKt =
        dao.getStreetArtById(
            getStreetArtQuery(id,null)
        )

    // END STREET ART

    private var _subtitle = MutableStateFlow(String())
    val subtitle: StateFlow<String>
        get() = _subtitle
    fun setSubtitle(subtitle: String) {
        _subtitle.value = subtitle
    }

    private var _title = // Debug
        MutableStateFlow(getResources().getString(R.string.app_name))
    val title: StateFlow<String>
        get() = _title
    fun setTitle(title: String) {
        _title.value = title
    }

    suspend fun toggleFavouriteAsync(
        checked: Boolean, itemId: Long, itemType: Int
    ) : Deferred<Pair<Long, Int>> =
        CoroutineScope(Dispatchers.IO).async {
            val result = toggleFavourite(
                checked, dao, itemId, itemType
            )
            val count = dao.getFavouritesCount()
            Pair(result, count)
        }

    // URBAN PLAY

    val urbanPlayItems: Flow<List<UrbanPlayKt>> =
        dao.getUrbanPlayFlow(getUrbanPlayQuery(0L))
    suspend fun getUrbanPlayItemById(id: Long): UrbanPlayKt =
        dao.getUrbanPlayItemById(getUrbanPlayQuery(id))
    suspend fun getUrbanPlayItems(): List<UrbanPlayKt> =
        dao.getUrbanPlayItems(getUrbanPlayQuery(0L))

    // END URBAN PLAY

    suspend fun getWaypointsListByRouteId(
        all: Boolean, id: Long
    ) : List<WaypointKt> {
        val waypoints = dao.getWaypointsListByRouteId(id)
        return getWaypointsWithStartWaypoint(all, dao, id, waypoints)
    }
}
