package dominando.android.sms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SMS = 1;
    private static String EXTRA_PERMISSAO = "pediu";
    private static final String ACAO_ENVIADO = "sms_enviado";
    private static final String ACAO_ENTREGUE = "sms_entregue";

    private EditText mEdtNumero;
    private EditText mEdtMensagem;
    private EnvioSmsReceiver mReceiver;

    private boolean mPediuPermissao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        mEdtNumero = (EditText) findViewById(R.id.edtNumeroTelefone);
        mEdtMensagem = (EditText) findViewById(R.id.edtMensagem);

        TelephonyManager tm = (TelephonyManager)
                getSystemService(TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            Toast.makeText(this, "Dispositivo não suporta SMS",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        if (savedInstanceState != null){
            mPediuPermissao = savedInstanceState.getBoolean(EXTRA_PERMISSAO);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_PERMISSAO, mPediuPermissao);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS);
            mPediuPermissao = true;

        } else {
            mReceiver = new EnvioSmsReceiver();
            registerReceiver(mReceiver, new IntentFilter(ACAO_ENVIADO));
            registerReceiver(mReceiver, new IntentFilter(ACAO_ENTREGUE));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        boolean success = true;
        for (int i = 0; i < permissions.length; i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                success = false;
                break;
            }
        }
        mPediuPermissao = false;
        if (!success){
            Toast.makeText(this, "Você necessita aceitar as permissões.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void enviarSmsClick(View v) {
        PendingIntent pitEnviado = PendingIntent.getBroadcast(
                this, 0, new Intent(ACAO_ENVIADO), 0);
        PendingIntent pitEntregue = PendingIntent.getBroadcast(
                this, 0, new Intent(ACAO_ENTREGUE), 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                mEdtNumero.getText().toString(),
                null,
                mEdtMensagem.getText().toString(),
                pitEnviado,
                pitEntregue);
    }

    public static class EnvioSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mensagem = null;
            String acao = intent.getAction();
            int resultado = getResultCode();
            if (resultado == Activity.RESULT_OK) {
                if (ACAO_ENVIADO.equals(acao)) {
                    mensagem = "Enviado com sucesso.";
                } else if (ACAO_ENTREGUE.equals(acao)) {
                    mensagem = "Entregue com sucesso.";
                }
            } else {
                if (ACAO_ENVIADO.equals(acao)) {
                    mensagem = "Falha ao enviar: " + resultado;
                } else if (ACAO_ENTREGUE.equals(acao)) {
                    mensagem = "Falhar ao entregar: " + resultado;
                }
            }
            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
        }
    }

}
