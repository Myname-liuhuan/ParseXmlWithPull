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
            XmlPullParser xmlPullParser=xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType=xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            int count=0;
            while(eventType!=xmlPullParser.END_DOCUMENT){
                String nodeName=xmlPullParser.getName();
                switch(eventType){
                    case XmlPullParser.START_TAG:{
                        if("id".equals(nodeName)){
                            id=xmlPullParser.nextText();
                        }else if("name".equals(nodeName)){
                            name= xmlPullParser.nextText();
                        }else if ("version".equals(nodeName)){
                            version=xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
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
                eventType=xmlPullParser.next();
                count++;
                Log.d("count",String.valueOf(count));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
