package com.example.imagecropper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.imagecropper.camera.CameraActivity;
import com.example.imagecropper.camera.Option;
import com.example.imagecropper.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity" ;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;
    private static final int RECORD_AUDIO_PERMISSION_CODE =3 ;
    private static final int OPEN_GALLERY_CODE =4 ;
    public static final int EDIT_ACTIVITY_CODE =5 ;
    LinearLayout linLayCamera,linLayGallery;
    RecyclerView recyclerView;
    TextView tvNotYet;
    private static final int GUIDELINES_ON_TOUCH = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        linLayCamera=findViewById(R.id.linlay_camera);
        linLayGallery=findViewById(R.id.linlay_gallery);
        recyclerView=findViewById(R.id.recycler_view);
        tvNotYet=findViewById(R.id.tv_not_processed);

        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        requestPermisson();
        linLayGallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                        ||checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
                else{
                    openGallery();
                }
            }
        });

        linLayCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                        ||checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
                else{
                    if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                    }
                    else{
                        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO_PERMISSION_CODE);
                        }
                        else{
                            openCameraActivity();
                        }
                    }
                }
            }
        });


    }

    private void openCameraActivity() {
        Intent intent=new Intent(MainActivity.this, CameraActivity.class);
        startActivityForResult(intent,EDIT_ACTIVITY_CODE);
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
    }

    private void openGallery() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_GALLERY_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //show images
            }
            else{
                Toast.makeText(MainActivity.this,"Please Give Permission for accessing files",Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
            }
        }
        if(requestCode==CAMERA_PERMISSION_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //
            }
            else{
                Toast.makeText(MainActivity.this,"Please Give Permission for accessing camera",Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
            }
        }
        if(requestCode==RECORD_AUDIO_PERMISSION_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //show images
                getImages();
            }
            else{
                Toast.makeText(MainActivity.this,"Please Give Permission for accessing camera",Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO_PERMISSION_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermisson()
    {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                ||checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
        else{
            getImages();
        }

    }

    List<ImageModel>bitmaps=new ArrayList<>();
    private void getImages() {
        bitmaps.clear();
        String path=getExternalFilesDir(null)+"/Files";
        File file=new File(path);
        if(!file.exists())
        {
            return;
        }
        File files[]=file.listFiles();
        Arrays.sort(files, Collections.reverseOrder());
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        for(File file1:files)
        {
            Bitmap image =decodeBitmapFromFile(file1.getAbsolutePath(), width/3,150);
            bitmaps.add(new ImageModel(image,file1.getAbsolutePath()));
        }
        if(bitmaps.size()>0)
        {
            recyclerView.setVisibility(View.VISIBLE);
            tvNotYet.setVisibility(View.GONE);
            ImageAdapter adapter=new ImageAdapter(MainActivity.this,bitmaps);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermisson();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==OPEN_GALLERY_CODE)
            {
                Uri uri=data.getData();
                if(uri!=null)
                {
                    startEditActivity(uri);
                }
            }
        }
    }

    private void startEditActivity(Uri uri) {
        Intent intent=new Intent(MainActivity.this,EditActivity.class);
        intent.putExtra("uri",uri.toString());
        Log.d(TAG, "startEditActivity: "+uri);
        startActivityForResult(intent,EDIT_ACTIVITY_CODE);
//        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
    }



        public  Bitmap decodeBitmapFromFile(String imagePath,
                                                  int reqWidth,
                                                  int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imagePath, options);
        }


        private  int calculateSampleSize(BitmapFactory.Options options,
                                               int reqHeight,
                                               int reqWidth) {

            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and
                // keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;

        }


}