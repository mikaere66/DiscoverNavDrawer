package com.michaelrmossman.kotlin.discoverchristchurch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelrmossman.kotlin.discoverchristchurch.entities.*
import com.michaelrmossman.kotlin.discoverchristchurch.repository.DiscoverRepository
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_00
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: DiscoverRepository
) : ViewModel() {

    suspend fun getAccessPointsListByChCh360Id(
        id: Long
    ) : List<WaypointKt> =
        repository.getAccessPointsListByChCh360Id(id)

    val areas: LiveData<List<AreaKt>> =
        repository.areas.asLiveData()
    suspend fun getAreaById(id: Long): Area =
        repository.getAreaById(id)

    // BATTERY RECYCLERS

    val batteryRecyclers: LiveData<List<BatteryRecyclerKt>> =
        repository.batteryRecyclers.asLiveData()
    suspend fun getBatteryRecyclerById(id: Long): BatteryRecyclerKt =
        repository.getBatteryRecyclerById(id)
    suspend fun getBatteryRecyclersList(): List<BatteryRecyclerKt> =
        repository.getBatteryRecyclersList()

    // BIKE TRACKS

    // Single BikeTrack Items
    private lateinit var _bikeTrackItem: BikeTrackKt
    val bikeTrackItem: BikeTrackKt
        get() = _bikeTrackItem
    fun setBikeTrackItem(item: BikeTrackKt) {
        _bikeTrackItem = item
    }
    suspend fun getBikeTrackById(id: Long): BikeTrackKt =
        repository.getBikeTrackById(id)

    // BikeTrack Item Lists
    val bikeTracks: LiveData<List<BikeTrackKt>> =
        repository.bikeTracks.asLiveData()
    suspend fun getBikeCoordsByTrackId(
        id: Long
    ) : List<BikeCoordsKt> =
        repository.getBikeCoordsByTrackId(id)

    val bikeTrackItems = repository.bikeTrackItems
    suspend fun getBikeTrackItems(
        persist: Boolean = false
    ) : List<BikeTrackKt> =
        repository.getBikeTrackItems(persist)

    fun setSortBikesBy(index: Int) {
        repository.setSortBikesBy(index)
    }
    fun setSortBikes(ascending: Boolean) {
        repository.setSortBikes(ascending)
    }

    // END BIKE TRACKS

    private var _bottomSheetState =
        MutableStateFlow(BottomSheetBehavior.SAVE_NONE)
    val bottomSheetState: LiveData<Int>
        get() = _bottomSheetState.asLiveData()
    fun setBottomSheetState(state: Int) {
        _bottomSheetState.value = state
    }

    // CHRISTCHURCH 360

    // Single ChCh360 Items
    suspend fun getChCh360ItemById(id: Long): ChCh360Kt =
        repository.getChCh360ItemById(id)
    private lateinit var _chCh360Item: ChCh360Kt
    val chCh360Item: ChCh360Kt
        get() = _chCh360Item
    fun setChCh360Item(item: ChCh360Kt) {
        _chCh360Item = item
    }
    val chCh360ItemId = repository.chCh360ItemId
    fun setChCh360ItemId(id: Long) {
        repository.setChCh360ItemId(id)
    }

    // ChCh360 Item Lists
    val chCh360ItemsKt: LiveData<List<ChCh360Kt>> =
        repository.chCh360ItemsFlow.asLiveData()

    val chCh360Items = repository.chCh360Items
    suspend fun getChCh360Items(
        persist: Boolean = false
    ) : List<ChCh360Kt> = repository.getChCh360Items(persist)
    fun getChCh360ItemsAsWaypoints(
        items: List<ChCh360Kt>
    ) : List<WaypointKt> =
        repository.getChCh360ItemsAsWaypoints(items)
    suspend fun getChCh360ItemsCount(): Int =
        repository.getChCh360ItemsCount()

    suspend fun getChCh360CoordsListByLegId(
        id: Long, track: Int
    ) : List<ChCh360CoordsKt> =
        repository.getChCh360CoordsListByLegId(id, track)

    // COMMUNITY ITEMS

    val communityItems: LiveData<List<CommunityItem>> =
        repository.communityItems.asLiveData()

    // CONVENIENCES

    val conveniences: LiveData<List<ConvenienceKt>> =
        repository.conveniences.asLiveData()
    suspend fun getConvenienceById(
        id: Long
    ) : ConvenienceKt =
        repository.getConvenienceById(id)
    suspend fun getConveniencesCount(): Int =
        repository.getConveniencesCount()
    suspend fun getConveniencesList(): List<ConvenienceKt> =
        repository.getConveniencesList()
    suspend fun getConvenienceTypesList(): List<ConvenienceType> =
        repository.getConvenienceTypesList()
    suspend fun updateConvenienceTypesSelected(
        types: List<ConvenienceType>
    ) : Int = repository.updateConvenienceTypesSelected(types)

    // END CONVENIENCES

    val currentAreaId = repository.currentAreaId
    fun setCurrentAreaId(id: Long) {
        repository.setCurrentAreaId(id)
    }

    val currentBatteryRecyclerId =
        repository.currentBatteryRecyclerId
    fun setCurrentBatteryRecyclerId(id: Long) {
        repository.setCurrentBatteryRecyclerId(id)
    }

    val currentBikeTrackId = repository.currentBikeTrackId
    fun setCurrentBikeTrackId(id: Long) {
        repository.setCurrentBikeTrackId(id)
    }

    val currentConvenienceId = repository.currentConvenienceId
    fun setCurrentConvenienceId(id: Long) {
        repository.setCurrentConvenienceId(id)
    }

    val currentCoords = repository.currentCoords
    fun setCurrentCoords(coords: Coords) {
        repository.setCurrentCoords(coords)
    }

    private lateinit var _currentDogPark: DogParkKt
    val currentDogPark: DogParkKt
        get() = _currentDogPark
    fun setCurrentDogPark(dogPark: DogParkKt) {
        _currentDogPark = dogPark
    }
    val currentDogParkId = repository.currentDogParkId
    fun setCurrentDogParkId(id: Long) {
        repository.setCurrentDogParkId(id)
    }

    val currentFacilityId = repository.currentFacilityId
    fun setCurrentFacilityId(id: Long) {
        repository.setCurrentFacilityId(id)
    }

    fun setCurrentFeature(feature: String) {
        repository.setCurrentFeature(feature)
    }

    val currentFountainId = repository.currentFountainId
    fun setCurrentFountainId(id: Long) {
        repository.setCurrentFountainId(id)
    }

    val currentFreeWiFiId = repository.currentFreeWiFiId
    fun setCurrentFreeWiFiId(id: Long) {
        repository.setCurrentFreeWiFiId(id)
    }

    val currentFruitTypeId = repository.currentFruitTypeId
    fun setCurrentFruitTypeId(id: Long) {
        repository.setCurrentFruitTypeId(id)
    }

    private lateinit var _currentHeritageSite: HeritageSiteKt
    val currentHeritageSite: HeritageSiteKt
        get() = _currentHeritageSite
    fun setCurrentHeritageSite(site: HeritageSiteKt) {
        _currentHeritageSite = site
    }
    val currentHeritageSiteId =
        repository.currentHeritageSiteId
    fun setCurrentHeritageSiteId(id: Long) {
        repository.setCurrentHeritageSiteId(id)
    }

    val currentParkId = repository.currentParkId
    fun setCurrentParkId(id: Long) {
        repository.setCurrentParkId(id)
    }

    val currentPlaceId = repository.currentPlaceId
    fun setCurrentPlaceId(id: Long) {
        repository.setCurrentPlaceId(id)
    }

    private var _currentPolylineId = 0L
    val currentPolylineId: Long
        get() = _currentPolylineId
    fun setCurrentPolylineId(id: Long) {
        _currentPolylineId = id
    }

    private lateinit var _currentRoute: RouteKt
    val currentRoute: RouteKt
        get() = _currentRoute
    fun setCurrentRoute(route: RouteKt) {
        _currentRoute = route
    }
    val currentRouteId = repository.currentRouteId
    fun setCurrentRouteId(id: Long) {
        repository.setCurrentRouteId(id)
    }

    /* These two relate to Search by
       Feature, not routesSearchedBy */
    val currentSearch = repository.currentSearch
    fun setCurrentSearch(search: String) {
        repository.setCurrentSearch(search)
    }
    fun setCurrentSelection(selection: Int) {
        repository.setCurrentSelection(selection)
    }

    val currentStreetArtId = repository.currentStreetArtId
    fun setCurrentStreetArtId(id: Long) {
        repository.setCurrentStreetArtId(id)
    }

    val currentUrbanPlayId = repository.currentUrbanPlayId
    fun setCurrentUrbanPlayId(id: Long) {
        repository.setCurrentUrbanPlayId(id)
    }

    private lateinit var _currentWaypoint: WaypointKt
    val currentWaypoint: WaypointKt
        get() = _currentWaypoint
    fun setCurrentWaypoint(waypoint: WaypointKt) {
        _currentWaypoint = waypoint
    }

    // DOG PARKS

    val dogParks: LiveData<List<DogParkKt>> =
        repository.dogParks.asLiveData()
    val dogParksKt: LiveData<List<DogParkKt>> =
        repository.dogParksKt.asLiveData()
    suspend fun getDogParkById(id: Long): DogParkKt =
        repository.getDogParkById(id)
    suspend fun getDogParksCount(): Int =
        repository.getDogParksCount()
    val dogParksList = repository.dogParksList
    suspend fun getDogParksList(): List<DogParkKt> =
        repository.getDogParksList()
    suspend fun getDogParkTypes(): List<DogTypeKt> =
        repository.getDogParkTypes()
    suspend fun getEnvironmentHoleListById(
        id: Long
    ) : List<HoleKt> =
        repository.getEnvironmentHoleListById(id)
    suspend fun getEnvironmentListByDogParkId(
        id: Long
    ) : List<DogEnvironmentKt> =
        repository.getEnvironmentListByDogParkId(id)
    suspend fun updateDogTypesSelected(
        types: List<DogType>
    ) : Int = repository.updateDogTypesSelected(types)

    /* Must come before dogParksFilteredBy
       in repository, hence name used */
    val dogParkFilterTerm = repository.dogParkFilterTerm
    val dogParksFilteredBy: LiveData<List<DogParkKt>> =
       repository.dogParksFilteredBy.asLiveData()
    fun setDogParkFilterTerm(filterTerm: String?) {
        repository.setDogParkFilterTerm(filterTerm)
    }

    // DRINK FOUNTAINS

    val drinkFountains: LiveData<List<DrinkFountainKt>> =
        repository.drinkFountains.asLiveData()
    suspend fun getFountainById(id: Long): DrinkFountainKt =
        repository.getFountainById(id)
    suspend fun getFountainsCount(): Int =
        repository.getFountainsCount()
    suspend fun getFountainsList(): List<DrinkFountainKt> =
        repository.getFountainsList()
    suspend fun getFountainTypesList(): List<DrinkType> =
        repository.getFountainTypesList()
    suspend fun updateDrinkTypesSelected(
        types: List<DrinkType>
    ) : Int = repository.updateDrinkTypesSelected(types)

    // FACILITIES

    /* Must come before facilitiesFilteredBy
       in repository, hence name used */
    val facilityFilterTerm = repository.facilityFilterTerm
    val facilitiesFilteredBy: LiveData<List<FacilityKt>> =
       repository.facilitiesFilteredBy.asLiveData()
    fun setFacilityFilterTerm(filterTerm: String?) {
        repository.setFacilityFilterTerm(filterTerm)
    }

    /* Must come before facilitiesSearchedBy
       in repository, hence name used */
    val facilitySearchTerm = repository.facilitySearchTerm
    val facilitiesSearchedBy: LiveData<List<FacilityKt>> =
       repository.facilitiesSearchedBy.asLiveData()
    fun setFacilitySearchTerm(searchTerm: String?) {
        repository.setFacilitySearchTerm(searchTerm)
    }

    suspend fun getFacilityById(id: Long): FacilityKt =
        repository.getFacilityById(id)
    suspend fun getFacilitiesCount(): Int =
        repository.getFacilitiesCount()
    suspend fun getFacilitiesList(): List<FacilityKt> =
        repository.getFacilitiesList()
    suspend fun getFacilityTypesSelectedCount() : Int =
        repository.getFacilityTypesSelectedCount()
    suspend fun getFacilityTypeSelected(): FacilityType =
        repository.getFacilityTypeSelected()
    suspend fun getFacilityTypes(): List<FacilityTypeKt> =
        repository.getFacilityTypes()
    suspend fun updateFacilityTypesSelected(
        types: List<FacilityType>
    ) : Int = repository.updateFacilityTypesSelected(types)

    // FAVOURITES

    val faves: LiveData<List<Favourite>> =
        repository.faves.asLiveData()
    val favouritesCount: LiveData<Int> =
        repository.favouritesCount.asLiveData()
    suspend fun nukeFavouritesTable(): Int =
        repository.nukeFavouritesTableAsync().await()

    // FREE WIFI

    val freeWiFiLocations: LiveData<List<FreeWiFiKt>> =
        repository.freeWiFiLocations.asLiveData()
    suspend fun getFreeWiFiLocationById(id: Long): FreeWiFiKt =
        repository.getFreeWiFiLocationById(id)
    suspend fun getFreeWiFiLocations(): List<FreeWiFiKt> =
        repository.getFreeWiFiLocations()

    // FRUIT TREES

    val fruitTypes: LiveData<List<FruitTypeKt>> =
        repository.fruitTypes.asLiveData()
    suspend fun getFruitCatsList(): List<FruitCat> =
        repository.getFruitCatsList()
    suspend fun getFruitCatsListKt(): List<FruitCatKt> =
        repository.getFruitCatsListKt()
    suspend fun getFruitTreesByTypeId(
        id: Long
    ) : List<FruitTreeKt> =
        repository.getFruitTreesByTypeId(id)
    suspend fun getFruitTypeById(id: Long): FruitTypeKt =
        repository.getFruitTypeById(id)
    suspend fun getFruitTypesByCatId(
        id: Long
    ) : List<FruitType> =
        repository.getFruitTypesByCatId(id)
    suspend fun getFruitTypesCount(): Int =
        repository.getFruitTypesCount()
    suspend fun updateFruitCatsSelected(
        types: List<FruitCat>
    ) : Int = repository.updateFruitCatsSelected(types)

    // HERITAGE SITES

    val heritageSites: LiveData<List<HeritageSiteKt>> =
        repository.heritageSites.asLiveData()
    val heritageSitesKt: LiveData<List<HeritageSiteKt>> =
        repository.heritageSitesKt.asLiveData()
    suspend fun getHeritageSiteById(id: Long): HeritageSiteKt =
        repository.getHeritageSiteById(id)
    suspend fun getHeritageSitesCount(): Int =
        repository.getHeritageSitesCount()
    suspend fun getHeritageTypes(): List<HeritageTypeKt> =
        repository.getHeritageTypes()
    suspend fun getHeritageTypesSelectedCount() : Int =
        repository.getHeritageTypesSelectedCount()
    suspend fun getHeritageTypeSelected(): FacilityType =
        repository.getHeritageTypeSelected()
    suspend fun updateHeritageTypesSelected(
        types: List<HeritageType>
    ) : Int = repository.updateHeritageTypesSelected(types)

    // END HERITAGE SITES

    val isNavDrawerOpen: LiveData<Boolean> =
        repository.isNavDrawerOpen.asLiveData()
    fun setIsNavDrawerOpen(isOpen: Boolean) {
        repository.setIsNavDrawerOpen(isOpen)
    }

    /* Dunno why, but conventional val DOESN'T
       work. Only used by InfoDetailFragment */
    val linkedRoute: LiveData<RouteKt> =
        repository.linkedRoute.asLiveData()
    fun setLinkedRoute(route: RouteKt) {
        repository.setLinkedRouteKt(route)
    }

    /* Represents the Coastal Pathway, Crater Rim, &
       Head to Head route IDs, as well as any linked
       routes from DogParksFragment. Also limits list
       to adapter.currentList when going from either
       Results|Routes fragments to DetailsActivity */
    val multiRouteIds = repository.multiRouteIds
    fun setMultiRouteIds(ids: List<Long>) {
        repository.setMultiRouteIds(ids)
    }
    val multiRouteIndex = repository.multiRouteIndex
    fun setMultiRouteIndex(index: Int) {
        repository.setMultiRouteIndex(index)
    }
    suspend fun getMultiLinkedRoutesKt(): List<RouteKt> =
        repository.getMultiLinkedRoutesKt()

    private var _optionalMarkersVisible: Boolean = false
    val optionalMarkersVisible: Boolean
        get() = _optionalMarkersVisible
    fun setOptionalMarkersVisible(visible: Boolean) {
        _optionalMarkersVisible = visible
    }

    val overflowIconIndex: LiveData<Int> =
        repository.overflowIconIndex.asLiveData()
    fun setOverflowIconIndex(index: Int) {
        repository.setOverflowIconIndex(index)
    }

    // PARKS

    /* Must come before parksFilteredBy
       in repository, hence name used */
    val parkFilterTerm = repository.parkFilterTerm
    val parksFilteredBy: LiveData<List<ParkKt>> =
       repository.parksFilteredBy.asLiveData()
    fun setParkFilterTerm(filterTerm: String?) {
        repository.setParkFilterTerm(filterTerm)
    }

    /* Must come before parksSearchedBy
       in repository, hence name used */
    val parkSearchTerm = repository.parkSearchTerm
    val parksSearchedBy: LiveData<List<ParkKt>> =
       repository.parksSearchedBy.asLiveData()
    fun setParkSearchTerm(searchTerm: String?) {
        repository.setParkSearchTerm(searchTerm)
    }

    suspend fun getEnvironmentListByParkId(
        id: Long
    ) : List<ParkEnvironmentKt> =
        repository.getEnvironmentListByParkId(id)

    suspend fun getParkById(id: Long): ParkKt =
        repository.getParkById(id)
    suspend fun getParksCount(): Int =
        repository.getParksCount()
    suspend fun getParksList(): List<ParkKt> =
        repository.getParksList()
    suspend fun getParkTypesNotSelectedCount() : Int =
        repository.getParkTypesNotSelectedCount() // Note: NOT
    suspend fun getParkTypes(): List<ParkTypeKt> =
        repository.getParkTypes()
    suspend fun updateParkTypesSelected(
        types: List<ParkType>
    ) : Int = repository.updateParkTypesSelected(types)

    // END PARKS

    val places: LiveData<List<PlaceKt>> =
        repository.places.asLiveData()
    suspend fun getPlaceById(id: Long): Place =
        repository.getPlaceById(id)
    suspend fun getPlacesCount(): Int =
        repository.getPlacesCount()

    // Single polylines: ExtendedActivity
    private var _polylineVisibility: Boolean = false
    val polylineVisibility: Boolean
        get() = _polylineVisibility
    fun setPolylineVisibility(visible: Boolean) {
        _polylineVisibility = visible
    }

    // Polylines list: RoutesActivity
    private var _polylineIds: List<Long> = listOf()
    val polylineIds: List<Long>
        get() = _polylineIds
    fun setPolylineIds(ids: List<Long>) {
        _polylineIds = ids
    }

    val resetFlippedViews: LiveData<Boolean> =
        repository.resetFlippedViews.asLiveData()
    fun setResetFlippedViews(reset: Boolean) {
        repository.setResetFlippedViews(reset)
    }

    // SINGLE ROUTES
    suspend fun getRouteById(id: Long): Route =
        repository.getRouteById(id) // Just for title in BasicActivity
    suspend fun getRouteKtById(id: Long): RouteKt =
        repository.getRouteKtById(id)

    // ROUTES LISTS
    val routes: LiveData<List<RouteKt>> =
        repository.routes.asLiveData()
    val routesKtList = repository.routesKtList
    fun setSortListsBy(index: Int) {
        repository.setSortListsBy(index)
    }
    fun setSortOrder(ascending: Boolean) {
        repository.setSortOrder(ascending)
    }
    suspend fun getRoutesByFeature(): List<RouteKt> {
        return repository.getRoutesByFeature()
    }
    suspend fun getRoutesCount(): Int =
        repository.getRoutesCount()
    suspend fun getRoutesCountByAreaId(id: Long): Int =
        repository.getRoutesCountByAreaId(id)
     /* Only used by MultiDay|MultiRoutes fragments to
        filter the route IDs for CraterRim/HeadToHead */
    suspend fun getRoutesList(): List<Route> =
        repository.getRoutesList()
    // Details|Nearest fragments
    suspend fun getRoutesKtList(
        sort: Boolean = false
    ) : List<RouteKt> {
        return repository.getRoutesKtList(sort)
    }
    suspend fun getRoutesListAsWaypoints(): List<WaypointKt> =
        repository.getRoutesListAsWaypoints()

    /* Must come before routesFilteredBy
       in repository, hence name used */
    val routeFilterTerm = repository.routeFilterTerm
    val routesFilteredBy: LiveData<List<RouteKt>> =
        repository.routesFilteredBy.asLiveData()
    fun setRouteFilterTerm(filterTerm: String?) {
        repository.setRouteFilterTerm(filterTerm)
    }

    val routesMenuVisible = repository.routesMenuVisible
    fun setRoutesMenuVisible(visible: Boolean) {
        repository.setRoutesMenuVisible(visible)
    }

    /* Must come before routesSearchedBy
       in repository, hence name used */
    val routeSearchTerm = repository.routeSearchTerm
    val routesSearchedBy: LiveData<List<RouteKt>> =
       repository.routesSearchedBy.asLiveData()
    fun setRouteSearchTerm(searchTerm: String?) {
        repository.setRouteSearchTerm(searchTerm)
    }

    // END ROUTES

    // Triggers navigation in Base|Routes activities
    private var _showStreetView =
        MutableStateFlow(false)
    val showStreetView: LiveData<Boolean>
        get() = _showStreetView.asLiveData()
    fun setShowStreetView(show: Boolean) {
        _showStreetView.value = show
    }

    // STREET ART

    // Single StreetArt Items
    private lateinit var _streetArtItem: StreetArtKt
    val streetArtItem: StreetArtKt
        get() = _streetArtItem
    fun setStreetArtItem(item: StreetArtKt) {
        _streetArtItem = item
    }
    suspend fun getStreetArtById(id: Long): StreetArtKt =
        repository.getStreetArtById(id)

    // StreetArt Item Lists
    val streetArtKt: LiveData<List<StreetArtKt>> =
        repository.streetArtKt.asLiveData()
    val streetArtSearchTerm = repository.streetArtSearchTerm
    val streetArtSearchedBy: LiveData<List<StreetArtKt>> =
       repository.streetArtSearchedBy.asLiveData()
    fun setStreetArtSearchTerm(searchTerm: String?) {
        repository.setStreetArtSearchTerm(searchTerm)
    }
    suspend fun getStreetArtCount(): Int =
        repository.getStreetArtCount()
    val streetArtItems = repository.streetArtItems
    suspend fun getStreetArtItems(): List<StreetArtKt> =
        repository.getStreetArtItems()

    // END STREET ART

    val subtitle: LiveData<String> =
        repository.subtitle.asLiveData()
    fun setSubtitle(subtitle: String) {
        repository.setSubtitle(subtitle)
    }

    val title: LiveData<String> =
        repository.title.asLiveData()
    fun setTitle(title: String) {
        repository.setTitle(title)
    }

    // URBAN PLAY

    val urbanPlayItems: LiveData<List<UrbanPlayKt>> =
        repository.urbanPlayItems.asLiveData()
    suspend fun getUrbanPlayItemById(id: Long): UrbanPlayKt =
        repository.getUrbanPlayItemById(id)
    suspend fun getUrbanPlayItems(): List<UrbanPlayKt> =
        repository.getUrbanPlayItems()

    // END URBAN PLAY

    suspend fun toggleFavourite(
        checked: Boolean, itemId: Long,
        itemType: Int, reInit: Boolean = false
    ) : Pair<Long, Int> {
        val result = repository.toggleFavouriteAsync(
            checked, itemId, itemType
        ).await()
        // Remember, result could be a negative value
        if (reInit && result.first != 0L) {
            when (itemType) {
                ITEM_VIEW_TYPE_ITEM_8 -> {
                    val bikeTrack = repository.getBikeTrackById(itemId)
                    setBikeTrackItem(bikeTrack) // Only for BikeDetails
                }
                ITEM_VIEW_TYPE_ITEM_3 -> {
                    val dogPark = repository.getDogParkById(itemId)
                    setCurrentDogPark(dogPark)
                }
                ITEM_VIEW_TYPE_ITEM_00 -> {
                    val chCh360 =
                        repository.getChCh360ItemById(itemId)
                    setChCh360Item(chCh360)
                }
                else -> {
                    // Log.d("HERE", itemId.toString())
                    val route = repository.getRouteKtById(itemId)
                    setCurrentRoute(route)
                }
            }
        }
        return result
    }

    /* Stupid name, but hey, it's at the end
       of the file, where we prefer it to be */
    fun unsetParams() {
        with (repository) {
            // Clear current lists to free up resources
            setBikeTrackItems(listOf())
            setChCh360Items(listOf())
            setDogParksList(listOf())
            setRoutesKtList(listOf())
            setStreetArtItems(listOf())
            // Reset multiRoutesIndex for title/subtitle
            setMultiRouteIndex(Int.MAX_VALUE)
            // Reset multiRouteIds to empty list
            setMultiRouteIds(listOf())
            /* Reset currentAreaId to 0L to infer ALL
               routes, based on _routeSearchTerm */
            // setCurrentAreaId(0L)
            // Reset currentDogParkId to infer ALL dog parks
            setCurrentDogParkId(0L)
            // Reset currentPlaceId, as for currentAreaId
            // setCurrentPlaceId(0L)
            // Reset currentRouteId, as for currentAreaId
            setCurrentRouteId(0L)
            // Reset currentStreetArtId, as for currentAreaId
            setCurrentStreetArtId(0L)
            // Reset facilities filter to ensure NOT library
            setFacilityFilterTerm(null)
            // Reset routes filter to ensure NOT chCh360
            setRouteFilterTerm(null)
            // Reset overflow menu icon type to default
            // setOverflowIconIndex(0)
            // Reset menu visibility to Show All state
            setRoutesMenuVisible(true)
            /* Intentionally does NOT include setRouteSearchTerm()...
               Refer MainActivity.addOnDestinationChangedListener */
        }
    }

    // WAYPOINTS

    suspend fun getWaypointsListByRouteId(
        id: Long, all: Boolean = true
    ) : List<WaypointKt> {
        return repository.getWaypointsListByRouteId(all, id)
    }
}
