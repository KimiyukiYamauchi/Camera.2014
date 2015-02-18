package com.example.camera;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView extends SurfaceView 
	implements Callback, PictureCallback
{
	
	private SurfaceHolder holder;
	private Camera camera;

	// コンストラクタ
	public CameraView(Context context) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(0);
		try {
			camera.setPreviewDisplay(holder);
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		String msg = "Taken";
		FileOutputStream stream = null;
		try {
			// 保存
			String path =
					Environment.getExternalStorageDirectory() +"/test.jpg";
			stream = new FileOutputStream(path);
			stream.write(data);
			stream.close();
			
			// ギャラリーに反映
			MediaScannerConnection.scanFile(
					this.getContext(),
					new String[]{path},
					new String[]{"image/jpeg"},
					null);
		} catch (Exception e) {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e1) {
				}
				msg = "Error: " + e.getMessage();
			}
		}
		
		camera.startPreview();
		Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
	}

	// 画面タッチイベント
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			camera.takePicture(null, null, this);
		}
		return true;
	}
}
