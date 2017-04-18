package trans.jingjing.com.websocketdemo;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wanghao on 16/8/8.
 */
public class LogToFileUtil {
    private static final SimpleDateFormat dirFormat = new SimpleDateFormat("yyyy年MM月dd号");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SS");
    private static final String SEP = "||";

    synchronized static protected void log(final Context context, String type, String tag, String content) {
        if (context == null) {
            return;
        }
        type = (type == null) ? "unknown_type" : type;
        tag = (tag == null) ? "" : tag;
        content = (content == null) ? "" : content;

        File dir = Environment.getExternalStorageDirectory();
        dir = new File(dir, "ROOT_PATH/cache/");
        dir = new File(dir, context.getPackageName());
        dir = new File(dir, "full_log");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String dateStr = dirFormat.format(date);
        String time = timeFormat.format(date);

        File path = new File(dir, "log-" + dateStr + ".log");
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path, true);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(time);
            stringBuilder.append(SEP);
            stringBuilder.append(type);
            stringBuilder.append(SEP);
            stringBuilder.append(tag);
            stringBuilder.append(SEP);
            stringBuilder.append(content);
            stringBuilder.append("\n");
            outputStream.write(stringBuilder.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
