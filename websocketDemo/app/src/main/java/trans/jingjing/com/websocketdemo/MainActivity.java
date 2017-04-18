package trans.jingjing.com.websocketdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static String SYSTEMID = "systemId";
    public static String BROAST_ACTION = "com.okay.websocket.broadcast";

    private String WEBSOCKETPATH = "http://spear.xk12.cn/socket.io/?EIO=3&transport=websocket";
    private static String PING_baidu_URL = "www.baidu.com"; //ping的外网
    private static String PING_myhost_URL = "spear.xk12.cn";//ping 的内网

    private static int SEND_HEART_PACKET = 2; //发包的间隔 second
    private static int PING_TIME = 3;//ping的次数
    private static int RE_CONNECT_TIME = 5; //连接失败之后重连次数


    public WebSocket socket = null;

    private boolean isOpen = false;
    private Thread mThread;

    private TextView mPrintLogTv, mTitleTv;
    private ScrollView scrollview;
    private StringBuilder mLogSb = new StringBuilder();
    private String mSystemId;

    private StringBuilder mExceptionSb = new StringBuilder();


    private static final OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)//允许失败重试
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.okhttp_start).setOnClickListener(this);
        findViewById(R.id.okhttp_stop).setOnClickListener(this);
        findViewById(R.id.clear_btn).setOnClickListener(this);
        mPrintLogTv = (TextView) findViewById(R.id.log_tv);
        mTitleTv = (TextView) findViewById(R.id.text_state);
        scrollview = (ScrollView) findViewById(R.id.scrollview);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSystemId = bundle.getString(SYSTEMID);
        } else {
            mSystemId = SharePreferenceUtils.getInstance().getString(MainActivity.SYSTEMID, "81951087878");
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startThread();
            }
        });

