package dominando.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HotelListFragment extends ListFragment
        implements ActionMode.Callback, AdapterView.OnItemLongClickListener {
    ListView mListView;
    ActionMode mActionMode;

    List<Hotel> mHoteis;
    ArrayAdapter<Hotel> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mHoteis = carregarHoteis();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        limparBusca();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActionMode != null) {
            iniciarModoExclusao();
            atualizarTitulo();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mActionMode == null) {
            Activity activity = getActivity();
            if (activity instanceof AoClicarNoHotel) {
                Hotel hotel = (Hotel) l.getItemAtPosition(position);
                AoClicarNoHotel listener = (AoClicarNoHotel) activity;
                listener.clicouNoHotel(hotel);
            }
        } else {
            atualizarItensMarcados(mListView, position);
            if (qtdeItensMarcados() == 0) {
                mActionMode.finish();
            }
        }

    }

    public void buscar(String s) {
        if (s == null || s.trim().equals("")) {
            limparBusca();
            return;
        }
        List<Hotel> hoteisEncontrados = new ArrayList<Hotel>(mHoteis);
        for (int i = hoteisEncontrados.size() - 1; i >= 0; i--) {
            Hotel hotel = hoteisEncontrados.get(i);
            if (!hotel.nome.toUpperCase().contains(s.toUpperCase())) {
                hoteisEncontrados.remove(hotel);
            }
        }
        mListView.setOnItemLongClickListener(null);
        mAdapter = new ArrayAdapter<Hotel>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                hoteisEncontrados);
        setListAdapter(mAdapter);
    }

    public void limparBusca() {
        mListView.setOnItemLongClickListener(this);
        ordenar();
        mAdapter = new ArrayAdapter<Hotel>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                mHoteis);
        setListAdapter(mAdapter);
    }

    public void adicionar(Hotel hotel) {
        mHoteis.add(hotel);
        ordenar();
        mAdapter.notifyDataSetChanged();
    }

    public interface AoClicarNoHotel {
        void clicouNoHotel(Hotel hotel);
    }

    private List<Hotel> carregarHoteis() {
        List<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("New Beach Hotel", "Av. Boa Viagem", 4.5f));
        hotels.add(new Hotel("Recife Hotel", "Av. Boa Viagem", 4.0f));
        hotels.add(new Hotel("Canario Hotel", "Rua dos Navegantes", 3.0f));
        hotels.add(new Hotel("Byanca Beach Hotel", "Rua Mamanguape", 4.0f));
        hotels.add(new Hotel("Grand Hotel Dor", "Av. Bernardo", 3.5f));
        hotels.add(new Hotel("Hotel Cool", "Av. Conselheiro Aguiar", 4.0f));
        hotels.add(new Hotel("Hotel Infinito", "Rua Ribeiro de Brito", 5.0f));
        hotels.add(new Hotel("Hotel Tulipa", "Av. Boa Viagem", 5.0f));
        return hotels;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_delete_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.acao_delete) {
            remover();
            actionMode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mActionMode = null;
        mListView.clearChoices();
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        limparBusca();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        boolean consumed = (mActionMode == null);
        if (consumed) {
            iniciarModoExclusao();
            mListView.setItemChecked(i, true);
            atualizarItensMarcados(mListView, i);
        }
        return consumed;
    }

    private void iniciarModoExclusao() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionMode = activity.startSupportActionMode(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void atualizarItensMarcados(ListView l, int position) {
        l.setItemChecked(position, l.isItemChecked(position));
        atualizarTitulo();
    }

    private int qtdeItensMarcados() {
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        int checkedCount = 0;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                checkedCount++;
            }
        }
        return checkedCount;
    }

    private void atualizarTitulo() {
        int checkedCount = qtdeItensMarcados();
        String selecionados = getResources().getQuantityString(
                R.plurals.numero_selecionados,
                checkedCount, checkedCount);
        mActionMode.setTitle(selecionados);
    }

    private void remover() {
        final List<Hotel> hoteisExcluidos = new ArrayList<Hotel>();
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        for (int i = checked.size() - 1; i >= 0; i--) {
            if (checked.valueAt(i)) {
                int position = checked.keyAt(i);
                hoteisExcluidos.add(mHoteis.remove(position));
            }
        }
        Snackbar.make(mListView,
                getString(R.string.mensagem_excluir, hoteisExcluidos.size()),
                Snackbar.LENGTH_LONG)
                .setAction(R.string.desfazer, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (Hotel hotel : hoteisExcluidos) {
                            mHoteis.add(hotel);
                        }
                        limparBusca();
                    }
                }).show();
    }

    private void ordenar() {
        Collections.sort(mHoteis, new Comparator<Hotel>() {
            @Override
            public int compare(Hotel h1, Hotel h2) {
                return h1.nome.compareTo(h2.nome);
            }
        });
    }
}
