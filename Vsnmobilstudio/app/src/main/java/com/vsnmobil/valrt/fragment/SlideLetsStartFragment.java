package com.vsnmobil.valrt.fragment;

import android.content.Intent;
//import
import android.net.Uri;
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
import android.webkit.WebView;
//import
import android.widget.Button;
//import
import android.widget.ImageView;
//import
import com.vsnmobil.valrt.BuildConfig;
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.activities.WelcomeActivity;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * SlideLetsStartFragment.java
 *
 * This class has two buttons to navigate to setup the application and to buy the puck from VSN
 * store.
 */
public class SlideLetsStartFragment extends Fragment implements OnClickListener {
	/** The setup device button. */
	private Button setupDeviceButton;
	/** The get device button. */
	private Button getDeviceButton;
	/** The device web view. */
	private WebView deviceWebView;
	/** The device image view. */
	private ImageView deviceImageView;
	/** The intent. */
	private Intent intent;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lets_start, container, false);
		deviceImageView = (ImageView) view.findViewById(R.id.initial_device_imageview);
		setupDeviceButton = (Button) view.findViewById(R.id.lets_start_setup_mydevice_button);
		setupDeviceButton.setOnClickListener(this);
		getDeviceButton = (Button) view.findViewById(R.id.lets_start_get_a_valrt_button);
		getDeviceButton.setOnClickListener(this);
		deviceWebView = (WebView) view.findViewById(R.id.lets_start_device_webview);
		//If medium size screen show the static puck image instead of animated gif.
		if(Utils.getScreenSize(getActivity()) <= 3){
			deviceWebView.setVisibility(View.GONE);
			deviceImageView.setVisibility(View.VISIBLE);
		}else{
			deviceWebView.loadUrl("file:///android_asset/animated_puck.html");  // changed from loading .gif to .html file because of Galaxy S6 transparency issue
			deviceWebView.setBackgroundColor(0x00000000);
			deviceWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
		return view;
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//Set up the application
			case R.id.lets_start_setup_mydevice_button:
				intent = new Intent(getActivity(), WelcomeActivity.class);
				startActivity(intent);
				getActivity().finishAffinity();
				break;
			//Go to the VSN store to buy puck.
			case R.id.lets_start_get_a_valrt_button:
				// If V.ALRT build variant then enable go to VSN website
				if (BuildConfig.FLAVOR.equals("VALRT")) {
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://store.vsnmobil.com/products/v-alrt"));
				} else {
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://store.vsnmobil.com/products/v-alrt"));
				}
				startActivity(intent);
				break;
		}
	}

}