//        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "PostLocationService");
//        wakeLock.acquire();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,intentFilter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        AlarmManagerUtils.stopPollingService(this,PollingService.ACTION);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okhttp_start:
                startSocket();
                break;
            case R.id.okhttp_stop:
                stopSocket();
                break;
            case R.id.clear_btn:
                mLogSb.delete(0, mLogSb.length());
                mPrintLogTv.setText(mExceptionSb);
                break;
        }
    }


    private void startSocket() {
//        try {
//            mThread.start();
//        } catch (Exception e) {
//        }

        //创建WebSocket链接
        Request request = new Request.Builder().url(WEBSOCKETPATH).build();

        client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                appendTv("onOpen text:" + response.toString());
                socket = webSocket;
                isOpen = true;
                AlarmManagerUtils.startPollingService(MainActivity.this, SEND_HEART_PACKET, PollingService.ACTION);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                appendTv("onMessage text:" + text);
                if (!"40".equals(text)) return;
                try {
//                    String token = RSAEncodeUtils.encryptByPrivateKey("81951087878_1223234435");
                    String token = RSAEncodeUtils.encryptByPrivateKey(mSystemId + "_" + new Date().getTime());
                    JSONObject jsonObject = new JSONObject("{\"id\":\"" + mSystemId + "\",\"class_id\":\"29367\",\"type\":0,\"token\":\"" + token + "\",\"name\":\"徐明强测试\"}");
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put("student_auth");
                    jsonArray.put(jsonObject.toString());
                    socket.send("42" + jsonArray.toString());
                    appendTv("send text:" + jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                appendTv("onMessage  bytes:" + bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                appendTv("onClosing: code:" + code + "reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                appendTv("onClosed: code:" + code + "reason:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {

                isOpen = false;
                mExceptionSb.delete(0, mExceptionSb.length());

                //打印错误
                String throwableStr;
                if (t instanceof SocketTimeoutException) {
                    throwableStr = "onFailure 连接超时:" + t.toString();
                } else if (t instanceof UnknownHostException) {
                    throwableStr = ("onFailure 服务器主机未找到:" + t.toString());
                } else if (t instanceof ConnectException) {
                    throwableStr = ("onFailure 连接异常" + " Throwable: " + t.toString());
                } else {
                    throwableStr = ("onFailure 其他错误" + " Throwable: " + t.toString());
                }
                appendTv(throwableStr);

                //打印返回结果
                String str = "  Response:";
                if (response != null) {
                    str = str + response.toString();
                }
                appendTv(str);

                //打印异常信息
                String exceptionStr = Utils.getErrorInfoFromException(t);
                appendTv(" 异常信息: " + exceptionStr);

                //打印dns
                String dnsStr;
                try {
                    dnsStr = ("DNS:  " + client.dns().lookup("spear.xk12.cn").toString() + "\n\n");
                } catch (UnknownHostException e) {
                    dnsStr = ("DNS 获取异常信息：" + Utils.getErrorInfoFromException(e));
                }
                appendTv(dnsStr);

                //ping操作
                ping(PING_baidu_URL, PING_TIME);

                //弹出notifications
                Utils.notification(MyApplication.getContext(), "长链接断开", exceptionStr);
            }
        });
    }

    private void setTitle(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitleTv.setText(str);
            }
        });
    }

    private void appendTv(String str) {
        String logStr = Utils.getTime(System.currentTimeMillis()) + str + "\n";
        LogUtils.e("===WebSocket test==:", logStr);
        mLogSb.append(logStr);
        mExceptionSb.append(logStr);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPrintLogTv.setText(mLogSb);
//                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    private void startThread() {
        while (true) {
            if (socket != null && isOpen) {
                String sendStr = "2";
                socket.send(sendStr);
                appendTv("onSend text : " + sendStr);
                setTitle("发包中...");
            } else {
                setTitle("未发包");
            }

            try {
                Thread.sleep(SEND_HEART_PACKET);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopSocket() {

        appendTv("Close(" + 1000 + ")客户端手动断开");
        appendTv("---------停止发包---------");
        setTitle("未发包");

        isOpen = false;
        if (mThread !=null && !mThread.isInterrupted()) {
            mThread.interrupt();
        }
        if (socket != null) {
            socket.close(1000, "客户端手动断开");
        }
        AlarmManagerUtils.stopPollingService(this,PollingService.ACTION);
//        client.dispatcher().executorService().shutdown();
    }


    private void ping(final String IP, int times) {
        appendTv("进行ping " + IP + " 操作，下面是异步输出结果：");
        try {
            Ping.onAddress(IP).setTimeOutMillis(1000).setTimes(times).doPing(new Ping.PingListener() {
                @Override
                public void onResult(PingResult pingResult) {
                    appendTv("onResult（返回结果） :  " + pingResult.toString());
                }

                @Override
                public void onFinished(PingStats pingStats) {
                    appendTv("onFinished（ping结束）: " + pingStats.toString() + "\n\n");
                    if (PING_baidu_URL.equals(IP)) {
                        ping(PING_myhost_URL, PING_TIME);
                    }
                }
            });
        } catch (UnknownHostException e) {
            appendTv("ping 出现异常 : " + Utils.getErrorInfoFromException(e));
            if (PING_baidu_URL.equals(IP)) {
                ping(PING_myhost_URL, PING_TIME);
            }
        }
    }



    private int reconnect = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            appendTv("-----广播接受者在继续(进程存在)----");
            if (socket != null && isOpen) {
                reconnect = 0;
                String sendStr = "2";
                socket.send(sendStr);
                appendTv("onSend text : " + sendStr);
                setTitle("发包中...");
            } else {
                reconnect++;
                if (reconnect ==RE_CONNECT_TIME){
                    AlarmManagerUtils.stopPollingService(MainActivity.this,PollingService.ACTION);
                    return;
                }
                setTitle("未发包");
                startSocket();
            }
//            Toast.makeText(MainActivity.this,"收到了广播", Toast.LENGTH_LONG).show();
        }
    };





}
