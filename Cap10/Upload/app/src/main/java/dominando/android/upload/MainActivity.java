package dominando.android.upload;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_FOTO = 1;
    EditText mEdtTexto;
    TextView mTxtArquivo;
    Uri mFotoSelecionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdtTexto = (EditText) findViewById(R.id.edtTexto);
        mTxtArquivo = (TextView) findViewById(R.id.txtArquivo);
        findViewById(R.id.btnSelFoto).setOnClickListener(this);
        findViewById(R.id.btnEnviarFoto).setOnClickListener(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            mFotoSelecionada = data.getData();
            mTxtArquivo.setText(mFotoSelecionada.toString());
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSelFoto:
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    selecionarFoto();
                } else {
                    ActivityCompat.requestPermissions( this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                break;
            case R.id.btnEnviarFoto:
                enviarFoto();
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selecionarFoto();
        }
    }
    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FOTO);
    }
    private void enviarFoto() {
        new UploadArquivoTask().execute(
                mEdtTexto.getText().toString(),
                mTxtArquivo.getText().toString()
        );
    }
    class UploadArquivoTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            boolean sucesso = false;
            try {
                UtilHttp.enviarFoto(
                        MainActivity.this,
                        strings[0], // texto
                        strings[1]  // caminho do arquivo
                );
                sucesso = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sucesso;
        }
        @Override
        protected void onPostExecute(Boolean sucesso) {
            super.onPostExecute(sucesso);
            int mensagem = sucesso ?
                    R.string.msg_sucesso :
                    R.string.msg_falha ;
            Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
        }
    }
}
