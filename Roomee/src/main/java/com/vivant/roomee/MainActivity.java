package com.vivant.roomee;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText txtToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewButtonOnClick(View view) {
        txtToken = (EditText) findViewById(R.id.txtToken);
        try
        {
            String token = txtToken.getText().toString();
            Log.d("MAD", token.toString());
            if(token.equals(""))
            {
                //display invalid token Toast message
                Toast toast = Toast.makeText(getApplicationContext(), "Empty Token Number", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                intent.putExtra("token", "e24cda222194876faaba860416f6ef126d328639");
                startActivity(intent);
            }
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
