package com.example.weatherapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;


import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Intent;
import android.text.InputType;
import android.content.Context;
import android.widget.Toast;


import com.example.weatherapp.Adapters.HourlyAdapters;
import com.example.weatherapp.Domains.Hourly;
import com.example.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_KEY = "0076ee81a63b89b07f08185f778da75b";
    private static String CITY_NAME = "Kavathe Ekand";
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;

    private EditText locationEntered;
    private Button searchButton;
    private TextView cityName;
    private TextView weatherDescription;
    private ImageView weatherIcon;
    private TextView dayMonthDateTime;
    private TextView temperature;
    private TextView highLowTemperature;
    private TextView pressureText;
    private TextView windSpeedText;
    private TextView humidityText;
    private TextView nextBtn;
    private TextView HighLowTemperature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//       Log.d("MainActivity", "onCreate: Start");

//        initRecyclerView();
//       Log.d("MainActivity", "onCreate: End");

        locationEntered = findViewById(R.id.locationEntered);
        searchButton = findViewById(R.id.searchButton);
        cityName = findViewById(R.id.cityName);

        weatherDescription = findViewById(R.id.weatherDescription);
        weatherIcon = findViewById(R.id.weatherIcon);

        dayMonthDateTime = findViewById(R.id.dayMonthDateTime);
        HighLowTemperature = findViewById(R.id.highLowTemperature);
        pressureText = findViewById(R.id.pressureText);

        temperature = findViewById(R.id.temperature);
        humidityText = findViewById(R.id.humidityText);
        windSpeedText = findViewById(R.id.windSpeedText);
        setVariable();

        new FetchWeatherTask().execute();
        new FetchForecastTask().execute();
    }


    private void setVariable() {
        cityName = findViewById(R.id.cityName);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationName = locationEntered.getText().toString();
                if (!locationName.equals("")) {
                    CITY_NAME = locationName;
                    new FetchWeatherTask().execute();
                    new FetchForecastTask().execute();
                    hideKeyboard();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Location", Toast.LENGTH_SHORT).show();
                    CITY_NAME = "Sangli";
                }
            }
        });
        TextView next7dayBtn = findViewById(R.id.nextBtn);
        next7dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NextActivity
                Intent intent = new Intent(MainActivity.this, FutureActivity.class);

                // Put the city name as an extra in the Intent
                intent.putExtra("CITY_NAME_EXTRA", CITY_NAME);

                // Start the NextActivity
                startActivity(intent);
