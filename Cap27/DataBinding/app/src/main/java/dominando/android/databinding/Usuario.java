package dominando.android.databinding;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

public class Usuario extends BaseObservable {
    public ObservableField<String> nome = new ObservableField<>();
    public ObservableField<String> sobrenome = new ObservableField<>();;

    public Usuario(String nome, String sobrenome) {
        this.nome.set(nome);
        this.sobrenome.set(sobrenome);
    }
}


