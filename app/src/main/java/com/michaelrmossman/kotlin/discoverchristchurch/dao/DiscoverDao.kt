package com.michaelrmossman.kotlin.discoverchristchurch.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.CONVENIENCES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FOUNTAINS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.STREET_ART_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscoverDao {

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
            "WHERE areaId = :id AND accessible = 2")
    suspend fun getAccessibleCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND accessible = 2")
    suspend fun getAccessibleCountByPlaceId(id: Long): Int

    @Query("SELECT * FROM areas_table WHERE id = :id")
    suspend fun getAreaById(id: Long): Area

    @Query("SELECT * FROM areas_table")
    fun getAreasFlow(): Flow<List<Area>>

    // BATTERY RECYCLERS

    @RawQuery(observedEntities =
        [BatteryRecycler::class, Favourite::class])
    suspend fun getBatteryRecyclerById(
        query: SimpleSQLiteQuery
    ) : BatteryRecyclerKt

    @Query("SELECT COUNT (*) FROM battery_recyclers_table")
    suspend fun getBatteryRecyclersCount(): Int

    @RawQuery(observedEntities =
        [BatteryRecycler::class, Favourite::class])
    fun getBatteryRecyclersFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<BatteryRecyclerKt>>

    @RawQuery(observedEntities =
     [BatteryRecycler::class, Favourite::class]) //
    suspend fun getBatteryRecyclersList(
        query: SimpleSQLiteQuery
    ) : List<BatteryRecyclerKt>

    // BIKE TRACKS

    @RawQuery(observedEntities = [BikeTrack::class, Favourite::class])
    suspend fun getBikeTrackById(query: SimpleSQLiteQuery): BikeTrackKt

    @Query("SELECT * FROM bike_coords_table WHERE trackId = :id")
    suspend fun getBikeCoordsByTrackId(id: Long): List<BikeCoordsKt>

    @Query("SELECT COUNT (*) FROM bike_tracks_table")
    suspend fun getBikeTracksCount(): Int

    @RawQuery(observedEntities = [BikeTrack::class, Favourite::class])
    fun getBikeTracksFlow(query: SimpleSQLiteQuery): Flow<List<BikeTrackKt>>

    @Query("SELECT * FROM bike_tracks_table ORDER BY track")
    suspend fun getBikeTracksList(): List<BikeTrack>

    @RawQuery(observedEntities = [BikeTrack::class, Favourite::class])
    suspend fun getBikeTrackItems(query: SimpleSQLiteQuery): List<BikeTrackKt>

    // END BIKE TRACKS

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
            "WHERE areaId = :id AND accessible = 1")
    suspend fun getBuggyCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND accessible = 1")
    suspend fun getBuggyCountByPlaceId(id: Long): Int

    // CHRISTCHURCH 360
    @Query("SELECT * FROM access_points_table WHERE legId = :id")
    suspend fun getChCh360AccessPointsById(id: Long): List<AccessPoint>

    @Query("SELECT * FROM ch_ch_360_coords_table " +
            "WHERE legId = :id AND trackId = :track")
    suspend fun getChCh360CoordsListByLegId(
        id: Long, track: Int
    ) : List<ChCh360CoordsKt>

    @Query("SELECT * FROM ch_ch_360_table WHERE id = :id")
    suspend fun getChCh360ItemById(id: Long): ChCh360

    @RawQuery(observedEntities =
        [ChCh360::class, Favourite::class])
    suspend fun getChCh360KtItemById(
        query: SimpleSQLiteQuery
    ) : ChCh360Kt

    @Query("SELECT COUNT (*) FROM ch_ch_360_table")
    suspend fun getChCh360ItemsCount(): Int

    @RawQuery(observedEntities =
        [ChCh360::class, Favourite::class])
    fun getChCh360ItemsFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<ChCh360Kt>>

    // Only used for initial import of gpx files
    @Query("SELECT * FROM ch_ch_360_table")
    suspend fun getChCh360List(): List<ChCh360>

    @RawQuery(observedEntities =
        [ChCh360::class, Favourite::class])
    suspend fun getChCh360ItemsList(
        query: SimpleSQLiteQuery
    ) : List<ChCh360Kt>

    // CHRISTCHURCH 360 ... see also INSERT below

    @Query("SELECT * FROM community_items_table")
    fun getCommunityItemsFlow(): Flow<List<CommunityItem>>

    // CONVENIENCES

    @RawQuery(observedEntities =
        [Convenience::class, ConvenienceType::class, Favourite::class])
    suspend fun getConvenienceById(query: SimpleSQLiteQuery): ConvenienceKt

    @Query("SELECT COUNT(*) FROM $CONVENIENCES_TABLE_NAME")
    suspend fun getConveniencesCount(): Int

    @RawQuery(observedEntities =
        [Convenience::class, ConvenienceType::class, Favourite::class])
    fun getConveniencesFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<ConvenienceKt>>

    @RawQuery(observedEntities =
        [Convenience::class, ConvenienceType::class, Favourite::class]) //
    suspend fun getConveniencesList(
        query: SimpleSQLiteQuery
    ) : List<ConvenienceKt>

    @Query("SELECT * FROM convenience_types_table")
    suspend fun getConvenienceTypesList(): List<ConvenienceType>

    // END CONVENIENCES

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE areaId = :id AND dogs = 2")
    suspend fun getDogsOkCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND dogs = 2")
    suspend fun getDogsOkCountByPlaceId(id: Long): Int

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE areaId = :id AND dogs != 0")
    suspend fun getDogsOnLeashCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND dogs != 0")
    suspend fun getDogsOnLeashCountByPlaceId(id: Long): Int

    // DOG PARKS ... see also UPDATE below

    @RawQuery(observedEntities =
        [DogPark::class, DogType::class, Favourite::class])
    suspend fun getDogParkById(query: SimpleSQLiteQuery): DogParkKt

    @Query("SELECT * FROM dog_parks_table WHERE typeId = :typeId")
    suspend fun getDogParksByType(typeId: Long): List<DogPark>

    @Query("SELECT COUNT (*) FROM dog_parks_table")
    suspend fun getDogParksCount(): Int

    @RawQuery(observedEntities =
        [DogPark::class, DogType::class, Favourite::class])
    fun getDogParksFlow(query: SimpleSQLiteQuery): Flow<List<DogParkKt>>

    @Query("SELECT * FROM dog_types_table WHERE id = :id")
    suspend fun getDogParkTypeById(id: Long): DogType

    @Query("SELECT COUNT (*) " +
           "FROM dog_parks_table " +
           "WHERE typeId = :typeId")
    suspend fun getDogParkCountByType(typeId: Long): Int

    @Query("SELECT * FROM dog_types_table")
    suspend fun getDogParkTypes(): List<DogType>

    @Query("SELECT * FROM holes_table " +
           "WHERE holeId = :id")
    suspend fun getEnvironmentHoleListById(
        id: Long
    ) : List<HoleKt>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id,dogParkId,latLng " +
           "FROM dog_environments_table " +
           "WHERE dogParkId = :id") // Note selection
    suspend fun getEnvironmentListByDogParkId(
        id: Long
    ) : List<DogEnvironmentKt>

    // DRINK FOUNTAINS ... see also UPDATE below

    @RawQuery(observedEntities =
        [DrinkFountain::class, DrinkType::class, Favourite::class])
    suspend fun getFountainById(query: SimpleSQLiteQuery): DrinkFountainKt

    @Query("SELECT COUNT(*) FROM $FOUNTAINS_TABLE_NAME")
    suspend fun getFountainsCount(): Int

    @RawQuery(observedEntities =
        [DrinkFountain::class, DrinkType::class, Favourite::class])
    fun getFountainsFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<DrinkFountainKt>>

    @RawQuery(observedEntities =
        [DrinkFountain::class, DrinkType::class, Favourite::class]) //
    suspend fun getFountainsList(
        query: SimpleSQLiteQuery
    ) : List<DrinkFountainKt>

    @Query("SELECT * FROM drink_types_table " +
           "WHERE selected = 1")
    fun getFountainTypesFlow(): Flow<List<DrinkType>>

    @Query("SELECT * FROM drink_types_table")
    suspend fun getFountainTypesList(): List<DrinkType>

    @Query("SELECT * FROM drink_types_table " +
           "WHERE selected = 1")
    suspend fun getFountainTypesSelected(): List<DrinkType>

    // FACILITIES ... see also UPDATE below

    @RawQuery(observedEntities =
        [Facility::class, FacilityType::class, Favourite::class])
    suspend fun getFacilityById(query: SimpleSQLiteQuery): FacilityKt

    @Query("SELECT COUNT (*) FROM facilities_table")
    suspend fun getFacilitiesCount(): Int

    @Query("SELECT COUNT (*) " +
           "FROM facilities_table " +
           "WHERE typeId = :typeId")
    suspend fun getFacilityCountByType(typeId: Long): Int

    @RawQuery(observedEntities =
        [Facility::class, FacilityType::class, Favourite::class])
    fun getFacilitiesFlow(query: SimpleSQLiteQuery): Flow<List<FacilityKt>>

    @RawQuery(observedEntities = [Facility::class, FacilityType::class, Favourite::class]) //
    suspend fun getFacilitiesList(query: SimpleSQLiteQuery): List<FacilityKt>

    @Query("SELECT * FROM facility_types_table WHERE id = :id")
    suspend fun getFacilityTypeById(id: Long): FacilityType

    @Query("SELECT * FROM facility_types_table ORDER BY type")
    suspend fun getFacilityTypes(): List<FacilityType>

    @Query("SELECT COUNT (*) " +
           "FROM facility_types_table " +
           "WHERE selected = 1")
    suspend fun getFacilityTypesSelectedCount(): Int

    @Query("SELECT * FROM facility_types_table " +
           "WHERE selected = 1 LIMIT 1")
    suspend fun getFacilityTypeSelected(): FacilityType

    // FAVOURITES

    @Query("DELETE FROM $FAVOURITES_TABLE_NAME " +
           "WHERE itemId = :itemId")
    suspend fun deleteFavourite(itemId: Long): Int

    @Query("SELECT COUNT (*) FROM $FAVOURITES_TABLE_NAME " +
           "WHERE itemId = :itemId AND itemType = :itemType")
    suspend fun getFavouriteCountByIdAndType(
        itemId: Long, itemType: Int
    ) : Int

    @Query("SELECT * FROM $FAVOURITES_TABLE_NAME")
    fun getFavouritesFlow(): Flow<List<Favourite>>

    @Query("SELECT COUNT (*) FROM $FAVOURITES_TABLE_NAME")
    fun getFavouritesCountFlow(): Flow<Int>

    @Query("SELECT COUNT (*) FROM $FAVOURITES_TABLE_NAME")
    suspend fun getFavouritesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourite): Long

    @Query("DELETE FROM $FAVOURITES_TABLE_NAME")
    suspend fun nukeFavouritesTable(): Int

    // FREE WIFI

    @RawQuery(observedEntities =
        [FreeWiFi::class, Favourite::class])
     suspend fun getFreeWiFiLocationById(
        query: SimpleSQLiteQuery
    ) : FreeWiFiKt

    @Query("SELECT COUNT (*) FROM free_wifi_table")
    suspend fun getFreeWiFiLocationsCount(): Int

    @RawQuery(observedEntities =
        [FreeWiFi::class, Favourite::class])
    fun getFreeWiFiLocationsFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<FreeWiFiKt>>

    @RawQuery(observedEntities =
        [FreeWiFi::class, Favourite::class])
    suspend fun getFreeWiFiLocationsList(
        query: SimpleSQLiteQuery
    ) : List<FreeWiFiKt>

    // FRUIT TREES ... see also UPDATE below

    @Query("SELECT * FROM tree_categories_table " +
           "WHERE id = :id")
    suspend fun getFruitCatById(id: Long): FruitCat

    @Query("SELECT * FROM tree_categories_table")
    suspend fun getFruitCatsAll(): List<FruitCat>

    @Query("SELECT * FROM tree_categories_table " +
           "WHERE selected = 1")
    fun getFruitCatsFlow(): Flow<List<FruitCat>>

    @Query("SELECT * FROM tree_categories_table " +
           "WHERE selected = 1")
    suspend fun getFruitCatsList(): List<FruitCat>

    @RawQuery(observedEntities =
        [FruitTree::class, FruitCat::class, FruitType::class, Favourite::class])
    suspend fun getFruitTreesByTypeId(
        query: SimpleSQLiteQuery
    ) : List<FruitTreeKt>

    @Query("SELECT COUNT (*) FROM fruit_trees_table")
    suspend fun getFruitTreesCount(): Int

    @Query("SELECT COUNT (*) " +
           "FROM fruit_trees_table " +
           "WHERE categoryId = :id")
    suspend fun getFruitTreesCountByCatId(id: Long): Int

    @Query("SELECT COUNT (*) " +
           "FROM fruit_trees_table " +
           "WHERE typeId = :id")
    suspend fun getFruitTreesCountByTypeId(id: Long): Int

    @Query("SELECT * FROM tree_types_table " +
           "WHERE id = :id")
    suspend fun getFruitTypeById(id: Long): FruitType

    @Query("SELECT * FROM tree_types_table " +
           "WHERE categoryId = :id")
    suspend fun getFruitTypesByCatId(id: Long): List<FruitType>

    @RawQuery(observedEntities =
        [FruitCat::class, FruitType::class, Favourite::class])
    suspend fun getFruitTypeByCatId(
        query: SimpleSQLiteQuery
    ) : FruitTypeKt

    @Query("SELECT COUNT (*) FROM tree_types_table")
    suspend fun getFruitTypesCount(): Int

    // HERITAGE SITES ... see also UPDATE below

    @RawQuery(observedEntities =
        [HeritageSite::class, Favourite::class])
    suspend fun getHeritageSiteById(
        query: SimpleSQLiteQuery
    ) : HeritageSiteKt

    @Query("SELECT COUNT (*) FROM heritage_sites_table")
    suspend fun getHeritageSitesCount(): Int

    @RawQuery(observedEntities =
        [HeritageSite::class, HeritageType::class, Favourite::class])
    fun getHeritageSitesFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<HeritageSiteKt>>

    @Query("SELECT COUNT (*) " +
           "FROM heritage_sites_table " +
           "WHERE typeId = :typeId")
    suspend fun getHeritageSiteCountByType(typeId: Long): Int

    @Query("SELECT * FROM heritage_types_table")
    suspend fun getHeritageTypes(): List<HeritageType>

    @Query("SELECT COUNT (*) " +
           "FROM heritage_types_table " +
           "WHERE selected = 1")
    suspend fun getHeritageTypesSelectedCount(): Int

    @Query("SELECT * FROM heritage_types_table " +
           "WHERE selected = 1 LIMIT 1")
    suspend fun getHeritageTypeSelected(): FacilityType

    // PARKS

    @RawQuery(observedEntities =
        [Park::class, ParkType::class, Favourite::class])
    suspend fun getParkById(query: SimpleSQLiteQuery): ParkKt

    @Query("SELECT COUNT (*) " +
           "FROM parks_table " +
           "WHERE typeId = :typeId")
    suspend fun getParkCountByType(typeId: Long): Int

    @Query("SELECT COUNT (*) FROM parks_table")
    suspend fun getParksCount(): Int

    @RawQuery(observedEntities =
        [Park::class, ParkType::class, Favourite::class])
    fun getParksFlow(query: SimpleSQLiteQuery): Flow<List<ParkKt>>

    @RawQuery(observedEntities = [Park::class, ParkType::class, Favourite::class]) //
    suspend fun getParksList(query: SimpleSQLiteQuery): List<ParkKt>

    @Query("SELECT * FROM park_types_table WHERE id = :id")
    suspend fun getParkTypeById(id: Long): ParkType

    @Query("SELECT * FROM park_types_table")
    suspend fun getParkTypes(): List<ParkType>

    @Query("SELECT COUNT (*) " +
           "FROM park_types_table " +
           "WHERE selected != 1")
    suspend fun getParkTypesNotSelectedCount(): Int // Note: NOT

    @Query("SELECT * FROM park_environments_table " +
           "WHERE parkId = :id")
    suspend fun getEnvironmentListByParkId(
        id: Long
    ) : List<ParkEnvironmentKt>

    // END PARKS ... see also UPDATE below

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE areaId = :id AND linkedIds IS NOT NULL")
    suspend fun getLinkedRoutesCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND linkedIds IS NOT NULL")
    suspend fun getLinkedRoutesCountByPlaceId(id: Long): Int

    @Query("SELECT * FROM places_table WHERE id = :id")
    suspend fun getPlaceById(id: Long): Place

    @Query("SELECT COUNT (*) FROM places_table")
    suspend fun getPlacesCount(): Int

    @Query("SELECT COUNT (*) " +
           "FROM places_table " +
           "WHERE areaId = :id")
    suspend fun getPlacesCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE placeId = :id")
    suspend fun getPlacesCountByPlaceId(id: Long): Int

    @Query("SELECT * FROM places_table " +
           "WHERE areaId = :id")
    fun getPlacesFlowByAreaId(id: Long): Flow<List<Place>>

    // ROUTES

    @Query("SELECT * FROM routes_table WHERE id = :id")
    suspend fun getRouteById(id: Long): Route

    @RawQuery(observedEntities = [Route::class, Favourite::class])
    suspend fun getRouteKtById(query: SimpleSQLiteQuery): RouteKt

    @RawQuery(observedEntities = [Route::class, Favourite::class]) //
    fun getRoutesBy(query: SimpleSQLiteQuery): Flow<List<Route>>

    @Query("SELECT COUNT (*) FROM routes_table")
    suspend fun getRoutesCount(): Int

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE areaId = :id")
    suspend fun getRoutesCountByAreaId(id: Long): Int

    @RawQuery(observedEntities = [Route::class, Favourite::class])
    fun getRoutesKtFlow(query: SimpleSQLiteQuery): Flow<List<RouteKt>>

    @Query("SELECT * FROM routes_table")
    suspend fun getRoutesList(): List<Route>

    @Query("SELECT * FROM routes_table ORDER BY route")
    suspend fun getRoutesListSorted(): List<Route>

    @RawQuery(observedEntities = [Route::class, Favourite::class])
    suspend fun getRoutesKtList(query: SimpleSQLiteQuery): List<RouteKt>

    @Query("SELECT SUM(distance) " +
           "FROM routes_table " +
           "WHERE areaId = :id")
    suspend fun getRoutesSumDistanceByAreaId(id: Long): Int

    @Query("SELECT SUM(distance) " +
           "FROM routes_table " +
           "WHERE placeId = :id")
    suspend fun getRoutesSumDistanceByPlaceId(id: Long): Int

    @Query("SELECT SUM(time) " +
           "FROM routes_table " +
           "WHERE areaId = :id")
    suspend fun getRoutesSumTimeByAreaId(id: Long): Int

    @Query("SELECT SUM(time) " +
           "FROM routes_table " +
           "WHERE placeId = :id")
    suspend fun getRoutesSumTimeByPlaceId(id: Long): Int

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE areaId = :id AND shared != 0")
    suspend fun getSharedUseCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND shared != 0")
    suspend fun getSharedUseCountByPlaceId(id: Long): Int

    // STREET ART

    @RawQuery(observedEntities = [StreetArt::class, Favourite::class])
    suspend fun getStreetArtById(query: SimpleSQLiteQuery): StreetArtKt

    @Query("SELECT COUNT(*) FROM $STREET_ART_TABLE_NAME")
    suspend fun getStreetArtCount(): Int

    @RawQuery(observedEntities = [StreetArt::class, Favourite::class])
    fun getStreetArtFlow(query: SimpleSQLiteQuery): Flow<List<StreetArtKt>>

    @Query("SELECT * FROM street_artists_table " +
           "WHERE id = :id") // Called from StreetArtHelpers
    suspend fun getStreetArtistById(id: Long): StreetArtist

    @RawQuery(observedEntities = [StreetArt::class, Favourite::class])
    suspend fun getStreetArtList(query: SimpleSQLiteQuery): List<StreetArtKt>

    // END STREET ART

    @Query("SELECT COUNT (*) " +
           "FROM routes_table " +
           "WHERE areaId = :id AND transport = 0")
    suspend fun getTransportReqdCountByAreaId(id: Long): Int

    @Query("SELECT COUNT (*) " +
            "FROM routes_table " +
            "WHERE placeId = :id AND transport = 0")
    suspend fun getTransportReqdCountByPlaceId(id: Long): Int

    // URBAN PLAY

    @RawQuery(observedEntities = [UrbanPlay::class, Favourite::class])
    fun getUrbanPlayFlow(query: SimpleSQLiteQuery): Flow<List<UrbanPlayKt>>

    @RawQuery(observedEntities = [UrbanPlay::class, Favourite::class])
    suspend fun getUrbanPlayItemById(query: SimpleSQLiteQuery): UrbanPlayKt

    @Query("SELECT COUNT (*) FROM urban_play_table")
    suspend fun getUrbanPlayItemCount(): Int

    @RawQuery(observedEntities = [UrbanPlay::class, Favourite::class])
    suspend fun getUrbanPlayItems(query: SimpleSQLiteQuery): List<UrbanPlayKt>

    @Query("SELECT * FROM waypoints_table WHERE routeId = :id")
    suspend fun getWaypointsListByRouteId(id: Long): List<Waypoint>

    @Insert
    suspend fun insertAllCoordinates(coordinates: List<ChCh360Coords>)

    @RawQuery(observedEntities = [CommunityItem::class])
    fun updateCommunityItemCount(query: SimpleSQLiteQuery): Int

    @Update
    suspend fun updateConvenienceTypesSelected(types: List<ConvenienceType>): Int

    @Update
    suspend fun updateDogTypesSelected(types: List<DogType>): Int

    @Update
    suspend fun updateDrinkTypesSelected(types: List<DrinkType>): Int

    @Update
    suspend fun updateFacilityTypesSelected(types: List<FacilityType>): Int

    @Update
    suspend fun updateFruitCatsSelected(types: List<FruitCat>): Int

    @Update
    suspend fun updateHeritageTypesSelected(types: List<HeritageType>): Int

    @Update
    suspend fun updateParkTypesSelected(types: List<ParkType>): Int
}
