package co.edu.udea.cmovil.gr11.gcmdemogr11;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    /*public GcmBroadcastReceiver() {
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        //Especifica que GcmIntentService se encargará del intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        //Iniciar el servicio, dejando el dispositivo despierto mientras se esta corriendo.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
