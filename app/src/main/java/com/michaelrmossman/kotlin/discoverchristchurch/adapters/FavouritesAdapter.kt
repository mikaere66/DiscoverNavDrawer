package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_00
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_01
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_10
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.*

class FavouritesAdapter(
    private val batteryRecyclerListener: BasicClickListener,
    private val batteryRecyclerLongListener: BatteryRecyclerLongListener,
    private val bikeTrackListener: BikeTrackListener,
    private val bikeTrackLongListener: BikeTrackLongListener,
    private val chCh360Listener: ChCh360Listener,
    private val chCh360LongListener: ChCh360LongListener,
    private val convenienceListener: BasicClickListener,
    private val convenienceLongListener: ConvenienceLongListener,
    private val dogParkListener: DogParkListener,
    private val dogParkLongListener: DogParkLongListener,
    private val facilityListener: BasicClickListener,
    private val facilityLongListener: FacilityLongListener,
    private val fountainListener: BasicClickListener,
    private val fountainLongListener: FountainLongListener,
    private val freeWiFiListener: BasicClickListener,
    private val freeWiFiLongListener: FreeWiFiLongListener,
    private val fruitTypeListener: BasicClickListener,
    private val fruitTypeLongListener: FruitTypeLongListener,
    private val heritageSiteListener: HeritageSiteListener,
    private val heritageSiteLongListener: HeritageSiteLongListener,
    private val parkListener: BasicClickListener,
    private val parkLongListener: ParkLongListener,
    private val routeFaveListener: RouteFaveListener,
    private val routeListener: RouteListener,
    private val routeLongListener: RouteLongListener,
    private val streetArtListener: StreetArtListener,
    private val streetArtLongListener: StreetArtLongListener,
    private val urbanPlayListener: UrbanPlayListener,
    private val urbanPlayLongListener: UrbanPlayLongListener
) : ListAdapter<FavesDataItem, RecyclerView.ViewHolder>(FavesDiffCallback()) {

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is BatteryRecyclerViewHolder -> {
                val batteryRecyclerItem = getItem(position) as FavesDataItem.BatteryRecyclerItem
                holder.bind(batteryRecyclerItem.batteryRecycler,
                    batteryRecyclerListener, batteryRecyclerLongListener)
            }
            is BikeTrackViewHolder -> {
                val bikeTrackItem = getItem(position) as FavesDataItem.BikeTrackItem
                holder.bind(bikeTrackItem.bikeTrack,
                    bikeTrackListener, bikeTrackLongListener)
            }
            is ChCh360ViewHolder -> {
                val chCh360Item = getItem(position) as FavesDataItem.ChCh360Item
                holder.bind(chCh360Item.chCh360,
                    chCh360Listener, chCh360LongListener)
            }
            is ConvenienceViewHolder -> {
                val convenienceItem = getItem(position) as FavesDataItem.ConvenienceItem
                holder.bind(convenienceItem.convenience,
                    convenienceListener, convenienceLongListener)
            }
            is DogParkViewHolder -> {
                val dogParkItem = getItem(position) as FavesDataItem.DogParkItem
                holder.bind(dogParkItem.dogPark,
                    dogParkListener, dogParkLongListener)
            }
            is FacilityViewHolder -> {
                val facilityItem = getItem(position) as FavesDataItem.FacilityItem
                holder.bind(facilityItem.facility,
                    facilityListener, facilityLongListener)
            }
            is FountainViewHolder -> {
                val fountainItem = getItem(position) as FavesDataItem.FountainItem
                holder.bind(fountainItem.fountain,
                    fountainListener, fountainLongListener)
            }
            is FreeWiFiViewHolder -> {
                val freeWiFiItem = getItem(position) as FavesDataItem.FreeWiFiItem
                holder.bind(freeWiFiItem.freeWiFi,
                    freeWiFiListener, freeWiFiLongListener)
            }
            is FruitTypeViewHolder -> {
                val fruitTypeItem = getItem(position) as FavesDataItem.FruitTypeItem
                holder.bind(fruitTypeItem.fruitType,
                    fruitTypeListener, fruitTypeLongListener)
            }
            is HeritageSiteViewHolder -> {
                val heritageSiteItem = getItem(position) as FavesDataItem.HeritageSiteItem
                holder.bind(heritageSiteItem.heritageSite,
                    heritageSiteListener, heritageSiteLongListener)
            }
            is ParkViewHolder -> {
                val parkItem = getItem(position) as FavesDataItem.ParkItem
                holder.bind(parkItem.park,
                    parkListener, parkLongListener)
            }
            is RouteViewHolder -> {
                val routeItem = getItem(position) as FavesDataItem.RouteItem
                holder.bind(routeItem.route,
                    routeFaveListener, routeListener, routeLongListener)
            }
            is StreetArtViewHolder -> {
                val streetArtItem = getItem(position) as FavesDataItem.StreetArtItem
                holder.bind(streetArtItem.streetArt,
                    streetArtListener, streetArtLongListener)
            }
            is UrbanPlayViewHolder -> {
                val urbanPlayItem = getItem(position) as FavesDataItem.UrbanPlayItem
                holder.bind(urbanPlayItem.urbanPlay,
                    urbanPlayListener, urbanPlayLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM_01 -> RouteViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_00 -> ChCh360ViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_1 -> BatteryRecyclerViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_2 -> FacilityViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_3 -> DogParkViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_4 -> FountainViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_5 -> FreeWiFiViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_6 -> FruitTypeViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_7 -> HeritageSiteViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_8 -> BikeTrackViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_9 -> ConvenienceViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_10 -> ParkViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_11 -> StreetArtViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM_12 -> UrbanPlayViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FavesDataItem.RouteItem -> ITEM_VIEW_TYPE_ITEM_01
            is FavesDataItem.ChCh360Item -> ITEM_VIEW_TYPE_ITEM_00
            is FavesDataItem.BatteryRecyclerItem -> ITEM_VIEW_TYPE_ITEM_1
            is FavesDataItem.FacilityItem -> ITEM_VIEW_TYPE_ITEM_2
            is FavesDataItem.DogParkItem -> ITEM_VIEW_TYPE_ITEM_3
            is FavesDataItem.FountainItem -> ITEM_VIEW_TYPE_ITEM_4
            is FavesDataItem.FreeWiFiItem -> ITEM_VIEW_TYPE_ITEM_5
            is FavesDataItem.FruitTypeItem -> ITEM_VIEW_TYPE_ITEM_6
            is FavesDataItem.HeritageSiteItem -> ITEM_VIEW_TYPE_ITEM_7
            is FavesDataItem.BikeTrackItem -> ITEM_VIEW_TYPE_ITEM_8
            is FavesDataItem.ConvenienceItem -> ITEM_VIEW_TYPE_ITEM_9
            is FavesDataItem.ParkItem -> ITEM_VIEW_TYPE_ITEM_10
            is FavesDataItem.StreetArtItem -> ITEM_VIEW_TYPE_ITEM_11
            is FavesDataItem.UrbanPlayItem -> ITEM_VIEW_TYPE_ITEM_12
        }
    }
}

