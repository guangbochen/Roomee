package com.vivant.roomee;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.vivant.roomee.json.JSONParser;
import com.vivant.roomee.json.JSONParserImpl;
import com.vivant.roomee.model.Constants;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * this class manages user login activity
 * and validates API key via the Roomee web services
 */
public class MainActivity extends Activity implements View.OnFocusChangeListener {

    private EditText txtAPIKey;
    private ImageButton btnEmptyKey;
    private final static String title = "Welcome to Roomee";

    /**
     * onCreate method initialises the view of main activity
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //find the view
        btnEmptyKey = (ImageButton) findViewById(R.id.btn_empty_key);
        txtAPIKey = (EditText) findViewById(R.id.txtAPIKey);
        txtAPIKey.setOnFocusChangeListener(this);

        //set custom title for the main activity
        ActionBar ab = getActionBar();
        ab.setTitle(title);
        ab.setDisplayShowTitleEnabled(true);

    }

    /**
     * this method handles loginButton onclick event
     * it validate the API key through Roomee web service
     */
    public void loginButtonOnClick(View view) {
        try
        {
            String apiKey = txtAPIKey.getText().toString();
            //if is empty displays error message
            if(apiKey.length() == 0)
            {
                String message = "Empty API Key";
                displayErrorMessage(message);
                txtAPIKey.requestFocus();
            }
            else
            {
                //validate the API key and login
                new AuthenticationTask(MainActivity.this, apiKey).execute();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(view.getId() == txtAPIKey.getId() && hasFocus)
        {
            if(txtAPIKey.getText().length()>0)
                btnEmptyKey.setVisibility(1);
        }
        else {
            view.setVisibility(0);
        }

    }

    /**
     * this is private AuthenticationTask class that validates the API key
     * whoever request to access the Roomee web services
     */
    private class AuthenticationTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private Context context;
        private Boolean done;
        private String message;
        private String token;
        private String apiKey;

        /**
         * AuthenticationTask constructor
         */
        public AuthenticationTask(Activity activity, String key) {
            context = activity;
            dialog = new ProgressDialog(context);
            this.apiKey = key;
        }

        /**
         * onPreExecute initialises AuthenticationTask before it starts
         */
        protected void onPreExecute() {
            this.message = "No internet connection";
            this.dialog.setMessage("Validating API key...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        /**
         * doInBackground method validates user API key in the background
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

            //if is not valid API key displays server validation message
            if(done == false)
            {
                displayErrorMessage(message);
            }
            else {
                //if is valid user login and calls roomList activity
                Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        }
    }

    /**
     * this method displays toast message
     * @param message, string message
     */
    private void displayErrorMessage(String message)
    {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /**
     * this method creates the menu bar of the activity
     * @param menu, Menu
     * @return false, do not display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }
}
