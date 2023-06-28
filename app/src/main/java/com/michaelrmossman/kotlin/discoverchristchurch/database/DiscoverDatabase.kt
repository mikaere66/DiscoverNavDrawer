package com.michaelrmossman.kotlin.discoverchristchurch.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.AccessPoint
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Area
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryColor
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecycler
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeCoords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrack
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Coords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.CommunityItem
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Convenience
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogEnvironment
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogPark
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountain
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Facility
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Favourite
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFi
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitCat
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTree
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSite
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Hole
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Park
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkEnvironment
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Place
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Route
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtist
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlay
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Waypoint

@Database(
    entities = [
        AccessPoint::class,
        Area::class,
        BatteryColor::class,
        BatteryRecycler::class,
        BatteryType::class,
        BikeCoords::class,
        BikeTrack::class,
        ChCh360::class,
        CommunityItem::class,
        Convenience::class,
        ConvenienceType::class,
        ChCh360Coords::class,
        DogEnvironment::class,
        DogPark::class,
        DogType::class,
        DrinkFountain::class,
        DrinkType::class,
        Facility::class,
        FacilityType::class,
        Favourite::class,
        FreeWiFi::class,
        FruitCat::class,
        FruitTree::class,
        FruitType::class,
        HeritageSite::class,
        HeritageType::class,
        Hole::class,
        Park::class,
        ParkEnvironment::class,
        ParkType::class,
        Place::class,
        Route::class,
        StreetArt::class,
        StreetArtist::class,
        UrbanPlay::class,
        Waypoint::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [Converters::class])
abstract class DiscoverDatabase: RoomDatabase() {
    abstract fun discoverDao(): DiscoverDao
}
