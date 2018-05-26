package com.example.parsexmlwithpull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button01=findViewById(R.id.button01);
        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestWithHttp();
            }
        });
    }

    private void sendRequestWithHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();//步骤1，实例化客户端client
                    Request request=new Request.Builder()//步骤2，实例化请求Request
                            //指定服务器是本机
                            .url("Http://192.168.1.102/httpgetxmldata.xml")
                            .build();
                    Response response=client.newCall(request).execute();//步骤3，用response接收返回
                    String responseData=response.body().string();//步骤4，将返回的response里面的Body转化为String，以便接下来的提取
                    parseXmlUsePull(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void parseXmlUsePull(String xmlData){
        try{
            XmlPullParserFactory xmlPullParserFactory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=xmlPullParserFactory.newPullParser();//得到解析器对象
            xmlPullParser.setInput(new StringReader(xmlData));//使用给定的方法接收数据
            int eventType=xmlPullParser.getEventType();//得到当前所处的 事件类型
            //事件就是:每一个部分都算作不同的事件
            //eg:<app>PullThings</app>   这个语句含有三个事件
            String id="";
            String name="";
            String version="";
            int count=0;//计算循环的次数，方便理解原理
            while(eventType!=xmlPullParser.END_DOCUMENT){//当解析器指到文档最后的时候跳出循环
                String nodeName=xmlPullParser.getName();//eg:id name version String类型
                switch(eventType){
                    case XmlPullParser.START_TAG:{//eg:<apps> <app> <id>
                        if("id".equals(nodeName)){
                            id=xmlPullParser.nextText();//当下一个事件是text则返回这个text,不是text就返回空
                        }else if("name".equals(nodeName)){
                            name= xmlPullParser.nextText();
                        }else if ("version".equals(nodeName)){
                            version=xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{//eg:</apps> </app> </id>
                        if ("app".equals(nodeName)){
                            Log.d("huan","id is:"+id);
                            Log.d("huan","name is:"+name);
                            Log.d("huan","version is:"+version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType=xmlPullParser.next();//next()表示的是下一个事件，可以从循环次数以及源文件的事件数得到，两者相差1，就是最后的一次由于不满足条件所以跳出循环
                count++;
                Log.d("count",String.valueOf(count));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
