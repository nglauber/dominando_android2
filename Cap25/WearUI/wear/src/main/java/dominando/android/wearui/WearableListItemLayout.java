package dominando.android.wearui;

import android.content.Context;
import android.graphics.Color;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WearableListItemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener {
    private static final float TRANSPARENCIA_INICIAL = 0.4f;
    private static final int COR_ITEM_DESELECIONADA = Color.LTGRAY;
    private final int mCorBgIcone;
    private CircledImageView mImgIcone;
    private TextView mTxtNome;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }
    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCorBgIcone = Color.rgb(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255));
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImgIcone = (CircledImageView) findViewById(R.id.imgIcone);
        mTxtNome = (TextView) findViewById(R.id.txtTexto);
    }

    @Override
    public void onCenterPosition(boolean b) {
        mImgIcone.setCircleColor(mCorBgIcone);
        mImgIcone.setScaleX(1.0f);
        mImgIcone.setScaleY(1.0f);
        mTxtNome.setAlpha(1.0f);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        mImgIcone.setScaleX(.5f);
        mImgIcone.setScaleY(.5f);
        mImgIcone.setCircleColor(COR_ITEM_DESELECIONADA);
        mTxtNome.setAlpha(TRANSPARENCIA_INICIAL);
    }
}

