package org.yooz.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Yooz on 2016/2/2.
 */
public class SmsUtils {


    public interface BackUpSmsInterface {
        public void befor(int count);
        public void onBackUpSms(int progress);
    }


    public static boolean backUp(Context context, BackUpSmsInterface pd) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = contentResolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            int count = cursor.getCount();
            pd.befor(count);
            int progress = 0;

            File file = new File(Environment.getExternalStorageDirectory(),"backupSMS.xml");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                XmlSerializer xmlSerializer = Xml.newSerializer();
                xmlSerializer.setOutput(fileOutputStream, "utf-8");
                xmlSerializer.startDocument("utf-8", true);
                xmlSerializer.startTag(null, "smss");

                while(cursor.moveToNext()) {
                    xmlSerializer.startTag(null,"sms");

                    String address = cursor.getString(0);
                    xmlSerializer.startTag(null, "address");
                    xmlSerializer.text(address);
                    xmlSerializer.endTag(null, "address");

                    String date = cursor.getString(1);
                    xmlSerializer.startTag(null, "date");
                    xmlSerializer.text(date);
                    xmlSerializer.endTag(null, "date");

                    String type = cursor.getString(2);
                    xmlSerializer.startTag(null, "type");
                    xmlSerializer.text(type);
                    xmlSerializer.endTag(null, "type");

                    String body = cursor.getString(3);
                    xmlSerializer.startTag(null, "body");
                    xmlSerializer.text(Crypto.encrypt("yooz",body));
                    xmlSerializer.endTag(null, "body");

                    xmlSerializer.endTag(null, "sms");
                    progress++;
                    pd.onBackUpSms(progress);
                }

                cursor.close();
                xmlSerializer.endTag(null, "smss");
                xmlSerializer.endDocument();

                fileOutputStream.flush();
                fileOutputStream.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
