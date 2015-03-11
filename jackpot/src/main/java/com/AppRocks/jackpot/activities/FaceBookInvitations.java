package com.AppRocks.jackpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotParameters;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.util.ConnectionDetector;
import com.AppRocks.jackpot.webservice.AddGiftJockersTask;
import com.AppRocks.jackpot.webservice.CheckToSendGiftJokerTask;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;

import org.json.JSONObject;

public class FaceBookInvitations extends Activity {


    // Parameters of a WebDialog that should be displayed
    private WebDialog dialog = null;
    private String dialogAction = null;
    private Bundle dialogParams = null;
    private JackpotParameters p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_friends);
        p = new JackpotParameters(FaceBookInvitations.this);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "BigSoftie-Fat.otf");
        ((TextView) findViewById(R.id.txtPlayFree)).setTypeface(font);
        ((TextView) findViewById(R.id.textView2)).setTypeface(font);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        msgToSend();
    }

    public void sendInvitatoins(View v) {
        //call server to check and call back method will take care
        checkIfAllowedToSendInvitation();
    }

    private void sendWhatsAppInvitation() {
        // add gift jocker
        ConnectionDetector cd = new ConnectionDetector(FaceBookInvitations.this);
        if (cd.isConnectingToInternet()) {
            new AddGiftJockersTask().execute();
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.setPackage("com.whatsapp");           // so that only WhatsApp reacts and not the chooser
        i.putExtra(Intent.EXTRA_SUBJECT, "Download and play with me");
        i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.AppRocks.jackpot"); //com.etermax.preguntados.lite
        startActivity(i);

        //setTimeForNextInvitation();
    }

    //This logic has been moved to server

//	private void setTimeForNextInvitation() {
//		Calendar timeForNextInvitaion = Calendar.getInstance();
//		timeForNextInvitaion.add(Calendar.DAY_OF_YEAR, 1);
//		jackpotParams.setLong(timeForNextInvitaion.getTimeInMillis(), "invitiation_time"+JackpotApplication.TOKEN_ID);
//	}

    private void checkIfAllowedToSendInvitation() {
        new CheckToSendGiftJokerTask(this).execute();
    }

    private void sendFacebookInvitation() {
        Session session = Session.getActiveSession();
        if (session != null) {
            msgToSend();
        } else {
            Intent i = new Intent(new Intent(this, Login.class));
            i.putExtra("requestCode", 1);
            startActivityForResult(i, 1);
        }
    }

    private void msgToSend() {
        Bundle params = new Bundle();
        params.putString("message", "I just pass level 3  friends! Can you beat it?");
        showDialogWithoutNotificationBar("apprequests", params);
    }

    private void showDialogWithoutNotificationBar(String action, Bundle params) {
        dialog = new WebDialog.Builder(FaceBookInvitations.this, Session.getActiveSession(), action, params).
                setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error != null && !(error instanceof FacebookOperationCanceledException)) {
                            Toast.makeText(FaceBookInvitations.this, "Internet Connection Error", Toast.LENGTH_LONG).show();
                        } else {
                            //HashMap<String, String> m = (HashMap<String, String>) values.getSerializable("mMap");
                            if (values != null)
                                Toast.makeText(FaceBookInvitations.this, "You invited: " + (values.size() - 1) + " Friends", Toast.LENGTH_LONG).show();
                        }
                        dialog = null;
                        dialogAction = null;
                        dialogParams = null;
                    }
                }).build();

        Window dialog_window = dialog.getWindow();
        dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialogAction = action;
        dialogParams = params;

        dialog.show();
    }

    //Callback method for checking send joker availability (user can send one joker every 24 hours)
    public void giftJokerAvailabilityReturned(JSONObject response) {
        Boolean status = false;
        try {
            status = response.getBoolean("status");
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Sorry, Problem when checking server for availability", Toast.LENGTH_LONG).show();
            return;
        }
        if (status) {
            sendWhatsAppInvitation();
        } else {
            Toast.makeText(getApplicationContext(), "Sorry, you can't send more one invitation per day", Toast.LENGTH_LONG).show();
        }
    }
}
