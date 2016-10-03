package jcsoluciones.com.socialfootball.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import jcsoluciones.com.socialfootball.R;


/**
 * Created by ADMIN on 08/08/2016.
 */
public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    //private final WeakReference<BootstrapCircleThumbnail> imageViewReference;
    private final ImageView imageViews;
    public ImageLoader(ImageView imageView) {
        imageViews = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

       /* if (imageViewReference != null) {
            BootstrapCircleThumbnail imageView = imageViewReference.get();*/
            if (imageViews != null) {
                if (bitmap != null) {
                    imageViews.setImageBitmap(bitmap);
                } else {

                    imageViews.setImageResource(R.mipmap.ic_launcher);
                }
            }
        //}
    }
    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
