package dominando.android.mapas;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    private static final int REQUEST_ERRO_PLAY_SERVICES = 1;
    private static final int REQUEST_CHECAR_GPS = 2;
    private  static final int REQUEST_PERMISSIONS = 3;

    private static final String EXTRA_DIALOG = "dialog";

    private static final int LOADER_ENDERECO = 1;
    private static final int LOADER_ROTA = 2;
    private static final String EXTRA_ROTA = "rota";

    private static final String EXTRA_ORIG = "orig";
    private static final String EXTRA_DEST = "dest";

    private static final String TAG = "map";

    GoogleApiClient mGoogleApiClient;
    GoogleMap mGoogleMap;
    LatLng mOrigem;

    Handler mHandler;
    boolean mDeveExibirDialog;
    int mTentativas;

    EditText mEdtLocal;
    ImageButton mBtnBuscar;
    DialogFragment mDialogEnderecos;
    TextView mTxtProgresso;
    LinearLayout mLayoutProgresso;
    LoaderManager mLoaderManager;
    LatLng mDestino;

    ArrayList<LatLng> mRota;
    Marker mMarkerLocalAtual;

    GeofenceInfo mGeofenceInfo;
    GeofenceDB mGeofenceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NGVL", "onCreate::BEGIN");
        setContentView(R.layout.activity_main);
        mEdtLocal = (EditText)findViewById(R.id.edtLocal);
        mBtnBuscar = (ImageButton)findViewById(R.id.imgBtnBuscar);
        mBtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnBuscar.setEnabled(false);
                buscarEndereco();
            }
        });
        mLoaderManager = getSupportLoaderManager();
        mTxtProgresso = (TextView)findViewById(R.id.txtProgresso);
        mLayoutProgresso = (LinearLayout)findViewById(R.id.llProgresso);
        mDeveExibirDialog = savedInstanceState == null;
        mHandler = new Handler();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGeofenceDB = new GeofenceDB(this);
        Log.d("NGVL", "onCreate::END");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NGVL", "onResume::BEGIN");
        if (permissoesHabilitadas()) {
            init();
        }
        Log.d("NGVL", "onResume::END");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("NGVL", "onPause::BEGIN");
        if (mDialogEnderecos != null) {
            mDialogEnderecos.dismiss();
        }
        Log.d("NGVL", "onPause::END");
    }
    @Override
    protected void onStop() {
        Log.d("NGVL", "onStop::BEGIN");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mHandler.removeCallbacksAndMessages(null);
        Log.d("NGVL", "onStop::END");
        super.onStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("NGVL", "onActivityResult::BEGIN");
        if (requestCode == REQUEST_ERRO_PLAY_SERVICES
                && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        } else if (requestCode == REQUEST_CHECAR_GPS){
            if (resultCode == RESULT_OK){
                mTentativas = 0;
                mHandler.removeCallbacksAndMessages(null);
                obterUltimaLocalizacao();
            } else {
                Toast.makeText(this, R.string.erro_gps, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        Log.d("NGVL", "onActivityResult::END");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("NGVL", "onSaveInstanceState");
        outState.putBoolean(EXTRA_DIALOG, mDeveExibirDialog);
        outState.putParcelable(EXTRA_ORIG, mOrigem);
        outState.putParcelable(EXTRA_DEST, mDestino);
        outState.putParcelableArrayList(EXTRA_ROTA, mRota);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("NGVL", "onRestoreInstanceState");
        mDeveExibirDialog = savedInstanceState.getBoolean(EXTRA_DIALOG, true);
        mOrigem = savedInstanceState.getParcelable(EXTRA_ORIG);
        mDestino = savedInstanceState.getParcelable(EXTRA_DEST);
        mRota = savedInstanceState.getParcelableArrayList(EXTRA_ROTA);
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d("NGVL", "onConnected::BEGIN");
        verificarStatusGPS();
        if (estaCarregando(LOADER_ENDERECO) && mDestino == null) {
            mLoaderManager.initLoader(LOADER_ENDERECO, null, mBuscaLocalCallback);
            exibirProgresso(getString(R.string.msg_buscar));
        } else if (estaCarregando(LOADER_ROTA) && mRota == null) {
            mLoaderManager.initLoader(LOADER_ROTA, null, mRotaCallback);
            exibirProgresso(getString(R.string.msg_rota));
        }
        Log.d("NGVL", "onConnected::END");
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("NGVL", "onConnectionFailed");
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_ERRO_PLAY_SERVICES);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            exibirMensagemDeErro(this, connectionResult.getErrorCode());
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d("NGVL", "onLocationChanged::BEGIN");
        if (mOrigem == null){
            mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
        }
        mMarkerLocalAtual.setPosition(
                new LatLng(location.getLatitude(), location.getLongitude()));
        Log.d("NGVL", "onLocationChanged::END");
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mGoogleApiClient.isConnected()) {
            PendingIntent pit = PendingIntent.getBroadcast(
                    this,
                    0,
                    new Intent(this, GeofenceReceiver.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mGeofenceInfo = new GeofenceInfo(
                    "1",
                    latLng.latitude, latLng.longitude,
                    1000, // metros
                    Geofence.NEVER_EXPIRE,
                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

            List<Geofence> geofences = new ArrayList<Geofence>();
            geofences.add(mGeofenceInfo.getGeofence());
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofences, pit)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                mGeofenceDB.salvarGeofence("1", mGeofenceInfo);
                                mGeofenceInfo = null;
                                atualizarMapa();
                            }
                        }
                    });
        }
    }

    private void obterUltimaLocalizacao() {
        Log.d("NGVL", "obterUltimaLocalizacao::BEGIN");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            mTentativas = 0;
            mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
            atualizarMapa();
        } else if (mTentativas < 10){  // vamos tentar obter a última localização 10 vezes
            mTentativas++;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    obterUltimaLocalizacao();
                }
            }, 2000); // a cada 2 segundos
        }
        Log.d("NGVL", "obterUltimaLocalizacao::END");
    }
    private void atualizarMapa() {
        Log.d("NGVL", "atualizarMapa::BEGIN");
        if (mGoogleMap == null) {
            SupportMapFragment fragment = (SupportMapFragment)
                    getSupportFragmentManager().findFragmentByTag(TAG);
            mGoogleMap = fragment.getMap();
            mGoogleMap.setOnMapLongClickListener(MainActivity.this);
        }

        mGoogleMap.clear();
        if (mOrigem != null) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(mOrigem)
                    .title(getString(R.string.origem)));
        }
        if (mDestino != null) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(mDestino)
                    .title(getString(R.string.destino)));
        }
        if (mOrigem != null) {
            if (mDestino != null) {
                LatLngBounds area = new LatLngBounds.Builder()
                        .include(mOrigem)
                        .include(mDestino)
                        .build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(area, 50));
            } else {
                mGoogleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(mOrigem, 17.0f));
            }
        }
        if (mRota != null && mRota.size() > 0) {
            BitmapDescriptor icon = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_launcher);
            mMarkerLocalAtual = mGoogleMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.local_atual))
                    .icon(icon)
                    .position(mOrigem));
            iniciarDeteccaoDeLocal();

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(mRota)
                    .width(5)
                    .color(Color.RED)
                    .visible(true);
            mGoogleMap.addPolyline(polylineOptions);
        }
        mGeofenceInfo = mGeofenceDB.getGeofence("1");
        if (mGeofenceInfo != null) {
            LatLng posicao = new LatLng(
                    mGeofenceInfo.mLatitude, mGeofenceInfo.mLongitude);
            mGoogleMap.addCircle(new CircleOptions()
                    .strokeWidth(2)
                    .fillColor(0x990000FF)
                    .center(posicao)
                    .radius(mGeofenceInfo.mRadius));
        }
        Log.d("NGVL", "atualizarMapa::END");
    }
    private void exibirMensagemDeErro(FragmentActivity activity, final int codigoDoErro) {
        Log.d("NGVL", "exibirMensagemDeErro");
        final String TAG = "DIALOG_ERRO_PLAY_SERVICES";

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            DialogFragment errorFragment = new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return GooglePlayServicesUtil.getErrorDialog(
                            codigoDoErro, getActivity(), REQUEST_ERRO_PLAY_SERVICES);
                }
            };
            errorFragment.show(activity.getSupportFragmentManager(), TAG);
        }
    }

    private void verificarStatusGPS() {
        Log.d("NGVL", "verificarStatusGPS::BEGIN");
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder locationSettingsRequest =
                new LocationSettingsRequest.Builder();
        locationSettingsRequest.setAlwaysShow(true);
        locationSettingsRequest.addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        locationSettingsRequest.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        obterUltimaLocalizacao();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if (mDeveExibirDialog) {
                            try {
                                status.startResolutionForResult(MainActivity.this, REQUEST_CHECAR_GPS);
                                mDeveExibirDialog = false;
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.wtf("NGVL", "Isso não deveria acontecer...");
                        break;
                }
            }
        });
        Log.d("NGVL", "verificarStatusGPS::END");
    }

    private void buscarEndereco() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtLocal.getWindowToken(), 0);
        mLoaderManager.restartLoader(LOADER_ENDERECO, null, mBuscaLocalCallback);
        exibirProgresso(getString(R.string.msg_buscar));
    }
    private void exibirProgresso(String texto) {
        mTxtProgresso.setText(texto);
        mLayoutProgresso.setVisibility(View.VISIBLE);
    }
    private void ocultarProgresso() {
        mLayoutProgresso.setVisibility(View.GONE);
    }
    private void exibirListaEnderecos(final List<Address> enderecosEncontrados) {
        ocultarProgresso();
        mBtnBuscar.setEnabled(true);
        if (enderecosEncontrados != null && enderecosEncontrados.size() > 0) {
            final String[] descricaoDosEnderecos = new String[enderecosEncontrados.size()];
            for (int i = 0; i < descricaoDosEnderecos.length; i++) {
                Address endereco = enderecosEncontrados.get(i);
                StringBuffer rua = new StringBuffer();
                for (int j = 0; j < endereco.getMaxAddressLineIndex(); j++) {
                    if (rua.length() > 0) {
                        rua.append('\n');
                    }
                    rua.append(endereco.getAddressLine(j));
                }
                String pais = endereco.getCountryName();
                String descricaoEndereco = String.format("%s, %s", rua, pais);
                descricaoDosEnderecos[i] = descricaoEndereco;
            }
            mDialogEnderecos = new DialogFragment(){
                DialogInterface.OnClickListener selecionarEnderecoClick =
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Address enderecoSelecionado = enderecosEncontrados.get(which);
                                mDestino = new LatLng(
                                        enderecoSelecionado.getLatitude(),
                                        enderecoSelecionado.getLongitude());
                                obterUltimaLocalizacao();
                                atualizarMapa();
                                carregarRota();
                            }
                        };
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.titulo_lista_endereco)
                            .setItems(descricaoDosEnderecos, selecionarEnderecoClick);
                    return builder.create();
                }
            };
            mDialogEnderecos.show(getSupportFragmentManager(), "TAG_ENDERECOS");
        }
    }
    private boolean estaCarregando(int id) {
        Loader<?> loader = mLoaderManager.getLoader(id);
        if (loader != null && loader.isStarted()) {
            return true;
        }
        return false;
    }
    LoaderManager.LoaderCallbacks<List<Address>> mBuscaLocalCallback =
            new LoaderManager.LoaderCallbacks<List<Address>>() {
                @Override
                public Loader<List<Address>> onCreateLoader(int i, Bundle bundle) {
                    return new BuscarLocalTask(MainActivity.this,
                            mEdtLocal.getText().toString());
                }
                @Override
                public void onLoadFinished(Loader<List<Address>> listLoader,
                                           final List<Address> addresses) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            exibirListaEnderecos(addresses);
                        }
                    });
                }
                @Override
                public void onLoaderReset(Loader<List<Address>> listLoader) {
                }
            };
    private void carregarRota() {
        mRota = null;
        mLoaderManager.initLoader(LOADER_ROTA, null, mRotaCallback);
        exibirProgresso(getString(R.string.msg_rota));
    }
    LoaderManager.LoaderCallbacks<List<LatLng>> mRotaCallback =
            new LoaderManager.LoaderCallbacks<List<LatLng>>() {
                @Override
                public Loader<List<LatLng>> onCreateLoader(int i, Bundle bundle) {
                    return new RotaTask(MainActivity.this, mOrigem, mDestino);
                }
                @Override
                public void onLoadFinished(final Loader<List<LatLng>> listLoader,
                                           final List<LatLng> latLngs) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mRota = new ArrayList<LatLng>(latLngs);
                            atualizarMapa();
                            ocultarProgresso();
                        }
                    });
                }
                @Override
                public void onLoaderReset(Loader<List<LatLng>> listLoader) {
                }
            };
    private void iniciarDeteccaoDeLocal() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
    }

    private boolean permissoesHabilitadas() {
        Log.d("NGVL", "permissoesHabilitadas::BEGIN");
        List<String> permissoes = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissoes.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissoes.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissoes.isEmpty()&& mDeveExibirDialog){
            String[] array = new String[permissoes.size()];
            permissoes.toArray(array);
            ActivityCompat.requestPermissions(this, array, REQUEST_PERMISSIONS);
            mDeveExibirDialog = false;
        }
        Log.d("NGVL", "permissoesHabilitadas::END");
        return permissoes.isEmpty();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("NGVL", "onRequestPermissionsResult::BEGIN");
        boolean success = true;
        for (int i = 0; i < permissions.length; i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                success = false;
                break;
            }
        }
        mDeveExibirDialog = true;
        if (!success){
            Toast.makeText(this, R.string.erro_local, Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.d("NGVL", "onRequestPermissionsResult::END");
    }

    private void init() {
        Log.d("NGVL", "init::BEGIN");
        SupportMapFragment fragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map, fragment, TAG)
                    .commit();
        }
        mGoogleApiClient.connect();
        Log.d("NGVL", "init::END");
    }
}

