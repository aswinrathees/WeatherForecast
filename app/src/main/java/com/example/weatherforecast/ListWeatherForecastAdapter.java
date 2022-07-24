package com.example.weatherforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListWeatherForecastAdapter extends ArrayAdapter<WeatherForecastDetails> {

    TextView temperatureTextView, minimumTemperatureTextView, maximumTemperatureTextView, climate_description, weather_date;

    public ListWeatherForecastAdapter(@NonNull Context context, int resource) {
        super( context, resource );
    }

    public ListWeatherForecastAdapter(@NonNull Context context, int resource, ArrayList<WeatherForecastDetails> weatherForecastDetailsArray) {
        super( context, resource ,weatherForecastDetailsArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WeatherForecastDetails weatherForecastDetails = getItem( position );

        if(convertView == null){
            convertView = LayoutInflater.from( getContext() ).inflate( R.layout.list_weather_forecast_adapter_layout,parent,false );
        }

        temperatureTextView = (TextView) convertView.findViewById(R.id.temperature);
        minimumTemperatureTextView = (TextView) convertView.findViewById(R.id.minimum_temperature);
        maximumTemperatureTextView = (TextView) convertView.findViewById(R.id.maximum_temperature);
        climate_description = (TextView) convertView.findViewById( R.id.climate_description );
        weather_date = (TextView) convertView.findViewById( R.id.weather_date );

        temperatureTextView.setText( weatherForecastDetails.temperature );
        minimumTemperatureTextView.setText( weatherForecastDetails.minimum_temperature );
        maximumTemperatureTextView.setText( weatherForecastDetails.maximum_temperature );
        climate_description.setText( weatherForecastDetails.climate );
        weather_date.setText( weatherForecastDetails.weather_date );

        return convertView;
    }
}
