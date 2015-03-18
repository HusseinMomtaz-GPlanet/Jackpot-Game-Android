package com.AppRocks.jackpot.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.models.ServerResult;
import com.AppRocks.jackpot.models.UserLogin;
import com.AppRocks.jackpot.models.UserSignUp;
import com.AppRocks.jackpot.webservice.JackpotServicesClient;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@EActivity(R.layout.login_old)
public class LoginOLD extends Activity {

    private static final int CAMERA_REQUEST = 1888;
    private static final List<String> PERMISSIONS = Arrays.asList(
            "publish_actions", "email");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    protected String facebookUName = "";
    protected String facebookPass = "";
    protected String facebookEmail = "";
    @ViewById
    Button btnSignUp;
    @ViewById
    ImageButton btnFace_Login;
    @ViewById
    LoginButton authButton;
    @ViewById
    Button btnSignUpForm;
    @ViewById
    RelativeLayout rlSignUp;
    @ViewById
    EditText edtFirstName;
    @ViewById
    EditText edtLastName;
    @ViewById
    EditText edtPassword;
    @ViewById
    EditText edtEmail;
    @ViewById
    TextView txtLogin;
    @ViewById
    TextView txtSignUp;
    @ViewById
    TextView txtlblFirstName;
    @ViewById
    TextView txtlblLastName;
    @ViewById
    TextView txtPassScan;
    @AnimationRes
    Animation rail_in_from_bottom;
    @AnimationRes
    Animation rail_out_to_bottom;
    JackpotParameters p;
    ProgressDialog progress;
    private boolean signUpProcess = false;
    private boolean pendingPublishReauthorization = false;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private String fName;
    private String lName;
    private String country;
    private boolean comingFromOnResume;
    private int requestCode;
    private String SERVICE_URi = "http://game.gplanet-tech.com/api/v1";
    private String TAG = "Login";
    private Gson gson;

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            // if (pendingPublishReauthorization &&
            // state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            // pendingPublishReauthorization = false;
            // doBatchRequest();
            // }
            btnFace_Login.setVisibility(View.GONE);
            authButton.setVisibility(View.VISIBLE);

            if (comingFromOnResume) {
                comingFromOnResume = false;
                btnFace_Login();

            }

            if (!p.getBoolean("isCalledRegisterTask", false)) {
                doBatchRequest();
            }


            //return to invitation activity after resign in
            if (requestCode == 1) {
                //Intent returnIntent = new Intent();
                /*new Thread(){
                    @Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally {
							setResult(2);
							finish();
						}
					}
				}.start();*/
                setResult(2);
                finish();
            } else if (getIntent().getBooleanExtra("login", false)) {

            } else {
                startActivity(new Intent(this, Jackpot_.class));
            }

