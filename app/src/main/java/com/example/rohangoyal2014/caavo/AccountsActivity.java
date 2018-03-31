package com.example.rohangoyal2014.caavo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class AccountsActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        FirebaseApp.initializeApp(getApplicationContext());
        Utilities.FirebaseUtilities.mAuth=FirebaseAuth.getInstance();
        CardView cardView=findViewById(R.id.login_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
        if(Utilities.FirebaseUtilities.mAuth.getCurrentUser()!=null) {
            startActivity(new Intent(this,StoriesActivity.class));
            finish();
        }

    }

    private void startLogin()
    {

        if (Utilities.FirebaseUtilities.mAuth.getCurrentUser()==null){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                            ))
                            .build(),
                    RC_SIGN_IN);

        }
        else{
            Toast.makeText(this,"You are alrady logged in",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startActivity(new Intent(this,StoriesActivity.class));
                finish();
            } else {
                Toast.makeText(this,getString(R.string.error),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
