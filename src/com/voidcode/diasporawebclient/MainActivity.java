package com.voidcode.diasporawebclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.voidcode.diasporawebclient.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends Activity {
	public static final String SETTINGS_FILENAME="settings";
	public static final String defaultPod = "https://diasp.eu"; // This is the default-pod
	public String main_domain;
	public WebView mWeb;
	public ProgressDialog mProgress;
	public SharedPreferences sp_currentpod;
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        NetworkInfo m3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        if (mWifi.isConnected() || m3G.isConnected()) 
	        {
	        	// set the home screen
	        	setContentView(R.layout.main);
	        	// load main domain�s rooturl
	        	SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
	        	this.main_domain = preferences.getString("currentpod", defaultPod); 
	        	
	        	// goto users stream
	        	startDiasporaBrowser("/stream");
	        }
	        else
	        {
	        	// if user don�t internet
	        	setContentView(R.layout.setupinternet);
	        }
	    }
		public void onclick_stream(View v)
		{
			startDiasporaBrowser("/stream");
		}
		public void onclick_share(View v)
		{
			startDiasporaBrowser("/status_messages/new");
		}
		public void onclick_contacts(View v)
		{
			startDiasporaBrowser("/contacts");
		}
		  
		public void onclick_findtag(View v)
		{
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String inputtag = input.getText().toString().trim();
					startDiasporaBrowser("/tags/"+inputtag);
				}
			});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();
		}
		 public void startDiasporaBrowser(String uri)
		 {
			 	mWeb = (WebView) findViewById(R.id.webView_main);
			 	
		       	//setContentView(mWeb);
		        // set Javascript
		        WebSettings settings = mWeb.getSettings();
		        settings.setJavaScriptEnabled(true);
		       
		        //settings.setBuiltInZoomControls(true);
		        
		        // the init state of progress dialog
		        mProgress = ProgressDialog.show(this, "Loading data", "Please wait a moment...");
		        
		        // add a WebViewClient for WebView, which actually handles loading data from web
		        mWeb.setWebViewClient(new WebViewClient() {
		        	// load url
		        	public boolean shouldOverrideUrlLoading(WebView view, String url) 
		        	{
		        		//this see if the user is trying to open a internel or externel link
		        		Pattern pattern = Pattern.compile("^(https?)://"+main_domain.substring(8)+"[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		        		Matcher matcher = pattern.matcher(url);
		        		if (matcher.matches()) //if internel(on main_domain) eks: joindiaspora.com
		        		{  
		        	         view.loadUrl(url);
		        	         return true;  
		        	    } 
		        		else // if user try to open a externel link, then open it in the default webbrowser.
		        		{
		        			 Intent i = new Intent(Intent.ACTION_VIEW);
		        			 i.setData(Uri.parse(url));
		  	               	 startActivity(i);
		  	               	 return true;
		        	    }
		        	}
		        	// when finish loading page
		        	public void onPageFinished(WebView view, String url) {
		        		if(mProgress.isShowing()) {
		        			mProgress.dismiss();
		        		}
		        	}
		        });      
		        // open pages in webview
				mWeb.loadUrl(main_domain+uri);
		    }
		    // Handle the Back button in WebView, to back in history.
		    @Override
		    public boolean onKeyDown(int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_BACK){    
		            if(mWeb.canGoBack()){
		            	mWeb.goBack();
		                return true;
		            }
		        }
		        return super.onKeyDown(keyCode, event); 
		    }
		    //Build the main menu in MainActivity
		    @Override
		    public boolean onCreateOptionsMenu(Menu menu) 
		    {
		        MenuInflater inflater = getMenuInflater();
		        inflater.inflate(R.menu.main_menu, menu);
		        return true;
		    }
		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		    	// Handle item selection
			    switch (item.getItemId()) 
			    {
				    case R.id.mainmenu_share:
				    	startDiasporaBrowser("/status_messages/new");
				        return true;
				    case R.id.mainmenu_settings:
				    	startActivity(new Intent(this, SettingsActivity.class));
				    	return true;
				    case R.id.mainmenu_exit:
				    	finish();
						return true;			
				    default:
				        return super.onOptionsItemSelected(item);
			    }
		    }
}