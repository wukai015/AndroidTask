# 自定义WebView验证隐式Intent的使用
本实验通过自定义WebView加载URL来验证隐式Intent的使用。

实验包含两个应用：

◼ 第一个应用：获取URL地址并启动隐式Intent的调用。

◼ 第二个应用：自定义WebView来加载URL。

## 新建一个工程用来获取URL地址并启动Intent

新建工程**IntentTest**

1.**activity_main**中增加一个EditText组件和一个按钮组件
```
<EditText
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:textSize="20sp"
       android:hint="输入网址"
       android:id="@+id/et_url"
       />

   <Button
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:text="浏览该网页"
       android:id="@+id/btn_go"
       />
```

2.为**MainActivity**指定视图，设置Intent

```
public class MainActivity extends AppCompatActivity {

   private Button btn_go;
   private EditText et_url;
   private String urlHead="https://";
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       et_url = (EditText) findViewById(R.id.et_url);
       btn_go = (Button) findViewById(R.id.btn_go);
       btn_go.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               // 创建Intent
               Intent intent = new Intent();
               // 为Intent设置Action属性
               intent.setAction(Intent.ACTION_VIEW);
               // 获取网址并解析为uri对象，设置Data属性
               intent.setData(Uri.parse(urlHead+et_url.getText().toString()));
               startActivity(intent);
           }
       });        
   }    
}
```
**实际效果：**

![image](https://img-blog.csdnimg.cn/20200524232911347.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODIzMTc5,size_16,color_FFFFFF,t_70)

## 新建一个工程使用WebView来加载URL

新建工程**MyBrowser**

1.**activity_webview.xml**，添加一个WebView组件
```
<WebView
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:id="@+id/my_webView"
       ></WebView>
```
2.**MyBrowser.java**，获取对应的Intent的Data属性，设置WebView组件
```
public class MyBrowser extends AppCompatActivity {
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_webview);

       WebView webView = (WebView)findViewById(R.id.my_webView);
       Intent intent = getIntent();
       //获取Intent的Data属性
       Uri data = intent.getData();
       URL url = null;
       try {
           //创建一个URL对象，参数为协议，主机名，路径
           url = new URL(data.getScheme(), data.getHost(), data.getPath());
       } catch (Exception e) {
           e.printStackTrace();
       }
       //WebView加载web资源
       webView.loadUrl(url.toString());
       //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
       webView.setWebViewClient(new WebViewClient() {
           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
               view.loadUrl(url);
               return true;
           }
       });
       webView.getSettings().setJavaScriptEnabled(true);//true允许使用JavaScript脚本
   }

}
```
3.**AndroidManifest.xml**文件中注册MyBrowser，设置意图过滤器intent-filter， 设置权限uses-permission。
```
<activity android:name=".MyBrowser">
           <intent-filter>
               <action android:name="android.intent.action.VIEW" />
               <category android:name="android.intent.category.DEFAULT"/>
               <category android:name="android.intent.category.BROWSABLE"/>
               <data android:scheme="http"></data>
               <data android:scheme="https"></data>
           </intent-filter>
       </activity>
```
```
</application>
  <uses-permission android:name="android.permission.INTERNET" />

</manifest>
```

**运行效果：**

输入网址，点击按钮跳转之后，出现选择项，选择自定义的MyBrowser进行浏览

![image](https://img-blog.csdnimg.cn/20200525000304438.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODIzMTc5,size_16,color_FFFFFF,t_70)


![image](https://img-blog.csdnimg.cn/20200525000646603.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODIzMTc5,size_16,color_FFFFFF,t_70)

