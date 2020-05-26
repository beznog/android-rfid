package com.example.rfiddetector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AsyncScan extends AsyncTask<Void, ArrayList<String>, Void> {

    private Context mContext;

    public AsyncScan (Context context){
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Notification.showPopUpWindow(this.mContext, "Start scanning");
    }

    @Override
    protected void onProgressUpdate(ArrayList<String>... values) {
        super.onProgressUpdate(values);

        try {
            JSONObject valuesJSON = new JSONObject();
            JSONArray tagsArr = new JSONArray();

            for (int i=0; i<values[0].size(); i++) {
                tagsArr.put(values[0].get(i));
            }
            valuesJSON.put("itemIds", tagsArr);

            HTTPRequest httpRequest = new HTTPRequest(this.mContext);
            httpRequest.sendPostRequest(
                    Request.Method.POST,
                    httpRequest.hostUrl,
                    httpRequest.hostPort,
                    valuesJSON
            );
            //Notification.showPopUpWindow(this.mContext, "TagIDs: \n" + valuesJSON.toString());
        }
        catch (Exception e) {
            Notification.showPopUpWindow(this.mContext, e.getMessage());
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ArrayList<String> tagsToShow = new ArrayList<String>();

        try{
            RFIDReader reader = new RFIDReader(this.mContext);
            reader.connect();

            for(int i = 0; i < reader.MAX_SCANNING_TIME; i++){
                tagsToShow = reader.explodeMultiReadResponse(reader.multiScanStep());
                publishProgress(tagsToShow);
            }
            TimeUnit.SECONDS.sleep(2);
        }
        catch (Exception e) {
            Log.d("AsyncScan error: ", e.getMessage());
            //Notification.showPopUpWindow(this.mContext, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {
        super.onPostExecute(voids);
        Notification.showPopUpWindow(this.mContext, "End of Scanning");
    }
}
