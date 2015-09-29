package com.example.jay.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by jay on 2015/9/25.
 */
public class btn_photo_1 extends Activity {

    SurfaceView sView;
    SurfaceHolder surfaceHolder;

    Spinner spinner;
    int screenWidth, screenHeight;

    Camera camera;
    boolean isPreview = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.btn_photo_1_1);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        sView = (SurfaceView) findViewById(R.id.sView);
        surfaceHolder = sView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(camera!=null){
                    if(isPreview){
                        camera.stopPreview();
                        camera.release();
                        camera=null;
                    }

                }
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);

    }

    private void initCamera() {
        //if (!isPreview) {
            camera = Camera.open(0);
       // }
        if (camera != null && !isPreview) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(screenWidth, screenHeight);
                parameters.setPreviewFrameRate(4);
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.set("jpeg-quality", "85");
                parameters.setPictureSize(screenWidth, screenWidth);
                camera.setParameters(parameters);
                camera.startPreview();
                camera.autoFocus(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_CAMERA:
                if (camera != null && event.getRepeatCount() == 0) {
                    camera.takePicture(null, null, pictureCallback);
                    return true;

                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            View saveDialog = getLayoutInflater().inflate(R.layout.btn_photo_1_2, null);
            spinner = (Spinner) findViewById(R.id.spinner_photo_2);
            spinner.setAdapter(ba);

            ImageView show = (ImageView) findViewById(R.id.img_photo);
            show.setImageBitmap(bm);
            new AlertDialog.Builder(btn_photo_1.this).setView(saveDialog)
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        //Spinner spinner = (Spinner) findViewById(R.id.spinner_photo_2);
                        String text=spinner.getSelectedItem().toString();
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file=new File(Environment.getExternalStorageDirectory(),text+".jpg");
                            FileOutputStream outputStream=null;
                            try {
                                outputStream=new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                                outputStream.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("取消",null).show();
            camera.stopPreview();
            camera.startPreview();
            isPreview=true;


        }
    };

    private BaseAdapter ba = new BaseAdapter() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout line = new LinearLayout(btn_photo_1.this);
            line.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(btn_photo_1.this);
            text.setText(position + "");
            text.setTextSize(10);
            line.addView(text);
            return line;
        }
    };
}
