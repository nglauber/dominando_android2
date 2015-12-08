package dominando.android.autocomplete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> cidade = new ArrayList<String>();
        cidade.add("Caruaru");
        cidade.add("Cabo de Santo Agostinho");
        cidade.add("Recife");
        cidade.add("São Paulo");
        cidade.add("Santos");
        cidade.add("Santa Cruz");
        MeuAutoCompleteAdapter adapter = new MeuAutoCompleteAdapter(this,
                android.R.layout.simple_dropdown_item_1line, cidade);

        AutoCompleteTextView actv = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);
        actv.setAdapter(adapter);
    }
}

