package dominando.android.enghaw;

import android.provider.BaseColumns;

public interface DiscoContract extends BaseColumns {
    String TABLE_DISCO = "discos";
    String COL_DISCO_ID = "disco_id";
    String COL_TITULO = "titulo";
    String COL_ANO = "ano";
    String COL_GRAVADORA = "gravadora";
    String COL_CAPA = "capa";
    String COL_CAPA_BIG = "capaGrande";

    String TABLE_INTEGRANTES = "integrantes";
    String COL_INTEGRANTE = "integrante";

    String TABLE_MUSICAS = "musicas";
    String COL_MUSICA = "musica";
    String COL_MUSICA_NUM = "musica_num";
}

