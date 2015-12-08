package dominando.android.mp3service;

public interface Mp3Service {
    void play(String arquivo);
    void pause();
    void stop();
    String getMusicaAtual();
    int getTempoTotal();
    int getTempoDecorrido();
}
