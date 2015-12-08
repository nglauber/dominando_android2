package dominando.android.wearui;

import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class MeuCardFragment extends CardFragment {
    public static MeuCardFragment newInstance(String string) {
        Bundle args = new Bundle();
        args.putString("nome", string);
        MeuCardFragment mcf = new MeuCardFragment();
        mcf.setArguments(args);
        return mcf;
    }
    @Override
    public View onCreateContentView(LayoutInflater inflater,
                                    ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meucard, container, false);
        TextView txt = (TextView)v.findViewById(R.id.textView);
        txt.setText(getArguments().getString("nome"));
        return v;
    }
}

