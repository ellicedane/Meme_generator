package cie199.meme_generator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText etResponse;
    GridView grdMemes;
    private ArrayList<String> mImageURLs = new ArrayList<>();
    private ArrayList<String> mIDList = new ArrayList<>();
    private ArrayList<String> mMemeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the views
        etResponse = (EditText) findViewById(R.id.etResponse);
        grdMemes = (GridView) findViewById(R.id.grd_memes);
        grdMemes.setAdapter(new ImageAdapterGridView(this));

        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("https://api.imgflip.com/get_memes");

        grdMemes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                //Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();
                Toast.makeText(getBaseContext(), "You selected " + mMemeList.get(position) , Toast.LENGTH_LONG).show();

                Intent i = new Intent(MainActivity.this, CaptionMemeActivity.class);

                i.putExtra("url", mImageURLs.get(position));
                i.putExtra("name", mMemeList.get(position));
                i.putExtra("id", mIDList.get(position));

                startActivity(i);
            }
        });


    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            try {
                JSONObject json = new JSONObject(result);
                String str = "";

                JSONObject data = json.getJSONObject("data");
                JSONArray memes = data.getJSONArray("memes");

                str += "number of memes = "+memes.length();
                str += "\n--------\n";
                str += "first meme name: "+ memes.getJSONObject(0).names();
                str += "\n--------\n";
                str += "first meme url: "+ memes.getJSONObject(0).getString("url");

                str+="List of Memes\n";

                for(int i = 0; i < memes.length(); i++){
                    mImageURLs.add(memes.getJSONObject(i).getString("url"));
                    mIDList.add(memes.getJSONObject(i).getString("id"));
                    mMemeList.add(memes.getJSONObject(i).getString("name"));
                    str += Integer.toString(i) + " " + memes.getJSONObject(i).getString("name") + "\n";
                }

               /* JSONArray articles = json.getJSONArray("articleList");
                str += "articles length = "+json.getJSONArray("articleList").length();
                str += "\n--------\n";
                str += "names: "+articles.getJSONObject(0).names();
                str += "\n--------\n";
                str += "url: "+articles.getJSONObject(0).getString("url");*/

                etResponse.setText(str);

               /* new DownloadImageFromInternet((ImageView) findViewById(R.id.img_image))
                        .execute(memes.getJSONObject(0).getString("url"));*/

                //etResponse.setText(result);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //etResponse.setText(result);
        }
    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mImageURLs.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(130, 130));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(8, 8, 8, 8);
            } else {
                mImageView = (ImageView) convertView;
            }

            new DownloadImageFromInternet(mImageView)
                    .execute(mImageURLs.get(position));
            return mImageView;
        }
    }


}