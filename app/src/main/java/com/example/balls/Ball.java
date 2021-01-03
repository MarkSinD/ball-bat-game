package com.example.balls;
import android.graphics.RectF;

class Ball {


    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;


    Ball(int screenX){


        mBallWidth = screenX / 100;
        mBallHeight = screenX / 100;


        mRect = new RectF();
    }



    RectF getRect(){
        return mRect;
    }


    void update(long fps){

        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);


        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }

    void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }

    void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }

    void reset(int x, int y){

        mRect.left = x / 2;
        mRect.top = y/2;
        mRect.right = x / 2 + mBallWidth;
        mRect.bottom = y/2 + mBallHeight;

        mYVelocity = -(y / 3);
        mXVelocity = (y / 3);
    }

    void increaseVelocity(){
        mXVelocity = mXVelocity * 1.05f;
        mYVelocity = mYVelocity * 1.05f;
    }

    void batBounce(RectF batPosition){

        float batCenter = batPosition.left +
                (batPosition.width() / 2);

        float ballCenter = mRect.left +
                (mBallWidth / 2);

        float relativeIntersect = (batCenter - ballCenter);


        if(relativeIntersect < 0){
            mXVelocity = Math.abs(mXVelocity);
        }else{
            // Go left
            mXVelocity = -Math.abs(mXVelocity);
        }

        reverseYVelocity();
    }
}
