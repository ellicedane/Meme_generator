package cie199.meme_generator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by dane on 5/2/2017.
 */

public class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;

    public DownloadImageFromInternet(ImageView imageView) {
        this.imageView = imageView;
       // Toast.makeText(getApplicationContext(), "downloading image...", Toast.LENGTH_SHORT).show();
    }

    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        Bitmap bimage = null;
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            bimage = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error Message", e.getMessage());
            e.printStackTrace();
        }
        return bimage;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}
