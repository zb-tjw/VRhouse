package com.agent.vrhouse.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.agent.vrhouse.DemoHelper;
import com.agent.vrhouse.R;
import com.hyphenate.util.EasyUtils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {

	private static final int sleepTime = 2000;
	private boolean isFirstIn;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	public static SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.em_activity_splash);
		super.onCreate(arg0);

		preferences = getSharedPreferences("choose_way", MODE_PRIVATE);

		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		TextView versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		isFirstIn = preferences.getBoolean("isFirstIn", true);

		new Thread(new Runnable() {
			public void run() {
				if (DemoHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
					if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()))) {
						// nop
						// avoid main screen overlap Calling Activity
					} else {
						//enter main screen
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
					}
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
//					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//					finish();
					// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
					if (!isFirstIn) {
						// 使用Handler的postDelayed方法，1秒后执行跳转到MainActivity
						mHandler.sendEmptyMessage(GO_HOME);
					} else {
						mHandler.sendEmptyMessage(GO_GUIDE);
					}

				}
			}
		}).start();

	}



	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GO_HOME:
					goHome();
					break;
				case GO_GUIDE:
					goGuide();
					break;
			}
			super.handleMessage(msg);
		}

	};
	private void goGuide() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SplashActivity.this, SplashVideoActivity.class);
		startActivity(intent);
		finish();
	}

	private void goHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * get sdk version
	 */
	private String getVersion() {
	    return EMClient.getInstance().getChatConfig().getVersion();
	}
}
