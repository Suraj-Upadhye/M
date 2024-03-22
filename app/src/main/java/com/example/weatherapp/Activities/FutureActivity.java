package com.example.weatherapp.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.Adapters.FutureAdapter;
import com.example.weatherapp.Domains.FutureDomain;
import com.example.weatherapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FutureActivity extends AppCompatActivity {

    private static final String TAG = FutureActivity.class.getSimpleName();
    private static final String API_KEY = "0076ee81a63b89b07f08185f778da75b";
    private static String CITY_NAME = "Kavathe Ekand";
    private ImageView backButton;

    private ImageView weatherIconTomorrow;
    private TextView temperatureTomorrow;
    private TextView weatherDescriptionTomorrow;

    private TextView pressureText;
    private TextView windSpeedText;
    private TextView humidityText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        weatherDescriptionTomorrow = findViewById(R.id.weatherDescriptionTomorrow);
        temperatureTomorrow = findViewById(R.id.temperatureTomorrow);
        weatherDescriptionTomorrow = findViewById(R.id.weatherDescriptionTomorrow);

        pressureText = findViewById(R.id.pressureText);
        windSpeedText = findViewById(R.id.windSpeedText);
        humidityText = findViewById(R.id.humidityText);


        // Inside NextActivity's onCreate method
        Intent intent = getIntent();

        // Retrieve the city name from the Intent extras
        String cityName = intent.getStringExtra("CITY_NAME_EXTRA");
        CITY_NAME = cityName;
        setVariable();

        TomorrowForecastTask forecastTask = new TomorrowForecastTask();
        forecastTask.execute();


        // Fetch forecast data
        new FetchForecastTask().execute();
    }


    private void setVariable(){
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FutureActivity.this, MainActivity.class));
            }
        });

    }

    private class FetchForecastTask extends AsyncTask<Void, Void, List<FutureDomain>> {


        @Override
        protected List<FutureDomain> doInBackground(Void... voids) {
            List<FutureDomain> futureList = new ArrayList<>();

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


                String temperature = "", pressure = "", humidity = "", windSpeed = "", description = "", iconUrl = "";




                // Iterate through forecast data
                for (int i = 0; i < forecastArray.length(); i += 8) {
                    JSONObject forecastObject = forecastArray.getJSONObject(i);

                    // Extract data
                    String dateTime = forecastObject.getString("dt_txt");

                    // Parse dateTime into a Date object
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = dateFormat.parse(dateTime);

                    // Extract day and month from the parsed Date object
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH); // Note: Month is zero-based, so January is 0

                    // Convert month to month name
                    String[] monthNames = new DateFormatSymbols().getMonths();
                    String monthName = monthNames[month];

                    // Format the date string with only date and month
                    String formattedDate = dayOfMonth + " " + monthName;

                    // Extract other data
                    JSONObject main = forecastObject.getJSONObject("main");
                    String temp = String.valueOf(Math.round(Double.parseDouble(main.getString("temp_max"))));
                    JSONArray weatherArray = forecastObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String status = weatherObject.getString("description");
                    String iconCode = weatherObject.getString("icon");
                    iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

                    // Add to list
                    futureList.add(new FutureDomain(formattedDate, iconUrl, status, temp));
                }
            } catch (IOException | JSONException | ParseException e) {
                Log.e(TAG, "Error fetching or parsing forecast data: " + e.getMessage());
            }

            return futureList;
        }

        @Override
        protected void onPostExecute(List<FutureDomain> futureList) {
            if (!futureList.isEmpty()) {
                // Set up RecyclerView with the forecast data
                RecyclerView recyclerView = findViewById(R.id.view2);
                recyclerView.setLayoutManager(new LinearLayoutManager(FutureActivity.this));
                FutureAdapter adapter = new FutureAdapter(futureList);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "No Forecast Data Available", Toast.LENGTH_SHORT).show();
                // Handle empty forecast data
            }


        }






    }


    public class TomorrowForecastTask extends AsyncTask<Void, Void, ForecastData> {

        private final String TAG = TomorrowForecastTask.class.getSimpleName();
//        private static final String API_KEY = ""; // Replace with your OpenWeatherMap API key
//        private static final String CITY_NAME = "Kavathe Ekand"; // Replace with your city name

        @Override
        protected ForecastData doInBackground(Void... voids) {
            String urlString = "https://api.openweathermap.org/data/2.5/forecast?q=" + CITY_NAME + "&units=metric&appid=" + API_KEY;

            // Get tomorrow's date
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tomorrowDate = dateFormat.format(calendar.getTime());

            try {
                // Create URL object
                URL url = new URL(urlString);

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

                // Iterate through forecast data to find tomorrow's data
                for (int i = 0; i < forecastArray.length(); i++) {
                    JSONObject forecastObject = forecastArray.getJSONObject(i);
                    String dateTime = forecastObject.getString("dt_txt");
                    String[] parts = dateTime.split(" ");
                    String date = parts[0];

                    if (date.equals(tomorrowDate)) {
                        // Extract required information
                        JSONObject main = forecastObject.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        double pressure = main.getDouble("pressure");
                        double humidity = main.getDouble("humidity");

                        JSONObject wind = forecastObject.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");

                        JSONArray weatherArray = forecastObject.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String description = weatherObject.getString("description");

                        // Create ForecastData object
                        ForecastData forecastData = new ForecastData(temperature, description, pressure, windSpeed, humidity);
                        return forecastData; // Return the forecast data
                    }
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching or parsing forecast data: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ForecastData forecastData) {
            if (forecastData != null) {
                // Display Tomorrow forecast information
                temperatureTomorrow.setText((int)forecastData.getTemperature() + "°C");
                weatherDescriptionTomorrow.setText(forecastData.getDescription());
                pressureText.setText(forecastData.getPressure() + "hPa");
                windSpeedText.setText(forecastData.getWindSpeed() + "m/s");
                humidityText.setText(forecastData.getHumidity()+ "%");

//                String toastMessage = "Temperature: " + forecastData.getTemperature() + "°C\n" +
//                        "Description: " + forecastData.getDescription() + "\n" +
//                        "Pressure: " + forecastData.getPressure() + " hPa\n" +
//                        "Wind Speed: " + forecastData.getWindSpeed() + " m/s\n" +
//                        "Humidity: " + forecastData.getHumidity() + " %";
//                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error fetching forecast data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ForecastData {
        private double temperature;
        private String description;
        private double pressure;
        private double windSpeed;
        private double humidity;

        public ForecastData(double temperature, String description, double pressure, double windSpeed, double humidity) {
            this.temperature = temperature;
            this.description = description;
            this.pressure = pressure;
            this.windSpeed = windSpeed;
            this.humidity = humidity;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getDescription() {
            return description;
        }

        public double getPressure() {
            return pressure;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public double getHumidity() {
            return humidity;
        }
    }






}
