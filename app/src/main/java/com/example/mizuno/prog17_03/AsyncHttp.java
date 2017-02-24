package com.example.mizuno.prog17_03;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by mizuno on 2017/02/22.
 */

public class AsyncHttp extends AsyncTask<File, Integer, String> {
    ProgressDialog dialog;
    Context context;
    int flag = 1;

    public AsyncHttp(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(File... params) {
        File filename = params[0];
        try {
            File file = new File(filename.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://ms000.sist.ac.jp/oc/recognitions/import");
            MultipartEntity entity = new MultipartEntity();
            //entity.addPart("キー名", new StringBody("送りたい文字列"));
            entity.addPart("data[Recognition][PhotoFile]", new FileBody(file));
            post.setEntity(entity);
            httpClient.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            flag = -1;
        } catch (IOException e) {
            e.printStackTrace();
            flag = -1;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(dialog != null){
            dialog.dismiss();
            if(flag == 1) {
                Toast.makeText(context, "送信終了しました", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "送信失敗しました", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("送信中");
        dialog.setMessage("Uploading...");
        dialog.show();
    }
}
