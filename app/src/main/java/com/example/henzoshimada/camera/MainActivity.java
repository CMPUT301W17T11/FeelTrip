package com.example.henzoshimada.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ByteArrayOutputStream photoStream = new ByteArrayOutputStream();
    private String encodedPhoto;
    private String imagePathAndFileName;

    Uri imageFileUri;
    private static int TAKE_PHOTO = 2;
    Activity activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonTakeImage = (Button) findViewById(R.id.buttonCapture);

        activity = this;
        context = this;

        buttonTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loginPermissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
                if (loginPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.d("tag", "1");
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    Log.d("tag", "2");
                    takeAPhoto();
                }
            }
        });

        //TextView tv = (TextView)findViewById(R.id.textView);
        //tv.setText(encodedPhoto);

    }


    public void takeAPhoto() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdir();
        imagePathAndFileName = path + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File imageFile = new File(imagePathAndFileName);
        imageFileUri = Uri.fromFile(imageFile);

        Log.d("tag", "Can make file path?");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        Log.d("tag", "Take photo");
        startActivityForResult(intent, TAKE_PHOTO);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d("tag", "In Take Photo");
        if (requestCode == TAKE_PHOTO){
            TextView tv = (TextView)findViewById(R.id.textView);
            if (resultCode == RESULT_OK){
                Log.d("tag", "Taking photo");
                //tv.setText("Photo completed!");

                //ImageButton ib = (ImageButton)findViewById(R.id.imageButton);
                //ib.setImageDrawable(Drawable.createFromPath(imageFileUri.getPath()));

                Bitmap photo = (Bitmap) intent.getExtras().get("data");
                photo.compress(Bitmap.CompressFormat.JPEG, 100, photoStream);
                byte[] compressedPhoto = photoStream.toByteArray();
                encodedPhoto = Base64.encodeToString(compressedPhoto, Base64.DEFAULT);
                ImageView imageView = (ImageView)findViewById(R.id.imgView);
                imageView.setImageBitmap(photo);

                tv.setText(encodedPhoto);
                /*
                Uri selectedImage = intent.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                ImageView imageView = (ImageView)findViewById(R.id.imgView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                */
            }
            else
            if (resultCode == RESULT_CANCELED)
                tv.setText("Photo was canceled!");
            else
                tv.setText("What happened?!!");
        }
    }
}
