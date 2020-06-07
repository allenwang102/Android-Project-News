package cn.itcast.news;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import java.util.List;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private LinearLayout loading;
    private ListView lvNews;
    private List<NewsInfo> newsInfos;
    private TextView tv_title;
    private TextView tv_description;
    private TextView tv_type;
    private NewsInfo newsInfo;
    private SmartImageView siv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        fillData();
    }
    //初始化控件
    private void initView(){
        loading = (LinearLayout)findViewById(R.id.loading);
        lvNews = (ListView) findViewById(R.id.lv_news);
    }
    //使用AsyncHttpClient访问网络
    private void fillData(){
        //创建AsyncHttpClient实例
        AsyncHttpClient client = new AsyncHttpClient();
        //使用GET方式请求
        client.get(getString(R.string.serverurl),new AsyncHttpResponseHandler(){
            //请求成功
            @Override
            public void onSuccess(int i,org.apache.http.Header[] headers,byte[] bytes){
                //调用JsonParse工具类解析JSON文件
                try{
                    String json = new String (bytes,"utf-8");
                    newsInfos = JsonParse.getNewsInfo(json);
                    if(newsInfo == null){
                        Toast.makeText(MainActivity.this,"解析失败",Toast.LENGTH_SHORT).show();
                    } else {
                        //更新界面
                        loading.setVisibility(View.INVISIBLE);
                        lvNews.setAdapter(new NewsAdapter());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i,org.apache.http.Header[] headers,byte[] bytes,Throwable throwable){
                Toast.makeText(MainActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //ListView适配器
    private class NewAdapter extends BaseAdapter{
        //List的Item数
        @Override
        public int getCount(){
            return newsInfos.size();
        }
        //得到ListView条目视图
        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            View view = view.inflate(MainActivity.this,R.layout.news_item,null);
            siv = (SmartImageView) view.findViewById(R.id.siv_icon);
            tv_title = (TextView) view.findViewById(R.id.iv_title);
            tv_description = (TextView) view.findViewById(R.id.iv_description);
            tv_type = (TextView) view.findViewById(R.id.iv_type);
            newsInfo = newsInfo.get(position);
            //SmartImageView加载指定路径图片
            siv.setImageUrl(newsInfo.getIcon(),R.mipmap.ic_launcher,R.mipmap.ic_launcher);
            //设置新闻标题
            tv_title.setText(newsInfo.getTitle());
            //设置新闻描述
            tv_description.setText(newsInfo.getContent());
            //1.一般新闻 2.专题 3.live
            int type = newsInfo.getType();
            switch (type){
                //不同新闻类型设置不同的颜色和不同的内容
                case 1:
                    tv_type.setText("评论：" + newsInfo.getComment());
                    break;
                case 2:
                    tv_type.setTextColor(Color.RED);
                    tv_type.setText("专题");
                    break;
                case 3:
                    tv_type.setTextColor(Color.BLUE);
                    tv_type.setText("LIVE");
                    break;
            }
            return view;
        }
        //条目对象
        @Override
        public  Object getItem(int position){
            return null;
        }
        //条目id
        @Override
        public long getItemId(int position){
            return 0;
        }
    }
}
