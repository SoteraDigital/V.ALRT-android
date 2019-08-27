package com.vsnmobil.valrt.activities;

import java.net.HttpURLConnection;
//import
import java.net.URL;
//import
import android.app.Activity;
//import
import android.content.res.Configuration;
//import
import android.media.MediaPlayer;
//import
import android.net.Uri;
//import
import android.os.AsyncTask;
//import
import android.os.Bundle;
//import
import android.view.View;
//import
import android.widget.MediaController;
//import
import android.widget.ProgressBar;
//import
import android.widget.VideoView;
//import
import com.vsnmobil.valrt.R;
/**
 * VideoPlayerActivity.java
 * <p/>
 * This Activity will play the instructional video from the remote URL.
 */
public class VideoPlayerActivity extends Activity {
    /** The progress bar. */
    private ProgressBar progressBar;
    /** The video view. */
    private VideoView videoView;
    /** The video position. */
    private int videoPosition;
    /** The initial uri. */
    private Uri initialUri;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        videoView = (VideoView) findViewById(R.id.videoplayer_video_view);
        progressBar = (ProgressBar) findViewById(R.id.videoplayer_progressbar);
        initialUri = Uri.parse(getString(R.string.instruction_video_link));
        // Media controller to play the video in video view.
        MediaController controller = new MediaController(this);
        videoView.setMediaController(controller);
        videoView.requestFocus();
        //Async Task top handle the URL redirect.
        RedirectTraceTask task = new RedirectTraceTask();
        task.execute(initialUri);
        // To show the progress bar before it prepare to play.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
            }
        });
        //If video is finished it will go back to Previous page
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Stop the video play back and navigate back to previous screen.
        if (videoView != null)
            videoView.stopPlayback();
        finish();
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen and continue the video play back
        // from the last seek bar position.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoPosition = videoView.getCurrentPosition();
            videoView.seekTo(videoPosition);
            videoView.start();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            videoPosition = videoView.getCurrentPosition();
            videoView.seekTo(videoPosition);
            videoView.start();
        }
    }
    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.seekTo(videoPosition);
            videoView.start();
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null)
            videoView.stopPlayback();
        finish();
    }
    /**
     * To handle the URL redirects of the video URL.
     */
    public class RedirectTraceTask extends AsyncTask<Uri, Void, Uri> {
        /** The initial uri. */
        private Uri initialUri;

        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        @Override
        protected Uri doInBackground(Uri... params) {
            initialUri = params[0];
            String redirected = null;
            try {
                URL url = new URL(initialUri.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                redirected = connection.getHeaderField("Location");
                return Uri.parse(redirected);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Uri result) {

            if (result != null) {
                videoView.setVideoURI(result);
                videoView.start();
            } else {
                videoView.setVideoURI(initialUri);
                videoView.start();
            }
        }
    }
}
