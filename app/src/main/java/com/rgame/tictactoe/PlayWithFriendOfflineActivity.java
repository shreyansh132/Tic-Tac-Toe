package com.rgame.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.rgame.tictactoe.Tools.AppVariables;
import com.rgame.tictactoe.Tools.OfflineData;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PlayWithFriendOfflineActivity extends AppCompatActivity {

    private OfflineData data;
    private Intent intent;
    private char current_symbol,toggle_symbol;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9,button_replay,button_finish;
    private String xName,oName;
    private TextView x_name,o_name,x_score,o_score,result_title;
    private ViewGroup playground,x_turn,o_turn;
    private int number_of_matches,x_win,o_win,number_of_matches_played;
    private Dialog result_dialog;
    private LottieAnimationView lottieAnimationView;

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

        setContentView(R.layout.activity_play_with_friend_offline);

        result_dialog = new Dialog(PlayWithFriendOfflineActivity.this);
        result_dialog.setCancelable(false);
        result_dialog.setContentView(R.layout.match_result_layout);
        Window window = result_dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        lottieAnimationView = result_dialog.findViewById(R.id.result_animation);
        result_title = result_dialog.findViewById(R.id.result_name);
        button_replay = result_dialog.findViewById(R.id.result_button_replay);
        button_finish = result_dialog.findViewById(R.id.result_button_finish);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.result_button_finish :
                        finish();
                        break;
                    case R.id.result_button_replay :
                        number_of_matches_played = 0;
                        x_win = 0;
                        o_win = 0;
                        setScore();
                        result_dialog.dismiss();
                        break;
                }
            }
        };

        button_replay.setOnClickListener(onClickListener);
        button_finish.setOnClickListener(onClickListener);

        current_symbol = 'X';
        toggle_symbol = 'O';

        x_win = 0;
        o_win = 0;
        number_of_matches_played = 0;

        data = new OfflineData(PlayWithFriendOfflineActivity.this);
        intent = getIntent();

        xName = data.getStoredName();
        oName = intent.getStringExtra(AppVariables.INTENT_NAME);
        number_of_matches = intent.getIntExtra(AppVariables.INTENT_MATCHES,-1);

        if (number_of_matches == -1) {
            finish();
        }
        
        button1 = findViewById(R.id.offline_friend_button_1);
        button2 = findViewById(R.id.offline_friend_button_2);
        button3 = findViewById(R.id.offline_friend_button_3);
        button4 = findViewById(R.id.offline_friend_button_4);
        button5 = findViewById(R.id.offline_friend_button_5);
        button6 = findViewById(R.id.offline_friend_button_6);
        button7 = findViewById(R.id.offline_friend_button_7);
        button8 = findViewById(R.id.offline_friend_button_8);
        button9 = findViewById(R.id.offline_friend_button_9);

        x_name = findViewById(R.id.offline_friend_x_name);
        x_score = findViewById(R.id.offline_friend_x_score);
        x_turn = findViewById(R.id.offline_friend_x_turn);

        o_name = findViewById(R.id.offline_friend_o_name);
        o_score = findViewById(R.id.offline_friend_o_score);
        o_turn = findViewById(R.id.offline_friend_o_turn);

        x_name.setText(xName + " [ X ]");
        x_score.setText("00");

        o_name.setText(oName + " [ O ]");
        o_score.setText("00");

        playground = findViewById(R.id.offline_friend_hall_view);

        displayPlayersTurn();

        Snackbar.make(findViewById(R.id.offline_friend_main_view),xName + " V/s " +  oName,Snackbar.LENGTH_LONG).show();
    }


    private void displayPlayersTurn() {
        if (current_symbol == 'X') {
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

    private void resetGame() {
        button1.setText("");
        button2.setText("");
        button3.setText("");
        button4.setText("");
        button5.setText("");
        button6.setText("");
        button7.setText("");
        button8.setText("");
        button9.setText("");

        current_symbol = toggle_symbol;
        if (toggle_symbol == 'X') {
            toggle_symbol = 'O';
        }else {
            toggle_symbol = 'X';
        }
        displayPlayersTurn();
        playground.setBackground(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    public void drawXorO(View view) {
        Button button = (Button) view;
        if (button.getText().toString().trim().equals("")) {
            if (current_symbol == 'X') {
                current_symbol = 'O';
                button.setText("X");
                button.setTextColor(getResources().getColor(R.color.colorLightBlue));
            }else {
                current_symbol = 'X';
                button.setText("O");
                button.setTextColor(getResources().getColor(R.color.colorYellow));
            }
            displayPlayersTurn();
            checkWinOrDraw();
        }
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

    private void draw() {
        number_of_matches_played++;
        Snackbar.make(findViewById(R.id.offline_friend_main_view),"Game Draw!!",Snackbar.LENGTH_LONG).show();
        checkForResult();
        holdScreen();
    }

    private void won() {
        number_of_matches_played++;
        if (current_symbol == 'X') {
            o_win++;
            Snackbar.make(findViewById(R.id.offline_friend_main_view),oName + " Won!",Snackbar.LENGTH_LONG).show();
        }else {
            x_win++;
            Snackbar.make(findViewById(R.id.offline_friend_main_view),xName + " Won!",Snackbar.LENGTH_LONG).show();
        }
        setScore();
        checkForResult();
        holdScreen();
    }

    private void checkForResult() {
        if (number_of_matches_played == number_of_matches) {
            String title;
            lottieAnimationView.setAnimation("winner.json");
            if (x_win == o_win) {
                title = "There is a tie between " + xName + " and " + oName;
                lottieAnimationView.setAnimation("draw.json");
            }else if (x_win > o_win) {
                title = xName + " is the winner!!";
            }else {
                title = oName + " is the winner!!";
            }
            result_title.setText(title);
            result_dialog.show();
            lottieAnimationView.playAnimation();
        }
    }

    private void setScore() {
        if (x_win < 10) {
            x_score.setText("0" + x_win);
        }else {
            x_score.setText("0" + x_win);
        }

        if (o_win < 10) {
            o_score.setText("0" + o_win);
        }else {
            o_score.setText("0" + o_win);
        }
    }

    private void holdScreen() {
        CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                resetGame();
                setAllButtonEnabled(true);
            }
        };
        countDownTimer.start();
    }
}
