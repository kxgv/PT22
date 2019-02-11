package org.escoladeltreball.dam5184295.pt22;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button sendButton, emojiButton, cameraButton;
    TextView mymessage;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.sendButton);
        emojiButton = findViewById(R.id.emojiButton);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.sendButton:
                break;

            case R.id.emojiButton:
                break;

        }
    }

    public void sendMessage(){

    }
}
