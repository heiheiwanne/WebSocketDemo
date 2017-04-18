package trans.jingjing.com.websocketdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by xmq on 2017/4/18.
 */

public class MyApplication extends Application{

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
