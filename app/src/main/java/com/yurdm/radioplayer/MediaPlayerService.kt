package com.yurdm.radioplayer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver
import com.yurdm.radioplayer.model.Radio


class MediaPlayerService : Service(), OnCompletionListener,
    OnPreparedListener, OnErrorListener, OnSeekCompleteListener, OnInfoListener,
    OnBufferingUpdateListener, OnAudioFocusChangeListener {

    private val NOTIFICATION_ID = 404
    private val NOTIFICATION_DEFAULT_CHANNEL_ID = "default_channel"

    private var current: Radio? = null
    private var player: MediaPlayer? = null
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private lateinit var audioManager: AudioManager
    lateinit var mediaSession: MediaSessionCompat
    var cancelSignal: Boolean = false

    val stateBuilder = PlaybackStateCompat.Builder()
        .setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP
//                    or PlaybackStateCompat.ACTION_PAUSE
//                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
//                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
//                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )

    private fun stopStream() {
        player?.let {
            if (it.isPlaying) {
                cancelSignal = true
                it.stop()
                it.release()
            }
        }

        player = null
    }

    // Binder given to clients
//    private val iBinder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder {
        return PlayerServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()

        val mediaButtonIntent = Intent(
            Intent.ACTION_MEDIA_BUTTON, null, this, MediaButtonReceiver::class.java
        )

        mediaSession = MediaSessionCompat(this, "PlayerService").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setCallback(mediaSessionCallback)
            setSessionActivity(
                PendingIntent.getActivity(
                    this@MediaPlayerService,
                    0,
                    Intent(this@MediaPlayerService, MainActivity::class.java),
                    0
                )
            )
            setMediaButtonReceiver(
                PendingIntent.getBroadcast(this@MediaPlayerService, 0, mediaButtonIntent, 0)
            )
        }

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopStream()

        val radio = intent?.getSerializableExtra("radio") as Radio?

        when (intent?.action) {
            "PLAY" -> {
                if (radio == null) stopSelf()
                else current = radio

                player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(current?.stream)
                    prepareAsync()
                    setOnPreparedListener(this@MediaPlayerService)
                    setOnCompletionListener(this@MediaPlayerService)
                    mediaSessionCallback.onPlay()
                }
            }
            else -> {
                stopSelf()
            }
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent)

        return super.onStartCommand(intent, flags, startId)
    }

    private val mediaSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                val metadata: MediaMetadataCompat = metadataBuilder
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, null)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, current?.title)
                    .build()

                mediaSession.apply {
                    setMetadata(metadata)

                    val audioFocusResult = audioManager.requestAudioFocus(
                        audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                    if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return

                    isActive = true
                    setPlaybackState(
                        stateBuilder.setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F
                        ).build()
                    );
                }

//                playStream(current?.stream.toString())
                refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_PLAYING)
            }

            override fun onStop() {
                stopStream()

                mediaSession.apply {
                    audioManager.abandonAudioFocus(audioFocusChangeListener);
                    isActive = false
                    setPlaybackState(
                        stateBuilder.setState(
                            PlaybackStateCompat.STATE_STOPPED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F
                        ).build()
                    )
                }

                refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_STOPPED)
                stopSelf()
            }
        }

    private val audioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN ->
                    // Фокус предоставлен.
                    // Например, был входящий звонок и фокус у нас отняли.
                    // Звонок закончился, фокус выдали опять
                    // и мы продолжили воспроизведение.
                    mediaSessionCallback.onPlay()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                    // Фокус отняли, потому что какому-то приложению надо
                    // коротко "крякнуть".
                    // Например, проиграть звук уведомления или навигатору сказать
                    // "Через 50 метров поворот направо".
                    // В этой ситуации нам разрешено не останавливать вопроизведение,
                    // но надо снизить громкость.
                    // Приложение не обязано именно снижать громкость,
                    // можно встать на паузу, что мы здесь и делаем.
                    player?.setVolume(.4F, .4F)
                else ->
                    // Фокус совсем отняли.
                    mediaSessionCallback.onStop()
            }
        }

    fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                startForeground(NOTIFICATION_ID, getNotification(playbackState))
            }
            PlaybackStateCompat.STATE_PAUSED -> {

                // На паузе мы перестаем быть foreground, однако оставляем уведомление,
                // чтобы пользователь мог play нажать
                NotificationManagerCompat.from(this)
                    .notify(NOTIFICATION_ID, getNotification(playbackState))
                stopForeground(false)
            }
            else -> {
                // Все, можно прятать уведомление
                stopForeground(true)
            }
        }
    }

    private fun getNotification(playbackState: Int = 0): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Mini Player"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel =
                NotificationChannel(NOTIFICATION_DEFAULT_CHANNEL_ID, name, importance)
            mChannel.setShowBadge(false);
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val controller = mediaSession.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description
        val builder =
            NotificationCompat.Builder(this, NOTIFICATION_DEFAULT_CHANNEL_ID)
                .apply {
                    setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.sessionToken)
                            .setShowActionsInCompactView(1)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this@MediaPlayerService,
                                    PlaybackStateCompat.ACTION_STOP
                                )
                            )
                    )
                    setContentTitle("Radio")
//                    setContentText(description?.subtitle)
//                    setSubText(description?.description)
//                    setLargeIcon(description?.iconBitmap)
                    setContentIntent(controller.sessionActivity)
                    setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MediaPlayerService,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
                    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    addAction(
                        NotificationCompat.Action(
                            R.drawable.ic_stop,
                            "Stop",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this@MediaPlayerService,
                                PlaybackStateCompat.ACTION_STOP
                            )
                        )
                    )
                }
        return builder.build()
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        if (cancelSignal) {
            mp.release()
            cancelSignal = false
        } else {
            // TODO: Show progress
        }
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    override fun onCompletion(mp: MediaPlayer) {
        //Invoked when playback of a media source has completed.
        mediaSessionCallback.onStop()
    }

    //Handle errors
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //Invoked when there has been an error during an asynchronous operation.
        return false
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //Invoked to communicate some info.
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
    }

    override fun onSeekComplete(mp: MediaPlayer) {
        //Invoked indicating the completion of a seek operation.
    }

    override fun onAudioFocusChange(focusChange: Int) {
        //Invoked when the audio focus of the system is updated.
    }

    inner class PlayerServiceBinder : Binder() {
        val mediaSessionToken: MediaSessionCompat.Token
            get() = mediaSession.sessionToken
    }
}