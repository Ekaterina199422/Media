package ru.netology.mypleyaer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.mypleyaer.entity.TrackEntity


@Dao
interface TrackDao {

    @Query("SELECT * FROM TrackEntity ORDER BY id")
    fun getAll(): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: List<TrackEntity>)

    @Query("DELETE FROM TrackEntity WHERE id = :id")
    suspend fun removeById(id: Int)

}