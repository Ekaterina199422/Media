package ru.netology.mypleyaer.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.mypleyaer.Api.AlbomApi
import ru.netology.mypleyaer.dao.TrackDao
import ru.netology.mypleyaer.entity.toDto
import ru.netology.mypleyaer.entity.toEntity
import ru.netology.mypleyaer.exceptions.ApiException
import ru.netology.mypleyaer.exceptions.ServerException
import ru.netology.mypleyaer.exceptions.UnknownException
import java.io.IOException

class TrackRepositoryImpl(
    private val dao: TrackDao
) : TrackRepository {
        override val data = dao.getAll()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

        override suspend fun getAlbum() = flow {
            try {
                val response = AlbomApi.service.getAlbum()

                if (!response.isSuccessful) {
                    throw ApiException(response.code(), response.message())
                }

                val body = response.body() ?: throw ApiException(response.code(), response.message())
                dao.insert(body.tracks.toEntity())
                emit(body)
            } catch (e: IOException) {
                throw ServerException
            } catch (e: Exception) {
                throw  UnknownException
            }
        }
    }