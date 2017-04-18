package trans.jingjing.com.websocketdemo;

import android.util.Log;


public class LogUtils {
    protected static final String TAG = "ok_log";
    protected static final boolean RENDER_LOG = false;
    protected static boolean IS_LOG = true;

    public static boolean isLogOpen() {
        return IS_LOG;
    }

    public static boolean setLogIsOpen(boolean islog) {
        IS_LOG = islog;
        return IS_LOG;
    }

    public static void d(String key, String value) {
        if (IS_LOG) {
            if (key == null) {
                key = "";
            }
            if (value == null) {
                value = "";
            }
            Log.d(key, value);
            LogToFileUtil.log(MyApplication.getContext(), "d", key, value);
        }
    }

    public static void d(String value) {
        if (IS_LOG) {
            if (value == null) {
                value = "";
            }
            log(Log.DEBUG, value, null);
            LogToFileUtil.log(MyApplication.getContext(), "d", value, null);
        }
    }

    public static void i(String key, String value) {
        if (IS_LOG) {
            if (key == null) {
                key = "";
            }
            if (value == null) {
                value = "";
            }
            Log.i(key, value);
            LogToFileUtil.log(MyApplication.getContext(), "i", key, value);
        }
    }

    public static void e(String key, String value) {
        if (IS_LOG) {
            if (key == null) {
                key = "";
            }
            if (value == null) {
                value = "";
            }
            Log.e(key, value);
            LogToFileUtil.log(MyApplication.getContext(), "e", key, value);
        }
    }

    public static void renderDebug(String key, String logMsg) {
        if (IS_LOG && RENDER_LOG) {

            Log.e(key, logMsg);
        }
    }

    public static void e(Throwable throwable) {
        e(throwable, throwable.getMessage());
    }

    private static void log(int priority, String msg, Throwable throwable) {
        if (!IS_LOG)
            return;

        try {
            Log.println(
                    priority,
                    "damai",
                    msg
                            + (throwable == null ? "" : Log
                            .getStackTraceString(throwable)));
        } catch (Exception e) {
            Log.e("damai", "Failed to log: " + e.getMessage());
        }
    }

    public static void e(Throwable throwable, String error) {
        if (!IS_LOG)
            return;

        if (error == null) {
            error = "";
        }

        try {
            log(Log.ERROR, error, throwable);
            LogToFileUtil.log(MyApplication.getContext(), "e", error, throwable.toString());
        } catch (Exception e) {
            Log.e("damai", "Failed to e: " + e.getMessage());
        }
    }

    public static void e(String error) {
        if (IS_LOG) {
            if (error == null) {
                error = "";
            }
            Log.e(TAG, error.toString());
            LogToFileUtil.log(MyApplication.getContext(), "e", error.toString(), null);
        }
    }


    public static boolean isLoggable(String var0, int var1) {
        return Log.isLoggable(var0, var1);
    }


}