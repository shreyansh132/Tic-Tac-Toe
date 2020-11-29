package com.rgame.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.rgame.tictactoe.Tools.AppVariables;
import com.rgame.tictactoe.Tools.OfflineData;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout local_play_button;
    private Button online_play,offline_friend,offline_ai,invite_friends,mode_button;
    private ViewGroup local_play_view, number_of_matches_view;
    private ImageView setting;
    private OfflineData data;
    private Dialog set_up_name_dialog,mode_dialog;
    private TextView set_up_title,matches_counter,mode_text;
    private ImageView left_arrow,right_arrow,mode_left,mode_right;
    private EditText name_edittext;
    private Button set_up_name;
    private Intent intent;
    private int counter = 3;
    private FirebaseAuth firebaseAuth;
    private String mode_list;

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

        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();

        mode_dialog = new Dialog(DashboardActivity.this);
        mode_dialog.setContentView(R.layout.ai_mode_layout);
        mode_dialog.setCancelable(true);
        mode_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mode_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mode_left = mode_dialog.findViewById(R.id.mode_left_arrow);
        mode_right = mode_dialog.findViewById(R.id.mode_right_arrow);
        mode_text = mode_dialog.findViewById(R.id.mode);
        mode_button = mode_dialog.findViewById(R.id.mode_button);
        mode_list = getResources().getString(R.string.level_easy);
        mode_text.setText(mode_list);
        mode_left.setOnClickListener(this);
        mode_right.setOnClickListener(this);
        mode_button.setOnClickListener(this);


        set_up_name_dialog = new Dialog(DashboardActivity.this);
        set_up_name_dialog.setContentView(R.layout.set_up_name_layout);
        set_up_name_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        set_up_name_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        name_edittext = set_up_name_dialog.findViewById(R.id.set_up_name_field);
        set_up_name = set_up_name_dialog.findViewById(R.id.set_up_name_field_button);
        set_up_title = set_up_name_dialog.findViewById(R.id.set_up_name_title);
        number_of_matches_view = set_up_name_dialog.findViewById(R.id.number_of_matches_view);
        matches_counter = set_up_name_dialog.findViewById(R.id.matches_counter);
        left_arrow = set_up_name_dialog.findViewById(R.id.left_arrow);
        right_arrow = set_up_name_dialog.findViewById(R.id.right_arrow);
        set_up_name.setOnClickListener(this);
        left_arrow.setOnClickListener(this);
        right_arrow.setOnClickListener(this);

        data = new OfflineData(DashboardActivity.this);
        local_play_button = findViewById(R.id.dashboard_local_play_button);
        local_play_view = findViewById(R.id.dashboard_local_play_view);
        offline_friend = findViewById(R.id.dashboard_local_play_with_friend_button);
        offline_ai = findViewById(R.id.dashboard_local_play_with_ai_button);
        online_play = findViewById(R.id.dashboard_real_time_play_button);
        setting = findViewById(R.id.dashboard_setting);
        invite_friends = findViewById(R.id.dashboard_invite_friends);
        

        local_play_button.setOnClickListener(this);
        offline_friend.setOnClickListener(this);
        offline_ai.setOnClickListener(this);
        invite_friends.setOnClickListener(this);
        online_play.setOnClickListener(this);
        
        if (data.getStoredName().equals(AppVariables.NOT_INITIALISED) || data.getStoredName().equals("")) {
            set_up_name_dialog.setCancelable(false);
            set_up_title.setText(getResources().getString(R.string.tell_us_name));
            name_edittext.setHint(getResources().getString(R.string.name_hint));
            set_up_name.setText(getResources().getString(R.string.submit));
            number_of_matches_view.setVisibility(View.GONE);
            set_up_name_dialog.show();
        }else {
            Snackbar.make(findViewById(R.id.dashboard_main_view),"Welcome back " + data.getStoredName() + "!",Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_up_name_field_button :
                if (set_up_name.getText().toString().trim().equals(getResources().getString(R.string.submit))) {
                    if (!TextUtils.isEmpty(name_edittext.getText().toString().trim()) && name_edittext.getText().toString().trim().length() >= 2) {
                        data.storeName(name_edittext.getText().toString().trim());
                        set_up_name_dialog.dismiss();
                    }else {
                        Snackbar.make(findViewById(R.id.dashboard_main_view),AppVariables.ENTER_HINT,Snackbar.LENGTH_SHORT).show();
                    }
                }else {
                    if (!TextUtils.isEmpty(name_edittext.getText().toString().trim()) &&
                            name_edittext.getText().toString().trim().length() >= 2 && !name_edittext.getText().toString().trim().equals(data.getStoredName())) {
                        intent = new Intent(DashboardActivity.this,PlayWithFriendOfflineActivity.class);
                        intent.putExtra(AppVariables.INTENT_NAME,name_edittext.getText().toString().trim());
                        intent.putExtra(AppVariables.INTENT_MATCHES,counter);
                        startActivity(intent);
                        set_up_name_dialog.dismiss();
                    }else {
                        Snackbar.make(findViewById(R.id.dashboard_main_view),AppVariables.ENTER_HINT,Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.dashboard_local_play_button :
                if (local_play_view.getVisibility() == View.GONE) {
                    local_play_view.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(local_play_view);
                    YoYo.with(Techniques.BounceInUp).duration(1000).playOn(findViewById(R.id.dashboard_main_logo));
                    online_play.setVisibility(View.GONE);
                }else {
                    YoYo.with(Techniques.FadeInRight).duration(1000).playOn(online_play);
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(findViewById(R.id.dashboard_main_logo));
                    local_play_view.setVisibility(View.GONE);
                    online_play.setVisibility(View.VISIBLE);
                }
                YoYo.with(Techniques.RotateIn).duration(1000).playOn(setting);
                break;

            case R.id.dashboard_local_play_with_friend_button :
                set_up_title.setText(getResources().getString(R.string.tell_us_friend_name));
                name_edittext.setHint(getResources().getString(R.string.friend_name_hint));
                set_up_name.setText(getResources().getString(R.string.play));
                number_of_matches_view.setVisibility(View.VISIBLE);
                setMatches();
                set_up_name_dialog.setCancelable(true);
                set_up_name_dialog.show();
                break;
            case R.id.dashboard_local_play_with_ai_button :
                mode_dialog.show();
                break;
            case R.id.mode_left_arrow :
                if (mode_list.equals(getResources().getString(R.string.level_hard))) {
                    mode_list = getResources().getString(R.string.level_medium);
                }else if (mode_list.equals(getResources().getString(R.string.level_medium))) {
                    mode_list = getResources().getString(R.string.level_easy);
                }
                mode_text.setText(mode_list);
                YoYo.with(Techniques.BounceInLeft).duration(400).playOn(mode_left);
                break;
            case R.id.mode_right_arrow :
                if (mode_list.equals(getResources().getString(R.string.level_easy))) {
                    mode_list = getResources().getString(R.string.level_medium);
                }else if (mode_list.equals(getResources().getString(R.string.level_medium))) {
                    mode_list = getResources().getString(R.string.level_hard);
                }
                YoYo.with(Techniques.BounceInRight).duration(400).playOn(mode_right);
                mode_text.setText(mode_list);
                break;
            case R.id.mode_button :
                if (mode_list.equals(getResources().getString(R.string.level_easy))) {
                    intent = new Intent(DashboardActivity.this,PlayWithAIEasyModeActivity.class);
                }else if (mode_list.equals(getResources().getString(R.string.level_medium))){
                    intent = new Intent(DashboardActivity.this,PlayWithAIMediumModeActivity.class);
                }else {
                    intent = new Intent(DashboardActivity.this,PlayWithAIHardModeActivity.class);
                }
                intent.putExtra(AppVariables.INTENT_NAME,"AI");
                startActivity(intent);
                mode_dialog.dismiss();
                break;
            case R.id.dashboard_invite_friends : 
                shareApp();
                break;
            case R.id.left_arrow :
                if (counter > 3) { //Here 3
                    counter -= 2;
                }
                setMatches();
                YoYo.with(Techniques.BounceInLeft).duration(400).playOn(left_arrow);
                break;
            case R.id.right_arrow :
                if (counter < 29) {
                    counter += 2;
                }
                setMatches();
                YoYo.with(Techniques.BounceInRight).duration(400).playOn(right_arrow);
                break;
            case R.id.dashboard_real_time_play_button :
                if (firebaseAuth.getCurrentUser() == null) {
                    intent = new Intent(DashboardActivity.this,LoginSignupActivity.class);
                }else {
                    intent = new Intent(DashboardActivity.this,RoomActivity.class);
                }
                startActivity(intent);
                break;

        }
    }

    private void setMatches() {
        if (counter < 10) {
            matches_counter.setText("0" + counter);
        }else {
            matches_counter.setText("" + counter);
        }
    }

    private void shareApp() {
        String appTitle = getResources().getString(R.string.app_name);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        try {
            String link= appTitle + "\n\n This is game \n\n";
            link = link + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            i.putExtra(Intent.EXTRA_TEXT,link);
        }catch (ActivityNotFoundException e) {
            Toast.makeText(DashboardActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        startActivity(Intent.createChooser(i,"Share Using"));
    }

    public void SettingActivity(View view) {
        startActivity(new Intent(DashboardActivity.this, SettingActivity.class));
    }
}