            // doBatchRequest();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            btnFace_Login.setVisibility(View.GONE);
            authButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        gson = new Gson();
        p = new JackpotParameters(this);
        progress = new ProgressDialog(this);
        progress.setTitle("Signing Up ...");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        Intent i = getIntent();
        if (i != null) {
            requestCode = i.getIntExtra("requestCode", 0);
        }
    }

    @AfterViews
    void findOtherViews() {
        // LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        // authButton.setReadPermissions(PERMISSIONS);
    }

    private boolean isSubsetOf(Collection<String> subset,
                               Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    private void getFacebookUserInfo() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {

            // make request to the /me API
            Request.newMeRequest(session, new GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // TODO Auto-generated method stub
                    if (user != null) {
                        user.getFirstName();
                        user.getLastName();
                        user.getUsername();
                    }
                }
            }).executeAsync();
        }
    }

    private void doBatchRequest() {

        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                        this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
        }
        // String[] requestIds = {"me", "4"};
        String[] requestIds = {"me"};

        RequestBatch requestBatch = new RequestBatch();
        requestBatch.add(new Request(Session.getActiveSession(), requestIds[0],
                null, null, new Request.Callback() {

            public void onCompleted(Response response) {
                GraphObject graphObject = response.getGraphObject();
                String s = "";
                if (graphObject != null) {
                    if (graphObject.getProperty("id") != null) {
                        s = s + String.format(
                                "%s: %s: %s\n",
                                graphObject.getProperty("id"),
                                graphObject.getProperty("name"),
                                graphObject.getProperty("email"));
                        Log.w("elsawafFB", graphObject.toString());
                        progress.setTitle("Signing Up ...");
                        progress.setMessage("Please wait...");
                        progress.show();

                        facebookUName = graphObject.getProperty("name")
                                + "";
                        facebookPass = graphObject.getProperty("id")
                                + "FACEBOOK";
                        facebookEmail = graphObject
                                .getProperty("email") + "";
                        fName = (String) graphObject.getProperty("first_name");
                        lName = (String) graphObject.getProperty("last_name");
                        country = (String) graphObject.getProperty("locale");

								/*doAddUser(facebookUName, facebookPass,
										facebookEmail, true);*/
                        new RegisterTask().execute();
                    }
                }
                Log.d(TAG, s);
            }
        }));
        requestBatch.executeAsync();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (p.getBoolean("login", false)) {
            finish();
            startActivity(new Intent(this, Main_.class));
        } else {

        }

        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();


    }

    @Override
    public void onDestroy() {
        // myController.destroyAd();
        super.onDestroy();
        uiHelper.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

        }

        comingFromOnResume = true;

        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Click
    void btnSignUp() {
        if (!isOnline()) {
            Toast.makeText(this, "Check Internet Connection!",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (signUpProcess) {
                if (validInputs()) {
                    // Toast.makeText(Login.this, "Sign UP",
                    // Toast.LENGTH_SHORT).show();
                    progress.setTitle("Signing Up ...");
                    progress.setMessage("Please wait...");
                    progress.show();
                    doAddUser(edtFirstName.getText().toString() + " "
                            + edtLastName.getText().toString(), edtPassword
                            .getText().toString(), edtEmail.getText()
                            .toString(), false);
                }

            } else {
                if (validInputs()) {
                    // Toast.makeText(Login.this, "Login",
                    // Toast.LENGTH_SHORT).show();
                    progress.setTitle("Logging In ...");
                    progress.setMessage("Please wait...");
                    progress.show();
                    doAuthInBackground(edtEmail.getText().toString(),
                            edtPassword.getText().toString());

                }

            }
        }

    }

    private boolean validInputs() {
        boolean result = true;

        if (signUpProcess && edtFirstName.length() < 2) {
            result = false;
            setErrorMessage(edtFirstName, "Enter proper name.");

        }
        if (signUpProcess && edtLastName.length() < 2) {
            result = false;
            setErrorMessage(edtLastName, "Enter proper name.");
        }
        if (edtPassword.length() < 8) {
            result = false;
            setErrorMessage(edtPassword, "Password must be more than 8 chars.");
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

    @Click
    void btnFace_Login() {
        doBatchRequest();
    }

    @Click
    void btnSignUpForm() {
        rlSignUp.setVisibility(View.VISIBLE);
        rlSignUp.startAnimation(rail_in_from_bottom);
    }

    @Override
    public void onBackPressed() {
        if (rlSignUp.getVisibility() == View.VISIBLE) {
            rlSignUp.setVisibility(View.GONE);
            rlSignUp.startAnimation(rail_out_to_bottom);
        } else
            super.onBackPressed();
    }

    public boolean isOnline() {
        boolean connected = false;
        // boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connected = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connected = true;
        }
        return connected;
    }

    @Click
    void txtPassScan() {
        Toast.makeText(LoginOLD.this, "Capture your passport", Toast.LENGTH_LONG)
                .show();
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Click
    void txtLogin() {
        edtFirstName.setVisibility(View.GONE);
        edtLastName.setVisibility(View.GONE);

        txtlblFirstName.setVisibility(View.GONE);
        txtlblLastName.setVisibility(View.GONE);
        txtPassScan.setVisibility(View.GONE);

        btnSignUp.setText("Login");

        signUpProcess = false;
    }

    @Click
    void txtSignUp() {
        edtFirstName.setVisibility(View.VISIBLE);
        edtLastName.setVisibility(View.VISIBLE);

        txtlblFirstName.setVisibility(View.VISIBLE);
        txtlblLastName.setVisibility(View.VISIBLE);
        txtPassScan.setVisibility(View.VISIBLE);

        btnSignUp.setText("Sign Up");

        signUpProcess = true;
    }

    @Background
    void doAddUser(String uname, String pass, String email,
                   boolean facebookProcess) {
        String responce = "";
        try {
            String allURL = SERVICE_URi + "/users";
            // log.d(TAG, "respond = "+allURL);
            HttpPost request = new HttpPost(allURL);
            request.setHeader("Accept", "application/json");
            request.addHeader("Content-type", "application/json");
            request.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials("a", "a"), "UTF-8", false));

            // ////////////////////////////////////////
            // setting timeouts start
            // ///////////////////////////////////////
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            // ////////////////////////////////////////
            // setting timeouts finish
            // ///////////////////////////////////////

            // String strGson =
            // "{\"username\":\""+uname+"\",\"password\":\""+pass+"\",\"email\":\""+email+"\"}";

            UserSignUp user = new UserSignUp();
            user.email = email;
            user.password = pass;
            user.username = uname;

            String strGson = gson.toJson(user);

            StringEntity entity = new StringEntity(strGson, "UTF-8");
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(request);

            // get JSON responce
            HttpEntity responseEntity = response.getEntity();
            responce = EntityUtils.toString(responseEntity);

            Log.d(TAG, "responce= " + responce);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        updateUI(responce, facebookProcess);
    }

    @Background
    void doAuthInBackground(String email, String password) {
        String responce = "";
        try {
            String allURL = SERVICE_URi + "/users/login";

            Log.d(TAG, "respond = " + allURL);
            HttpPost request = new HttpPost(allURL);
            request.setHeader("Accept", "application/json");
            request.addHeader("Content-type", "application/json");
            request.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials("a", "a"), "UTF-8", false));

            // ////////////////////////////////////////
            // setting timeouts start
            // ///////////////////////////////////////
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 60000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            // ////////////////////////////////////////
            // setting timeouts finish
            // ///////////////////////////////////////

            // String strGson =
            // "{\"email\":\""+email+"\",\"password\":\""+password+"\"}";

            UserLogin user = new UserLogin();
            user.email = email;
            user.password = password;

            String strGson = gson.toJson(user);

            StringEntity entity = new StringEntity(strGson, "UTF-8");
            request.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(request);

            // get JSON responce
            HttpEntity responseEntity = response.getEntity();
            responce = EntityUtils.toString(responseEntity);

            Log.d(TAG, "responce= " + responce);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        updateUI(responce, false);
    }

    // Notice that we manipulate the activity ref only from the UI thread
    @UiThread
    void updateUI(String result, boolean facebookProcess) {
        progress.dismiss();

        ServerResult sresult = gson.fromJson(result, ServerResult.class);
        // Toast.makeText(this,sresult.Message, Toast.LENGTH_LONG).show();

        if (facebookProcess)// try logining in;
        {
            progress.setTitle("Logging In ...");
            progress.setMessage("Please wait...");
            progress.show();
            doAuthInBackground(facebookEmail, facebookPass);
        }

        if (sresult.Status == 1) {
            if (signUpProcess) {
                txtLogin();
                signUpProcess = false;
                progress.setTitle("Logging In ...");
                progress.setMessage("Please wait...");
                progress.show();
                doAuthInBackground(edtEmail.getText().toString(), edtPassword
                        .getText().toString());
            } else {
                if (rlSignUp.getVisibility() == View.VISIBLE) {
                    rail_out_to_bottom
                            .setAnimationListener(new AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(
                                        Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    p.setBoolean(true, "login");
                                    p.setString(edtEmail.getText().toString(),
                                            "email");
                                    p.setString(edtFirstName.getText()
                                            .toString(), "fname");
                                    p.setString(edtLastName.getText()
                                            .toString(), "lname");

                                    finish();
                                    startActivity(new Intent(LoginOLD.this,
                                            Main_.class));

                                }
                            });

                    rlSignUp.setVisibility(View.GONE);
                    rlSignUp.startAnimation(rail_out_to_bottom);
                } else {
                    p.setBoolean(true, "login");
                    p.setString(facebookEmail, "email");
                    p.setString(facebookUName, "fname");

                    finish();
                    startActivity(new Intent(LoginOLD.this, Main_.class));
                }

            }

        } else {
            Toast.makeText(this, sresult.Message, Toast.LENGTH_LONG).show();
        }

    }

    class RegisterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            JSONObject j = JackpotServicesClient.getInstance().register(facebookUName, fName, lName, fName + lName, country, facebookEmail, JackpotApplication.TOKEN_ID);
            Log.w("elsawafRegister", j.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progress.dismiss();
            p.setBoolean(true, "isCalledRegisterTask");
        }

    }

}
