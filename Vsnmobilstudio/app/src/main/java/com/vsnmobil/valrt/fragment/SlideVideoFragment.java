package com.vsnmobil.valrt.fragment;

import android.app.Dialog;
//import
import android.content.Intent;
//import
import android.os.Bundle;
//import
import android.support.v4.app.Fragment;
//import
import android.view.LayoutInflater;
//import
import android.view.View;
//import
import android.view.View.OnClickListener;
//import
import android.view.ViewGroup;
//import
import android.view.Window;
//import
import android.widget.Button;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.activities.VideoPlayerActivity;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * SlideVideoFragment.java
 * To show the tour video of how it works.
 */
public class SlideVideoFragment extends Fragment {
    /** The play text view. */
    private TextView playTextView;
    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide_video, container, false);
        playTextView = (TextView) view.findViewById(R.id.slide_video_play_textview);
        playTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.isNetConnected(getActivity())) {
                    // Navigate to instructional video screen
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    startActivity(intent);
                } else {
                    // A pop will notify the user that no Internet connect is there.
                    alertDialogShow( getResources().getString(R.string.not_internet_connection));
                }
            }
        });
        return view;
    }
    /**
     * To show the alert message that no Internet connection in dialog.
     *
     * @param message which want to show the user.
     */
    public void alertDialogShow(String message) {

        final Dialog noInternetConnectionDialog = new Dialog(getActivity(), R.style.ThemeWithCorners);
        noInternetConnectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        noInternetConnectionDialog.setContentView(R.layout.dialog_info);
        noInternetConnectionDialog.setCancelable(false);

        TextView contentTextView = (TextView) noInternetConnectionDialog
                .findViewById(R.id.info_title_textview);
        Button btn_silentmode = (Button) noInternetConnectionDialog
                .findViewById(R.id.info_ok_button);
        contentTextView.setText(message);

        noInternetConnectionDialog.show();
        btn_silentmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetConnectionDialog.dismiss();
            }
        });
    }
}
