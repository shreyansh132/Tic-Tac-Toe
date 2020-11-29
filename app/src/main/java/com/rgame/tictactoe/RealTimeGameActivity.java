package com.rgame.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rgame.tictactoe.Tools.AppVariables;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RealTimeGameActivity extends AppCompatActivity {

    private Intent intent;
    private String room_key,email;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9;
    private TextView x_name,o_name,x_score,o_score;
    private ViewGroup playground,x_turn,o_turn;
    private char current_symbol;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private boolean myroom;

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
                                .setDefaultFontPath("fonts/dumb2d.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.activity_real_time_game);

        intent = getIntent();
        room_key = intent.getStringExtra("HOST");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        email = firebaseAuth.getCurrentUser().getEmail();
        email = email.substring(0,email.lastIndexOf("@"));

        if (email.equals(room_key)) {
            myroom = true;
        }else {
            myroom = false;
        }

        button1 = findViewById(R.id.online_friend_button_1);
        button2 = findViewById(R.id.online_friend_button_2);
        button3 = findViewById(R.id.online_friend_button_3);
        button4 = findViewById(R.id.online_friend_button_4);
        button5 = findViewById(R.id.online_friend_button_5);
        button6 = findViewById(R.id.online_friend_button_6);
        button7 = findViewById(R.id.online_friend_button_7);
        button8 = findViewById(R.id.online_friend_button_8);
        button9 = findViewById(R.id.online_friend_button_9);

        playground = findViewById(R.id.online_friend_hall_view);

        x_name = findViewById(R.id.online_friend_x_name);
        x_score = findViewById(R.id.online_friend_x_score);
        x_turn = findViewById(R.id.online_friend_x_turn);

        o_name = findViewById(R.id.online_friend_o_name);
        o_score = findViewById(R.id.online_friend_o_score);
        o_turn = findViewById(R.id.online_friend_o_turn);

        setTitles();
        setScore();
        updateTurn();
    }

    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    private void setTitles() {
        databaseReference.child(room_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                x_name.setText(dataSnapshot.child(AppVariables.OWNER_NAME).getValue(String.class));
                o_name.setText(dataSnapshot.child(AppVariables.OPONANT).getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setScore() {
        databaseReference.child(AppVariables.SCORE).child(room_key).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int xs = 0,os = 0;
                        if (dataSnapshot.child(AppVariables.X_SCORE).exists()) {
                            xs = (int) dataSnapshot.child(AppVariables.X_SCORE).getChildrenCount();
                        }

                        if (dataSnapshot.child(AppVariables.O_SCORE).exists()) {
                            os = (int) dataSnapshot.child(AppVariables.O_SCORE).getChildrenCount();
                        }

                        if (xs < 10) {
                            x_score.setText("0" + xs);
                        }else {
                            x_score.setText("" + xs);
                        }

                        if (os < 10) {
                            o_score.setText("0" + os);
                        }else {
                            o_score.setText("" + os);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void updateTurn() {
        databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.TURN).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String string_turn = dataSnapshot.getValue(String.class);
                        if (string_turn.equals("XO")) {
                            updateAllButtons();
                            updateStatus();
                            updateCurrentTurn();
                        }else if (string_turn.equals("X")) {
                            current_symbol = 'X';
                            if (myroom) {
                                setAllButtonEnabled(true);
                            }else {
                                setAllButtonEnabled(false);
                            }
                            displayPlayersTurn("X");
                            updateAllButtons();
                        }else {
                            current_symbol = 'O';
                            if (myroom) {
                                setAllButtonEnabled(false);
                            }else {
                                setAllButtonEnabled(true);
                            }
                            displayPlayersTurn("O");
                            updateAllButtons();
                            updateStatus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void updateCurrentTurn() {
        if (myroom) {
            databaseReference.child(room_key).child(AppVariables.XO_TURN).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String next_turn = dataSnapshot.child(AppVariables.NEXT_SYMBOL).getValue(String.class);
                            if (next_turn.equals("X")) {
                                databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.TURN)
                                        .setValue("O");
                            }else {
                                databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.TURN)
                                        .setValue("X");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
    }

    private void displayPlayersTurn(String turn) {
        if (turn.equals("X")) {
            x_turn.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            o_turn.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }else {
            o_turn.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            x_turn.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void setAllButtonEnabled(boolean b) {
        button1.setEnabled(b);
        button2.setEnabled(b);
        button3.setEnabled(b);
        button4.setEnabled(b);
        button5.setEnabled(b);
        button6.setEnabled(b);
        button7.setEnabled(b);
        button8.setEnabled(b);
        button9.setEnabled(b);
    }

    public void drawXorO(View view) {
        Button button = (Button) view;
        if (button.getText().toString().equals("")) {
            switch (view.getId()) {
                case R.id.online_friend_button_1 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_1).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_2 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_2).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_3 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_3).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_4 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_4).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_5 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_5).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_6 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_6).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_7 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_7).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_8 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_8).setValue(current_symbol + "");
                    break;
                case R.id.online_friend_button_9 :
                    databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_9).setValue(current_symbol + "");
                    break;
            }
            setAllButtonEnabled(false);
            toggleCurrentTurn();
        }else {
            Snackbar.make(findViewById(R.id.online_friend_main_view),"Invalid Move",Snackbar.LENGTH_SHORT).show();
        }
    }

    private void toggleCurrentTurn() {
            databaseReference.child(room_key).child(AppVariables.XO_TURN).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String string_turn = dataSnapshot.child(AppVariables.TURN).getValue(String.class);
                            if (string_turn.equals("X")) {
                                databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.TURN).setValue("O");
                            }else {
                                databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.TURN).setValue("X");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
    }

    private void updateStatus() {
        if (myroom) {
            if (button1.getText().toString().equals("") && button2.getText().toString().equals("") && button3.getText().toString().equals("")
                    && button4.getText().toString().equals("") && button5.getText().toString().equals("") && button6.getText().toString().equals("")
                    && button7.getText().toString().equals("") && button8.getText().toString().equals("") && button9.getText().toString().equals("")
            ) {
                databaseReference.child(room_key).child(AppVariables.STATUS).setValue("NEW");
            } else {
                databaseReference.child(room_key).child(AppVariables.STATUS).setValue("INGAME");
            }
        }
    }

    private void updateAllButtons() {
        databaseReference.child(room_key).child(AppVariables.PLAYGROUND).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!button1.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_1).getValue(String.class))) {
                            button1.setText(dataSnapshot.child(AppVariables.BUTTON_1).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_1).getValue(String.class).equals("X")) {
                                button1.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button1.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button2.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_2).getValue(String.class))) {
                            button2.setText(dataSnapshot.child(AppVariables.BUTTON_2).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_2).getValue(String.class).equals("X")) {
                                button2.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button2.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button3.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_3).getValue(String.class))) {
                            button3.setText(dataSnapshot.child(AppVariables.BUTTON_3).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_3).getValue(String.class).equals("X")) {
                                button3.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button3.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button4.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_4).getValue(String.class))) {
                            button4.setText(dataSnapshot.child(AppVariables.BUTTON_4).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_4).getValue(String.class).equals("X")) {
                                button4.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button4.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button5.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_5).getValue(String.class))) {
                            button5.setText(dataSnapshot.child(AppVariables.BUTTON_5).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_5).getValue(String.class).equals("X")) {
                                button5.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button5.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button6.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_6).getValue(String.class))) {
                            button6.setText(dataSnapshot.child(AppVariables.BUTTON_6).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_6).getValue(String.class).equals("X")) {
                                button6.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button6.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button7.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_7).getValue(String.class))) {
                            button7.setText(dataSnapshot.child(AppVariables.BUTTON_7).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_7).getValue(String.class).equals("X")) {
                                button7.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button7.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button8.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_8).getValue(String.class))) {
                            button8.setText(dataSnapshot.child(AppVariables.BUTTON_8).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_8).getValue(String.class).equals("X")) {
                                button8.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            }else {
                                button8.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }

                        if (!button9.getText().toString().equals(dataSnapshot.child(AppVariables.BUTTON_9).getValue(String.class))) {
                            button9.setText(dataSnapshot.child(AppVariables.BUTTON_9).getValue(String.class));
                            checkWinOrDraw();
                            if (dataSnapshot.child(AppVariables.BUTTON_9).getValue(String.class).equals("X")) {
                                button9.setTextColor(getResources().getColor(R.color.colorLightBlue));
                            } else {
                                button9.setTextColor(getResources().getColor(R.color.colorYellow));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void checkWinOrDraw() {
        if (button1.getText().toString().equals(button2.getText().toString()) && button2.getText().toString().equals(button3.getText().toString())
                && !button1.getText().toString().equals("") && !button2.getText().toString().equals("") && !button3.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_1));
        }else if (button4.getText().toString().equals(button5.getText().toString()) && button5.getText().toString().equals(button6.getText().toString())
                && !button4.getText().toString().equals("") && !button5.getText().toString().equals("") && !button6.getText().toString().equals("")){
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_2));
        }else if (button7.getText().toString().equals(button8.getText().toString()) && button8.getText().toString().equals(button9.getText().toString())
                && !button7.getText().toString().equals("") && !button8.getText().toString().equals("") && !button9.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_3));
        }else if (button1.getText().toString().equals(button4.getText().toString()) && button4.getText().toString().equals(button7.getText().toString())
                && !button1.getText().toString().equals("") && !button4.getText().toString().equals("") && !button7.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_4));
        }else if (button2.getText().toString().equals(button5.getText().toString()) && button5.getText().toString().equals(button8.getText().toString())
                && !button2.getText().toString().equals("") && !button5.getText().toString().equals("") && !button8.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_5));
        }else if (button3.getText().toString().equals(button6.getText().toString()) && button6.getText().toString().equals(button9.getText().toString())
                && !button3.getText().toString().equals("") && !button6.getText().toString().equals("") && !button9.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_6));
        }else if (button1.getText().toString().equals(button5.getText().toString()) && button5.getText().toString().equals(button9.getText().toString())
                && !button1.getText().toString().equals("") && !button5.getText().toString().equals("") && !button9.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_7));
        }else if (button3.getText().toString().equals(button5.getText().toString()) && button5.getText().toString().equals(button7.getText().toString())
                && !button3.getText().toString().equals("") && !button5.getText().toString().equals("") && !button7.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            won();
            playground.setBackground(getResources().getDrawable(R.drawable.win_position_8));
        }else if (!button1.getText().toString().equals("") && !button2.getText().toString().equals("") && !button3.getText().toString().equals("") && !button4.getText().toString().equals("")
                && !button5.getText().toString().equals("") && !button6.getText().toString().equals("") && !button7.getText().toString().equals("") && !button8.getText().toString().equals("")
                && !button9.getText().toString().equals("")) {
            setAllButtonEnabled(false);
            draw();
        }
    }

    private void won() {
        if (myroom) {
            databaseReference.child(room_key).child(AppVariables.STATUS).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class).equals("INGAME")) {
                                if (current_symbol != 'X') {
                                    databaseReference.child(AppVariables.SCORE).child(room_key).child(AppVariables.X_SCORE).push().setValue(1);
                                    showToast("X");
                                }else {
                                    databaseReference.child(AppVariables.SCORE).child(room_key).child(AppVariables.O_SCORE).push().setValue(1);
                                    showToast("O");
                                }
                                databaseReference.child(room_key).child(AppVariables.STATUS).setValue("NEW");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
        waitingScreen();
    }

    private void showToast(final String winner) {
        databaseReference.child(room_key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (winner.equals("X")) {
                            Snackbar.make(findViewById(R.id.online_friend_main_view),dataSnapshot.child(AppVariables.OWNER_NAME)
                                    .getValue(String.class) + " Wins!!",Snackbar.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(findViewById(R.id.online_friend_main_view),dataSnapshot.child(AppVariables.OPONANT)
                                    .getValue(String.class) + " Wins!!",Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void waitingScreen() {
        CountDownTimer countDownTimer = new CountDownTimer(1500,500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                resetGame();
            }
        };
        countDownTimer.start();
    }

    private void draw() {
        Snackbar.make(findViewById(R.id.online_friend_main_view),"Game Draw",Snackbar.LENGTH_LONG).show();
        resetGame();
    }

    private void resetGame() {
        playground.setBackground(null);
        if (myroom) {
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_1).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_2).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_3).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_4).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_5).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_6).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_7).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_8).setValue("");
            databaseReference.child(room_key).child(AppVariables.PLAYGROUND).child(AppVariables.BUTTON_9).setValue("");

            databaseReference.child(room_key).child(AppVariables.XO_TURN).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String next_turn = dataSnapshot.child(AppVariables.NEXT_SYMBOL).getValue(String.class);
                            if (next_turn.equals("X")) {
                                databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.NEXT_SYMBOL)
                                        .setValue("O");
                            }else {
                                databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.NEXT_SYMBOL)
                                        .setValue("X");
                            }
                            databaseReference.child(room_key).child(AppVariables.XO_TURN).child(AppVariables.TURN)
                                    .setValue("XO");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
    }
}
