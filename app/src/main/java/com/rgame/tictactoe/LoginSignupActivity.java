package com.rgame.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rgame.tictactoe.Tools.AppVariables;

import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginSignupActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView logo;
    private EditText email,password,confirm_password;
    private ImageButton password_visibility;
    private TextView forgot_password,alternate_text,alternate;
    private Button login_signup_button;
    private ViewGroup google_login_button;
    private int hide_show = 0;
    private FirebaseAuth firebaseAuth;
    private Intent intent;
    private Dialog progressDialog;

    //Gmail Login
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/aller.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.activity_login_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new SpotsDialog.Builder().setContext(LoginSignupActivity.this).setTheme(R.style.custom_dialog).setCancelable(false).build();

        logo = findViewById(R.id.login_signup_logo);
        email = findViewById(R.id.login_signup_email);
        password = findViewById(R.id.login_signup_password);
        confirm_password = findViewById(R.id.login_signup_confirm_password);
        password_visibility = findViewById(R.id.login_signup_password_visibilty);
        forgot_password = findViewById(R.id.login_signup_forgot_password);
        alternate = findViewById(R.id.login_signup_alternate);
        alternate_text = findViewById(R.id.login_signup_alternate_text);
        login_signup_button = findViewById(R.id.login_signup_button);
        google_login_button = findViewById(R.id.login_signup_google_button);

        forgot_password.setOnClickListener(this);
        alternate.setOnClickListener(this);
        google_login_button.setOnClickListener(this);
        login_signup_button.setOnClickListener(this);
        password_visibility.setOnClickListener(this);


        //Gmail Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_signup_alternate :
                if (alternate.getText().toString().trim().equals(getResources().getString(R.string.signup))) {
                    confirm_password.setVisibility(View.VISIBLE);
                    forgot_password.setVisibility(View.GONE);
                    login_signup_button.setText(getResources().getString(R.string.signup));
                    google_login_button.setVisibility(View.GONE);
                    alternate_text.setText(getResources().getString(R.string.already_have_account));
                    alternate.setText(getResources().getString(R.string.login));
                    YoYo.with(Techniques.BounceInUp).duration(1000).playOn(logo);
                }else {
                    confirm_password.setVisibility(View.GONE);
                    forgot_password.setVisibility(View.VISIBLE);
                    login_signup_button.setText(getResources().getString(R.string.login));
                    google_login_button.setVisibility(View.VISIBLE);
                    alternate_text.setText(getResources().getString(R.string.dont_have_account));
                    alternate.setText(getResources().getString(R.string.signup));
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(logo);
                }
                email.setText("");
                password.setText("");
                confirm_password.setText("");
                break;
            case R.id.login_signup_password_visibilty :
                if (hide_show == 0) {
                    hide_show = 1;
                    password_visibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_invisible));
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    hide_show = 0;
                    password_visibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_visible));
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                password.setSelection(password.length());
                confirm_password.setSelection(confirm_password.length());
                break;
            case R.id.login_signup_forgot_password :
                break;
            case R.id.login_signup_google_button :
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.login_signup_button :
                if (!TextUtils.isEmpty(email.getText().toString()) && isEmail(email.getText().toString())
                        && !TextUtils.isEmpty(password.getText().toString())) {
                    appLoginSignup();
                }else {
                    Snackbar.make(findViewById(R.id.login_signup_main_view), AppVariables.ERROR_MESSAGE,Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void appLoginSignup() {
        if (login_signup_button.getText().toString().trim().equals(getResources().getString(R.string.login))) {
            loginUser();
        }else {
            if (!TextUtils.isEmpty(confirm_password.getText().toString())
                    && confirm_password.getText().toString().equals(password.getText().toString())) {
                signupUser();
            }else {
                Snackbar.make(findViewById(R.id.login_signup_main_view), AppVariables.ERROR_MESSAGE,Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void signupUser() {
        String userid = email.getText().toString().trim();
        String key = password.getText().toString().trim();
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(userid,key).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    intent = new Intent(LoginSignupActivity.this,RoomActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Snackbar.make(findViewById(R.id.login_signup_main_view), AppVariables.ERROR_MESSAGE,Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void loginUser() {
        String userid = email.getText().toString().trim();
        String key = password.getText().toString().trim();
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userid,key).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    intent = new Intent(LoginSignupActivity.this,RoomActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Snackbar.make(findViewById(R.id.login_signup_main_view), AppVariables.ERROR_MESSAGE,Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern p = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return p.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginSignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            intent = new Intent(LoginSignupActivity.this,RoomActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Snackbar.make(findViewById(R.id.login_signup_main_view), AppVariables.ERROR_MESSAGE,Snackbar.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

}

/*
public void sendForgotLink(View view) {
        EditText email = findViewById(R.id.forgot_email_field);

        if (!TextUtils.isEmpty(email.getText().toString().trim()) && Validation.isEmail(email.getText().toString().trim())) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString().trim())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginSignupActivity.this,appData.RESET_LINK,Toast.LENGTH_LONG).show();
                        forgotPasswordView.setVisibility(View.GONE);
                        loginView.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(LoginSignupActivity.this,appData.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            email.setError(appData.VALID_EMAIL);
        }
    }
*/