//                startActivity(new Intent(MainActivity.this, FutureActivity.class));
            }
        });
    }


    // Method to hide the keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locationEntered.getWindowToken(), 0);
    }


    private String formatDate(long timestamp) {
        // Convert timestamp to milliseconds
        Date date = new Date(timestamp * 1000);
        // Define date format
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        // Format the date
        return sdf.format(date);
    }


    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String response = null;

            try {
                // Construct URL for OpenWeatherMap API
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + CITY_NAME + "&units=metric&appid=" + API_KEY);

                // Create connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Get input stream
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // Read response into StringBuilder
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                // Close streams
                reader.close();
                inputStream.close();

                // Disconnect
                connection.disconnect();

                // Convert StringBuilder to String
                response = stringBuilder.toString();

            } catch (IOException e) {
                Log.e("TAG", "Error fetching data: " + e.getMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response);

                    // Extract weather information of main
                    JSONObject main = jsonResponse.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");
                    double pressure = main.getDouble("pressure");

                    // Extract weather information of wind
                    JSONObject wind = jsonResponse.getJSONObject("wind");
                    double wind_speed = wind.getDouble("speed");

                    // Extract name of city
                    String name = jsonResponse.getString("name");


                    // Extract Current date from object
                    long unixTimestamp = jsonResponse.getLong("dt"); // Assuming "dt" is the key for the current date

                    // Format current date and Extracting meaningful inforamtion
                    Date currentDate = (new Date(unixTimestamp * 1000l));  // Convert seconds to milliseconds


                    // Create a SimpleDateFormat object to format the date
                    SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a");

                    // Format the current date to get the desired output
                    String formattedDate = sdf2.format(currentDate);

                    // Extracting individual components
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

                    String dayOfWeek = dayFormat.format(currentDate);
                    String month = monthFormat.format(currentDate);
                    String date = dateFormat.format(currentDate);
                    String time = timeFormat.format(currentDate);
                    String daymonthdatetime = dayOfWeek + " " + month + " " + date + " | " + time;


                    // Extract weather information array
                    JSONArray weatherArray = jsonResponse.getJSONArray("weather");

                    // Get the first item from the array (assuming it contains the current weather)
                    JSONObject weatherObject = weatherArray.getJSONObject(0);

                    // Extract the description
                    String description = weatherObject.getString("description");

                    // Extract the icon code
                    String iconCode = weatherObject.getString("icon");

                    // Construct the URL for the icon
                    String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

                    // Download and display the icon using Picasso
                    Picasso.get()
                            .load(iconUrl)
                            .resize(300, 300) // Resize the image to 300x300 pixels
                            .onlyScaleDown() // Only resize if the original image is larger than the target size
                            .into(weatherIcon);


                    //1 Kelvin = -273.15 Celsius i.e 273.15 Kelvin = 0 Celsius
                    // Update UI


                    cityName.setText(name);

                    weatherDescription.setText(description);


                    dayMonthDateTime.setText(daymonthdatetime);
                    temperature.setText(String.valueOf(Math.round(temp)) + "°C");
                    pressureText.setText(String.valueOf(pressure) + "hPa");
                    windSpeedText.setText(String.valueOf(wind_speed) + "m/s");
                    humidityText.setText(String.valueOf(humidity) + "%");
                    locationEntered.setText("");
                } catch (JSONException e) {
                    Log.e("TAG", "Error parsing JSON: " + e.getMessage());
                }
            } else {
                Log.e("TAG", "No response received from server");
            }
        }
    }





    private class FetchForecastTask extends AsyncTask<Void, Void, List<Hourly>> {

        @Override
        protected List<Hourly> doInBackground(Void... voids) {
            List<Hourly> hourlyList = new ArrayList<>();

            try {
                // Construct URL for OpenWeatherMap API forecast endpoint
                URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?q=" + CITY_NAME + "&units=metric&appid=" + API_KEY);

                // Create connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Get input stream
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // Read response into StringBuilder
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                // Close streams
                reader.close();
                inputStream.close();

                // Disconnect
                connection.disconnect();

                // Convert StringBuilder to String
                String response = stringBuilder.toString();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray forecastArray = jsonResponse.getJSONArray("list");

                // Get today's date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String todayDate = dateFormat.format(calendar.getTime());

                // Iterate through forecast data
                for (int i = 0; i < forecastArray.length(); i++) {
                    JSONObject forecastObject = forecastArray.getJSONObject(i);

                    // Extract date and time
                    String dateTime = forecastObject.getString("dt_txt");
                    String[] parts = dateTime.split(" ");
                    String date = parts[0];
                    String time = parts[1].substring(0, 5); // Extract only time without seconds

                    // If it's today's forecast, extract temperature and icon
                    if (date.equals(todayDate)) {
                        JSONObject main = forecastObject.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        double celsiusTemperature = Math.round(temperature);

                        // Extract weather icon
                        JSONArray weatherArray = forecastObject.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String iconCode = weatherObject.getString("icon");
                        String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

                        // Add hourly forecast to the list
                        hourlyList.add(new Hourly(time, String.format("%.0f", celsiusTemperature) + "°C", iconUrl));
                        String highLowTemperature = findHighLowTemperature(hourlyList);
                        HighLowTemperature.setText(highLowTemperature);
                    }
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching or parsing forecast data: " + e.getMessage());
            }

            return hourlyList;
        }

        @Override
        protected void onPostExecute(List<Hourly> hourlyList) {
            if (!hourlyList.isEmpty()) {
                // Set up RecyclerView with the hourly forecast data
                RecyclerView recyclerView = findViewById(R.id.view1);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                HourlyAdapters adapter = new HourlyAdapters(hourlyList);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "No Forecast Data Available", Toast.LENGTH_SHORT).show();
                // Handle empty forecast data
            }
        }



        private String findHighLowTemperature(List<Hourly> hourlyList) {
            // Initialize variables for high and low temperatures
            double maxTemp = Double.MIN_VALUE;
            double minTemp = Double.MAX_VALUE;

            // Iterate through hourly forecast data
            for (Hourly hourly : hourlyList) {
                // Extract temperature from hourly forecast
                double temperature = Double.parseDouble(hourly.getTemp().replace("°C", "").trim());

                // Update maximum temperature if necessary
                if (temperature > maxTemp) {
                    maxTemp = temperature;
                }

                // Update minimum temperature if necessary
                if (temperature < minTemp) {
                    minTemp = temperature;
                }
            }

           String maTemp =String.format("%.0f",maxTemp);
           String miTemp = String.format("%.0f",minTemp);

            // Construct and return the formatted string
            return "H: " + maTemp + "°C | L: " + miTemp + "°C";
        }




    }


}

