package com.michaelrmossman.kotlin.discoverchristchurch.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.communityTableNames
import com.michaelrmossman.kotlin.discoverchristchurch.database.DiscoverDatabase
import com.michaelrmossman.kotlin.discoverchristchurch.repository.CommunityHelpers.getCommunityQuery
import com.michaelrmossman.kotlin.discoverchristchurch.repository.TrackPointHelpers.insertAllTrackPoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    lateinit var discoverDatabase: DiscoverDatabase

    @Provides
    fun provideDiscoverDao(database: DiscoverDatabase): DiscoverDao {
        return database.discoverDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ) : DiscoverDatabase {
        discoverDatabase = Room.databaseBuilder(
            appContext,
            DiscoverDatabase::class.java,
            "discover_database"
        )
        .createFromAsset("databases/discover_database.db")
        .addCallback(object: RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    val dao = discoverDatabase.discoverDao()

                    // Import chCh360 trackPoints from gpx files
                    val chCh360Items = dao.getChCh360List()
                    insertAllTrackPoints(appContext, chCh360Items, dao)

                    // Count/store result for all Community tables
                    for (i in communityTableNames.indices) {
                        val count = getCount(dao, i)
                        val query = getCommunityQuery(count, i)
                        dao.updateCommunityItemCount(query)
                    }
                }
            }
        })
//        .fallbackToDestructiveMigration() // TODO
        .build()

        return discoverDatabase
    }

    private suspend fun getCount(dao: DiscoverDao, index: Int): Int {
        return when (index) {
            0 -> dao.getBatteryRecyclersCount()
            1 -> dao.getFacilitiesCount()
            2 -> dao.getDogParksCount()
            3 -> dao.getFountainsCount()
            4 -> dao.getFreeWiFiLocationsCount()
            5 -> dao.getFruitTreesCount()
            6 -> dao.getHeritageSitesCount()
            7 -> dao.getBikeTracksCount()
            8 -> dao.getConveniencesCount()
            9 -> dao.getParksCount()
            10 -> dao.getStreetArtCount()
            else -> dao.getUrbanPlayItemCount()
        }
    }
}
