package dominando.android.navegacao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.LinkedHashMap;

public class PrimeiroNivelFragment extends Fragment
        implements View.OnClickListener {
    private static final String EXTRA_TIPO = "mTipo";
    private String mTipo;
    private LinkedHashMap<String, Class> mAcoes;
    public static PrimeiroNivelFragment novaInstancia(String tipo) {
        Bundle params = new Bundle();
        params.putString(EXTRA_TIPO, tipo);
        PrimeiroNivelFragment f = new PrimeiroNivelFragment();
        f.setArguments(params);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAcoes = new LinkedHashMap<String, Class>();
        mAcoes.put(getString(R.string.opcao_aba), TelaAbasActivity.class);
        mAcoes.put(getString(R.string.opcao_spinner), TelaSpinnerActivity.class);
        mAcoes.put(getString(R.string.opcao_pager), TelaPagerActivity.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mTipo = getArguments().getString(EXTRA_TIPO);
        View layout = inflater.inflate(R.layout.fragment_primeiro_nivel, container, false);
        Button button = (Button) layout.findViewById(R.id.button);
        button.setOnClickListener(this);
        TextView textView = (TextView) layout.findViewById(R.id.textView);
        textView.setText(mTipo);
        return layout;
    }
    @Override
    public void onClick(View view) {
        Class classe = mAcoes.get(mTipo);
        startActivity(new Intent(getActivity(), classe));
    }
}

