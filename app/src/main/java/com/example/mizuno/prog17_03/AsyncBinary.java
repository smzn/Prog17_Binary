package com.example.mizuno.prog17_03;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mizuno on 2017/03/01.
 */

public class AsyncBinary extends AsyncTask<Void, Void, byte[]> {

    final static private String BOUNDARY = "MyBoundaryString";
    private String mURL;
    ProgressDialog dialog;//ダイアログ用
    Context context;//Toast用
    int flag = 1;//Booleanの方がいいけどミスがこわいので今はそのまま

    String fname;
    byte[] binary;

    public AsyncBinary(Context context) {
        this.context = context;
        mURL = "http://ms000.sist.ac.jp/oc/recognitions/import";
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        byte[] data = makePostData();
        byte[] result = send(data);
        return result;
    }

    private byte[] send(byte[] data) {
        if (data == null)
            return null;

        byte[] result = null;
        HttpURLConnection connection = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;

        try {
            URL url = new URL(mURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 接続
            connection.connect();

            // 送信
            OutputStream os = connection.getOutputStream();
            os.write(data);
            os.close();

            // レスポンスを取得する
            byte[] buf = new byte[10240];
            int size;
            is = connection.getInputStream();
            while ((size = is.read(buf)) != -1)//1回の書きこみ量には限度があるので数回に分けて書きこむ
            {
                baos.write(buf, 0, size);
            }
            result = baos.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
            flag = -1;
        } finally {
            try {
                is.close();
            } catch (Exception e) {}

            try {
                connection.disconnect();
            } catch (Exception e) {}

            try {
                baos.close();
            } catch (Exception e) {}
        }
        return result;
    }

    private byte[] makePostData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            // 画像の設定
            String key = fname;
            byte[] data = binary;
            String name = "data[Recognition][PhotoFile]";

            baos.write(("--" + BOUNDARY + "\r\n").getBytes());
            baos.write(("Content-Disposition: form-data;").getBytes());
            baos.write(("name=\"" + name + "\";").getBytes());//Post受け取り時のname
            baos.write(("filename=\"" + key + "\"\r\n").getBytes());//ファイルの名前
            baos.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());
            baos.write(data);//バイナリデータ
            baos.write(("\r\n").getBytes());

            // 最後にバウンダリを付ける
            baos.write(("--" + BOUNDARY + "--\r\n").getBytes());

            return baos.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
            flag = -1;
            return null;
        } finally {
            try {
                baos.close();
            } catch (Exception e) {}
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("送信中");
        dialog.setMessage("Uploading...");
        dialog.show();
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        if (dialog != null) {
            dialog.dismiss();

            if (flag == 1) {
                Toast.makeText(context, "送信終了しました", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "送信失敗しました", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void setValue(String name, byte[] bytes) {
        fname = name;
        binary = bytes;
    }
}
