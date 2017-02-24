package com.example.mizuno.prog17_03;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Prog1703Activity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_CAPTURE = 100;
    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";//終了時の状態を保存するときのキー
    Button cameraBtn, sendBtn;
    private Uri mImageUri;
    File file;
    File photoDir = null;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prog1703);

        cameraBtn = (Button) findViewById(R.id.button1);
        cameraBtn.setOnClickListener(this);
        sendBtn = (Button)findViewById(R.id.button2);
        sendBtn.setOnClickListener(this);
        ctx = Prog1703Activity.this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {//写真撮影ボタン
            mImageUri = getPhotoUri();
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, IMAGE_CAPTURE);
        }
        if(v.getId() == R.id.button2){//送信ボタン
            AsyncHttp post = new AsyncHttp(ctx);//コンテキストを渡す(Toastのため)
            post.execute(file);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                setImageView();
            }
        }
    }

    private void setImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        imageView.setImageURI(mImageUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_IMAGE_URI, mImageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageUri = (Uri) savedInstanceState.get(KEY_IMAGE_URI);
        setImageView();
    }

    private Uri getPhotoUri() {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = title + ".jpg";
        String path = dirPath + "/" + fileName;//絶対パス
        file = new File(path);
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DATA, path);
        values.put(Images.Media.DATE_TAKEN, currentTimeMillis);
        if (file.exists()) {
            values.put(Images.Media.SIZE, file.length());
        }
        Uri uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }

    private String getDirPath() {
        String dirPath = "";
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            photoDir = new File(extStorageDir.getPath() + "/DCIM/Camera");
        }

        if (photoDir != null) {
            if (!photoDir.exists()) {
                photoDir.mkdirs();//ファイルがなかった場合作成
            }
            if (photoDir.canWrite()) {
                dirPath = photoDir.getPath();
            }
        }
        return dirPath;
    }
}
