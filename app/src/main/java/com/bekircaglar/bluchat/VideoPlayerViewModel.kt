package com.bekircaglar.bluchat

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

class VideoPlayerViewModel : ViewModel() {
    var exoPlayer: ExoPlayer? = null
        private set

    var mediaSource: MediaSource? = null
        private set

    @OptIn(UnstableApi::class)
    fun initializePlayer(context: Context, videoUri: String) {
        if (exoPlayer == null) {
            val mediaItem = MediaItem.fromUri(videoUri)
            mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(mediaItem)

            exoPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaSource(mediaSource!!)
                playWhenReady = true
                prepare()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}