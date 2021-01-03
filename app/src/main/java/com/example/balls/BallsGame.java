package com.example.balls;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class BallsGame extends SurfaceView implements Runnable{

    // Are we debugging?
    private final boolean DEBUGGING = true;

    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private long mFPS;
    private final int MILLIS_IN_SECOND = 1000;

    private int mScreenX;
    private int mScreenY;
    private int mFontSize;
    private int mFontMargin;

    private Bat mBat1;
    private BatOpposite mBatO;
    private Ball mBall;

    private int mScore = 0;
    private int mLivesOur = 3;
    private int mLivesOpo = 3;
    private int mLevel = 1;

    private Thread mGameThread = null;
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    private SoundPool mSP;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;

    public BallsGame(Context context, int x, int y) {
        super(context);
        mScreenX = x;
        mScreenY = y;

        mFontSize = mScreenX / 30;
        mFontMargin = mScreenX / 100;

        mOurHolder = getHolder();
        mPaint = new Paint();

        mBall = new Ball(mScreenX);
        mBat1 = new Bat(mScreenX, mScreenY);
        mBatO = new BatOpposite(mScreenX, mScreenY);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }


        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("boop.ogg");
            mBoopID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("bop.ogg");
            mBopID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("miss.ogg");
            mMissID = mSP.load(descriptor, 0);


        }catch(IOException e){
            Log.e("error", "failed to load sound files");
        }

        startNewGame();
    }


    private void startNewGame(){

        mBall.reset(mScreenX, mScreenY);
        mBatO.restart(mScreenX, mScreenY);
        mScore = 0;
        mLivesOur = 3;
        mLivesOpo = 3;
        mLevel = 1;

    }

    @Override
    public void run() {
        while (mPlaying) {

            long frameStartTime = System.currentTimeMillis();

            if(!mPaused){
                moveBallsOpposite();
                update();
                detectCollisions();

            }

            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;

            if (timeThisFrame > 0) {
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }

        }

    }

    private void update() {
        mBall.update(mFPS);
        mBat1.update(mFPS);
        mBatO.update(mFPS);
    }
    private void moveBallsOpposite(){
        float batCenter = mBatO.getRect().left +
                (mBatO.getRect().width() / 2);

        float ballCenter = mBall.getRect().left +
                (mBall.getRect().width() / 2);

        float batLeft =  mBatO.getRect().left;
        float batRight = mBatO.getRect().right;

        float ballLeft = mBall.getRect().left;
        float ballRight = mBall.getRect().right;

        if(batRight < ballLeft)
            if(batCenter < ballCenter)
                mBatO.setMovementState(mBatO.RIGHT);
        if(batLeft > ballRight)
            if(batCenter > ballCenter)
                mBatO.setMovementState(mBatO.LEFT);
    }

    private void detectCollisions(){
        if(RectF.intersects(mBat1.getRect(), mBall.getRect())) {
            mBall.batBounce(mBat1.getRect());
            mBall.increaseVelocity();
            mScore++;
            mSP.play(mBeepID, 1, 1, 0, 0, 1);
            switch (mScore){
                case 2 : mLevel++;
                    mBatO.increaseBatVelosity();
                    break;
                case 4 : mLevel++;
                    mBatO.increaseBatVelosity();
                    break;
                case 6 : mLevel++;
                    mBatO.increaseBatVelosity();
                    break;
            }


        }

        if(RectF.intersects(mBatO.getRect(), mBall.getRect())) {
            mBall.batBounce(mBatO.getRect());
            mBall.increaseVelocity();
            mSP.play(mBeepID, 1, 1, 0, 0, 1);
        }



        if(mBall.getRect().bottom > mScreenY){
            mBall.reverseYVelocity();

            mLivesOur--;
            mSP.play(mMissID, 1, 1, 0, 0, 1);

            if(mLivesOur == 0){
                mPaused = true;
                startNewGame();
            }
        }

        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();

            mLivesOpo--;
            mSP.play(mMissID, 1, 1, 0, 0, 1);

            if(mLivesOpo == 0){
                mPaused = true;
                startNewGame();
            }
        }
        // Left
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }

        // Right
        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }

    }

    void draw() {
        if (mOurHolder.getSurface().isValid()) {
            mCanvas = mOurHolder.lockCanvas();

            mCanvas.drawColor(Color.argb
                    (255, 26, 128, 182));

            mPaint.setColor(Color.argb
                    (255, 255, 255, 255));

            mCanvas.drawRect(mBall.getRect(), mPaint);
            mCanvas.drawRect(mBat1.getRect(), mPaint);
            mCanvas.drawRect(mBatO.getRect(), mPaint);

            mPaint.setTextSize(mFontSize);

            mCanvas.drawText("Score: " + mScore + "   Your Lives: "
                            + mLivesOur + "    Level: " + mLevel
                    + "   Opposite Lives: " + mLivesOpo,
                    mFontMargin , mFontSize, mPaint);

            if(DEBUGGING){
                printDebuggingText();
            }
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() &
                MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                mPaused = false;

                if(motionEvent.getX() > mScreenX / 2){
                    mBat1.setMovementState(mBat1.RIGHT);
                }
                else{
                    mBat1.setMovementState(mBat1.LEFT);
                }

                break;


            case MotionEvent.ACTION_UP:

                mBat1.setMovementState(mBat1.STOPPED);
                break;
        }


        return true;
    }

    private void printDebuggingText(){
        int debugSize = mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS ,
                10, debugStart + debugSize , mPaint);

    }

    public void pause() {

        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }


    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);

        mGameThread.start();
    }

}