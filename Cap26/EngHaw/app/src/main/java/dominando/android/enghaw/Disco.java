package dominando.android.enghaw;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
public class Disco implements Serializable {
    public String titulo;
    public String capa;
    @SerializedName("capa_big")
    public String capaGrande;
    public int ano;
    public String gravadora;
    public String[] formacao;
    public String[] faixas;
}