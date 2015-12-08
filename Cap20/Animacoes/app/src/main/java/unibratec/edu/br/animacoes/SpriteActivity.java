package unibratec.edu.br.animacoes;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class SpriteActivity extends AppCompatActivity {
    private AnimationDrawable mSpriteAnimation;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprite);
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setBackgroundResource(R.drawable.sprite_coruja);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSpriteAnimation.isRunning()) {
                    mSpriteAnimation.stop();
                } else {
                    mSpriteAnimation.start();
                }
            }
        });
        mSpriteAnimation = (AnimationDrawable)imgView.getBackground();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_right);
    }

}
