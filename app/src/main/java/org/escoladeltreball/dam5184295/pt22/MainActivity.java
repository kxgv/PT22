package org.escoladeltreball.dam5184295.pt22;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.PrecomputedText;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton emojiButton, cameraButton, sendButton;
    LinearLayout linearLayout, secondlinear;
    LinearLayout.LayoutParams params;
    ScrollView scrollView;
    Boolean left = true;
    Boolean garanted = false;
    Toolbar mytoolbar;
    EditText message;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 3;
    static final int REQUEST_IMAGE_CAPTURE2 = 2;

    private static final int MY_PERMISSIONS_REQUESTS = 70;
    private Camera camera;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_layout);

        garanted = false;
        left = true;

        linearLayout = findViewById(R.id.first_linear);
        secondlinear = findViewById(R.id.secon_linear);
        cameraButton = findViewById(R.id.cameraButton);
        emojiButton = findViewById(R.id.emojiButton);
        scrollView = findViewById(R.id.scroll_view);
        sendButton = findViewById(R.id.sendButton);
        message = findViewById(R.id.my_edit_text);

        sendButton.setOnClickListener(this);
        emojiButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);

        checkPermissions();
    }

    public void setScrollView(View view){


        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, scrollView.getBottom());
            }
        });
    }


    @Override
    protected void onPause() {
        if (camera != null){
            camera.restore();
            camera = null;
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.sendButton:
                sendMessage(v);
                setScrollView(v);
                break;

            case R.id.cameraButton:
                takePhoto();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendMessage(View v){

        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.gravity = left ? Gravity.START : Gravity.END;
        params.topMargin = 15;

        TextView textView = new TextView(this);

        String space = "  ";
        String msg = message.getText().toString();
        String data = new SimpleDateFormat("HH:mm", Locale.UK).format(new Date());

        StringBuilder sms = new StringBuilder();
        sms.append(msg).append(space).append(data);

        int endIndex = msg.length();

        SpannableString spannableString = new SpannableString(sms);
        spannableString.setSpan(new RelativeSizeSpan(2f), 0, endIndex, 0);
        textView.setText(spannableString);

        if (left) {

            textView.setScrollBarSize(1);
            textView.setTextSize(10);
            textView.setPadding(30, 10, 20, 10);
            textView.setElevation(2f);
            textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_message, null));
            left = false;

        } else {

            textView.setScrollBarSize(1);
            textView.setTextSize(10);
            textView.setPadding(20, 10, 30, 10);
            textView.setElevation(2f);
            textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_blue_message, null));
            left = true;
        }

        textView.setLayoutParams(params);
        linearLayout.addView(textView);
        message.setText("");
    }


    public void takePhoto(){

        try {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;

                try {

                    photoFile = createImageFile(false);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("foto", "takePhoto: " + e.getCause() + e.getMessage());
                }

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "org.escoladeltreball.dam5184295.pt22.fileprovider", photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE2);

                    Log.d("test", "takePhoto(): " + photoURI + "\n" + currentPhotoPath);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("test", "test: " + e.getMessage() + e.getCause());
        }
    }

    private File createImageFile(boolean Video) throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageVids = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File image;

        if (!Video){
            String imageFileName = "JPEG_" + timeStamp + "_";

            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            currentPhotoPath = image.getAbsolutePath();
            Log.d("foto", "takePhoto: " + currentPhotoPath);
        } else {

            String imageFileName = "VID_" + timeStamp + "_";
            image = File.createTempFile(
                    imageFileName,
                    ".mp4",
                    storageVids
            );
            currentPhotoPath = image.getAbsolutePath();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(imageBitmap);

            imageView.setLayoutParams(params);
            linearLayout.addView(imageView);

        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){

            try {

                checkPermissions();
                if (garanted == true){
                    Uri videoUri = data.getData();

                    if (videoUri != null){
                        VideoView videoView = findViewById(R.id.videoView);
                        videoView.setVideoURI(videoUri);
                        videoView.start();
                        galleryAddPic();

                    } else Log.d("test", "onActivityResult: no te permissos a external");
                }

            } catch (Exception e){
                e.printStackTrace();
                Log.d("test", "onActivityResult: " + e.getMessage());
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE2 && resultCode == RESULT_OK){

            try {

                ImageView imageView = new ImageView(this);

                int targetW = 300;
                int targetH = 300;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(currentPhotoPath, options);


                int photoW = options.outWidth;
                int photoH = options.outHeight;


                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                options.inSampleSize = scaleFactor;
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;


                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
                imageView.setImageBitmap(bitmap);
                imageView.setLayoutParams(params);
                linearLayout.addView(imageView);

                galleryAddPic();

            } catch (Exception e){
                e.printStackTrace();
                Log.d("test", "onActivityResult: error " + e.getCause());
            }
        }
    }

    private void galleryAddPic(){

        try{

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            intent.setData(Uri.fromFile(f));
            sendBroadcast(intent);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkPermissions(){

        int permCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!(permCheck == PackageManager.PERMISSION_GRANTED) | (!(permCheck2 == PackageManager.PERMISSION_GRANTED))){

            ActivityCompat.requestPermissions(this,
                    new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE } ,
                    MY_PERMISSIONS_REQUESTS);

            Log.d("test", " no rationale");

        } else garanted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case MY_PERMISSIONS_REQUESTS: {

                if (grantResults.length > 0
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)){

                    garanted = true;

                } else {
                    garanted = false;
                }
            }
        }
    }
}