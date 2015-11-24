package co.edu.udea.cmovil.weather;

import android.app.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity {
    EditText city;
    TextView description;
    TextView temp;
    Button button;
    String findCity;
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (EditText) findViewById(R.id.cityText);
        description = (TextView) findViewById(R.id.condDescr);
        temp = (TextView) findViewById(R.id.temp);
        button = (Button) findViewById(R.id.button);
        icon = (ImageView) findViewById(R.id.iconTemp);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherTask task = new WeatherTask();
                findCity = city.getText().toString();
                task.execute(findCity);

            }
        });
    }

    public class WeatherTask extends AsyncTask<String, Void, Void> {
        private static final String TAG = "WeatherTask";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        String data = "";
        byte[] iconData;

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Por favor espere");
            Dialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                data = ((new WeatherHTTPClient()).getWeatherData(params[0]));
                iconData = ((new WeatherHTTPClient()).getImage("04n"));

            } catch (Exception ex) {
                Error = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Dialog.dismiss();
            if (Error != null) {
            } else {
                String OutputData = "";
                Double celsius = 0.0;
                JSONObject jsonResponse;
                //   String iconURL = "http://openweathermap.org/img/w/";

                try {
                    if (iconData != null && iconData.length > 0) {
                        Bitmap img = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                        icon.setImageBitmap(img);
                    }
                    //Se crea el JSONObject con mapeo nombre/valor desde el string JSON obtenido.
                    jsonResponse = new JSONObject(data);

                    //Se lee el nombre de la ciudad.
                    OutputData = jsonResponse.optString("name");
                    city.setText(OutputData);

                    //Se lee la temperatura actual.
                    OutputData = jsonResponse.getJSONObject("main").optString("temp");
                    celsius = Double.parseDouble(OutputData) - 273.15; //Conversión de Kelvin a Celsius
                    temp.setText(celsius + " °C");

                    //Se lee el clima actual y su descripción.
                    OutputData = jsonResponse.getJSONArray("weather").getJSONObject(0).optString("main") + ", " + jsonResponse.getJSONArray("weather").getJSONObject(0).optString("description");
                    description.setText(OutputData);

                    //  OutputData = jsonResponse.getJSONArray("weather").getJSONObject(0).optString("icon");


//                    try {
//                        iconData = ((new WeatherHTTPClient()).getImage(OutputData));
//
//                    } catch (Exception ex) {
//                        Error = ex.getMessage();
//                    }
//                     iconURL = iconURL + OutputData + ".png";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
