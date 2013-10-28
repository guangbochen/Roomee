package com.vivant.roomee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * this activity class manages login actions
 * it validates user via api keys through Roomee web service
 */
public class MainActivity extends Activity {

    private EditText txtAPIKey;


    /**
     * onCreate methods initialize the view once activity is created
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * this method handles loginButton onclick event
     * it validate the user with api key through Roomee web service
     */
    public void loginButtonOnClick(View view) {
        txtAPIKey = (EditText) findViewById(R.id.txtAPIKey);
        try
        {
            String apiKey = txtAPIKey.getText().toString();

            //if is empty displays error message
            if(apiKey.equals(""))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Empty API Key", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            else
            {
                //login via Roomee web service
                new AuthenticationTask(MainActivity.this, apiKey).execute();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * this is private AuthenticationTask class that validates user
     * whoever request to access the Roomee web service
     */
    private class AuthenticationTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Activity activity;
        private Context context;
        private Boolean done;
        private String message;
        private String token;
        private String apiKey;

        /**
         * AuthenticationTask constructor
         */
        public AuthenticationTask(Activity activity, String key) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
            this.apiKey = key;
        }

        /**
         * onPreExecute initalise AuthenticationTask before it starts
         */
        protected void onPreExecute() {
            this.dialog.setMessage("Validating token...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        /**
         * doInBackground method validates user key in the background
         * via communicating with Roomee web service server
         * and parse the json data that is returned by the server
         */
        @Override
        protected Boolean doInBackground(String... strings) {

            done = false;
            //crate Json parser instance
            JSONParser jsonParser = new JSONParserImpl();

            try{
                //getting json data from url
                String url = "auth?api_key=" + apiKey;
                JSONObject json = jsonParser.getJSONFromUrl(url);

                if(json != null)
                {
                    String HttpStatus = json.getString(Constants.TAG_STATUS);
                    //if is valid api key set done to true
                    if(HttpStatus.equals("success"))
                    {
                        JSONObject data = json.getJSONObject(Constants.TAG_DATA);
                        token = data.getString(Constants.TAG_OAUTH);
                        done = true;
                    }
                    //invalid api key
                    else message = json.getString(Constants.TAG_MESSAGE);
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
                //displays server internal error message
                Toast toast = Toast.makeText(context, "Server internal error, please try again", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            return null;
        }

        /**
         * onPostExecute method manges action should be done
         * after the doInBackground method is finished
         */
        @Override
        protected void onPostExecute(final Boolean success) {

            //dismiss the loading dialog
            if(dialog.isShowing()) dialog.dismiss();

            //if is not valid user display message from server
            if(done == false)
            {
                //display invalid token Toast message
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            else {
                //if is valid user login and calls roomList activity
                Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        }
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
    
}
