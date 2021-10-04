package com.example.imagecropper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagecropper.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    CropImageView cropImageView;
    ImageView finalImageView;
    ImageView imgCrop,imgUndo,imgRotate;
    TextView tvSave;
    SeekBar seekBar;
    Uri uri;
    List<Bitmap>bitmaps=new ArrayList<>();
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit);

        cropImageView=findViewById(R.id.CropImageView);
        finalImageView=findViewById(R.id.img_final_image);
        imgCrop=findViewById(R.id.img_crop);
        imgUndo=findViewById(R.id.img_undo);
        tvSave=findViewById(R.id.tv_save);
        seekBar=findViewById(R.id.seek_bar);
        imgRotate=findViewById(R.id.img_rotate);


        if(getIntent().hasExtra("uri"))
        {
             path=getIntent().getStringExtra("uri");
              uri = Uri.parse(path);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            float dip = 280f;
            Resources r = getResources();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip,
                    r.getDisplayMetrics()
            );
            Log.d(TAG, "onCreate: "+px);
            height=(int)px;
            Log.d(TAG, "onCreate: "+height);
                if(getIntent().hasExtra("from_dialog"))
                {
                     Bitmap bitmap = Helper.bitmap;
                    bitmap=Bitmap.createScaledBitmap(bitmap,width,height,false);
                    cropImageView.setImageBitmap(bitmap);
                    finalImageView.setImageBitmap(bitmap);
                    bitmaps.add(bitmap);
                }
                else {


                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        bitmap=Bitmap.createScaledBitmap(bitmap,width,height,false);
                        cropImageView.setImageBitmap(bitmap);
                        finalImageView.setImageBitmap(bitmap);
                        bitmaps.add(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setResult(RESULT_CANCELED);
                    }
                }


        }
        else{
            setResult(RESULT_CANCELED);
        }
        imgRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.setRotation();
            }
        });

        imgCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap=cropImageView.getCroppedImage();
                finalImageView.setImageBitmap(bitmap);
                bitmaps.add(bitmap);
            }
        });
        imgUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmaps.size()<=1)
                {
                    Toast.makeText(EditActivity.this,"This is orignal Image",Toast.LENGTH_LONG).show();
                }
                else{
                    bitmaps.remove(bitmaps.size()-1);
                    finalImageView.setImageBitmap(bitmaps.get(bitmaps.size()-1));
                }
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog=new ProgressDialog(EditActivity.this);
                dialog.setMessage("Cropping...");
                dialog.show();
                File folder=new File(getExternalFilesDir(null)+"/Files");
                if(!folder.exists())
                {
                    if(!folder.mkdir())
                            folder.mkdirs();

                }
                String fileName="IMG_"+System.currentTimeMillis()+".jpg";
                File file=new File(folder,fileName);

                    try {
                        file.createNewFile();
                        Bitmap bitmap=bitmaps.get(bitmaps.size()-1);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
                        if(getIntent().hasExtra("from_dialog")&&getIntent().getBooleanExtra("from_dialog",false))
                        {
                            new File(path).delete();
                        }
                        setResult(RESULT_OK);
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(dialog!=null)
                                dialog.show();
                        }
                    },100);


            }
        });

    }
}