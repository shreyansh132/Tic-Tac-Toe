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
import com.rgame.tictactoe.Tools.Move;
import com.rgame.tictactoe.Tools.OfflineData;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PlayWithAIHardModeActivity extends AppCompatActivity {
    private OfflineData data;
    private Intent intent;
    private char current_symbol,toggle_symbol;
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9;
    private String xName,oName;
    private TextView display_turn;
    private ViewGroup playground;
    private char board[][];
    private Move move;

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

        setContentView(R.layout.activity_play_with_aihard_mode);

        current_symbol = 'X';
        toggle_symbol = 'O';
        board = new char[3][3];
        move = new Move();
        resetBoard();

        data = new OfflineData(PlayWithAIHardModeActivity.this);
        intent = getIntent();

        oName = data.getStoredName();
        xName = intent.getStringExtra(AppVariables.INTENT_NAME);

        button1 = findViewById(R.id.offline_ai_hard_mode_button_1);
        button2 = findViewById(R.id.offline_ai_hard_mode_button_2);
        button3 = findViewById(R.id.offline_ai_hard_mode_button_3);
        button4 = findViewById(R.id.offline_ai_hard_mode_button_4);
        button5 = findViewById(R.id.offline_ai_hard_mode_button_5);
        button6 = findViewById(R.id.offline_ai_hard_mode_button_6);
        button7 = findViewById(R.id.offline_ai_hard_mode_button_7);
        button8 = findViewById(R.id.offline_ai_hard_mode_button_8);
        button9 = findViewById(R.id.offline_ai_hard_mode_button_9);
        display_turn = findViewById(R.id.offline_ai_hard_mode_display_turn);
        playground = findViewById(R.id.offline_ai_hard_mode_hall_view);

        displayPlayersTurn();

        Snackbar.make(findViewById(R.id.offline_ai_hard_mode_main_view),xName + " V/s " +  oName,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '_';
            }
        }
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
        playground.setBackground(null);
        displayPlayersTurn();
    }

    private void displayPlayersTurn() {
        if (current_symbol == 'X') {
            display_turn.setText(getResources().getString(R.string.x_turn));
            setAllButtonEnabled(false);
            cpuMove();
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
                int buttonToPress = move.findBestMove(board);
                setAllButtonEnabled(true);
                pressButtonForCPU(buttonToPress);
            }
        };
        countDownTimer.start();
    }

    private void pressButtonForCPU(int buttonToPress) {
        switch (buttonToPress) {
            case 1 :
                button1.performClick();
                board[0][0] = 'x';
                break;
            case 2 :
                button2.performClick();
                board[0][1] = 'x';
                break;
            case 3 :
                button3.performClick();
                board[0][2] = 'x';
                break;
            case 4 :
                button4.performClick();
                board[1][0] = 'x';
                break;
            case 5 :
                button5.performClick();
                board[1][1] = 'x';
                break;
            case 6 :
                button6.performClick();
                board[1][2] = 'x';
                break;
            case 7 :
                button7.performClick();
                board[2][0] = 'x';
                break;
            case 8 :
                button8.performClick();
                board[2][1] = 'x';
                break;
            case 9 :
                button9.performClick();
                board[2][2] = 'x';
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
                fillOById(view.getId());
                button.setTextColor(getResources().getColor(R.color.colorYellow));
            }
            checkWinOrDraw();
            displayPlayersTurn();
        }
    }

    private void fillOById(int id) {
        switch (id) {
            case R.id.offline_ai_hard_mode_button_1 :
                board[0][0] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_2 :
                board[0][1] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_3 :
                board[0][2] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_4 :
                board[1][0] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_5 :
                board[1][1] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_6 :
                board[1][2] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_7 :
                board[2][0] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_8 :
                board[2][1] = 'o';
                break;
            case R.id.offline_ai_hard_mode_button_9 :
                board[2][2] = 'o';
                break;
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
        Snackbar.make(findViewById(R.id.offline_ai_hard_mode_main_view),"Game Draw!!",Snackbar.LENGTH_LONG).show();
        holdScreen();
    }

    private void won() {
        if (current_symbol == 'X') {
            Snackbar.make(findViewById(R.id.offline_ai_hard_mode_main_view),xName + " Won!",Snackbar.LENGTH_LONG).show();
        }else {
            Snackbar.make(findViewById(R.id.offline_ai_hard_mode_main_view),oName + " Won!",Snackbar.LENGTH_LONG).show();
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
                resetBoard();
                resetGame();
                setAllButtonEnabled(true);
            }
        };
        countDownTimer.start();
    }
}
