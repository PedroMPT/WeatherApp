package pt.ismai.pedro.appweather;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    EditText cityName;
    TextView resultTextView;

    public void findWeather(View view){

        Log.i("City Name",cityName.getText().toString());

        //Setting keyboard with get INPUT_METHOD_SERVICE that app currently using which is KEYBOARD
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //link editText and hide
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {

            //encode url like san francisco to san%20francisco
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=8f680035d35c279a9846cafb18ecb14d");

        } catch (UnsupportedEncodingException e) {

            Toast.makeText(getApplicationContext(), "Failed3", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        resultTextView = findViewById(R.id.resultTextView);

    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }
                return result;


            } catch (Exception e) {

                e.printStackTrace();

            }
            return "";
        }

        //when background method is completed, return here
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //convert result to JSON
            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                //extract only weather part
                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather Content" , weatherInfo);

                //taking the detail part of weatherinfo with array
                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main;
                    String description;

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != ""){

                        message += main + ": " + description + "\r\n";
                    }
                }

                if (message != ""){

                    resultTextView.setText(message);

                }else {

                    Toast.makeText(getApplicationContext(), "Failed1", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Failed2", Toast.LENGTH_LONG).show();

            }

        }
    }
}
