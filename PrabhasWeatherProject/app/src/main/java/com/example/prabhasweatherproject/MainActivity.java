package com.example.prabhasweatherproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    TextView hOneT, hTwoT, hThreeT, hFourT, city$latLon;
    ImageView hOneI, hTwoI, hThreeI, hFourI;
    EditText enterZip;
    Button search;
    APIExecution myThread;
    String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hOneT = findViewById(R.id.textView);
        hTwoT = findViewById(R.id.textView5);
        hThreeT = findViewById(R.id.textView6);
        hFourT = findViewById(R.id.textView7);
        hOneI = findViewById(R.id.imageView);
        hTwoI = findViewById(R.id.imageView5);
        hThreeI = findViewById(R.id.imageView6);
        hFourI = findViewById(R.id.imageView7);
        enterZip = findViewById(R.id.enterZip);
        search = findViewById(R.id.search);
        city$latLon = findViewById(R.id.textView8);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipcode = enterZip.getText().toString();
                APIExecution myThread = new APIExecution();
                myThread.execute(zipcode);
            }
        });
    }

    public class APIExecution extends AsyncTask<String, Void, ArrayList<JSONObject>>{

        @Override
        protected ArrayList<JSONObject> doInBackground(String... strings) {
            JSONObject [] jsonObjects = new JSONObject[2];

            try{
                URL gU = new URL("http://api.openweathermap.org/geo/1.0/zip?zip=" + strings[0] + ",US&appid=a0c3ae002e1ed7e6aca7483837055ee9");
                URLConnection gUC = gU.openConnection();
                InputStream gIS = gUC.getInputStream();
                BufferedReader gBR = new BufferedReader(new InputStreamReader(gIS));

                String gD = "";
                String gL = gBR.readLine();
                while(gL != null){
                    gD += gL;
                    gL = gBR.readLine();
                }
                JSONObject gJO = new JSONObject(gD);
                jsonObjects[0] = gJO;

                URL oU = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + gJO.getDouble("lat") + "&lon=" + gJO.getDouble("lon") + "&exclude=minutely,daily,alerts&appid=a0c3ae002e1ed7e6aca7483837055ee9");
                URLConnection oUC = oU.openConnection();
                InputStream oIS = oUC.getInputStream();
                BufferedReader oBR = new BufferedReader(new InputStreamReader(oIS));

                String oD = "";
                String oL = oBR.readLine();
                while(oL != null){
                    oD += oL;
                    oL = oBR.readLine();
                }
                JSONObject oJO = new JSONObject(oD);
                jsonObjects[1] = oJO;

                ArrayList<JSONObject> arrayListJO = new ArrayList<JSONObject>(Arrays.asList(jsonObjects));

                return arrayListJO;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(ArrayList<JSONObject> jsonObjects) {
            super.onPostExecute(jsonObjects);
            try {
                JSONObject g = jsonObjects.get(0);
                JSONObject o = jsonObjects.get(1);

                city$latLon.setText("City: "+g.getString("name")+"\t\t\t(Lat., Lon.): (" + g.getDouble("lat") + ", " + g.getDouble("lon") + ")");

                JSONObject h1 = o.getJSONArray("hourly").getJSONObject(0);
                JSONObject h2 = o.getJSONArray("hourly").getJSONObject(1);
                JSONObject h3 = o.getJSONArray("hourly").getJSONObject(2);
                JSONObject h4 = o.getJSONArray("hourly").getJSONObject(3);

                String time1 = new SimpleDateFormat("hh:mm").format(new java.util.Date((h1.getLong("dt"))*1000));
                String time2 = new SimpleDateFormat("hh:mm").format(new java.util.Date((h2.getLong("dt"))*1000));
                String time3 = new SimpleDateFormat("hh:mm").format(new java.util.Date((h3.getLong("dt"))*1000));
                String time4 = new SimpleDateFormat("hh:mm").format(new java.util.Date((h4.getLong("dt"))*1000));


                hOneT.setText("Time: "+time1+"\t\tTemp: "+(Math.round((h1.getDouble("temp")*9/5 - 459.67)*100)/100.0)+ " °F\t\tDescription: "+h1.getJSONArray("weather").getJSONObject(0).getString("description"));
                hTwoT.setText("Time: "+time2+"\t\tTemp: "+(Math.round((h2.getDouble("temp")*9/5 - 459.67)*100)/100.0)+"\t\tDescription: "+h2.getJSONArray("weather").getJSONObject(0).getString("description"));
                hThreeT.setText("Time: "+time3+"\t\tTemp: "+(Math.round((h3.getDouble("temp")*9/5 - 459.67)*100)/100.0)+"\t\tDescription: "+h3.getJSONArray("weather").getJSONObject(0).getString("description"));
                hFourT.setText("Time: "+time4+"\t\tTemp: "+(Math.round((h4.getDouble("temp")*9/5 - 459.67)*100)/100.0)+"\t\tDescription: "+h4.getJSONArray("weather").getJSONObject(0).getString("description"));

                Picasso.with(MainActivity.this).load("https://openweathermap.org/img/wn/" + h1.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").into(hOneI);
                Picasso.with(MainActivity.this).load("https://openweathermap.org/img/wn/" + h2.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").into(hTwoI);
                Picasso.with(MainActivity.this).load("https://openweathermap.org/img/wn/" + h3.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").into(hThreeI);
                Picasso.with(MainActivity.this).load("https://openweathermap.org/img/wn/" + h4.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").into(hFourI);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}