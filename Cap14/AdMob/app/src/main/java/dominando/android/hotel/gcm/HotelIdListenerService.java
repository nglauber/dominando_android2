package dominando.android.hotel.gcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dominando.android.hotel.HotelHttp;
import dominando.android.hotel.R;

public class HotelIdListenerService extends InstanceIDListenerService {
    public static final String URL_DO_SERVIDOR = HotelHttp.SERVIDOR + "/gcmserver.php";
    public static final String REGISTRATION_ID = "registrationId";
    public static final String ENVIADO_PRO_SERVIDOR = "enviadoProServidor";
    public static final String EXTRA_REGISTRAR = "registrar";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(EXTRA_REGISTRAR, false)) {
            try {
                if (getRegistrationId() == null) {
                    obterToken();
                } else if (!enviadoProServidor()){
                    enviarRegistrationIdParaServidor(getRegistrationId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        setRegistrationId(null);
        setEnviadoProServidor(false);
        try {
            obterToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void obterToken() throws IOException {
        new Thread(){
            @Override
            public void run() {
                super.run();
                InstanceID instanceID = InstanceID.getInstance(HotelIdListenerService.this);
                try {
                    String token = instanceID.getToken(
                            getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    setRegistrationId(token);
                    enviarRegistrationIdParaServidor(token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void enviarRegistrationIdParaServidor(final String key) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(URL_DO_SERVIDOR);
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestMethod("POST");
                    conexao.setDoOutput(true);
                    OutputStream os = conexao.getOutputStream();
                    os.write(("acao=registrar&regId=" + key).getBytes());
                    os.flush();
                    os.close();
                    conexao.connect();
                    int responseCode = conexao.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        setEnviadoProServidor(true);
                    } else {
                        throw new RuntimeException("Erro ao salvar no servidor");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void setEnviadoProServidor(boolean enviado){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ENVIADO_PRO_SERVIDOR, true);
        editor.apply();
    }
    private boolean enviadoProServidor(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(ENVIADO_PRO_SERVIDOR, false);
    }
    private void setRegistrationId(String regId){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REGISTRATION_ID, regId);
        editor.apply();
    }
    private String getRegistrationId(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(REGISTRATION_ID, null);
    }
}

