/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Northkastt S.A. de C.V.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.nkastt.sep.madrepatria;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class MPWallpaperService extends WallpaperService {

	private static final String TAG = "MPWallpaperService";
	
	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	class WallpaperEngine extends Engine {
		
		private final Paint mPaint = new Paint();
		private final Handler mHandler = new Handler();
		private final Bitmap mOriginalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.madre_patria);
		private final Runnable drawRunnable = new Runnable() {
			
			@Override
			public void run() {
				try {
					drawImage();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		};
		
		private Bitmap mBitmap;
		private boolean mVisible = true;
		
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			
			mBitmap = ThumbnailUtils.extractThumbnail(mOriginalBitmap, width, height);
			drawImage();
		}
		
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mHandler.removeCallbacks(drawRunnable);
		}
		
		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			
			if (visible) {
				mHandler.post(drawRunnable);
			} else {
				mHandler.removeCallbacks(drawRunnable);
			}
		}
		
		private void drawImage() {
			Canvas canvas = null;
			SurfaceHolder surfaceHolder = getSurfaceHolder();
			
			try {
				canvas = surfaceHolder.lockCanvas();
				canvas.drawBitmap(mBitmap, 0, 0, mPaint);
			} finally {
				if (null != canvas) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
			
			mHandler.removeCallbacks(drawRunnable);
			if (mVisible) {
				mHandler.postDelayed(drawRunnable, 1000 / 25);
			}
		}
	}
}
