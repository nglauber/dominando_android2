package dominando.android.googleplus;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiProvider {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mOpcaoSelecionada;

    public static final int REQUEST_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selecionarOpcaoMenu(menuItem);
                        return true;
                    }
                });
        if (savedInstanceState == null){
            mOpcaoSelecionada = R.id.action_aba;
        } else {
            mOpcaoSelecionada = savedInstanceState.getInt("menuItem");
        }
        selecionarOpcaoMenu(mNavigationView.getMenu().findItem(mOpcaoSelecionada));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("menuItem", mOpcaoSelecionada);
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == REQUEST_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        mIntentInProgress = false;
        atualizarMenu();
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
            if (mSignInClicked) {
                login();
            }
        }
        atualizarMenu();
    }


    private void selecionarOpcaoMenu(MenuItem menuItem){
        if (menuItem.getItemId() == R.id.action_login_logout){
            if (!mGoogleApiClient.isConnected()
                    && !mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                login();
            } else if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
            return;
        }
        mOpcaoSelecionada = menuItem.getItemId();
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();

        String titulo = menuItem.getTitle().toString();
        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(titulo) == null) {
            Fragment f;
            if (menuItem.getItemId() == R.id.action_friends){
                f = new ListaAmigosFragment();
            } else {
                f = PrimeiroNivelFragment.novaInstancia(titulo);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.conteudo, f, titulo)
                    .commit();
        }
    }

    private void login() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, REQUEST_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    private void atualizarMenu(){
        final ImageView imgCapa = (ImageView)findViewById(R.id.imgCapa);
        final ImageView imgFoto = (ImageView)findViewById(R.id.imgFotoPerfil);
        final TextView txtNome = (TextView)findViewById(R.id.txtNome);
        if (mGoogleApiClient.isConnected()) {
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (person != null) {
                txtNome.setText(person.getDisplayName());
                if (person.hasImage()) {
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                            RoundedBitmapDrawable fotoRedonda = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            fotoRedonda.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                            imgFoto.setImageDrawable(fotoRedonda);
                        }
                        @Override
                        public void onBitmapFailed(Drawable drawable) {
                        }
                        @Override
                        public void onPrepareLoad(Drawable drawable) {
                        }
                    };
                    Picasso.with(MainActivity.this)
                            .load(person.getImage().getUrl())
                            .into(target);
                }
                if (person.hasCover()) {
                    Picasso.with(MainActivity.this)
                            .load(person.getCover().getCoverPhoto().getUrl())
                            .into(imgCapa);
                }
            }
        } else {
            imgCapa.setImageBitmap(null);
            txtNome.setText(R.string.app_name);
            imgFoto.setImageResource(R.mipmap.ic_launcher);
        }
        mNavigationView.getMenu()
                .findItem(R.id.action_login_logout)
                .setTitle(mGoogleApiClient.isConnected() ?
                        R.string.opcao_logout : R.string.opcao_login);
    }
    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}

