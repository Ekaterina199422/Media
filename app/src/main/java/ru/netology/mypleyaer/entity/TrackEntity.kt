package ru.netology.mypleyaer.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mypleyaer.dto.Track

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val file: String,
    var playing: Boolean
) {
    fun toDto() = Track(
        id,
        file,
        playing
    )
}

fun Track.toEntity() = TrackEntity(
    id,
    file,
    playing
)

fun List<TrackEntity>.toDto(): List<Track> = map(TrackEntity::toDto)
fun List<Track>.toEntity(): List<TrackEntity> = map(Track::toEntity)