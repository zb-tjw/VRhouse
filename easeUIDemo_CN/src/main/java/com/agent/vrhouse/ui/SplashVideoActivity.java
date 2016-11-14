package com.agent.vrhouse.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences.Editor;

import com.agent.vrhouse.R;
import com.agent.vrhouse.utils.CustomVideoView;

/**
 * Created by Lenovo_PC on 2016/10/17.
 */

public class SplashVideoActivity extends Activity implements View.OnClickListener{

    private CustomVideoView videoview;
    private Button btn_start;
    private Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_video);

        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        videoview = (CustomVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                editor = SplashActivity.preferences.edit();
                // 存入数据
                editor.putBoolean("isFirstIn", false);
                // 提交修改
                editor.commit();
                videoview.pause();
                Intent intent =  new Intent(SplashVideoActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
