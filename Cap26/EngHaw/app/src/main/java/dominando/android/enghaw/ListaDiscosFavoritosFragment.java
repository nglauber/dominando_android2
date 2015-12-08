package dominando.android.enghaw;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListaDiscosFavoritosFragment extends Fragment
        implements DiscoAdapter.AoClicarNoDiscoListener {

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipe;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    DiscoDb mDiscoDb;
    List<Disco> mDiscos;
    Bus mBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBus = ((DiscoApp)getActivity().getApplication()).getBus();
        mBus.register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
        mBus = null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lista_discos, container, false);
        ButterKnife.bind(this, v);

        mSwipe.setEnabled(false);
        mRecyclerView.setTag("fav");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        return v;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDiscoDb = new DiscoDb(getActivity());
        if (mDiscos == null) {
            mDiscos = mDiscoDb.getDiscos();
        }
        atualizarLista();
    }
    private void atualizarLista() {
        Disco[] array = new Disco[mDiscos.size()];
        mDiscos.toArray(array);
        DiscoAdapter adapter = new DiscoAdapter(getActivity(), array);
        adapter.setAoClicarNoDiscoListener(this);
        mRecyclerView.setAdapter(adapter);
    }
    @Override
    public void aoClicarNoDisco(View v, int position, Disco disco) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                Pair.create(v.findViewById(R.id.imgCapa), "capa"),
                Pair.create(v.findViewById(R.id.txtTitulo), "titulo"),
                Pair.create(v.findViewById(R.id.txtAno), "ano")
        );
        Intent it = new Intent(getActivity(), DetalheActivity.class);
        it.putExtra(DetalheActivity.EXTRA_DISCO, disco);
        ActivityCompat.startActivity(getActivity(), it, options.toBundle());
    }
    @Subscribe
    public void atualizarLista(DiscoEvento event) {
        mDiscos = mDiscoDb.getDiscos();
        atualizarLista();
    }
}

