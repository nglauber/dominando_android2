package dominando.android.mapas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            Toast.makeText(context, "Erro no serviço de localização: " + errorCode,
                    Toast.LENGTH_LONG).show();
        } else {
            int transicao = geofencingEvent.getGeofenceTransition();
            if (transicao == Geofence.GEOFENCE_TRANSITION_ENTER
                    || transicao == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
                String acao = transicao == 1 ? "Entrou" : "Saiu";
                Toast.makeText(context,
                        "Geofence ID: "+ geofences.get(0).getRequestId() +"  "+ acao +" do perímetro",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context,
                        "Erro no Geofence: " + transicao, Toast.LENGTH_LONG).show();
            }
        }
    }
}

