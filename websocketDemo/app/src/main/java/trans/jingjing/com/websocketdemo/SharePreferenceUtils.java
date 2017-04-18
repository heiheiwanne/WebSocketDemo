package trans.jingjing.com.websocketdemo;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by xmq on 2017/4/18.
 */

public class SharePreferenceUtils {
    private SharedPreferences sp;
    public SharePreferenceUtils() {
        sp = MyApplication.getContext().getSharedPreferences("websocketlogin",MODE_PRIVATE);
    }

    static class SINGLE {
        private static SharePreferenceUtils spUtils = new SharePreferenceUtils();
    }
    public static SharePreferenceUtils getInstance() {
        return SINGLE.spUtils;
    }

    public boolean commit(String key ,String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        return editor.commit();
    }

    public String getString(String key , String defaultKey) {
       return sp.getString(key,defaultKey);
    }

}
