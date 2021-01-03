package com.example.balls;

import android.graphics.RectF;

public class BatOpposite {


    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private float mBatSpeed;
    private int mScreenX;

    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;


    private int mBatMoving = STOPPED;

    BatOpposite(int sx, int sy){


        mScreenX = sx;

        mLength = mScreenX / 8;
        float height = sy / 40;

        mXCoord = mScreenX / 2;
        float mYCoord = 0;

        mRect = new RectF(mXCoord, mYCoord,
                mXCoord + mLength,
                mYCoord + height);

        mBatSpeed = mScreenX/4;
    }

    void increaseBatVelosity(){
        mBatSpeed = mBatSpeed * 1.3f;
    }
    void restart(int sx, int sy) {
        mLength = mScreenX / 8;
        float height = sy / 40;

        mXCoord = mScreenX / 2;
        float mYCoord = 0;

        mRect = new RectF(mXCoord, mYCoord,
                mXCoord + mLength,
                mYCoord + height);

        mBatSpeed = mScreenX/4;
    }

    RectF getRect(){
        return mRect;
    }

    void setMovementState(int state){
        mBatMoving = state;
    }

    void update(long fps){

        if(mBatMoving == LEFT){
            mXCoord = mXCoord - mBatSpeed / fps;
        }

        if(mBatMoving == RIGHT){
            mXCoord = mXCoord + mBatSpeed / fps;
        }

        if(mXCoord < 0){
            mXCoord = 0;
        }

        if(mXCoord + mLength > mScreenX){
            mXCoord = mScreenX - mLength;
        }

        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
}
