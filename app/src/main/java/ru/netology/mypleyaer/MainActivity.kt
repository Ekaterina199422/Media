package ru.netology.mypleyaer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.mypleyaer.Api.BASE_URL
import ru.netology.mypleyaer.adapter.OnInteractionListener
import ru.netology.mypleyaer.adapter.TrackAdapter
import ru.netology.mypleyaer.databinding.ActivityMainBinding
import ru.netology.mypleyaer.dto.Track

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: TrackView by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var player: SimpleExoPlayer? = null
    private var playReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private val mediaItemListener: Player.Listener = mediaItemListener()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlayMedia(track: Track) {
                initializePlayer(track)
                binding.playButton.setImageResource(R.drawable.ic_pause_circle_24)
            }

            override fun onPause(track: Track) {
                binding.playButton.setImageResource(R.drawable.ic_play_circle_24)
                stopPlaying()
                binding.tracklist.adapter?.notifyDataSetChanged()
            }
        })
        lifecycleScope.launchWhenStarted {
            viewModel.getAlbum().collectLatest { album ->
                binding.apply {
                    albumName.text = album.title
                    artistName.text = album.artist
                    titlePublished.text = album.published
                    genre.text = album.genre
                    playButton.setOnClickListener {
                        if (player?.isPlaying == false) {
                            initializePlayer(null)
                        } else {
                            stopPlaying()
                            playButton.setImageResource(R.drawable.ic_play_circle_24)
                            viewModel.data.value?.map { it.playing = false }
                            binding.tracklist.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        viewModel.loadTracksExceptionEvent.observe(this, {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(R.string.error_loading)
                .setPositiveButton(R.string.dialog_positive_button) { dialogs, _ ->
                    dialogs.dismiss()
                }
                .create()
                .show()
        })

        binding.tracklist.adapter = adapter
        viewModel.data.observe(this, { tracklist ->
            adapter.submitList(tracklist)
        })
    }

    public override fun onResume() {
        super.onResume()
        initializePlayer(null)
    }

    public override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer(track: Track?) {
        binding.playerView.visibility = View.VISIBLE
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
                val mediaItems = mutableListOf<MediaItem>()
                viewModel.data.value?.forEach { track ->
                    mediaItems.addAll(
                        listOf(MediaItem.fromUri(BASE_URL + track.file))
                    )
                }
                if (track != null) {
                    val mediaItem = MediaItem.fromUri(BASE_URL + track.file)
                    val currentTrack = mediaItems.indexOf(mediaItem)
                    currentWindow = currentTrack
                }
                stopPlaying()
                exoPlayer.addMediaItems(mediaItems)
                exoPlayer.addListener(mediaItemListener)
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.playWhenReady = playReady
                exoPlayer.prepare()
                exoPlayer.repeatMode
            }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            removeListener(mediaItemListener)
            release()
        }
        player = null
    }

    private fun stopPlaying() {
        player?.run {
            release()
        }
    }

    private fun mediaItemListener() = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            lifecycleScope.launch {
                when (reason) {
                    ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                        refreshUI()
                    }
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                        delay(10)
                        if (player?.isPlaying == true) refreshUI()
                    }
                    else -> null
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            when (isPlaying) {
                false -> {
                    viewModel.data.value?.map { it.playing = false }
                    binding.playButton.setImageResource(R.drawable.ic_play_circle_24)
                    binding.tracklist.adapter?.notifyDataSetChanged()
                }
                true -> {
                    binding.playButton.setImageResource(R.drawable.ic_pause_circle_24)
                    refreshUI()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshUI() {
        viewModel.data.value?.let { tracklist ->
            tracklist.map { it.playing = false }
            player?.let { currentWindow = player!!.currentWindowIndex }
            val playingTrack = tracklist.find { it.id == player?.nextWindowIndex }
            playingTrack?.playing = true
            binding.tracklist.adapter?.notifyDataSetChanged()
        }
    }
}


