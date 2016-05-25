package youmo.slook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import youmo.slook.HttpHelper;

import static youmo.slook.HttpHelper.AsyncTask;

public class MainActivity extends AppCompatActivity {

    EditText et;
    Button btn;
    ListView lv;
    ArrayList<BookModel> bookData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //tv=(TextView)findViewById(R.id.textView_content);
        lv=(ListView)findViewById(R.id.listView_BookList);
        et=(EditText)findViewById(R.id.editText_search);
        btn=(Button)findViewById(R.id.button_search);



        bookData=new ArrayList<BookModel>();
        final ArrayAdapter adapter=new ArrayAdapter<BookModel>(this,R.layout.adapter_booklist,bookData){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v=getLayoutInflater().inflate(R.layout.adapter_booklist,null);
                TextView tv=(TextView) v.findViewById(R.id.text1);
                tv.setText(bookData.get(position).getName());
                return v;
            }
        };

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                AsyncTask("http://k.sogou.com/search?keyword="+et.getText(),new HttpHelper.IHttpResult(){
                    @Override
                    public void HttpResult(Object... objects) {
                        String html=(String)objects[0];
                        ArrayList<String> l=StringHelper.MidListString(html,"startReadingBook({","}");
                        for (String s:l)
                        {
                            BookModel b=new BookModel();
                            String url="id="+StringHelper.MidString(s,"id\":\"","\"")+"&md="+StringHelper.MidString(s,"md\":\"","\"");
                            b.setUrl("http://k.sogou.com/list?"+url);
                            b.setName(StringHelper.MidString(s,"name\":\"","\"")+" - "+StringHelper.MidString(s,"author\":\"","\""));
                            bookData.add(b);
                        }
                        lv.setAdapter(adapter);
                    }
                });
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(view.getContext(),ReadActivity.class).putExtra("url",bookData.get(position).getUrl()));
            }
        });

    }
    class BookModel{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String name;
        private String url;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
