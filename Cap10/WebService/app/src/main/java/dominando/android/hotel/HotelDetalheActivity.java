package dominando.android.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class HotelDetalheActivity extends AppCompatActivity
        implements HotelDetalheFragment.AoEditarHotel,
        HotelDialogFragment.AoSalvarHotel {

    public static final String EXTRA_HOTEL = "hotel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detalhe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Hotel hotel = (Hotel)intent.getSerializableExtra(EXTRA_HOTEL);
        exibirHotelFragment(hotel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void exibirHotelFragment(Hotel hotel) {
        HotelDetalheFragment fragment = HotelDetalheFragment.novaInstancia(hotel);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.detalhe, fragment, HotelDetalheFragment.TAG_DETALHE);
        ft.commit();
    }
    @Override
    public void aoEditarhotel(Hotel hotel) {
        HotelDialogFragment editNameDialog = HotelDialogFragment.newInstance(hotel);
        editNameDialog.abrir(getSupportFragmentManager());
    }
    @Override
    public void salvouHotel(Hotel hotel) {
        HotelRepositorio repo = new HotelRepositorio(this);
        repo.salvar(hotel);
        exibirHotelFragment(hotel);
        setResult(RESULT_OK);
    }


}
