package dominando.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class Tela2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela2);
        TextView txt = (TextView)findViewById(R.id.txtTexto);
        Intent it = getIntent();
        Cliente cliente = it.getParcelableExtra("cliente");
        if (cliente != null) {
            String texto = String.format("Nome: %s / Código: %d",
                    cliente.nome, cliente.codigo);
            txt.setText(texto);
        } else {
            String nome = it.getStringExtra("nome");
            int idade = it.getIntExtra("idade", -1);
            txt.setText(String.format("Nome: %s / Idade: %d", nome, idade));
        }
        Log.i("NGVL", "Tela2::onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("NGVL", "Tela2::onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("NGVL", "Tela2::onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("NGVL", "Tela2::onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("NGVL", "Tela2::onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("NGVL", "Tela2::onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("NGVL", "Tela2::onDestroy");
    }
}

