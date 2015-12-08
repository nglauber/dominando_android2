package dominando.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    EditText edtTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtTexto = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        Button buttonTela2 = (Button) findViewById(R.id.button2);
        buttonTela2.setOnClickListener(this);
        Button buttonTela2Parcelable = (Button) findViewById(R.id.button3);
        buttonTela2Parcelable.setOnClickListener(this);

        Log.i("NGVL", "Tela1::onCreate");
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                String texto = edtTexto.getText().toString();
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                Intent it = new Intent(this, Tela2Activity.class);
                it.putExtra("nome", "Glauber");
                it.putExtra("idade", 31);
                startActivity(it);
                break;
            case R.id.button3:
                Cliente cliente = new Cliente(1, "Glauber");
                Intent intent = new Intent(this, Tela2Activity.class);
                intent.putExtra("cliente", cliente);
                startActivity(intent);
                break;
        }
    }

        @Override
    protected void onStart() {
        super.onStart();
        Log.i("NGVL", "Tela1::onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("NGVL", "Tela1::onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("NGVL", "Tela1::onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("NGVL", "Tela1::onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("NGVL", "Tela1::onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("NGVL", "Tela1::onDestroy");
    }
}