sealed class FavesDataItem {
    abstract val id: Long

    data class BatteryRecyclerItem(val batteryRecycler: BatteryRecyclerKt): FavesDataItem() {
        override val id = batteryRecycler.id
    }

    data class BikeTrackItem(val bikeTrack: BikeTrackKt): FavesDataItem() {
        override val id = bikeTrack.id
    }

    data class ChCh360Item(val chCh360: ChCh360Kt): FavesDataItem() {
        override val id = chCh360.id
    }

    data class ConvenienceItem(val convenience: ConvenienceKt): FavesDataItem() {
        override val id = convenience.id
    }

    data class DogParkItem(val dogPark: DogParkKt): FavesDataItem() {
        override val id = dogPark.id
    }

    data class FacilityItem(val facility: FacilityKt): FavesDataItem() {
        override val id = facility.id
    }

    data class FountainItem(val fountain: DrinkFountainKt): FavesDataItem() {
        override val id = fountain.id
    }

    data class FreeWiFiItem(val freeWiFi: FreeWiFiKt): FavesDataItem() {
        override val id = freeWiFi.id
    }

    data class FruitTypeItem(val fruitType: FruitTypeKt): FavesDataItem() {
        override val id = fruitType.id
    }

    data class HeritageSiteItem(val heritageSite: HeritageSiteKt): FavesDataItem() {
        override val id = heritageSite.id
    }

    data class ParkItem(val park: ParkKt): FavesDataItem() {
        override val id = park.id
    }

    data class RouteItem(val route: RouteKt): FavesDataItem() {
        override val id = route.id
    }

    data class StreetArtItem(val streetArt: StreetArtKt): FavesDataItem() {
        override val id = streetArt.id
    }

    data class UrbanPlayItem(val urbanPlay: UrbanPlayKt): FavesDataItem() {
        override val id = urbanPlay.id
    }
}

class FavesDiffCallback: DiffUtil.ItemCallback<FavesDataItem>() {

    override fun areItemsTheSame(oldItem: FavesDataItem, newItem: FavesDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavesDataItem, newItem: FavesDataItem): Boolean {
        return oldItem == newItem
    }
}
