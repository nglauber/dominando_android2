package dominando.android.textview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface typeface = Typeface.createFromAsset(
                getAssets(), "From Cartoon Blocks.ttf");
        TextView txt1 = (TextView)findViewById(R.id.txtFonte);
        txt1.setTypeface(typeface);
        TextView txt2 = (TextView)findViewById(R.id.txtStike);
        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        TextView txtHtml = (TextView) findViewById(R.id.txtHtml);
        final String textoEmHtml =
                "<html><body>Html em "
                        + "<b>Negrito</b>, <i>Itálico</i>"
                        + "e <u>Sublinhado</u>.<br>"
                        + "Mario: <img src='mario.png' /><br>"
                        + "Yoshi: <img src='yoshi.png' /><br>"
                        + "Um texto depois da imagem</body></html>";
        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                BitmapDrawable drawable = null;
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(
                            getAssets().open(source));
                    drawable = new BitmapDrawable(getResources(), bmp);
                    drawable.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return drawable;
            }
        };
        txtHtml.setText(Html.fromHtml(textoEmHtml, imgGetter, null));
    }
}
