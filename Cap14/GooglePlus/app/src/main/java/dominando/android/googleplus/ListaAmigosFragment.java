package dominando.android.googleplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;

public class ListaAmigosFragment extends Fragment
        implements ResultCallback<People.LoadPeopleResult> {
    private GoogleApiClient mGoogleApiClient;
    private PessoaAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private List<Pessoa> mPessoas;
    private String mProxPagina;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_amigos, container, false);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mListView = (ListView)view.findViewById(R.id.listView);
        mListView.setOnScrollListener(new ScrollPaginado(5) {
            @Override
            public void aoPrecisarCarregarMais() {
                if (mProxPagina != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    Plus.PeopleApi.loadVisible(mGoogleApiClient, mProxPagina)
                            .setResultCallback(ListaAmigosFragment.this);
                }
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof GoogleApiProvider){
            mGoogleApiClient = ((GoogleApiProvider)getActivity()).getGoogleApiClient();
        }
        if (mPessoas == null || mPessoas.isEmpty()) {
            Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                    .setResultCallback(this);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new PessoaAdapter(getActivity(), mPessoas);
            mListView.setAdapter(mAdapter);
        }
    }
    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        mProgressBar.setVisibility(View.GONE);
        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            mProxPagina = loadPeopleResult.getNextPageToken();
            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            try {
                if (mPessoas == null || mPessoas.isEmpty()) {
                    mPessoas = new ArrayList<Pessoa>();
                    mAdapter = new PessoaAdapter(getActivity(), mPessoas);
                    mListView.setAdapter(mAdapter);
                }
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Person person = personBuffer.get(i);
                    mPessoas.add(new Pessoa(person.getDisplayName(), person.getImage().getUrl()));
                }
                mAdapter.notifyDataSetChanged();
            } finally {
                personBuffer.close();
            }
        } else {
            Toast.makeText(getActivity(),
                    "Erro ao carregar contatos: " + loadPeopleResult.getStatus(),
                    Toast.LENGTH_LONG).show();
        }
    }
}

