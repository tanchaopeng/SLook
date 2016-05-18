package youmo.slook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tanch on 2016/1/12.
 */
public class HttpHelper  {

    public interface IHttpResult{
        public void HttpResult(Object... objects);
    }
    public static void AsyncTask(String url, final IHttpResult httpResult)
    {
        new AsyncTask<String,Integer,String>()
        {
            @Override
            protected String doInBackground(String... params) {
                return Get(params[0]);
            }

            @Override
            protected void onPostExecute(String o) {
                httpResult.HttpResult(o);
            }
        }.execute(url);
    }
    public static String Get(String url)
    {
        String result="";
        try{
            URL address=new URL(url);
            HttpURLConnection http=(HttpURLConnection)address.openConnection();

            InputStreamReader isr=new InputStreamReader(http.getInputStream());
            BufferedReader br=new BufferedReader(isr);
            List<String> head=http.getHeaderFields().get("Set-Cookie");
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

    public String OkHttpGet(String adress)
    {
        String ret=null;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
            .url(adress)
            .build();

        //new call
        Call call = mOkHttpClient.newCall(request);

        try {
            Response res= call.execute();
            ret=res.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static byte[] GetToByte(String url)
    {
        try{
            URL address=new URL(url);
            HttpURLConnection http=(HttpURLConnection)address.openConnection();
            http.setConnectTimeout(6000);
            http.setUseCaches(false);
//            Bitmap b1= BitmapFactory.decodeStream(http.getInputStream());
            byte[] b = toByteArray(http.getInputStream());
            http.disconnect();
            return b;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    /**
     * 键值对转换成Byte[]
     * @param values
     * @return
     */
    public static byte[] MapToByte(Map<String,String> values)
    {
        StringBuffer sb = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : values.entrySet()) {
                sb.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(),"UTF-8"))
                        .append("&");
            }
            //删除最后的一个"&"
            sb.deleteCharAt(sb.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().getBytes();
    }

    /**
     * 一个请求
     * @param url               访问URL
     * @param method            访问方式，默认或者null为GET访问，POST,GET,其他
     * @param values            请求参数键值对
     * @param IsSaveCookies     是否开启COOKIES存储，开启则可以保持登录状态
     * @return
     */
    public static String Connection(String url,String method,Map<String,String> values,boolean IsSaveCookies)
    {
        String result="";
        if (IsSaveCookies)
        {
            //初始化一个CookieManager，用于存放Cookies
            CookieManager cookieManager = new CookieManager();
            //设置接收规则，接收所有链接
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            //设置本地默认CookieManager，使接收到的COOKIES生效,清空->CookieHandler.setDefault(null)
            CookieHandler.setDefault(cookieManager);
        }

        try {
            URL address=new URL(url);
            HttpURLConnection http=(HttpURLConnection)address.openConnection();
            if (!method.isEmpty())
                http.setRequestMethod(method);
            if (values!=null&&values.size()>0)
            {
                //打开输入流
                OutputStream outPut=http.getOutputStream();
                //写入参数
                outPut.write(MapToByte(values));
                //关闭流
                outPut.close();
            }

            //开始请求，读取输出流,BufferedReader用于接收返回数据
            BufferedReader br=new BufferedReader(new InputStreamReader(http.getInputStream()));
            String resultLine;
            while((resultLine=br.readLine())!=null)
            {
                result+=resultLine;
            }
            return result;
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[100];
        int n = 0;
        while ((n = input.read(buffer,0,100))!=-1) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 取出所有COOKIES键值对
     * @param store COOKIES存储体
     * @return HashMap<String, String> COOKIES键值对
     */
    public static HashMap<String, String> CookiesToHashMap(CookieStore store) {
        boolean needUpdate = false;
        List<HttpCookie> cookies = store.getCookies();
        HashMap<String, String> cookieMap = null;
        if (cookieMap == null) {
            cookieMap = new HashMap<String, String>();
        }
        for (HttpCookie cookie : cookies) {
            String key = cookie.getName();
            String value = cookie.getValue();
            if (cookieMap.size() == 0 || !value.equals(cookieMap.get(key))) {
                needUpdate = true;
            }
            Log.i("Cookies",key+"|"+value);
            cookieMap.put(key, value);
        }
        return cookieMap;
    }

    public static void ClearCookies()
    {
        //清空所有
        CookieHandler.setDefault(null);
        //((CookieManager)CookieHandler.getDefault()).removeAll()
    }
    public static void ClearCookies(String uri)
    {
        //取得本地CookieManager
        CookieManager cookieManager =(CookieManager)CookieHandler.getDefault();
        //清楚指定URI  COOKIES
        cookieManager.getCookieStore().get(URI.create(uri)).clear();

    }
}
