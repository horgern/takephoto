package com.example.chz.testapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picture = (ImageView)findViewById(R.id.picture);

        Button take_photo = (Button)findViewById(R.id.take);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                        Log.d("TAG", "outputImage delete");
                    }
                    outputImage.createNewFile();
                    Log.d("TAG", "got it");
                } catch (Exception e) {
                    Log.e("ERROR", Log.getStackTraceString(e));
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.chz.testapplication.fileprovider", outputImage);
                    Log.d("TAG", "The paths is : " + imageUri);

                } else {
                    imageUri = Uri.fromFile(outputImage);
                    Log.d("TAG", "The paths is : " + imageUri + "(SDK < 24)");
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "content provider");
        switch (requestCode) {
            case TAKE_PHOTO :

                 {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        Log.d("TAG", "show image");
                    } catch (FileNotFoundException e) {
                        Log.e("ERROR", Log.getStackTraceString(e));
                    }
                }
                Log.d("TAG", "requestCode : " +  String.valueOf(requestCode));
                Log.d("TAG", "RESULT_OK : " +  String.valueOf(RESULT_OK));
                Log.d("TAG", "here ============");
                Intent intent = new Intent(this, NotificationActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Notification")
                        .setContentText("This is content text")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();
                manager.notify(1, notification);
                break;
            default:
                Log.d("TAG", "requestCode : " +  String.valueOf(requestCode));
                break;
        }
    }
}
