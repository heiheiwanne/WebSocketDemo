package trans.jingjing.com.websocketdemo;

import android.content.Context;
import android.os.PowerManager;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by xmq on 2017/4/18.
 */

public class WakeLockManager {

    private PowerManager.WakeLock mWakelock;

    public WakeLockManager(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);// init powerManager
//            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK,"target");
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "pollingservice");
    }

    public void awake() {
        mWakelock.acquire();
    }

    public void release() {
        mWakelock.release();
    }
}
