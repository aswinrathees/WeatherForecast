package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView selectCity, cityField, updatedField, temperature, minimum_temperature, maximum_temperature, climate_description;
    ProgressBar loader;
    String city = "New York, US";

    String OPEN_WEATHER_MAP_API = "209e949a38ee4a704da34e4172a12d90";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        loader = (ProgressBar) findViewById(R.id.loader);
        selectCity = (TextView) findViewById(R.id.selectCity);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        temperature = (TextView) findViewById(R.id.temperature);
        minimum_temperature = (TextView) findViewById(R.id.minimum_temperature);
        maximum_temperature = (TextView) findViewById(R.id.maximum_temperature);
        climate_description =  (TextView) findViewById(R.id.climate_description);

        taskLoadUp(city);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Change City");
                final EditText input = new EditText(MainActivity.this);
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public void taskLoadUp(String query) {
        if (WeatherFunction.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }



    class DownloadWeather extends AsyncTask< String, Void, String > {

        int is_first_element_added = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility( View.VISIBLE );

        }

        protected String doInBackground(String... args) {
            String xml = WeatherFunction.excuteGet( "http://api.openweathermap.org/data/2.5/forecast?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API );

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            ArrayList<WeatherForecastDetails> weatherForecastDetailsArray = new ArrayList<WeatherForecastDetails>(  );

            try{

                JSONObject weatherForecastObject = null;

                weatherForecastObject = new JSONObject(xml);

                JSONArray weatherDataArray = weatherForecastObject.getJSONArray( "list" );

                JSONObject cityDetailsObject =  weatherForecastObject.getJSONObject( "city" );

                String city = cityDetailsObject.getString( "name" );
                String country = cityDetailsObject.getString( "country" );

                cityField.setText(city+","+country);

                for (int i = 0; i < weatherDataArray.length(); i++) {

                        WeatherForecastDetails weatherForecastDetails = new WeatherForecastDetails();

                        JSONObject listObject = weatherDataArray.getJSONObject(i);

                        JSONObject main = listObject.getJSONObject( "main" );

                        JSONArray weather = listObject.getJSONArray( "weather" );

                        JSONObject weatherObject = weather.getJSONObject( 0 );

                        String date_value = listObject.getString( "dt_txt" );

                        if(is_first_element_added == 0){

                            temperature.setText( String.format( "%.2f", main.getDouble( "temp" ) ) + "°C" );
                            minimum_temperature.setText( String.format( "%.2f", main.getDouble( "temp_min" ) ) + "°C" );
                            maximum_temperature.setText( String.format( "%.2f", main.getDouble( "temp_max" ) ) + "°C" );
                            climate_description.setText( weatherObject.getString( "description" )  );
                            is_first_element_added = 1;
                        }else {
                            weatherForecastDetails.temperature = String.format( "%.2f", main.getDouble( "temp" )  ) + "°C";
                            weatherForecastDetails.maximum_temperature = String.format( "%.2f", main.getDouble( "temp_max" )  ) + "°C";
                            weatherForecastDetails.minimum_temperature = String.format( "%.2f", main.getDouble( "temp_min" )  ) + "°C";
                            weatherForecastDetails.climate = weatherObject.getString( "description" );
                            weatherForecastDetails.weather_date = date_value;

                            weatherForecastDetailsArray.add( weatherForecastDetails );
                        }

                }

                ListWeatherForecastAdapter listWeatherForecastAdapterObject = new ListWeatherForecastAdapter( getBaseContext(),0,weatherForecastDetailsArray );

                ListView weatherListView = (ListView) findViewById( R.id.weather_forecast_list );
                weatherListView.setAdapter( listWeatherForecastAdapterObject );

                loader.setVisibility( View.GONE );

            }catch (JSONException jsonException){
                Toast.makeText( getApplicationContext(), "Error.Your API Key Expired.", Toast.LENGTH_SHORT ).show();
                loader.setVisibility( View.GONE );
            }catch (Exception exception){
                exception.printStackTrace();
            }

        }
    }
}
