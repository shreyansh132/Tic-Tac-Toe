package com.rgame.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rgame.tictactoe.Adapter.SearchAdapter;
import com.rgame.tictactoe.Modal.SearchDataItem;
import com.rgame.tictactoe.Tools.AppVariables;
import com.rgame.tictactoe.Tools.OfflineData;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewGroup create_join_view,share_view,search_join_view;
    private Button create_room,join_room,share_room,private_button,public_button,play_button;
    private TextView owner_name,room_name,room_password;
    private EditText search_area;
    private RecyclerView recyclerView;
    private OfflineData offlineData;
    private SearchAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private String email,rondom_password;
    private int counter;
    private Dialog progressbar;
    private DatabaseReference databaseReference;
    private List<SearchDataItem> list,filtered_list;


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

        setContentView(R.layout.activity_room);

        counter = 1;
        progressbar = new SpotsDialog.Builder().setContext(RoomActivity.this).setCancelable(false)
                .setTheme(R.style.custom_dialog).build();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        offlineData = new OfflineData(RoomActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth == null) {
            Toast.makeText(RoomActivity.this, "Not Authenticated!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RoomActivity.this,LoginSignupActivity.class));
            finish();
        }

        email = firebaseAuth.getCurrentUser().getEmail();
        email = email.substring(0,email.lastIndexOf("@"));

        create_join_view = findViewById(R.id.room_create_join_view);
        share_view = findViewById(R.id.room_share_view);
        search_join_view = findViewById(R.id.room_search_join_view);
        create_room = findViewById(R.id.room_create_button);
        join_room = findViewById(R.id.room_join_button);
        share_room = findViewById(R.id.room_share_button);
        search_area = findViewById(R.id.room_search);
        recyclerView = findViewById(R.id.room_recycler_view);
        owner_name = findViewById(R.id.room_owner_name);
        room_name = findViewById(R.id.room_name);
        private_button = findViewById(R.id.room_private_button);
        public_button = findViewById(R.id.room_public_button);
        room_password = findViewById(R.id.room_password);
        play_button = findViewById(R.id.room_play_button);

        owner_name.setText("Created By " + offlineData.getStoredName());
        room_name.setText("As " + email);
        getRandomPassword();


        create_room.setOnClickListener(this);
        join_room.setOnClickListener(this);
        share_room.setOnClickListener(this);
        private_button.setOnClickListener(this);
        public_button.setOnClickListener(this);
        play_button.setOnClickListener(this);

        list = new ArrayList<>();
        filtered_list = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RoomActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        search_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFromList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchFromList(CharSequence s) {
        filtered_list.clear();
        for (SearchDataItem item : list) {
            if (item.getOwner_name().toLowerCase().contains(s) || item.getRoom_name().toLowerCase().contains(s)) {
                filtered_list.add(item);
            }
        }
        adapter = new SearchAdapter(RoomActivity.this,filtered_list);
        recyclerView.setAdapter(adapter);
    }

    private void getAllList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                String o,n,p,t;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (!d.getKey().equals(email) && !d.child(AppVariables.OPONANT).exists()) {
                        o = d.child(AppVariables.OWNER_NAME).getValue(String.class);
                        n = d.child(AppVariables.ROOM_NAME).getValue(String.class);
                        p = d.child(AppVariables.ROOM_PASSWORD).getValue(String.class);
                        t = d.child(AppVariables.ROOM_TYPE).getValue(String.class);

                        SearchDataItem item = new SearchDataItem(o,n,t,p);

                        list.add(item);
                    }
                    adapter = new SearchAdapter(RoomActivity.this,list);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.room_create_button :
                if (private_button.getVisibility() == View.GONE && public_button.getVisibility() == View.GONE) {
                    public_button.setVisibility(View.VISIBLE);
                    private_button.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeInLeft).duration(1500).playOn(public_button);
                    YoYo.with(Techniques.FadeInRight).duration(1500).playOn(private_button);
                    YoYo.with(Techniques.BounceInUp).duration(1000).playOn(create_room);
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(join_room);
                }else {
                    public_button.setVisibility(View.GONE);
                    private_button.setVisibility(View.GONE);
                    YoYo.with(Techniques.BounceInUp).duration(1000).playOn(join_room);
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(create_room);
                }
                break;
            case R.id.room_private_button :
                counter = 1;
                room_password.setVisibility(View.VISIBLE);
                room_password.setText("Password \'" + rondom_password + "\'");
                showRoomScreen();
                createRoom();
                startMatch();
                break;
            case R.id.room_public_button :
                counter = 2;
                room_password.setVisibility(View.GONE);
                room_password.setText("");
                showRoomScreen();
                createRoom();
                startMatch();
                break;
            case R.id.room_join_button :
                YoYo.with(Techniques.FadeInLeft).duration(500).playOn(search_join_view);
                YoYo.with(Techniques.FadeOutRight).duration(500).playOn(create_join_view);
                YoYo.with(Techniques.FadeOutRight).duration(500).playOn(share_view);

                share_view.setVisibility(View.GONE);
                create_join_view.setVisibility(View.GONE);
                search_join_view.setVisibility(View.VISIBLE);

                getAllList();

                break;
            case R.id.room_share_button :
                shareRoomInfo();
                break;
            case R.id.room_play_button :
                progressbar.show();

                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_1).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_2).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_3).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_4).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_5).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_6).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_7).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_8).setValue("");
                databaseReference.child(email).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_9).setValue("");
                databaseReference.child(email).child(AppVariables.STATUS).setValue("NEW");

                databaseReference.child(email).child(AppVariables.XO_TURN).child(AppVariables.TURN).setValue("X");
                databaseReference.child(email).child(AppVariables.XO_TURN).child(AppVariables.NEXT_SYMBOL).setValue("O");
                databaseReference.child(AppVariables.START_MATCH).child(email).setValue("START");

                Intent i = new Intent(RoomActivity.this,RealTimeGameActivity.class);
                i.putExtra("HOST",email);
                startActivity(i);
                progressbar.dismiss();
                break;
        }
    }

    public void startMatch() {
        Snackbar.make(findViewById(R.id.room_main_view),"Waiting For Someone To Join",Snackbar.LENGTH_LONG).show();
        databaseReference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(AppVariables.OPONANT).exists()) {
                    Snackbar.make(findViewById(R.id.room_main_view),"Your Friend Joined Game",Snackbar.LENGTH_LONG).show();
                    play_button.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(1000).playOn(play_button);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void createRoom() {
        databaseReference.child(email).child(AppVariables.ROOM_NAME).setValue(email);
        databaseReference.child(email).child(AppVariables.OWNER_NAME).setValue(offlineData.getStoredName());
        if (counter == 1) {
            databaseReference.child(email).child(AppVariables.ROOM_TYPE).setValue("Private");
            databaseReference.child(email).child(AppVariables.ROOM_PASSWORD).setValue(rondom_password);
        }else {
            databaseReference.child(email).child(AppVariables.ROOM_TYPE).setValue("Open");
            databaseReference.child(email).child(AppVariables.ROOM_PASSWORD).setValue("");
        }
    }

    private void shareRoomInfo() {
        String appTitle = getResources().getString(R.string.app_name);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        try {
            String link= appTitle + "\n\n This is game \n\n";
            link = link + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            link = link + "Join Your Friend\n By - " + offlineData.getStoredName() + "\nRoom - rahuljha18101996";
            if (counter == 1) {
                link = link + "\nPassword - " + rondom_password + "\n";
            }
            link = link + "\n\nOpen App\n\nopen.rgame.tictactoe.app\n\n";
            i.putExtra(Intent.EXTRA_TEXT,link);
        }catch (ActivityNotFoundException e) {
            Toast.makeText(RoomActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        startActivity(Intent.createChooser(i,"Share Using"));
    }

    @Override
    public void onBackPressed() {
        if (create_join_view.getVisibility() == View.GONE) {
            share_view.setVisibility(View.GONE);
            create_join_view.setVisibility(View.VISIBLE);
            search_join_view.setVisibility(View.GONE);

            YoYo.with(Techniques.FadeOutLeft).duration(500).playOn(search_join_view);
            YoYo.with(Techniques.FadeInRight).duration(500).playOn(create_join_view);
            YoYo.with(Techniques.FadeOutLeft).duration(500).playOn(share_view);

        }else {
            super.onBackPressed();
        }
    }

    public void showRoomScreen() {
        YoYo.with(Techniques.FadeInLeft).duration(500).playOn(share_view);
        YoYo.with(Techniques.FadeOutRight).duration(500).playOn(create_join_view);
        YoYo.with(Techniques.FadeOutRight).duration(500).playOn(search_join_view);

        share_view.setVisibility(View.VISIBLE);
        create_join_view.setVisibility(View.GONE);
        search_join_view.setVisibility(View.GONE);
    }

    public void getRandomPassword() {
        String random_key = "qwertyuiopasdfghjklzxcvbnm1234567890";
        rondom_password = "";
        for (int i = 1; i <= 5; i++) {
            rondom_password += random_key.charAt((int)(Math.random() * random_key.length()));
        }
    }


}
