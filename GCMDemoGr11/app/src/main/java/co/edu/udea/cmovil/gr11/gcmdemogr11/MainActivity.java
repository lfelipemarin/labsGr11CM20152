package co.edu.udea.cmovil.gr11.gcmdemogr11;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {
    EditText editText_user_name;
    EditText editText_email;
    Button button_login;

    static final String TAG = "pavan";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        if (isUserRegistered(context)) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
            finish();
        } else {
            editText_user_name = (EditText) findViewById(R.id.editText_user_name);
            editText_email = (EditText) findViewById(R.id.editText_email);
            button_login = (Button) findViewById(R.id.button_login);
            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendRegistrationIdToBackend();
                }
            });

            //Verificar el APK de play service. si es correcto, proceder con el registro GCM
            if (checkPlayService()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(context);

                if (regid.isEmpty()) {
                    registerInBackground();
                }
            } else {
                Log.i("pavan", "No valid Google Play Service APK found.");
            }
        }
    }

    private boolean checkPlayService() {
        int resutCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resutCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resutCode)) {
                GooglePlayServicesUtil.getErrorDialog(resutCode, this, Util.PLAY_SERVICE_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Util.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        //Verificar si la aplicacion fue actualizada; en ese caso se debe limpiar el ID del registro
        int registeredVersion = prefs.getInt(Util.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private boolean isUserRegistered(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String user_name = prefs.getString(Util.USER_NAME, "");
        if (user_name.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regid = gcm.register(Util.SENDER_ID);
                    msg = "Device registered, registration ID= " + regid;

                    //Se debe enviar el ID de registro hacia el servidor HTTP GoogleCloudMessaging gcm,
                    //para que este pueda utilizar GCM/HTTP o CSS para enviar mensajes a la aplicación.
                    //Persistir el ID de registro para no tener que registrarse de nuevo
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error:" + ex.getMessage();
                }
                return msg;
            }
        }.execute();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Util.PROPERTY_REG_ID, regId);
        editor.putInt(Util.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void storeUserDetails(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Util.USER_NAME, editText_user_name.getText().toString());
        editor.putString(Util.EMAIL, editText_email.getText().toString());
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        //Esta aplicación persiste el ID de registro en las preferencias compartidas.
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void sendRegistrationIdToBackend() {

        new SendGcmToServer().execute();
    }

    private class SendGcmToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = Util.register_url + "?name=" + editText_user_name.getText().toString() + "&email=" + editText_email.getText().toString() + "&regId=" + regid;
            Log.i("pavan", "url: " + url);

            OkHttpClient client_for_getMyFriends = new OkHttpClient();

            String response = null;

            try {
                url = url.replace(" ", "%20");
                response = callOkHttpResquest(new URL(url), client_for_getMyFriends);
                Log.i("pavan", "response: " + response);
                if (response.matches("\nsuccess\n")) {
                    response = "success";
                } else {
                    response = "failure";
                }
            } catch (MalformedURLException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.i("pavan", "response2: " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            //TODO Auto-generated method stub
            super.onPostExecute(result);

            if (result != null) {
                if (result.equals("success")) {
                    storeUserDetails(context);
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                    finish();
                } else {
                    Toast.makeText(context, "Try Again: " + result, Toast.LENGTH_LONG).show();
                    Log.i("pavan", "Try Again: **" + result);
                }
            } else {
                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Peticion HTTP usando OkHttpClient
    String callOkHttpResquest(URL url, OkHttpClient tempClient)
            throws IOException {
        HttpURLConnection connection = tempClient.open(url);

        connection.setConnectTimeout(40000);
        InputStream in = null;
        try {
            //Leer la respuesta
            in = connection.getInputStream();
            byte[] response = readFully(in);
            return new String(response, "UTF-8");
        } finally {
            if (in != null)
                in.close();
        }
    }

    byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }
}