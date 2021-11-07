package ru.netology.mypleyaer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import ru.netology.mypleyaer.db.AppDb
import ru.netology.mypleyaer.dto.Album
import ru.netology.mypleyaer.repository.TrackRepositoryImpl
import ru.netology.mypleyaer.utils.SingleLiveEvent

class TrackView(application: Application): AndroidViewModel(application) {

    private val repository: TrackRepositoryImpl =
        TrackRepositoryImpl(AppDb.getInstance(application).trackDao())

    val data = repository.data.asLiveData()

    private val _loadTracksExceptionEvent = SingleLiveEvent<Unit>()
    val loadTracksExceptionEvent: LiveData<Unit>
        get() = _loadTracksExceptionEvent

    suspend fun getAlbum(): Flow<Album> =
        repository.getAlbum()
            .catch { e ->
                e.printStackTrace()
                _loadTracksExceptionEvent.call()
            }
}