package co.edu.udea.cmovil.weather;

import java.io.*;
import java.net.*;

/**
 * Created by dorian.jaramillo on 21/11/15.
 */

public class WeatherHTTPClient {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?APPID=";
    private static String OPEN_WEATHER_MAP_API_KEY = "1b726bb22987b2c0fbbd4b4dd332a77a";
    private static String IMG_URL = "http://openweathermap.org/img/w/";

    public String getWeatherData(String location) {
        HttpURLConnection conn = null;
        InputStream is = null;
        URL url = null;

        try {
            url = new URL(BASE_URL + OPEN_WEATHER_MAP_API_KEY + "&q=" + location);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            //Se abre la conexión con el servidor remoto
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            //Se lee la respuesta
            StringBuffer buffer = new StringBuffer();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");
            is.close();
            conn.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                conn.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;
    }

    public byte[] getImage(String code) {
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            con = (HttpURLConnection) (new URL(IMG_URL + code + ".png")).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            //con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

}
