package ru.netology.mypleyaer.dto

data class Album(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val artist: String = "",
    val published: String = "",
    val genre: String = "",
    val tracks: List<Track> = emptyList()
)

data class Track(
    val id: Int,
    val file: String,
    var playing: Boolean
)