package dominando.android.enghaw;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.util.concurrent.TimeUnit;
public class DiscoHttp {
    public static final String BASE_URL =
            "https://raw.githubusercontent.com/nglauber/dominando_android2/master/enghaw/";
    public static Disco[] obterDiscosDoServidor(){
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        Request request = new Request.Builder()
                .url(BASE_URL + "enghaw.json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, Disco[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
