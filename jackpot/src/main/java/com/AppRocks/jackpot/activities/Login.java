package com.AppRocks.jackpot.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.webservice.GetTermsTask;
import com.AppRocks.jackpot.webservice.JackpotServicesClient;
import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends Activity {

    protected String facebookUName = "";
    protected String facebookPass = "";
    protected String facebookEmail = "";
    ProgressDialog progress;
    boolean signUpProcess;
    View dimView;
    WindowManager.LayoutParams params;
    private GraphUser user;
    private LoginButton loginButton;
    private String fName;
    private String lName;
    private String country;
    private String password;
    private int requestCode;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };
    private JackpotParameters p;
    private Button btnSignUpForm;
    private RelativeLayout rlSignUp;
    private Animation rail_in_from_bottom;
    private EditText edtNickName;
    private EditText edtPassword;
    private EditText edtEmail;
    private String nickName;
    private EditText edtCity;
    private Button btnSignUp;
    private EditText edtAge;
    private TextView txtSex;
    private RadioGroup radioGruopSex;
    private TextView txtSignUp;
    private TextView txtLogin;
    private CheckBox conditionsCheckbox;
    private ImageButton conditions_button;
    private Button btnLogout;
    private int Facebook_Login_Mode = 1;
    private int Normal_Login_Mode = 2;
    private int loginMethod = 0;
    private WindowManager windowManager;
    private View getReadyView;
    private boolean ConditonsAccepted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));
        p = new JackpotParameters(this);
        rlSignUp = (RelativeLayout) findViewById(R.id.rlSignUp);
        rail_in_from_bottom = AnimationUtils.loadAnimation(this, R.anim.rail_in_from_bottom);
        btnSignUpForm = (Button) findViewById(R.id.btnSignUpForm);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnSignUpForm.setOnClickListener(new ShowSignUpFormClickListener());

        edtNickName = (EditText) findViewById(R.id.edtNickName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtAge = (EditText) findViewById(R.id.edtAge);
        txtSex = (TextView) findViewById(R.id.txtLblSex);
        radioGruopSex = (RadioGroup) findViewById(R.id.radioGroupSex);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        conditionsCheckbox = (CheckBox) findViewById(R.id.iagree_checkbutton);
        conditions_button = (ImageButton) findViewById(R.id.conditions_button);


        initOperations();


    }

    private void initOperations() {

        loginMethod = Normal_Login_Mode;

        conditions_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetTermsTask(Login.this).execute();
            }
        });
        conditionsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConditonsAccepted = isChecked;
            }
        });

        btnLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clearTheToken();

                //change UI to login UI
                btnSignUpForm.setText("Or Click Here");
                btnLogout.setVisibility(View.GONE);
                btnSignUpForm.setOnClickListener(new ShowSignUpFormClickListener());

            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("Getting your Info ...");
        progress.setMessage("Please wait...\nNow Get your Info");
        progress.setCancelable(false);

        Intent i = getIntent();
        if (i != null) {
            requestCode = i.getIntExtra("requestCode", 0);
        }
    }

    private void showTermsScreen(String terms) {

        //dimView.setVisibility(View.VISIBLE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getReadyView = inflator.inflate(R.layout.conditions_screen, null);

        int windowHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.windowAnimations = R.style.screen_animation;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = windowHeight / 6;
        windowManager.addView(getReadyView, params);

        TextView termsTextView = (TextView) getReadyView.findViewById(R.id.txt_cheese_info);
        if (terms == null)
            terms = "Terms failed to load!";
        termsTextView.setText(Html.fromHtml(terms));
        termsTextView.setMovementMethod(new ScrollingMovementMethod());

        Button backBtn = (Button) getReadyView.findViewById(R.id.btnBack);

        Button accept = (Button) getReadyView.findViewById(R.id.btnaccept);

        accept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // elsawaf
                if (getReadyView != null) {
                    windowManager.removeView(getReadyView);
                    getReadyView = null;
                    ConditonsAccepted = true;
                }
            }
        });

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // elsawaf
                if (getReadyView != null) {
                    windowManager.removeView(getReadyView);
                    getReadyView = null;
                    dimView.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private boolean getSavedToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString(JackpotApplication.PREF_USER_TOKEN, "");
        p.setString(token, JackpotApplication.PREF_USER_TOKEN);
        if (TextUtils.isEmpty(token))
            return false;

        JackpotApplication.TOKEN_ID = token;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            Log.w("elsawafLogin", "stateOpened");
            //	if (!jackpotParams.getBoolean("isUserRegisterOnService", false)) {
            ConnectionDetector cd = new ConnectionDetector(Login.this);
            if (cd.isConnectingToInternet() && TextUtils.isEmpty(JackpotApplication.TOKEN_ID)) {
                registerUser();
            }
            loginMethod = Facebook_Login_Mode;
            btnSignUpForm.setText("Return at game");
            btnSignUpForm.setOnClickListener(new ReturnToHomeClickListener());

            //return to invitation activity after resign in
            if (requestCode == 1) {
                setResult(2);
                finish();
            } else if (getIntent().getBooleanExtra("jackpot", false)) {
                Intent intent = new Intent(this, Jackpot_.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (getIntent().getBooleanExtra("savedGame", false)) {
                Intent intent = new Intent(this, Question.class);
                intent.putExtra("savedGame", true);
                startActivity(intent);
            }
        } else if (state.isClosed()) {
            Log.w("elsawafLogin", "stateClosed");
            clearTheToken();
            btnSignUpForm.setText("Or Click Here");
            btnLogout.setVisibility(View.GONE);
            btnSignUpForm.setOnClickListener(new ShowSignUpFormClickListener());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        if (getSavedToken()) {
            showButtonGoToGame();
        }

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);

        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        //updateUI();
    }

    @Override
    public void onBackPressed() {
        if (rlSignUp.getVisibility() == View.VISIBLE) {
            rlSignUp.setVisibility(View.INVISIBLE);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void registerUser() {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());
        if (enableButtons) {
            progress.show();

            // facebookEmail = user.get

            String[] requestIds = {"me"};

            RequestBatch requestBatch = new RequestBatch();
            requestBatch.add(new Request(Session.getActiveSession(),
                    requestIds[0], null, null, new Request.Callback() {


                public void onCompleted(Response response) {
                    GraphObject graphObject = response.getGraphObject();
                    String s = "";
                    if (graphObject != null) {
                        if (graphObject.getProperty("id") != null) {
                            s = s
                                    + String.format(
                                    "%s: %s: %s\n",
                                    graphObject
                                            .getProperty("id"),
                                    graphObject
                                            .getProperty("name"),
                                    graphObject
                                            .getProperty("email"));
                            Log.w("elsawafFB", graphObject.toString());
                            progress.setTitle("Signing Up ...");
                            progress.setMessage("Please wait...");
                            progress.show();

                            facebookUName = graphObject
                                    .getProperty("name") + "";
                            facebookPass = graphObject
                                    .getProperty("id") + "FACEBOOK";
                            facebookEmail = graphObject
                                    .getProperty("email") + "";
                            fName = (String) graphObject
                                    .getProperty("first_name");
                            lName = (String) graphObject
                                    .getProperty("last_name");
                            country = (String) graphObject
                                    .getProperty("locale");

                            password = "f";
                            nickName = fName + lName;

									/*
                                     * doAddUser(facebookUName, facebookPass,
									 * facebookEmail, true);
									 */

                            new RegisterTask().execute();


                        }
                    }
                    Log.d("elsawafFB", s);
                }

            }));
            requestBatch.executeAsync();

        }

    }

    private void updateUI() {
        // TODO Auto-generated method stub

    }

    public void hideSignUpForm() {
        rlSignUp.setVisibility(View.INVISIBLE);
        showButtonGoToGame();
    }

    private void showButtonGoToGame() {
        btnSignUpForm.setText("Return at game");
        btnSignUpForm.setOnClickListener(new ReturnToHomeClickListener());
        //show logout if normall login not facebook login
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString(JackpotApplication.PREF_USER_TOKEN, "");
        if (loginMethod == Normal_Login_Mode)
            btnLogout.setVisibility(View.VISIBLE);
    }

    public void updateTheToken() {
        Log.w("elsawafToken", JackpotApplication.TOKEN_ID);
        p.setString(JackpotApplication.TOKEN_ID, JackpotApplication.PREF_USER_TOKEN);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = prefs.edit();
        editor.putString(JackpotApplication.PREF_USER_TOKEN, JackpotApplication.TOKEN_ID);
        editor.commit();
    }

    //when press logout
    public void clearTheToken() {
        p.setString("", JackpotApplication.PREF_USER_TOKEN);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = prefs.edit();
        editor.putString(JackpotApplication.PREF_USER_TOKEN, "");
        editor.commit();

    }

    public void txtLoginClick(View v) {
        edtNickName.setVisibility(View.GONE);
        edtCity.setVisibility(View.GONE);
        edtAge.setVisibility(View.GONE);
        txtSex.setVisibility(View.GONE);
        radioGruopSex.setVisibility(View.GONE);

        btnSignUp.setText("Login");
        txtSignUp.setTextColor(getResources().getColor(R.color.Blue));
        txtLogin.setTextColor(getResources().getColor(R.color.Black));

        signUpProcess = false;

    }

    public void txtSignupClick(View v) {
        edtNickName.setVisibility(View.VISIBLE);
        edtCity.setVisibility(View.VISIBLE);
        edtAge.setVisibility(View.VISIBLE);
        txtSex.setVisibility(View.VISIBLE);
        radioGruopSex.setVisibility(View.VISIBLE);

        btnSignUp.setText("Sign Up");
        txtSignUp.setTextColor(getResources().getColor(R.color.Black));
        txtLogin.setTextColor(getResources().getColor(R.color.Blue));

        signUpProcess = true;
    }

    public void btnSignUpClick(View v) {
        ConnectionDetector cd = new ConnectionDetector(Login.this);
        if (cd.isConnectingToInternet()) {
            if (validInputs()) {
                if (signUpProcess) {
                    progress.setTitle("Signing Up ...");
                    progress.setMessage("Please wait...");
                    progress.show();
                    nickName = edtNickName.getText().toString();
                    password = edtPassword.getText().toString();
                    country = edtCity.getText().toString();
                    facebookEmail = edtEmail.getText().toString();

                    new RegisterTask().execute();
                } else {
                    progress.setTitle("Logging In ...");
                    progress.setMessage("Please wait...");
                    progress.show();
                    password = edtPassword.getText().toString();
                    facebookEmail = edtEmail.getText().toString();

                    new LoginTask().execute();
                }

            }
        }
    }

    private boolean validInputs() {
        boolean result = true;

        if (signUpProcess && edtNickName.length() < 2) {
            result = false;
            setErrorMessage(edtNickName, "Enter proper name.");

        }

        if (edtPassword.length() < 2) {
            result = false;
            setErrorMessage(edtPassword, "Password must be more than 1 chars.");
        }

        if (edtEmail.length() < 3
                || !edtEmail.getText().toString().contains(".")
                || !edtEmail.getText().toString().contains("@")) {
            result = false;
            setErrorMessage(edtEmail, "Enter proper email");
        }

        return result;
    }

    private void setErrorMessage(EditText edttxt, String message) {
        int ecolor = Color.BLUE; // whatever color you want

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(message);
        ssbuilder.setSpan(fgcspan, 0, message.length(), 0);

        edttxt.setError(ssbuilder);

    }

    public void onGetTermsTaskFinish(String res) {
        showTermsScreen(res);
    }

    class ShowSignUpFormClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            rlSignUp.setVisibility(View.VISIBLE);
            rlSignUp.startAnimation(rail_in_from_bottom);
        }

    }

    class ReturnToHomeClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (ConditonsAccepted) {
                startActivity(new Intent(Login.this, Main_.class));
            } else {
                Toast.makeText(Login.this, "You must accept terms and conditions first", Toast.LENGTH_LONG).show();
            }
        }
    }

    class RegisterTask extends AsyncTask<Void, Void, Integer> {

        public static final int SUCCESS = 1;
        public static final int EMAIL_EXISTS = 2;


        @Override
        protected Integer doInBackground(Void... arg0) {
            JSONObject responseJSON = JackpotServicesClient.getInstance().register(
                    facebookUName, fName, lName, nickName, country,
                    facebookEmail, password);
            Log.w("elsawafRegister", responseJSON.toString());
            try {
                int status = responseJSON.getInt("status");
                if (status == 1) {
                    JackpotApplication.TOKEN_ID = responseJSON.getString("message");
                    updateTheToken();
                    loginMethod = Facebook_Login_Mode;
                    return SUCCESS;
                } else if (status == EMAIL_EXISTS) {
                    return EMAIL_EXISTS;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if (result == SUCCESS) {
                hideSignUpForm();
            } else if (result == EMAIL_EXISTS) {
                Toast.makeText(getApplicationContext(), "The email already exists!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error, please try again.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
            //jackpotParams.setBoolean(true, "isUserRegisterOnService");
        }

    }

    class LoginTask extends AsyncTask<Void, Void, Integer> {

        public static final int SUCCESS = 1;
        public static final int PASSWORD_INVALID = 2;
        public static final int EMAIL_INVALID = 3;

        @Override
        protected Integer doInBackground(Void... arg0) {
            JSONObject responseJSON = JackpotServicesClient.getInstance().login(facebookEmail, password);
            Log.w("elsawafLogin", responseJSON.toString());
            try {
                int status = responseJSON.getInt("status");
                if (status == SUCCESS) {
                    JackpotApplication.TOKEN_ID = responseJSON.getString("message");
                    loginMethod = Normal_Login_Mode;
                    updateTheToken();
                    return SUCCESS;
                } else if (status == PASSWORD_INVALID) {
                    return PASSWORD_INVALID;
                } else if (status == EMAIL_INVALID) {
                    return EMAIL_INVALID;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return -1;
        }


        @Override
        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if (result == SUCCESS) {
                hideSignUpForm();
            } else if (result == PASSWORD_INVALID) {
                Toast.makeText(getApplicationContext(), "The password is wrong", Toast.LENGTH_SHORT).show();
            } else if (result == EMAIL_INVALID) {
                Toast.makeText(getApplicationContext(), "The email is wrong", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error, please try again.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
            //jackpotParams.setBoolean(true, "isUserRegisterOnService");
        }

    }
}
