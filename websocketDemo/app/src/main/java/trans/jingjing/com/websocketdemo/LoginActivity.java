package trans.jingjing.com.websocketdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by xmq on 2017/4/18.
 */

public class LoginActivity extends AppCompatActivity{

    private String mSystemId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        mSystemId = SharePreferenceUtils.getInstance().getString(MainActivity.SYSTEMID,"81951087878");
        ((EditText) findViewById(R.id.system_id_et)).setText(mSystemId);

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str  = ((EditText) findViewById(R.id.system_id_et)).getText().toString();
                if (TextUtils.isEmpty(str)) {
                    Toast.makeText(LoginActivity.this,"请输入用户ID",Toast.LENGTH_LONG).show();
                } else {
                    SharePreferenceUtils.getInstance().commit(MainActivity.SYSTEMID,str);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.SYSTEMID, str);
                    startActivity(intent);
                }
            }
        });
    }
}
