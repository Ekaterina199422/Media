package ru.netology.mypleyaer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mypleyaer.R
import ru.netology.mypleyaer.databinding.ItemTrackBinding
import ru.netology.mypleyaer.dto.Track


class TrackAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(MarkerDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TrackViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.apply {
                trackName.text = track.file
                playButton.setOnClickListener {
                    when (track.playing) {
                        false -> {
                            onInteractionListener.onPlayMedia(track)
                            track.playing = true
                        }

                        else -> {
                            onInteractionListener.onPause(track)
                            track.playing = false
                        }
                    }
                }
                playButton.setIconResource(
                    when (track.playing) {
                        false -> R.drawable.ic_play_circle_24
                        true -> R.drawable.ic_pause_circle_24
                    }
                )
            }
        }
    }

    class MarkerDiffCallBack : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}