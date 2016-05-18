package youmo.slook;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static youmo.slook.HttpHelper.AsyncTask;

public class ReadActivity extends AppCompatActivity {

    WebView wb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String url= getIntent().getStringExtra("url")+"&v=2";
        wb=(WebView)findViewById(R.id.webView_read);

        AsyncTask(url,new HttpHelper.IHttpResult(){
            @Override
            public void HttpResult(Object... objects) {
                String html=(String)objects[0];
                String bookurl = "";
                bookurl =StringHelper.MidString(html,"最新:<a href=\"","\">");
                bookurl=bookurl.replace("amp;","");
                bookurl="http://k.sogou.com"+bookurl;
                wb.loadUrl(bookurl);
            }
        });
        wb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);   //在当前的webview中跳转到新的url
                return true;
            }
        });
        WebSettings settings = wb.getSettings();
        settings.setTextZoom(130);
    }

    public static String Get(String url,String InCookie,List<String> OutCookie)
    {
        String result="";
        try{
            URL address=new URL(url);
            HttpURLConnection http=(HttpURLConnection)address.openConnection();
            http.setRequestProperty("Connection", "keep-alive");
            if (InCookie!=null)
            {
                http.setRequestProperty("Cookie",InCookie);
            }
            //http.setRequestProperty("Cookie",);
            InputStreamReader isr=new InputStreamReader(http.getInputStream());
            BufferedReader br=new BufferedReader(isr);
            OutCookie.addAll(http.getHeaderFields().get("Set-Cookie"));
            String resultLine;
            while((resultLine=br.readLine())!=null)
            {
                result+=resultLine;
            }
            isr.close();
            http.disconnect();
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
        return result;
    }
}
