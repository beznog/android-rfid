package com.example.rfiddetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.preference.PreferenceManager;
import com.android.volley.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private SharedPreferences preferences;

    private AsyncScan currentScanningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo);
        setSupportActionBar(toolbar);

        this.mContext = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);

        try {
            WebView webview =(WebView)findViewById(R.id.webView);
            webview.setWebViewClient(new WebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
            webview.loadUrl("http://18.224.232.10:8000/list/systems");

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Context context = MainActivity.this;

                        if(currentScanningTask==null || currentScanningTask.isCancelled() || currentScanningTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                            currentScanningTask = new AsyncScan(context);
                            currentScanningTask.execute();
                        }
                        else {
                            currentScanningTask.cancel(true);
                        }
                    }
                    catch (Exception e) {
                        Notification.showPopUpWindow((Activity) mContext, e.getMessage());
                    }
                }
            });
        }
        catch (Exception e) {
            Notification.showPopUpWindow((Activity) this.mContext, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
