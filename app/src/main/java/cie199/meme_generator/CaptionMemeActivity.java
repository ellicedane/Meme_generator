package cie199.meme_generator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CaptionMemeActivity extends AppCompatActivity {
    EditText edtCaption1;
    EditText edtCaption2;

    String id, username, password, text0, text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption_meme);

        final Intent receivedIntent = getIntent();

        String mTitle = receivedIntent.getStringExtra("name");
        TextView txvTitle = (TextView) findViewById(R.id.txv_title);
        txvTitle.setText(mTitle);

        String imageUrl = receivedIntent.getStringExtra("url");
        new DownloadImageFromInternet((ImageView) findViewById(R.id.img_blank))
                .execute(imageUrl);

        Button btn_submit = (Button) findViewById(R.id.btn_caption);
        btn_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(CaptionMemeActivity.this, ResultMemeActivity.class);

                        edtCaption1 = (EditText) findViewById(R.id.edt_caption1);
                        edtCaption2 = (EditText) findViewById(R.id.edt_caption2);

                        text0 = edtCaption1.getText().toString();
                        text1 = edtCaption2.getText().toString();

                        i.putExtra("caption1", text0);
                        i.putExtra("caption2", text1);

                        new HttpAsyncTask().execute("https://api.imgflip.com/caption_image");

                        //startActivity(i);
                    }
                }
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 999 && resultCode == RESULT_OK){

        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], id, username, password, text0, text1);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            try {
                JSONObject json = new JSONObject(result);
                String str = "";

                edtCaption2.setText(result);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String POST(String url, String id, String username, String password, String text0, String text1){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("template_id", id);
            jsonObject.accumulate("username", username);
            jsonObject.accumulate("password", password);
            jsonObject.accumulate("text0", text0);
            jsonObject.accumulate("text1", text1);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            //httpPost.setHeader("Accept", "application/json");
            //httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
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
}
