package com.liao.momo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {


    @Bind(R.id.welcome_img)
    ImageView welcome_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.9f, 0.9f);
        alphaAnimation.setDuration(2000);//设置时间
        welcome_img.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new AnimationImpl());
    }

    private class AnimationImpl implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            welcome_img.setBackgroundResource(R.mipmap.dream);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            skip();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    private void skip() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
