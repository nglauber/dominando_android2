package dominando.android.webview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "dominando");
        mWebView.loadUrl("file:///android_asset/meu.html");
    }
    @JavascriptInterface
    public void showToast(String s, String t) {
        Toast.makeText(this,
                "Nome: " + s + " Idade: " + t,
                Toast.LENGTH_SHORT).show();
    }
}



