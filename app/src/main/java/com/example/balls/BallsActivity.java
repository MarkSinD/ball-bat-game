package com.example.balls;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class BallsActivity extends Activity {

    private BallsGame mBallsGame; // обьявить игровое поле и потом запустить

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // При запуске сразу считаем данные экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        mBallsGame = new BallsGame(this, size.x, size.y);
        setContentView(mBallsGame);
    }


    @Override
    protected void onResume() {

        super.onResume();
        mBallsGame.resume();
    }


    @Override
    protected void onPause() {

        super.onPause();
        mBallsGame.pause();
    }
}
