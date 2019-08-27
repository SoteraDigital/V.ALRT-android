package com.vsnmobil.valrt.utils;

import java.io.IOException;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.media.AudioManager;
//import
import android.media.MediaPlayer;
//import
import android.net.Uri;
//import
import android.telephony.PhoneStateListener;
//import
import android.telephony.TelephonyManager;
//import
import com.vsnmobil.valrt.R;
/**
 * PlaySound.java
 *
 * This class is a common class to play,pause,resume and stop the media player.
 *
 */
public class PlaySound {
    /** The media player. */
    private MediaPlayer mediaPlayer;
    /** The audio manager. */
    private AudioManager audioManager;
    /** The ringtoneuri. */
    private Uri ringtoneuri;
    /** The mcontext. */
    private Context mcontext;
    /** The Constant SERVICECMD. */
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    /** The Constant CMDNAME. */
    public static final String CMDNAME = "command";
    /** The Constant CMDPAUSE. */
    public static final String CMDPAUSE = "pause";
    /**
     * Instantiates a new play sound.
     *
     * @param context the context
     * @param isDeviceDisconnected the is device disconnected
     */
    public PlaySound(Context context,boolean isDeviceDisconnected){
        mcontext = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMusicActive()) {
            Intent intent = new Intent(SERVICECMD);
            intent.putExtra(CMDNAME, CMDPAUSE);
            mcontext.sendBroadcast(intent);
        }
        if(isDeviceDisconnected)
            ringtoneuri =  Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.siren);
        else
            ringtoneuri = Utils.getAlertToneUri(context);


        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        // To listen the incoming / outgoing call status.
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber){
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:    //Phone is in Idle State
                        resume();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:  //Phone is Ringing
                        pause();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:  //Call is Received
                        pause();
                        break;
                }

            }
        };
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }
    /**
     * Start.
     */
    //To start the music player
    public void start(){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(mcontext, ringtoneuri);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamVolume(AudioManager.STREAM_RING), 0);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {}

    }
    /**
     * Resume.
     */
    // To resume the music player
    public void resume() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.start();
        } catch (Exception e) {}
    }
    /**
     * Pause.
     */
    // To pause the music player
    public void pause() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying())
                mediaPlayer.pause();
        } catch (IllegalStateException e) {}
    }
    /**
     * Stop.
     */
    // To stop the music player.
    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

        } catch (Exception e) {}
    }
}
