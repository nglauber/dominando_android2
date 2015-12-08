package dominando.android.wearui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.TextView;

public class MainWearActivity extends WearableActivity {
    MeuListAdapter mAdapter;
    WearableListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        setAmbientEnabled();

        String[] dados = new String[] {
                "Opção 1", "Opção 2", "Opção 3", "Opção 4"
        };
        mAdapter = new MeuListAdapter(this, dados);
        mListView = (WearableListView) findViewById(R.id.wearable_list);
        mListView.setAdapter(mAdapter);
        mListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                // Clicou em um item da lista...
                if (position == 3) {
                    mListView.setVisibility(View.GONE);
                    exibirConfirmacao();
                }
            }

            @Override
            public void onTopEmptyRegionClick() {
                // Clicou na parte superior vazia da lista
            }
        });
    }

    private boolean mPodeFechar;
    private void exibirConfirmacao() {
        final View messageView = findViewById(R.id.message);
        messageView.setVisibility(View.VISIBLE);
        TextView txtTitulo = (TextView)messageView.findViewById(R.id.txtTitulo);
        TextView txtMensagem = (TextView)messageView.findViewById(R.id.txtMensagem);
        TextView txtCancel = (TextView)messageView.findViewById(R.id.txtCancel);
        txtTitulo.setText("Confirmação");
        txtMensagem.setText("Deseja sair realmente da aplicação?");
        txtCancel.setText("Saindo...");
        final DelayedConfirmationView delayedView =
                (DelayedConfirmationView) messageView.findViewById(R.id.delayed_confirm);
        delayedView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                if (mPodeFechar) {
                    finish();
                }
            }
            @Override
            public void onTimerSelected(View view) {
                mPodeFechar = false;
                messageView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
        });
        mPodeFechar = true;
        delayedView.setTotalTimeMs(3000);
        delayedView.start();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        findViewById(R.id.box).setBackgroundColor(Color.BLACK);
        mAdapter.setAmbient(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        findViewById(R.id.box).setBackgroundColor(Color.WHITE);
        mAdapter.setAmbient(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }
}
