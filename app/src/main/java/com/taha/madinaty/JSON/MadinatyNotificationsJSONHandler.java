package com.taha.madinaty.JSON;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.taha.madinaty.BusinessEntities.MadinatyNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MadinatyNotificationsJSONHandler extends AsyncTask<String, String, List<MadinatyNotification>> {

    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_PUBLISHDATE = "PublishDate";
    public static final String COLUMN_IMAGEURL = "ImageUrl";
    public static final String COLUMN_IMAGETHUMBURL = "ImageThumbUrl";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_BODY = "Body";
    private static final String TAG = MadinatyNotificationsJSONHandler.class.getSimpleName();
    private final MadinatyNotificationsJSONHandlerClient mClient;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public MadinatyNotificationsJSONHandler(MadinatyNotificationsJSONHandlerClient client) {
        mClient = client;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected List<MadinatyNotification> doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        //String ImagesUrl = Resources.getSystem().getString(com.taha.madinaty.R.string.ImagesURL);
        List<MadinatyNotification> AlrehabNotificationList = new ArrayList<>();
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();

            //JSONObject parentObject = new JSONObject(finalJson);
            // JSONArray parentArray = parentObject.getJSONArray("stories");
            JSONArray parentArray = new JSONArray(finalJson);


            Gson gson = new Gson();
            for (int i = 0; i < parentArray.length(); i++) {

                JSONObject finalObject = parentArray.getJSONObject(i);
                try {
                    int _id = finalObject.getInt(COLUMN_ID);
                    String _title = finalObject.getString(COLUMN_TITLE);
                    String _body = finalObject.getString(COLUMN_BODY);

                    Date _publishdate = dateFormat.parse(finalObject.getString(COLUMN_PUBLISHDATE));
                    String _imageUrl = finalObject.getString(COLUMN_IMAGEURL);
                    String _imageThumbUrl = finalObject.getString(COLUMN_IMAGETHUMBURL);
                    int _type = finalObject.getInt(COLUMN_TYPE);
                    _imageUrl = _imageUrl.replace("../", "http://cms.madinatylife.com/");
                    _imageThumbUrl = _imageThumbUrl.replace("../", "http://cms.madinatylife.com/");
                    AlrehabNotificationList.add(new MadinatyNotification(_id,
                            _title,
                            _publishdate,
                            _imageUrl,
                            _imageThumbUrl,
                            _type,
                            _body));


                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return AlrehabNotificationList;
    }

    @Override
    protected void onPostExecute(List<MadinatyNotification> result) {
        try {
            mClient.onMadinatyNotificationsJSONHandlerClientResult(result);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public interface MadinatyNotificationsJSONHandlerClient {
        void onMadinatyNotificationsJSONHandlerClientResult(List<MadinatyNotification> result);
    }
}