package com.vsnmobil.valrt.activities;

import android.app.Activity;
//import
import android.content.Intent;
//import
import android.graphics.drawable.AnimationDrawable;
//import
import android.os.Bundle;
//import
import android.os.CountDownTimer;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.widget.Button;
//import
import android.widget.ImageView;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
/**
 * FindMeAnimationActivity.java This Activity will show a circular wave
 * animation (with different color like red,green and yellow) when the user
 * trigger the Find me button in the device dash board.
 */
public class FindMeActivity extends Activity {
    /** The title text view. */
    private TextView titleTextView;
    /** The find me image view. */
    private ImageView findMeImageView;
    /** The back button. */
    private Button backButton;
    /** The totalcountervalue. */
    private int TOTALCOUNTERVALUE = 20000;
    /** The singleunit. */
    private int SINGLEUNIT = 1000;
    /** The data intent. */
    private Intent dataIntent;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findme);

        titleTextView = (TextView) findViewById(R.id.findme_title_textview);
        findMeImageView = (ImageView) findViewById(R.id.findme_imageview);
        backButton = (Button) findViewById(R.id.findme_back_button);
        // To get the value from the previous activity.
        dataIntent = getIntent();
        findMeImageView.setBackgroundResource(R.drawable.animation_list_find_me);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) findMeImageView.getBackground();
        // Start the animation (By default looped).
        frameAnimation.start();
        if (dataIntent.hasExtra(VALRTApplication.FINEMEHEADING))
            // set the MAC address of the puck as heading.
            titleTextView.setText(dataIntent.getStringExtra(VALRTApplication.FINEMEHEADING));

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new CountDownTimer(TOTALCOUNTERVALUE, SINGLEUNIT) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish this activity.
    }
}
