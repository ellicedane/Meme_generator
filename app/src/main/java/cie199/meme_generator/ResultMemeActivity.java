package cie199.meme_generator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ResultMemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_meme);

        Button btn_submit = (Button) findViewById(R.id.btn_caption);
        btn_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent i = new Intent();

                        i.putExtra("Article", "sample");

                        setResult(RESULT_OK, i);
                        finish();*/
                    }
                }
        );
    }
}
