package ru.netology.mypleyaer.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.mypleyaer.dto.Album
import ru.netology.mypleyaer.dto.Track



interface TrackRepository {
    val data: Flow<List<Track>>
    suspend fun getAlbum(): Flow<Album>
}

