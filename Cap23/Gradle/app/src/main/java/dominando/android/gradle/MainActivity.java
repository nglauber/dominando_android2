package dominando.android.gradle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import dominando.android.minhalib.LibActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chamarLibActivity(View v) {
        startActivity(new Intent(this, LibActivity.class));
    }

    public void chamarSegundaActivity(View v) {
        startActivity(new Intent(this, SegundaActivity.class));
    }

}
