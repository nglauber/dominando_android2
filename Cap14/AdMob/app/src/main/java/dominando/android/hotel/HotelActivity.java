package dominando.android.hotel;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

import dominando.android.hotel.gcm.HotelIdListenerService;

public class HotelActivity extends AppCompatActivity
        implements
        HotelListFragment.AoClicarNoHotel,
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener,
        HotelDialogFragment.AoSalvarHotel,
        HotelDetalheFragment.AoEditarHotel,
        HotelListFragment.AoExcluirHoteis {

    public static final int REQUEST_PLAY_SERVICES = 1;
    public static final int REQUEST_EDITAR_HOTEL = 0;
    private long mIdSelecionado;

    private FragmentManager mFragmentManager;
    private HotelListFragment mListFragment;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        mFragmentManager = getSupportFragmentManager();
        mListFragment = (HotelListFragment)
                mFragmentManager.findFragmentById(R.id.fragmentLista);
        iniciarGooglePlayServices();

        adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("TEST_DEVICE_ID")
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDITAR_HOTEL && resultCode == RESULT_OK) {
            mListFragment.limparBusca();
        } else if (requestCode == REQUEST_PLAY_SERVICES){
            iniciarGooglePlayServices();
        }
    }

    public void clicouNoHotel(Hotel hotel) {
        mIdSelecionado = hotel.id;
        if (isTablet()) {
            HotelDetalheFragment fragment =
                    HotelDetalheFragment.novaInstancia(hotel);
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.detalhe, fragment,
                    HotelDetalheFragment.TAG_DETALHE);
            ft.commit();
        } else {

            Intent it = new Intent(this, HotelDetalheActivity.class);
            it.putExtra(HotelDetalheActivity.EXTRA_HOTEL, hotel);
            startActivityForResult(it, REQUEST_EDITAR_HOTEL);
        }
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.tablet);
    }
    private boolean isSmartphone() {
        return getResources().getBoolean(R.bool.smartphone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hotel, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.hint_busca));
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                SobreDialogFragment dialogFragment = new SobreDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "sobre");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }
    @Override
    public boolean onQueryTextChange(String s) {
        mListFragment.buscar(s);
        return false;
    }
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true; // para expandir a view
    }
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mListFragment.limparBusca();
        return true; // para voltar ao normal
    }

    public static class SobreDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_NEGATIVE){
                        Intent it = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.nglauber.com.br"));
                        startActivity(it);
                    }
                }
            };
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.sobre_titulo)
                    .setMessage(R.string.sobre_mensagem)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(R.string.sobre_botao_site, listener)
                    .create();
            return dialog;
        }
    }

    @Override
    public void salvouHotel(Hotel hotel) {
        HotelRepositorio repo = new HotelRepositorio(this);
        repo.salvar(hotel);
        mListFragment.limparBusca();
        if (isTablet()) {
            clicouNoHotel(hotel);
        }
    }

    public void adicionarHotelClick(View v){
        HotelDialogFragment hotelDialogFragment =
                HotelDialogFragment.newInstance(null);
        hotelDialogFragment.abrir(getSupportFragmentManager());
    }

    @Override
    public void aoEditarhotel(Hotel hotel) {
        HotelDialogFragment editNameDialog = HotelDialogFragment.newInstance(hotel);
        editNameDialog.abrir(getSupportFragmentManager());
    }
    @Override
    public void exclusaoCompleta(List<Hotel> excluidos) {
        HotelDetalheFragment f = (HotelDetalheFragment)
                mFragmentManager.findFragmentByTag(HotelDetalheFragment.TAG_DETALHE);
        if (f != null) {
            boolean encontrou = false;
            for (Hotel h : excluidos) {
                if (h.id == mIdSelecionado) {
                    encontrou = true;
                    break;
                }
            }
            if (encontrou) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(f);
                ft.commit();
            }
        }
    }

    private void iniciarGooglePlayServices(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                Dialog dialog = api.getErrorDialog(this, resultCode, REQUEST_PLAY_SERVICES);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                Toast.makeText(this, R.string.gcm_nao_suportado,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Intent it = new Intent(this, HotelIdListenerService.class);
            it.putExtra(HotelIdListenerService.EXTRA_REGISTRAR, true);
            startService(it);
        }
    }

}
