package com.example.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Multiplayer extends Activity implements ViewFactory, OnClickListener {
    /** Called when the activity is first created. */

    public static LinearLayout layout;

    // All elements used are defined here
    public static TextView tvDealerBet, tvPlayer1Bet, tvPlayer2Bet, tvPlayer3Bet, tvPlayer1Money, tvPlayer2Money, tvPlayer3Money,
             tvDealerScore, tvPlayer1Score, tvPlayer2Score, tvPlayer3Score, text;
    ImageSwitcher ivDealerCard1, ivDealerCard2, ivDealerCard3, ivDealerCard4, ivDealerCard5;
    ImageSwitcher ivPlayer1Card1, ivPlayer1Card2, ivPlayer1Card3, ivPlayer1Card4, ivPlayer1Card5;
    ImageSwitcher ivPlayer2Card1, ivPlayer2Card2, ivPlayer2Card3, ivPlayer2Card4, ivPlayer2Card5;
    ImageSwitcher ivPlayer3Card1, ivPlayer3Card2, ivPlayer3Card3, ivPlayer3Card4, ivPlayer3Card5;
    Button btnPlaceBet, btnExit;
    Button btnHit, btnStand, btnSurrender;
    Button btnOneDollar, btnFiveDollar, btnTenDollar, btnTwentyFiveDollar;

    static final String[] challengeString = new String[3];
    // SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

    MediaPlayer mp;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");
    // All local variables here
    static double _Player1money = 0, _Player2money = 0, _Player3money = 0, _Dealermoney = 0;
    int _Player1bet = 0, _Player2bet = 0, _Player3bet = 0, _DealerBet = 0;
    int _dealerScore = 0, _player1Score = 0, _player2Score = 0, _player3Score = 0;
    int _dealerCardNumber = 0, _player1CardNumber = 0, _player2CardNumber = 0, _player3CardNumber = 0;
    int _randomNumber;
    int _splitCard;
    int _splitDealerCard;
    int _splitScore;
    int _splitBet = 0;
//    int _highestScore;
    boolean player1Burst=false, player2Burst=false, player3Burst=false;

    // To make sure no card comes twice
    ArrayList<Integer> _alCardsTracking = new ArrayList<Integer>();

    // Dealer and Player Aces Check
    char[] _dealerCardArray = new char[] { '0', '0', '0', '0', '0' };
    char[] _player1CardArray = new char[] { '0', '0', '0', '0', '0' };
    char[] _player2CardArray = new char[] { '0', '0', '0', '0', '0' };
    char[] _player3CardArray = new char[] { '0', '0', '0', '0', '0' };

    // Dealer and Player Score Count
    int[] _dealerScoreCount = new int[] { 0, 0, 0, 0, 0 };
    int[] _player1ScoreCount = new int[] { 0, 0, 0, 0, 0 };
    int[] _player2ScoreCount = new int[] { 0, 0, 0, 0, 0 };
    int[] _player3ScoreCount = new int[] { 0, 0, 0, 0, 0 };

    // Internal Storage
    private final String saveFileName1 = "savingHighScoreOfBlackJack1";
    private final String saveFileName2 = "savingHighScoreOfBlackJack2";
    private final String saveFileName3 = "savingHighScoreOfBlackJack3";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        casinoMusic();

        // Setting up all variables here
        setupVariables();

        // ImageSwitcher
        imageSwitcherStuff();

        // Starting stuff
        _Player1money = 500;
        _Player2money = 500;
        _Player3money = 500;

        tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        tvPlayer2Money.setText("PLayer2 Balance: $" + _Player2money);
        tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);

        hidePlayButtons();
        loadingHighScorePlayer1();
        loadingHighScorePlayer2();
        loadingHighScorePlayer3();
    }

    public void casinoMusic()
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
        if(gameSound){
            stop();
        }

        mp = MediaPlayer
                .create(Multiplayer.this, R.raw.casino);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
    }

    public void cardSwingMusic()
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
        if(gameSound){
            stop();
        }

        mp = MediaPlayer
                .create(Multiplayer.this, R.raw.card_swing);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
    }

    private void imageSwitcherStuff() {

        cardSwingMusic();
        ivDealerCard1.setFactory(this);
        ivDealerCard2.setFactory(this);
        ivDealerCard3.setFactory(this);
        ivDealerCard4.setFactory(this);
        ivDealerCard5.setFactory(this);

        ivPlayer1Card1.setFactory(this);
        ivPlayer1Card2.setFactory(this);
        ivPlayer1Card3.setFactory(this);
        ivPlayer1Card4.setFactory(this);
        ivPlayer1Card5.setFactory(this);

        ivPlayer2Card1.setFactory(this);
        ivPlayer2Card2.setFactory(this);
        ivPlayer2Card3.setFactory(this);
        ivPlayer2Card4.setFactory(this);
        ivPlayer2Card5.setFactory(this);

        ivPlayer3Card1.setFactory(this);
        ivPlayer3Card2.setFactory(this);
        ivPlayer3Card3.setFactory(this);
        ivPlayer3Card4.setFactory(this);
        ivPlayer3Card5.setFactory(this);

        ivDealerCard1.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivDealerCard1.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivDealerCard2.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivDealerCard2.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivDealerCard3.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivDealerCard3.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivDealerCard4.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivDealerCard4.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivDealerCard5.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivDealerCard5.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));
//=====================================
        ivPlayer1Card1.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer1Card1.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer1Card2.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer1Card2.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer1Card3.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer1Card3.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer1Card4.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer1Card4.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer1Card5.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer1Card5.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));
//====================
        ivPlayer2Card1.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer2Card1.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer2Card2.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer2Card2.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer2Card3.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer2Card3.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer2Card4.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer2Card4.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer2Card5.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer2Card5.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));
