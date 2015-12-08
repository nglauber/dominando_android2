package dominando.android.enghaw;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiscoDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "discosDB";
    public static final int DB_VERSION = 1;

    public DiscoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ DiscoContract.TABLE_DISCO +" (" +
                        DiscoContract._ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DiscoContract.COL_TITULO +" TEXT NOT NULL UNIQUE," +
                        DiscoContract.COL_CAPA +" TEXT, " +
                        DiscoContract.COL_CAPA_BIG +" TEXT, " +
                        DiscoContract.COL_ANO +" INTEGER, " +
                        DiscoContract.COL_GRAVADORA +" TEXT)");

        db.execSQL("CREATE TABLE "+ DiscoContract.TABLE_INTEGRANTES +" (" +
                DiscoContract.COL_DISCO_ID +" INTEGER, " +
                DiscoContract.COL_INTEGRANTE +" TEXT," +
                "FOREIGN KEY("+ DiscoContract.COL_DISCO_ID + ") " +
                "REFERENCES "+ DiscoContract.TABLE_DISCO +"("+ DiscoContract._ID + ") " +
                "ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE "+ DiscoContract.TABLE_MUSICAS +" (" +
                DiscoContract.COL_DISCO_ID +" INTEGER, " +
                DiscoContract.COL_MUSICA_NUM +" INTEGER, "+
                DiscoContract.COL_MUSICA +" TEXT," +
                "FOREIGN KEY("+ DiscoContract.COL_DISCO_ID + ") " +
                "REFERENCES "+ DiscoContract.TABLE_DISCO +"("+ DiscoContract._ID + ") " +
                "ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

