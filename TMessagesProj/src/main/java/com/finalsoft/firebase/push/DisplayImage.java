package com.finalsoft.firebase.push;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DisplayImage extends AsyncTask<String, Void, Bitmap> {
  @SuppressLint("StaticFieldLeak")
  private ImageView bmImage;

  public DisplayImage(ImageView bmImage) {
      this.bmImage = bmImage;
  }

  protected Bitmap doInBackground(String... urls) {
      String urlDisplay = urls[0];
      Bitmap mIcon11 = null;
      try {
        InputStream in = new java.net.URL(urlDisplay).openStream();
        mIcon11 = BitmapFactory.decodeStream(in);
      } catch (Exception e) {
          Log.e("Error", e.getMessage());
          e.printStackTrace();
      }
      return mIcon11;
  }

  protected void onPostExecute(Bitmap result) {
      bmImage.setImageBitmap(result);
  }
}