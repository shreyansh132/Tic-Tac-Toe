package com.rgame.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.rgame.tictactoe.Tools.AppVariables;
import com.rgame.tictactoe.Tools.OfflineData;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PlayWithAIEasyModeActivity extends AppCompatActivity {
    private OfflineData data;
    private Intent intent;
    private char current_symbol,toggle_symbol;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9;
    private String xName,oName;
    private TextView display_turn;
    private ViewGroup playground;
    private boolean won_or_draw;

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

        setContentView(R.layout.activity_play_with_aieasy_mode);

        current_symbol = 'X';
        toggle_symbol = 'O';
        won_or_draw = false;

        data = new OfflineData(PlayWithAIEasyModeActivity.this);
        intent = getIntent();

        oName = data.getStoredName();
        xName = intent.getStringExtra(AppVariables.INTENT_NAME);

        button1 = findViewById(R.id.offline_ai_easy_mode_button_1);
        button2 = findViewById(R.id.offline_ai_easy_mode_button_2);
        button3 = findViewById(R.id.offline_ai_easy_mode_button_3);
        button4 = findViewById(R.id.offline_ai_easy_mode_button_4);
        button5 = findViewById(R.id.offline_ai_easy_mode_button_5);
        button6 = findViewById(R.id.offline_ai_easy_mode_button_6);
        button7 = findViewById(R.id.offline_ai_easy_mode_button_7);
        button8 = findViewById(R.id.offline_ai_easy_mode_button_8);
        button9 = findViewById(R.id.offline_ai_easy_mode_button_9);
        display_turn = findViewById(R.id.offline_ai_easy_mode_display_turn);
        playground = findViewById(R.id.offline_ai_easy_mode_hall_view);

        displayPlayersTurn();

        Snackbar.make(findViewById(R.id.offline_ai_easy_mode_main_view),xName + " V/s " +  oName,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
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

        won_or_draw = false;

        current_symbol = toggle_symbol;
        if (toggle_symbol == 'X') {
            toggle_symbol = 'O';
        }else {
            toggle_symbol = 'X';
        }
        playground.setBackground(null);
        displayPlayersTurn();
    }

    private void displayPlayersTurn() {
        if (current_symbol == 'X') {
            display_turn.setText(getResources().getString(R.string.x_turn));
            setAllButtonEnabled(false);
            if (!won_or_draw) {
                cpuMove();
            }
        }else {
            display_turn.setText(getResources().getString(R.string.o_turn));
        }
    }

    private void cpuMove() {
        CountDownTimer countDownTimer = new CountDownTimer(1000,500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int buttonToPress = getMove();
                setAllButtonEnabled(true);
                pressButtonForCPU(buttonToPress);
            }
        };
        countDownTimer.start();
    }

    private int getMove() {
        int move;
        int guess_move;
        while (true) {
            guess_move = (int)(Math.random() * 9);
            if (check(guess_move)) {
                move = guess_move + 1;
                break;
            }
        }
        return move;
    }

    private boolean check(int guess) {
        switch (guess + 1) {
            case 1 :
                if (button1.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 2 :
                if (button2.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 3 :
                if (button3.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 4 :
                if (button4.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 5 :
                if (button5.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 6 :
                if (button6.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 7 :
                if (button7.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 8 :
                if (button8.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
            case 9 :
                if (button9.getText().toString().trim().equals("")) {
                    return true;
                }
                break;
        }
        return false;
    }

    private void pressButtonForCPU(int buttonToPress) {
        switch (buttonToPress) {
            case 1 :
                button1.performClick();
                break;
            case 2 :
                button2.performClick();
                break;
            case 3 :
                button3.performClick();
                break;
            case 4 :
                button4.performClick();
                break;
            case 5 :
                button5.performClick();
                break;
            case 6 :
                button6.performClick();
                break;
            case 7 :
                button7.performClick();
                break;
            case 8 :
                button8.performClick();
                break;
            case 9 :
                button9.performClick();
                break;
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
            checkWinOrDraw();
            displayPlayersTurn();
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
        won_or_draw = true;
        Snackbar.make(findViewById(R.id.offline_ai_easy_mode_main_view),"Game Draw!!",Snackbar.LENGTH_LONG).show();
        holdScreen();
    }

    private void won() {
        won_or_draw = true;
        if (current_symbol == 'X') {
            Snackbar.make(findViewById(R.id.offline_ai_easy_mode_main_view),xName + " Won!",Snackbar.LENGTH_LONG).show();
        }else {
            Snackbar.make(findViewById(R.id.offline_ai_easy_mode_main_view),oName + " Won!",Snackbar.LENGTH_LONG).show();
        }
        holdScreen();
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

