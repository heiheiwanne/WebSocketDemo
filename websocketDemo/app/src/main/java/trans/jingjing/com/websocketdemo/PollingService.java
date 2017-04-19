package trans.jingjing.com.websocketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by xmq on 2017/4/18.
 */
public class PollingService extends Service {

    public static final String ACTION = "com.okay.service.PollingService";


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("PollingService 服务被创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("AlarmManager 发出的定时器，service依然在进行～");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent().setAction(MainActivity.BROAST_ACTION));
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}