//====================================
        ivPlayer3Card1.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer3Card1.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer3Card2.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer3Card2.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer3Card3.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer3Card3.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer3Card4.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer3Card4.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivPlayer3Card5.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        ivPlayer3Card5.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.sequential));

        ivDealerCard1.setImageResource(R.drawable.default_red);
        ivDealerCard2.setImageResource(R.drawable.default_red);
        ivDealerCard3.setImageResource(R.drawable.default_red);
        ivDealerCard4.setImageResource(R.drawable.default_red);
        ivDealerCard5.setImageResource(R.drawable.default_red);

        ivPlayer1Card1.setImageResource(R.drawable.default_blue);
        ivPlayer1Card2.setImageResource(R.drawable.default_blue);
        ivPlayer1Card3.setImageResource(R.drawable.default_blue);
        ivPlayer1Card4.setImageResource(R.drawable.default_blue);
        ivPlayer1Card5.setImageResource(R.drawable.default_blue);

        ivPlayer2Card1.setImageResource(R.drawable.default_blue);
        ivPlayer2Card2.setImageResource(R.drawable.default_blue);
        ivPlayer2Card3.setImageResource(R.drawable.default_blue);
        ivPlayer2Card4.setImageResource(R.drawable.default_blue);
        ivPlayer2Card5.setImageResource(R.drawable.default_blue);

        ivPlayer3Card1.setImageResource(R.drawable.default_blue);
        ivPlayer3Card2.setImageResource(R.drawable.default_blue);
        ivPlayer3Card3.setImageResource(R.drawable.default_blue);
        ivPlayer3Card4.setImageResource(R.drawable.default_blue);
        ivPlayer3Card5.setImageResource(R.drawable.default_blue);

    }

    private void resetEveryThing() {

        ivDealerCard1.setImageResource(R.drawable.default_red);
        ivDealerCard2.setImageResource(R.drawable.default_red);
        ivDealerCard3.setImageResource(R.drawable.default_red);
        ivDealerCard4.setImageResource(R.drawable.default_red);
        ivDealerCard5.setImageResource(R.drawable.default_red);

        ivPlayer1Card1.setImageResource(R.drawable.default_blue);
        ivPlayer1Card2.setImageResource(R.drawable.default_blue);
        ivPlayer1Card3.setImageResource(R.drawable.default_blue);
        ivPlayer1Card4.setImageResource(R.drawable.default_blue);
        ivPlayer1Card5.setImageResource(R.drawable.default_blue);

        ivPlayer2Card1.setImageResource(R.drawable.default_blue);
        ivPlayer2Card2.setImageResource(R.drawable.default_blue);
        ivPlayer2Card3.setImageResource(R.drawable.default_blue);
        ivPlayer2Card4.setImageResource(R.drawable.default_blue);
        ivPlayer2Card5.setImageResource(R.drawable.default_blue);

        ivPlayer3Card1.setImageResource(R.drawable.default_blue);
        ivPlayer3Card2.setImageResource(R.drawable.default_blue);
        ivPlayer3Card3.setImageResource(R.drawable.default_blue);
        ivPlayer3Card4.setImageResource(R.drawable.default_blue);
        ivPlayer3Card5.setImageResource(R.drawable.default_blue);

//        btnPlaceBet.setVisibility(View.VISIBLE);
        hidePlayButtons();
        _dealerCardNumber = _player1CardNumber = _player2CardNumber = _player3CardNumber = 0;
        _dealerScore = _player1Score = _player2Score = _player3Score = 0;
        _alCardsTracking.clear();

        for (int i = 0; i < 5; i++) {
            _dealerCardArray[i] = '0';
            _dealerScoreCount[i] = 0;
            _player1CardArray[i] = '0';
            _player1ScoreCount[i] = 0;
            _player2CardArray[i] = '0';
            _player2ScoreCount[i] = 0;
            _player3CardArray[i] = '0';
            _player3ScoreCount[i] = 0;
        }

        showTextViews();

        // Setting up high score
//        highScoreCompare();

        // User don't have any money left
        if (_Player1money <= 0 || _Player1money <= 0 || _Player1money <= 0) {
            playAllOverAgainAlertBox();
        }

    }

    // Setting up high score
//    private void highScoreCompare() {
//        if (_player1Score > _player2Score && _player1Score > _player3Score) {
//            _player1M
//            tvPlayer1Money.setText("Player1 Score: $" + _highestScore);
//        }
//    }

    // Saving highScore
    private void savingHighScorePlayer1() {

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(saveFileName1, MODE_PRIVATE)));
            writer.write("" + _Player1money);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Saving highScore
    private void savingHighScorePlayer2() {

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(saveFileName2, MODE_PRIVATE)));
            writer.write("" + _Player2money);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Saving highScore
    private void savingHighScorePlayer3() {

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(saveFileName3, MODE_PRIVATE)));
            writer.write("" + _Player3money);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Loading high score
    private void loadingHighScorePlayer1() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput(saveFileName1)));
            String highScore = reader.readLine();
            BigDecimal bd = new BigDecimal(highScore).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player1money += newNum;
            reader.close();
            tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadingHighScorePlayer2() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput(saveFileName2)));
            String highScore = reader.readLine();
            BigDecimal bd = new BigDecimal(highScore).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player2money += newNum;
            reader.close();
            tvPlayer2Money.setText("Player2 Balance: $" + _Player2money);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadingHighScorePlayer3() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput(saveFileName3)));
            String highScore = reader.readLine();
            BigDecimal bd = new BigDecimal(highScore).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player3money += newNum;
            reader.close();
            tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // setupVariables
    private void setupVariables() {

        layout = (LinearLayout) findViewById(R.id.parentLayout);

        tvPlayer1Money = (TextView) findViewById(R.id.tvPlayer1Money);
        tvPlayer2Money = (TextView) findViewById(R.id.tvPlayer2Money);
        tvPlayer3Money = (TextView) findViewById(R.id.tvPlayer3Money);

        tvDealerScore = (TextView) findViewById(R.id.tvDealer);
        tvPlayer1Score = (TextView) findViewById(R.id.tvPlayer1Score);
        tvPlayer2Score = (TextView) findViewById(R.id.tvPlayer2Score);
        tvPlayer3Score = (TextView) findViewById(R.id.tvPlayer3Score);

        tvPlayer1Bet = (TextView) findViewById(R.id.tvPlayer1Bet);
        tvPlayer2Bet = (TextView) findViewById(R.id.tvPlayer2Bet);
        tvPlayer3Bet = (TextView) findViewById(R.id.tvPlayer3Bet);
        tvDealerBet = (TextView) findViewById(R.id.tvDealerBet);

        ivDealerCard1 = (ImageSwitcher) findViewById(R.id.ivDealerCard1);
        ivDealerCard2 = (ImageSwitcher) findViewById(R.id.ivDealerCard2);
        ivDealerCard3 = (ImageSwitcher) findViewById(R.id.ivDealerCard3);
        ivDealerCard4 = (ImageSwitcher) findViewById(R.id.ivDealerCard4);
        ivDealerCard5 = (ImageSwitcher) findViewById(R.id.ivDealerCard5);

        ivPlayer1Card1 = (ImageSwitcher) findViewById(R.id.ivPlayer1Card1);
        ivPlayer1Card2 = (ImageSwitcher) findViewById(R.id.ivPlayer1Card2);
        ivPlayer1Card3 = (ImageSwitcher) findViewById(R.id.ivPlayer1Card3);
        ivPlayer1Card4 = (ImageSwitcher) findViewById(R.id.ivPlayer1Card4);
        ivPlayer1Card5 = (ImageSwitcher) findViewById(R.id.ivPlayer1Card5);

        ivPlayer2Card1 = (ImageSwitcher) findViewById(R.id.ivPlayer2Card1);
        ivPlayer2Card2 = (ImageSwitcher) findViewById(R.id.ivPlayer2Card2);
        ivPlayer2Card3 = (ImageSwitcher) findViewById(R.id.ivPlayer2Card3);
        ivPlayer2Card4 = (ImageSwitcher) findViewById(R.id.ivPlayer2Card4);
        ivPlayer2Card5 = (ImageSwitcher) findViewById(R.id.ivPlayer2Card5);

        ivPlayer3Card1 = (ImageSwitcher) findViewById(R.id.ivPlayer3Card1);
        ivPlayer3Card2 = (ImageSwitcher) findViewById(R.id.ivPlayer3Card2);
        ivPlayer3Card3 = (ImageSwitcher) findViewById(R.id.ivPlayer3Card3);
        ivPlayer3Card4 = (ImageSwitcher) findViewById(R.id.ivPlayer3Card4);
        ivPlayer3Card5 = (ImageSwitcher) findViewById(R.id.ivPlayer3Card5);

        btnHit = (Button) findViewById(R.id.btnHit);
        btnStand = (Button) findViewById(R.id.btnStand);
        btnSurrender = (Button) findViewById(R.id.btnSurrender);

        btnHit.setOnClickListener(this);
        btnStand.setOnClickListener(this);
        btnSurrender.setOnClickListener(this);

        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);

        btnOneDollar = (Button) findViewById(R.id.btnOneDollar);
        btnOneDollar.setOnClickListener(this);

        btnFiveDollar = (Button) findViewById(R.id.btnFiveDollar);
        btnFiveDollar.setOnClickListener(this);

        btnTenDollar = (Button) findViewById(R.id.btnTenDollar);
        btnTenDollar.setOnClickListener(this);

        btnTwentyFiveDollar = (Button) findViewById(R.id.btnTwentyFiveDollar);
        btnTwentyFiveDollar.setOnClickListener(this);
    }

    public void onClick(View v) {

        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
            if(gameSound){
                stop();
            }

            mp = MediaPlayer
                    .create(Multiplayer.this, R.raw.btn_click);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });


