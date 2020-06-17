package com.hongchao;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SplashActivity extends Activity {
    private RelativeLayout rl_splash_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rl_splash_root = findViewById(R.id.rl_splash_root);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
//        alphaAnimation.setDuration(500);//设置持续时间
        alphaAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1,0,1,ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
//        scaleAnimation.setDuration(500);//设置持续时间
        scaleAnimation.setFillAfter(true);
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
//        rotateAnimation.setDuration(500);//设置持续时间
        rotateAnimation.setFillAfter(true);

        AnimationSet animationSet = new AnimationSet(false);
        //添加三个动画，无先后顺序同时播放
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setDuration(2000);

        rl_splash_root.setAnimation(animationSet);
        //监听动画
        animationSet.setAnimationListener(new MyAnimation());
    }

    class MyAnimation implements Animation.AnimationListener{
        //当动画播放开始的时候回调这个方法
        @Override
        public void onAnimationStart(Animation animation) {

        }

        //当动画播放结束的时候回调这个方法
        @Override
        public void onAnimationEnd(Animation animation) {
            Toast.makeText(SplashActivity.this, "动画播放完成了", Toast.LENGTH_SHORT).show();

        }

        //当动画播放重复的时候回调这个方法
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
