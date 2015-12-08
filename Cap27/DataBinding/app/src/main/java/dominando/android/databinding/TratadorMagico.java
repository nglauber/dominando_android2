package dominando.android.databinding;

import android.util.Log;
import android.view.View;

public class TratadorMagico {
    public boolean longClick(View v){
        Log.d("NGVL", "longClick!");
        return true;
    }
    public void click(View v){
        Log.d("NGVL", "click!");
    }
}
