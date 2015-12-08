package dominando.android.multimidia;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.ArrayList;
import java.util.Stack;
public class GerenciadorDeSom {
    private static final int QTDE_MAX_DE_SONS = 5;
    private static GerenciadorDeSom sInstancia;
    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    private ArrayList<Integer> mListaIdsSons;
    private Stack<Integer> mSonsEmExecucao;
    private Context mContext;
    private GerenciadorDeSom(Context ct) {
        mContext = ct;
        mListaIdsSons = new ArrayList<Integer>();
        mSonsEmExecucao = new Stack<Integer>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool(QTDE_MAX_DE_SONS, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(QTDE_MAX_DE_SONS)
                    .setAudioAttributes(attrs)
                    .build();
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int id, int status) {
                if (status == 0) {
                    mListaIdsSons.add(id);
                }
            }
        });
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    public static GerenciadorDeSom getInstance(Context ctx) {
        if (sInstancia == null) {
            sInstancia = new GerenciadorDeSom(ctx);
        }
        sInstancia.mContext = ctx;
        return sInstancia;
    }
    public void adicionarSom(int idSom) {
        mSoundPool.load(mContext, idSom, 1);
    }
    public void tocarSom(int index) {
        if (index >= mListaIdsSons.size()) return;
        float volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int prioridade = 1;
        int repeticoes = 0;
        float rate = 1.0f;
        int playId = mSoundPool.play(
                mListaIdsSons.get(index), volume, volume, prioridade, repeticoes, rate);
        mSonsEmExecucao.push(playId);
    }
    public void pararTodosOsSons() {
        while (mSonsEmExecucao.size() > 0) {
            mSoundPool.stop(mSonsEmExecucao.pop());
        }
    }
    public void liberarRecursos() {
        mSoundPool.release();
        mSoundPool = null;
        mListaIdsSons.clear();
        mSonsEmExecucao.clear();
        mAudioManager.unloadSoundEffects();
    }
}