//            }

        }
//        if (_player1CardNumber > 5 || _player2CardNumber > 5 || _player3CardNumber > 5 || _dealerCardNumber > 5) {
//            return;
//        }

        switch (v.getId()) {

            case R.id.btnOneDollar:
                    try {
                        btnOneDollarClick();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                break;

            case R.id.btnFiveDollar:
                    try {
                        btnFiveDollarClick();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                break;
            case R.id.btnTenDollar:
                    try {
                        btnTenDollarClick();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                break;
            case R.id.btnTwentyFiveDollar:
                    try {
                        btnTwentyFiveDollarClick();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                break;

            // Hit
            case R.id.btnHit:

                try {
                    btnHitClick();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            // Stand
            case R.id.btnStand:

                try {
                    btnStandClick();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                alertBox();
                break;

            case R.id.btnSurrender:

                // Surrender
                _Player1money += _Player1bet / 2;
                _Player2money += _Player2bet / 2;
                _Player3money += _Player3bet / 2;
                alertBoxToast("You Surrendered, You got half of your money back." + (_Player1bet/2));
                resetEveryThing();
//                alertBox();
                break;

            case R.id.btnExit:
                alertBoxWithBet("Are you sure to exit game?");
                savingHighScorePlayer1();
                savingHighScorePlayer2();
                savingHighScorePlayer3();
//                finish();

                Intent intent1 = new Intent(Multiplayer.this, Home1.class);
                startActivity(intent1);
                break;

            default:
                break;
        }

        // OutSide Switch and case

        // showTextViews function
        showTextViews();

    }

    private void gameStart() throws InterruptedException {

        // Opening 1 Card of Dealer
        dealerCall();
        calculateDealerScore();

        // Opening 2 Cards of Player
//        ivDealerCard1.setVisibility(View.VISIBLE);

        ivPlayer1Card1.setVisibility(View.VISIBLE);
        ivPlayer1Card2.setVisibility(View.VISIBLE);

        ivPlayer2Card1.setVisibility(View.VISIBLE);
        ivPlayer2Card2.setVisibility(View.VISIBLE);

        ivPlayer3Card1.setVisibility(View.VISIBLE);
        ivPlayer3Card2.setVisibility(View.VISIBLE);

        player1Call();
        player2Call();
        player3Call();

        // Opening 2 Cards of Player
        player1Call();
        player2Call();
        player3Call();

        calculatePlayer1Score();
        calculatePlayer2Score();
        calculatePlayer3Score();

        // Looking for BlackJack
        blackJack();
    }

    private void showTextViews() {

        tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        tvPlayer2Money.setText("Player2 Balance: $" + _Player2money);
        tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);

//        tvPlayer1Bet.setText("$" + _Player1bet);
//        tvPlayer2Bet.setText("$" + _Player2bet);
//        tvPlayer3Bet.setText("$" + _Player3bet);

        tvDealerScore.setText("" + _dealerScore);
        tvPlayer1Score.setText("" + _player1Score);
        tvPlayer2Score.setText("" + _player2Score);
        tvPlayer3Score.setText("" + _player3Score);
    }

    private void hidePlayButtons() {

        btnHit.setVisibility(View.INVISIBLE);
        btnStand.setVisibility(View.INVISIBLE);
        btnSurrender.setVisibility(View.INVISIBLE);

        btnOneDollar.setVisibility(View.VISIBLE);
        btnFiveDollar.setVisibility(View.VISIBLE);
        btnTenDollar.setVisibility(View.VISIBLE);
        btnTwentyFiveDollar.setVisibility(View.VISIBLE);

        tvDealerScore.setVisibility(View.INVISIBLE);
        tvPlayer1Score.setVisibility(View.INVISIBLE);
        tvPlayer2Score.setVisibility(View.INVISIBLE);
        tvPlayer3Score.setVisibility(View.INVISIBLE);
        tvPlayer1Bet.setVisibility(View.INVISIBLE);
        tvPlayer2Bet.setVisibility(View.INVISIBLE);
        tvPlayer3Bet.setVisibility(View.INVISIBLE);
        tvDealerBet.setVisibility(View.INVISIBLE);

//        ivDealerCard1.setVisibility(View.INVISIBLE);
//        ivDealerCard2.setVisibility(View.INVISIBLE);
//        ivDealerCard3.setVisibility(View.INVISIBLE);
//        ivDealerCard4.setVisibility(View.INVISIBLE);
//        ivDealerCard5.setVisibility(View.INVISIBLE);

        ivPlayer1Card1.setVisibility(View.INVISIBLE);
        ivPlayer1Card2.setVisibility(View.INVISIBLE);
        ivPlayer1Card3.setVisibility(View.INVISIBLE);
        ivPlayer1Card4.setVisibility(View.INVISIBLE);
        ivPlayer1Card5.setVisibility(View.INVISIBLE);

        ivPlayer2Card1.setVisibility(View.INVISIBLE);
        ivPlayer2Card2.setVisibility(View.INVISIBLE);
        ivPlayer2Card3.setVisibility(View.INVISIBLE);
        ivPlayer2Card4.setVisibility(View.INVISIBLE);
        ivPlayer2Card5.setVisibility(View.INVISIBLE);

        ivPlayer3Card1.setVisibility(View.INVISIBLE);
        ivPlayer3Card2.setVisibility(View.INVISIBLE);
        ivPlayer3Card3.setVisibility(View.INVISIBLE);
        ivPlayer3Card4.setVisibility(View.INVISIBLE);
        ivPlayer3Card5.setVisibility(View.INVISIBLE);
    }

    private void showPlayButtons() {

        btnHit.setVisibility(View.VISIBLE);
        btnStand.setVisibility(View.VISIBLE);
        btnSurrender.setVisibility(View.VISIBLE);

        btnOneDollar.setVisibility(View.INVISIBLE);
        btnFiveDollar.setVisibility(View.INVISIBLE);
        btnTenDollar.setVisibility(View.INVISIBLE);
        btnTwentyFiveDollar.setVisibility(View.INVISIBLE);

        tvDealerScore.setVisibility(View.VISIBLE);
        tvPlayer1Score.setVisibility(View.VISIBLE);
        tvPlayer2Score.setVisibility(View.VISIBLE);
        tvPlayer3Score.setVisibility(View.VISIBLE);
        tvPlayer1Bet.setVisibility(View.VISIBLE);
        tvPlayer2Bet.setVisibility(View.VISIBLE);
        tvPlayer3Bet.setVisibility(View.VISIBLE);
        tvDealerBet.setVisibility(View.VISIBLE);
    }

    public char cardsCalling(int cardNumberFromRandom, ImageSwitcher imageView) throws InterruptedException {

        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
            if(gameSound){
                stop();
            }

            mp = MediaPlayer
                    .create(Multiplayer.this, R.raw.card_swing);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            // Thread.sleep(300);

//            }
        }
        // Club,Diamond,Hearts,Spades Sequence--(c,d,h,s)
        // Number sequence A,2,3,4,5,6,7,8,9,10,J,Q,K
        // Card Names : A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, 2

        switch (cardNumberFromRandom) {
            case 0:
                imageView.setImageResource(R.drawable.c1);
                return 'A';
            case 1:
                imageView.setImageResource(R.drawable.c2);
                return '2';
            case 2:
                imageView.setImageResource(R.drawable.c3);
                return '3';
            case 3:
                imageView.setImageResource(R.drawable.c4);
                return '4';
            case 4:
                imageView.setImageResource(R.drawable.c5);
                return '5';
            case 5:
                imageView.setImageResource(R.drawable.c6);
                return '6';
            case 6:
                imageView.setImageResource(R.drawable.c7);
                return '7';
            case 7:
                imageView.setImageResource(R.drawable.c8);
                return '8';
            case 8:
                imageView.setImageResource(R.drawable.c9);
                return '9';
            case 9:
                imageView.setImageResource(R.drawable.c10);
                return 'T';
            case 10:
                imageView.setImageResource(R.drawable.cj);
                return 'J';
            case 11:
                imageView.setImageResource(R.drawable.cq);
                return 'Q';
            case 12:
                imageView.setImageResource(R.drawable.ck);
                return 'K';

            // Diamonds

            case 13:
                imageView.setImageResource(R.drawable.d1);
                return 'A';
            case 14:
                imageView.setImageResource(R.drawable.d2);
                return '2';
            case 15:
                imageView.setImageResource(R.drawable.d3);
                return '3';
            case 16:
                imageView.setImageResource(R.drawable.d4);
                return '4';
            case 17:
                imageView.setImageResource(R.drawable.d5);
                return '5';
            case 18:
                imageView.setImageResource(R.drawable.d6);
                return '6';
            case 19:
                imageView.setImageResource(R.drawable.d7);
                return '7';
            case 20:
                imageView.setImageResource(R.drawable.d8);
                return '8';
            case 21:
                imageView.setImageResource(R.drawable.d9);
                return '9';
            case 22:
                imageView.setImageResource(R.drawable.d10);
                return 'T';
            case 23:
                imageView.setImageResource(R.drawable.dj);
                return 'J';
            case 24:
                imageView.setImageResource(R.drawable.dq);
                return 'Q';
            case 25:
                imageView.setImageResource(R.drawable.dk);
                return 'K';

            // Hearts

            case 26:
                imageView.setImageResource(R.drawable.h1);
                return 'A';
            case 27:
                imageView.setImageResource(R.drawable.h2);
                return '2';
            case 28:
                imageView.setImageResource(R.drawable.h3);
                return '3';
            case 29:
                imageView.setImageResource(R.drawable.h4);
                return '4';
            case 30:
                imageView.setImageResource(R.drawable.h5);
                return '5';
            case 31:
                imageView.setImageResource(R.drawable.h6);
                return '6';
            case 32:
                imageView.setImageResource(R.drawable.h7);
                return '7';
            case 33:
                imageView.setImageResource(R.drawable.h8);
                return '8';
            case 34:
                imageView.setImageResource(R.drawable.h9);
                return '9';
            case 35:
                imageView.setImageResource(R.drawable.h10);
                return 'T';
            case 36:
                imageView.setImageResource(R.drawable.hj);
                return 'J';
            case 37:
                imageView.setImageResource(R.drawable.hq);
                return 'Q';
            case 38:
                imageView.setImageResource(R.drawable.hk);
                return 'K';

            // Spades

            case 39:
                imageView.setImageResource(R.drawable.s1);
                return 'A';
            case 40:
                imageView.setImageResource(R.drawable.s2);
                return '2';
            case 41:
                imageView.setImageResource(R.drawable.s3);
                return '3';
            case 42:
                imageView.setImageResource(R.drawable.s4);
                return '4';
            case 43:
                imageView.setImageResource(R.drawable.s5);
                return '5';
            case 44:
                imageView.setImageResource(R.drawable.s6);
                return '6';
            case 45:
                imageView.setImageResource(R.drawable.s7);
                return '7';
            case 46:
                imageView.setImageResource(R.drawable.s8);
                return '8';
            case 47:
                imageView.setImageResource(R.drawable.s9);
                return '9';
            case 48:
                imageView.setImageResource(R.drawable.s10);
                return 'T';
            case 49:
                imageView.setImageResource(R.drawable.sj);
                return 'J';
            case 50:
                imageView.setImageResource(R.drawable.sq);
                return 'Q';
            case 51:
                imageView.setImageResource(R.drawable.sk);
                return 'K';

            default:

                return 0;
        }

    }
    private int dealerNumber;
    public void dealerCall() throws InterruptedException {

        // To make sure no card comes twice
        do {
            Random _random = new Random();
            _randomNumber = _random.nextInt(52);
        } while (_alCardsTracking.contains(_randomNumber) == true);
        _alCardsTracking.add(_randomNumber);

        switch (_dealerCardNumber) {
            case 0:
                _dealerCardArray[_dealerCardNumber] = cardsCalling(_randomNumber,
                        ivDealerCard1);
                break;
            case 1:
                _dealerCardArray[_dealerCardNumber] = cardsCalling(_randomNumber,
                        ivDealerCard2);
                break;
            case 2:
                _dealerCardArray[_dealerCardNumber] = cardsCalling(_randomNumber,
                        ivDealerCard3);
                break;
            case 3:
                _dealerCardArray[_dealerCardNumber] = cardsCalling(_randomNumber,
                        ivDealerCard4);
                break;
            case 4:
                _dealerCardArray[_dealerCardNumber] = cardsCalling(_randomNumber,
                        ivDealerCard5);
                break;
        }
        // Very important
        _dealerScoreCount[_dealerCardNumber] = getIntValueFromCard(_dealerCardArray[_dealerCardNumber]);
        _dealerCardNumber++;

    }

    public void player1Call() throws InterruptedException {

        // To make sure no card comes twice
        do {
            Random _random = new Random();
            _randomNumber = _random.nextInt(52);
        } while (_alCardsTracking.contains(_randomNumber) == true);
        _alCardsTracking.add(_randomNumber);

        switch (_player1CardNumber) {
            case 0:
                _player1CardArray[_player1CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer1Card1);
                break;
            case 1:
                _player1CardArray[_player1CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer1Card2);
                break;
            case 2:
                _player1CardArray[_player1CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer1Card3);
                break;
            case 3:
                _player1CardArray[_player1CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer1Card4);
                break;
            case 4:
                _player1CardArray[_player1CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer1Card5);
                break;
        }
        // Very Important
        _player1ScoreCount[_player1CardNumber] = getIntValueFromCard(_player1CardArray[_player1CardNumber]);
        _player1CardNumber++;

    }

    public void player2Call() throws InterruptedException {

        // To make sure no card comes twice
        do {
            Random _random = new Random();
            _randomNumber = _random.nextInt(52);
        } while (_alCardsTracking.contains(_randomNumber) == true);
        _alCardsTracking.add(_randomNumber);

        switch (_player2CardNumber) {
            case 0:
                _player2CardArray[_player2CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer2Card1);
                break;
            case 1:
                _player2CardArray[_player2CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer2Card2);
                break;
            case 2:
                _player2CardArray[_player2CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer2Card3);
                break;
            case 3:
                _player2CardArray[_player2CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer2Card4);
                break;
            case 4:
                _player2CardArray[_player2CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer2Card5);
                break;
        }
        // Very Important
        _player2ScoreCount[_player2CardNumber] = getIntValueFromCard(_player2CardArray[_player2CardNumber]);
        _player2CardNumber++;

    }

    public void player3Call() throws InterruptedException {

        // To make sure no card comes twice
        do {
            Random _random = new Random();
            _randomNumber = _random.nextInt(52);
        } while (_alCardsTracking.contains(_randomNumber) == true);
        _alCardsTracking.add(_randomNumber);

        switch (_player3CardNumber) {
            case 0:
                _player3CardArray[_player3CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer3Card1);
                break;
            case 1:
                _player3CardArray[_player3CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer3Card2);
                break;
            case 2:
                _player3CardArray[_player3CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer3Card3);
                break;
            case 3:
                _player3CardArray[_player3CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer3Card4);
                break;
            case 4:
                _player3CardArray[_player3CardNumber] = cardsCalling(_randomNumber,
                        ivPlayer3Card5);
                break;
        }
        // Very Important
        _player3ScoreCount[_player3CardNumber] = getIntValueFromCard(_player3CardArray[_player3CardNumber]);
        _player3CardNumber++;

    }

    public void calculateDealerScore() {

        int j = 0;
        for (int i = 0; i < 5; i++) {
            j += _dealerScoreCount[i];
        }
        _dealerScore = j;
        tvDealerScore.setText("Dealer's Score : " + _dealerScore);
    }

    public void calculatePlayer1Score() {

        int j = 0;
        for (int i = 0; i < 5; i++) {
            j += _player1ScoreCount[i];
        }
        _player1Score = j;
        tvPlayer1Score.setText("" + _player1Score);
    }

    public void calculatePlayer2Score() {

        int j = 0;
        for (int i = 0; i < 5; i++) {
            j += _player2ScoreCount[i];
        }
        _player2Score = j;
        tvPlayer2Score.setText("" + _player2Score);
    }

    public void calculatePlayer3Score() {

        int j = 0;
        for (int i = 0; i < 5; i++) {
            j += _player3ScoreCount[i];
        }
        _player3Score = j;
        tvPlayer3Score.setText("" + _player3Score);
    }

    public void youLose() throws InterruptedException {

//        Toast.makeText(Multiplayer.this, "You Lose !!!",
//                Toast.LENGTH_SHORT).show();
//        alertBoxWithBet("You Lose !!! Bet Amount - $" + _bet + "\nDealer's Score - " + _dealerScore
//                + "\nPlayer1 Score - " + _player1Score + "\nPlayer2 Score - " + _player2Score
//                + "\nPlayer3 Score - " + _player3Score);
        double playerWin=0;
        playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
        BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
        double newNum = bd.doubleValue();
        _Dealermoney += newNum;

            alertBoxWithBet("Dealer won !!! Bet Amount - $"
                    + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//        Toast.makeText(Multiplayer.this, "Dealer won !!! Bet Amount - $"
//                        + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                        + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                Toast.LENGTH_SHORT).show();

        _Player1bet = 0;
        _Player2bet = 0;
        _Player3bet = 0;
        _DealerBet = 0;
        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
            if(gameSound){
                stop();
            }
            mp = MediaPlayer.create(Multiplayer.this, R.raw.you_lose);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            // Thread.sleep(700);

//            }
        }
//        resetEveryThing();
    }

    public void youLoseBurst() throws InterruptedException {

//        Toast.makeText(Multiplayer.this, "You Lose !!!",
//                Toast.LENGTH_SHORT).show();
//        alertBoxWithBet("Your score Burst, \nYou Lose !!! Bet Amount - $" + _bet + "\nDealer's Score - " + _dealerScore
//                + "\nPlayer1 Score - " + _player1Score + "\nPlayer2 Score - " + _player2Score
//                + "\nPlayer3 Score - " + _player3Score);

        double playerWin=0;
        if(_player1Score > 21)
        {
            playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Dealermoney += newNum;

            alertBoxWithBet("Player1 Score Burst, \nPlayer1 Lose !!! Bet Amount - $" + _Player1bet +
                    "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Player1 score Burst, \nYou Lose !!! Bet Amount - $"
//                            + _Player1bet + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                Toast.LENGTH_SHORT).show();
        }
        else if(_player2Score > 21)
        {
            playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Dealermoney += newNum;

            alertBoxWithBet("Player2 Score Burst, \nPlayer2 Lose !!! Bet Amount - $" + _Player2bet +
                    "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Player2 score Burst, \nYou Lose !!! Bet Amount - $"
//                            + _Player2bet + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();
        }
        else if(_player3Score > 21)
        {
            playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Dealermoney += newNum;
            alertBoxWithBet("Player3 Score Burst, \nPlayer3 Lose !!! Bet Amount - $" + _Player3bet +
                    "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Player3 score Burst, \nYou Lose !!! Bet Amount - $"
//                            + _Player3bet + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();
        }

        _Player1bet = 0;
        _Player2bet = 0;
        _Player3bet = 0;
        // Making some sound here
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
            if(gameSound){
                stop();
            }

            mp = MediaPlayer.create(Multiplayer.this, R.raw.you_lose);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
            // Thread.sleep(700);

//            }
        }
//        resetEveryThing();
    }

    public void youWon() throws InterruptedException {

//        alertBoxWithBet( "You Won !!! Bet Amount - $" + _bet + "\nDealer's Score - " + _dealerScore
//                + "\nPlayer1 Score - " + _player1Score + "\nPlayer2 Score - " + _player2Score
//                + "\nPlayer3 Score - " + _player3Score);

        double playerWin = 0, decWin = 0;
//        playerWinSound();
        gunshotSound();
        if(_player1Score > _player2Score && _player1Score > _player3Score) {
            playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player1money += newNum;

            alertBoxWithBet("Player1 Won !!! Bet Amount - $" + newNum + "\nDealer's Score - "
                    + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Player1 Won !!! Bet Amount - $" + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();

        }
        else if(_player2Score > _player1Score && _player2Score > _player3Score)
        {
            playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player2money += newNum;

            alertBoxWithBet("Player2 Won !!! Bet Amount - $" + newNum + "\nDealer's Score - "
                    + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Player2 Won !!! Bet Amount - $" + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();

        }
        else if(_player3Score > _player2Score && _player3Score > _player1Score)
        {
            playerWin = _Player1bet + _Player2bet + _Player3bet + _DealerBet;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player3money += newNum;

            alertBoxWithBet("Player3 Won !!! Bet Amount - $" + newNum + "\nDealer's Score - "
                    + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);


//            Toast.makeText(Multiplayer.this, "Player3 Won !!! Bet Amount - $" + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();

        }
        _Player1bet = 0;
        _Player2bet = 0;
        _Player3bet = 0;
        _DealerBet = 0;
//        resetEveryThing();
    }

    public void playerWinSound()
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
        if(gameSound){
            stop();
        }

        mp = MediaPlayer
                .create(Multiplayer.this, R.raw.player_wins);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
    }

    public void gunshotSound()
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
        if(gameSound){
            stop();
        }

        mp = MediaPlayer
                .create(Multiplayer.this, R.raw.gunshot);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
    }

//    public void toastPopup(String popup)
//    {
//        LayoutInflater li = getLayoutInflater();
//        View layout = li.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.custom_toast_layout_id));
//
//        Toast toast = new Toast(Multiplayer.this);
//        toast.setText(popup);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(layout);//setting the view of custom toast layout
//        toast.show();
//    }

    public void youWonDealerBurst() throws InterruptedException {
        double playerWin = 0, decWin = 0;
        gunshotSound();
//        playerWinSound();
        if(_player1Score > _player2Score && _player1Score > _player3Score)
        {
//            _Player1money += _Player1bet * 2;

            playerWin = (double)_DealerBet/3;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player1money += newNum;
            alertBoxWithBet("Dealer Score Burst, \nPlayer1 Won !!! Bet Amount - $" + newNum +
                    "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);
//            Toast.makeText(Multiplayer.this, "Dealer Score Burst, \nPlayer1 Won !!! Bet Amount - $"
//                            + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();
        }
        else if(_player2Score > _player1Score && _player2Score > _player3Score)
        {
//            _Player2money += _Player2bet * 2;
//            alertBoxWithBet("Dealer Score Burst, \nPlayer2 Won !!! Bet Amount - $" + _Player2bet);

            playerWin = (double)_DealerBet/3;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player1money += newNum;
            alertBoxWithBet("Dealer Score Burst, \nPlayer2 Won !!! Bet Amount - $" + newNum +
                    "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Dealer Score Burst, \nPlayer2 Won !!! Bet Amount - $"
//                            + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();
        }
        else if(_player3Score > _player2Score && _player3Score > _player1Score)
        {
//            _Player3money += _Player3bet * 2;
//            alertBoxWithBet("Dealer Score Burst, \nPlayer3 Won !!! Bet Amount - $" + _Player2bet);

            playerWin = (double)_DealerBet/3;
            BigDecimal bd = new BigDecimal(playerWin).setScale(2, RoundingMode.HALF_UP);
            double newNum = bd.doubleValue();
            _Player1money += newNum;

            alertBoxWithBet("Dealer Score Burst, \nPlayer3 Won !!! Bet Amount - $" + newNum +
                    "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
                    + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score);

//            Toast.makeText(Multiplayer.this, "Dealer Score Burst, \nPlayer3 Won !!! Bet Amount - $"
//                            + newNum + "\nDealer's Score - " + _dealerScore + "\nPlayer1 Score - " + _player1Score
//                            + "\nPlayer2 Score - " + _player2Score + "\nPlayer3 Score - " + _player3Score,
//                    Toast.LENGTH_SHORT).show();
        }
        _Player1bet = 0;
        _Player2bet = 0;
        _Player3bet = 0;
        _DealerBet = 0;
//        Toast.makeText(Multiplayer.this, "You Won !!!",
//                Toast.LENGTH_SHORT).show();
//        resetEveryThing();
    }

    public void stop() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    public void blackJack() throws InterruptedException {

        if (_player1Score <= 21) {
            if ((_player1CardArray[0] == 'A' && _player1CardArray[1] == 'J')
                    || ((_player1CardArray[0] == 'J' && _player1CardArray[1] == 'A'))) {
                _Player1money += _Player1bet * 3;
                alertBoxToast("Congrats! Player1 Hit BLACKJACK");
                youWon();
                _Player1bet = 0;
                _Player2bet = 0;
                _Player3bet = 0;
            }
        }
      else if (_player2Score <= 21) {
                if ((_player2CardArray[0] == 'A' && _player2CardArray[1] == 'J')
                        || ((_player2CardArray[0] == 'J' && _player2CardArray[1] == 'A'))) {
                    _Player2money += _Player2bet * 3;
                    alertBoxToast("Congrats! Player2 Hit BLACKJACK");
                    youWon();
                    _Player1bet = 0;
                    _Player2bet = 0;
                    _Player3bet = 0;
                }
            }
        else if (_player3Score <= 21) {
            if ((_player3CardArray[0] == 'A' && _player3CardArray[1] == 'J')
                    || ((_player3CardArray[0] == 'J' && _player3CardArray[1] == 'A'))) {
                _Player3money += _Player3bet * 3;
                alertBoxToast("Congrats! Player3 Hit BLACKJACK");
                youWon();
                _Player1bet = 0;
                _Player2bet = 0;
                _Player3bet = 0;
            }
        }
    }

    public int getIntValueFromCard(char card) {

        switch (card) {
            case 'A':
                return 1;
            case 'K':
                return 10;
            case 'Q':
                return 10;
            case 'J':
                return 10;
            case 'T':
                return 10;
            case '9':
                return 9;
            case '8':
                return 8;
            case '7':
                return 7;
            case '6':
                return 6;
            case '5':
                return 5;
            case '4':
                return 4;
            case '3':
                return 3;
            case '2':
                return 2;
            default:
                return 0;

        }
    }

    private void btnOneDollarClick() throws InterruptedException {
        _Player1bet = _Player2bet = _Player3bet = _DealerBet = 1;

//        toastPopup("$");

        _Player1money -= _Player1bet;
        _Player2money -= _Player2bet;
        _Player3money -= _Player3bet;

        tvPlayer1Bet.setText("$" + _Player1bet);
        tvPlayer2Bet.setText("$" + _Player2bet);
        tvPlayer3Bet.setText("$" + _Player3bet);
        tvDealerBet.setText("$" + _DealerBet);

        tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        tvPlayer2Money.setText("PLayer2 Balance: $" + _Player2money);
        tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);
        showPlayButtons();
        gameStart();
    }

    private void btnFiveDollarClick() throws InterruptedException {
        _Player1bet = _Player2bet = _Player3bet = _DealerBet = 5;
        _Player1money -= _Player1bet;
        _Player2money -= _Player2bet;
        _Player3money -= _Player3bet;

        tvPlayer1Bet.setText("$" + _Player1bet);
        tvPlayer2Bet.setText("$" + _Player2bet);
        tvPlayer3Bet.setText("$" + _Player3bet);
        tvDealerBet.setText("$" + _DealerBet);

        tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        tvPlayer2Money.setText("PLayer2 Balance: $" + _Player2money);
        tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);
        showPlayButtons();
        gameStart();
    }

    private void btnTenDollarClick() throws InterruptedException {
        _Player1bet = _Player2bet = _Player3bet = _DealerBet = 10;
        _Player1money -= _Player1bet;
        _Player2money -= _Player2bet;
        _Player3money -= _Player3bet;

        tvPlayer1Bet.setText("$" + _Player1bet);
        tvPlayer2Bet.setText("$" + _Player2bet);
        tvPlayer3Bet.setText("$" + _Player3bet);
        tvDealerBet.setText("$" + _DealerBet);

        tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        tvPlayer2Money.setText("PLayer2 Balance: $" + _Player2money);
        tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);
        showPlayButtons();
        gameStart();
    }

    private void btnTwentyFiveDollarClick() throws InterruptedException {
        _Player1bet = _Player2bet = _Player3bet = _DealerBet = 25;
        _Player1money -= _Player1bet;
        _Player2money -= _Player2bet;
        _Player3money -= _Player3bet;

        tvPlayer1Bet.setText("$" + _Player1bet);
        tvPlayer2Bet.setText("$" + _Player2bet);
        tvPlayer3Bet.setText("$" + _Player3bet);
        tvDealerBet.setText("$" + _DealerBet);

        tvPlayer1Money.setText("Player1 Balance: $" + _Player1money);
        tvPlayer2Money.setText("PLayer2 Balance: $" + _Player2money);
        tvPlayer3Money.setText("Player3 Balance: $" + _Player3money);
        showPlayButtons();
        gameStart();
    }

    private void verifyPlayerBurst() throws InterruptedException
    {
        if(_player1Score > 21) {
            if (player1Burst == false) {
                ivPlayer1Card1.setVisibility(View.INVISIBLE);
                ivPlayer1Card2.setVisibility(View.INVISIBLE);
                ivPlayer1Card3.setVisibility(View.INVISIBLE);
                ivPlayer1Card4.setVisibility(View.INVISIBLE);
                ivPlayer1Card5.setVisibility(View.INVISIBLE);
//                youLoseBurst();
                player1Burst = true;
            }
        }
        if(_player2Score > 21) {
            if (player2Burst == false) {
                ivPlayer2Card1.setVisibility(View.INVISIBLE);
                ivPlayer2Card2.setVisibility(View.INVISIBLE);
                ivPlayer2Card3.setVisibility(View.INVISIBLE);
                ivPlayer2Card4.setVisibility(View.INVISIBLE);
                ivPlayer2Card5.setVisibility(View.INVISIBLE);
//                youLoseBurst();
                player2Burst = true;
            }
        }
        if(_player3Score > 21)
        {
            if (player2Burst == false) {
                ivPlayer3Card1.setVisibility(View.INVISIBLE);
                ivPlayer3Card2.setVisibility(View.INVISIBLE);
                ivPlayer3Card3.setVisibility(View.INVISIBLE);
                ivPlayer3Card4.setVisibility(View.INVISIBLE);
                ivPlayer3Card5.setVisibility(View.INVISIBLE);
//                youLoseBurst();
                player3Burst = true;
            }
        }
        if(_player1Score > 21 && _player2Score > 21)
        {
            checkWin();
        }
        else if(_player1Score > 21 && _player3Score > 21)
        {
            checkWin();
        }

    }

    private void verifyPlayerCall() throws InterruptedException {
        if(_player1CardNumber == 2 && _player2CardNumber == 2 && _player3CardNumber == 2)
        {
            ivPlayer1Card3.setVisibility(View.VISIBLE);
            player1Call();
            calculatePlayer1Score();
        }
        else if(_player1CardNumber == 3 && _player2CardNumber == 2 && _player3CardNumber == 2)
        {
            ivPlayer2Card3.setVisibility(View.VISIBLE);
            player2Call();
            calculatePlayer2Score();
        }
        else if(_player1CardNumber == 3 && _player2CardNumber == 3 && _player3CardNumber == 2)
        {
            ivPlayer3Card3.setVisibility(View.VISIBLE);
            player3Call();
            calculatePlayer3Score();
        }
        else if(_player1CardNumber == 3 && _player2CardNumber == 3 && _player3CardNumber == 3)
        {
            ivPlayer1Card4.setVisibility(View.VISIBLE);
            player1Call();
            calculatePlayer1Score();
        }
        else if(_player1CardNumber == 4 && _player2CardNumber == 3 && _player3CardNumber == 3)
        {
            ivPlayer2Card4.setVisibility(View.VISIBLE);
            player2Call();
            calculatePlayer2Score();
        }
        else if(_player1CardNumber == 4 && _player2CardNumber == 4 && _player3CardNumber == 3)
        {
            ivPlayer3Card4.setVisibility(View.VISIBLE);
            player3Call();
            calculatePlayer3Score();
        }
        else if(_player1CardNumber == 4 && _player2CardNumber == 4 && _player3CardNumber == 4)
        {
            ivPlayer1Card5.setVisibility(View.VISIBLE);
            player1Call();
            calculatePlayer1Score();
        }
        else if(_player1CardNumber == 5 && _player2CardNumber == 4 && _player3CardNumber == 4)
        {
            ivPlayer2Card5.setVisibility(View.VISIBLE);
            player2Call();
            calculatePlayer2Score();
        }
        else if(_player1CardNumber == 5 && _player2CardNumber == 5 && _player3CardNumber == 4)
        {
            ivPlayer3Card5.setVisibility(View.VISIBLE);
            player3Call();
            calculatePlayer3Score();
        }
    }

    private void verifyDealerCall() throws InterruptedException {
        if (_dealerCardNumber == 1) {
            ivDealerCard2.setVisibility(View.VISIBLE);
            dealerCall();
            calculateDealerScore();
            calculatePlayer1Score();
            calculatePlayer2Score();
            calculatePlayer3Score();
        } else if (_dealerCardNumber == 2) {
            ivDealerCard3.setVisibility(View.VISIBLE);
            dealerCall();
            calculateDealerScore();
            calculatePlayer1Score();
            calculatePlayer2Score();
            calculatePlayer3Score();
        } else if (_dealerCardNumber == 3) {
            ivDealerCard4.setVisibility(View.VISIBLE);
            dealerCall();
            calculateDealerScore();
            calculatePlayer1Score();
            calculatePlayer2Score();
            calculatePlayer3Score();
        } else if (_dealerCardNumber == 4) {
            ivDealerCard5.setVisibility(View.VISIBLE);
            dealerCall();
            calculateDealerScore();
            calculatePlayer1Score();
            calculatePlayer2Score();
            calculatePlayer3Score();
        } else if (_dealerCardNumber == 5) {
            ivDealerCard5.setVisibility(View.VISIBLE);
            dealerCall();
            calculateDealerScore();
            calculatePlayer1Score();
            calculatePlayer2Score();
            calculatePlayer3Score();
        }

    }
        private void btnHitClick() throws InterruptedException {

            if (_player1Score < 17 || _player2Score < 17 || _player3Score < 17) {
                verifyPlayerCall();
            }
            if (_player1Score > 21 || _player2Score > 21 || _player3Score > 21) {
                verifyPlayerBurst();
            }
            if(_dealerScore > 21)
            {
                checkWin();
            }
            if (_player1Score >= 17 || _player2Score >= 17 || _player3Score >= 17) {
                do {
                    dealerCall();
                    calculateDealerScore();
                    calculatePlayer1Score();
                } while (_dealerScore < 17);

                checkWin();
            }
        }

    public void btnStandClick() throws InterruptedException {
        if (_player1Score >= 17 || _player2Score >= 17 || _player3Score >= 17) {
            do {
                dealerCall();
                calculateDealerScore();
                calculatePlayer1Score();
            } while (_dealerScore < 17);

            if (_dealerCardNumber > 4) {
                verifyPlayerCall();
                calculateDealerScore();
                calculatePlayer1Score();
            }
            if (_dealerScore >= 17) {
                checkWin();
            }
        }
    }

    public void checkWin() throws InterruptedException {
        if (_player1Score > 21 || _player2Score > 21 || _player3Score > 21) {
            verifyPlayerBurst();
        }
        else if (_dealerScore > 21) {
            youWonDealerBurst();
        }
        else if (_player1Score > _dealerScore || _player2Score > _dealerScore || _player3Score > _dealerScore)
        {
            youWon();
        }
        else if (_player1Score < _dealerScore || _player2Score < _dealerScore || _player3Score < _dealerScore)
        {
            youLose();
        }
    }

    public void alertBoxToast(String toast) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setCancelable(false);
        Thread t = new Thread() {
            public void run() {

//                try {
//                    sleep(1500);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

                runOnUiThread(new Runnable() {
                    public void run() {

                        alert.setMessage(toast);
                        alert.setCancelable(false);
                        alert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        {
                                            SharedPreferences prefs = PreferenceManager
                                                    .getDefaultSharedPreferences(getApplicationContext());
                                            boolean gameSound = prefs
                                                    .getBoolean(
                                                            "pref_cb_sound",
                                                            true);

//                                            if (gameSound) {
//                                                 mp.release();
//                                            }
                                        }
                                    }
                                });
                        alert.show();
                    }
                });
            }
        };
        t.start();
        // Thread ends here
    }

    public void alertBoxWithBet(String toast) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        Thread t = new Thread() {
            public void run() {

//                try {
//                    sleep(1500);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        // set the custom layout

                        alert.setMessage(toast);
                        alert.setCancelable(false);
                        alert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        resetEveryThing();
//                                        hidePlayButtons();
                                        {
                                            SharedPreferences prefs = PreferenceManager
                                                    .getDefaultSharedPreferences(getApplicationContext());
                                            boolean gameSound = prefs
                                                    .getBoolean(
                                                            "pref_cb_sound",
                                                            true);

//                                            if (gameSound) {
//                                                 mp.release();
//                                            }
                                        }
                                    }
                                });
                        alert.show();
                    }
                });
            }
        };
        t.start();
        // Thread ends here
    }

    public void playAllOverAgainAlertBox() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);

        Thread t = new Thread() {
            public void run() {

                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    public void run() {

                        alert.setMessage("Play Again");

                        alert.setPositiveButton("Get more $500 to Play!",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        _Player1money = 500;
                                        _Player2money = 500;
                                        _Player3money = 500;
                                        resetEveryThing();
//                                        hidePlayButtons();
                                    }
                                });

                        alert.setNegativeButton("I'm Sick of this game!",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        savingHighScorePlayer1();
                                        savingHighScorePlayer2();
                                        savingHighScorePlayer3();
                                        finish();
                                    }
                                });

                        alert.show();
                    }
                });
            }
        };
        t.start();
        // Thread ends here
    }

    public View makeView() {
        ImageView iView = new ImageView(this);
        iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iView.setLayoutParams(new ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return iView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);

        MenuInflater menuSetBg = getMenuInflater();
        menuSetBg.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.mISelectBackground:

                Intent intent = new Intent(Multiplayer.this,
                        SetBackground.class);
                startActivity(intent);

                break;

            case R.id.mISettings:

                Intent prefIntent = new Intent(Multiplayer.this,
                        PreferencesActivity.class);
                startActivity(prefIntent);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;

    }


}