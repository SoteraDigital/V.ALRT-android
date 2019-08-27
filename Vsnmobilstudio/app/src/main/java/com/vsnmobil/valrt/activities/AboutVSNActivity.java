package com.vsnmobil.valrt.activities;
//import
import android.app.Activity;
//import
import android.os.Bundle;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.widget.Button;
//import
import com.vsnmobil.valrt.R;
/**
 * AboutVSNActivity.java
 * This class have view to show the content of about VSN.
 */
public class AboutVSNActivity extends Activity {
    /** The back button. */
    private Button backButton;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {//onCreate
        super.onCreate(savedInstanceState);//super
        setContentView(R.layout.activity_aboutvsn);//setContentView
        backButton = (Button) findViewById(R.id.aboutvsn_back_button);//backButton
        // if user click the back button it will finish the current activity and navigate to
        // previous activity.
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// if user click the back button it will finish the current activity and navigate to previous activity.
            }
        });
    }
}
