package dominando.android.databinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import dominando.android.databinding.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);

        usuario = new Usuario("Nelson", "Glauber");
        binding.setUsuario(usuario);
        binding.setTratador(new TratadorMagico());

        EditText edtNome = (EditText) findViewById(R.id.edtNome);
        edtNome.addTextChangedListener(new AoMudarTexto(R.id.edtNome));

        EditText edtSobrenome = (EditText) findViewById(R.id.edtSobrenome);
        edtSobrenome.addTextChangedListener(new AoMudarTexto(R.id.edtSobrenome));
    }

    class AoMudarTexto implements TextWatcher {
        private int id;

        public AoMudarTexto(int id) {
            this.id = id;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (id){
                case R.id.edtNome:
                    usuario.nome.set(s.toString());
                    break;
                case R.id.edtSobrenome:
                    usuario.sobrenome.set(s.toString());

            }
        }

        @Override
        public void afterTextChanged(Editable s) {   }
    }
}

