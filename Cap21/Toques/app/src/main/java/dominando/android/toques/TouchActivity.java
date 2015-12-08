package dominando.android.toques;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
public class TouchActivity extends AppCompatActivity {
    TextView mTxtEvento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTxtEvento = new TextView(this);
        setContentView(mTxtEvento);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String log = "onTouch\n";
        boolean consumiu = super.onTouchEvent(event);
        int acao = MotionEventCompat.getActionMasked(event);
        switch (acao) {
            case MotionEvent.ACTION_DOWN:
                log += "ACTION_DOWN\n";
                consumiu = true;
                break;
            case MotionEvent.ACTION_MOVE:
                log += "ACTION_MOVE\n";
                consumiu = true;
                break;
            case MotionEvent.ACTION_UP:
                log += "ACTION_UP\n";
                consumiu = true;
                break;
            case MotionEvent.ACTION_CANCEL:
                log += "ACTION_CANCEL\n";
                consumiu = true;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                log += "ACTION_OUTSIDE\n";
                consumiu = true;
                break;
        }
        int toquesNaTela = event.getPointerCount();
        log += String.format("Toques na tela: %d\n", toquesNaTela);
        for (int i = 0; i < toquesNaTela; i++) {
            log += String.format(
                    "%d: x=%.2f, y=%.2f]\n", i, event.getX(i), event.getY(i));
        }
        mTxtEvento.setText(log);
        return consumiu;
    }
}

