package ru.netology.mypleyaer.adapter

import ru.netology.mypleyaer.dto.Track

interface OnInteractionListener {
    fun onPlayMedia(track: Track)
    fun onPause(track: Track)

}