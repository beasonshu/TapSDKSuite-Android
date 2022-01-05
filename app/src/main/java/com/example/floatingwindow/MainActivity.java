package com.example.floatingwindow;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.floatingwindow.friends.FriendsActivity;
import com.tapsdk.bootstrap.Callback;
import com.tapsdk.bootstrap.TapBootstrap;
import com.tapsdk.bootstrap.account.TDSUser;
import com.tapsdk.bootstrap.exceptions.TapError;
import com.tapsdk.moment.TapMoment;
import com.tapsdk.suite.Action;
import com.tapsdk.suite.TapSDKSuite;
import com.tapsdk.suite.component.TapComponent;
import com.tapsdk.suite.constants.Constants;
import com.taptap.sdk.TapLoginHelper;
import com.tds.achievement.AchievementCallback;
import com.tds.achievement.AchievementException;
import com.tds.achievement.TapAchievement;
import com.tds.achievement.TapAchievementBean;
import com.tds.common.entities.TapConfig;
import com.tds.common.entities.TapDBConfig;
import com.tds.common.models.TapRegionType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final String clientId = "0RiAlMny7jiz086FaU";

    private final int[] componentTypeArray = new int[]{Constants.TapComponentType.MOMENTS
            , Constants.TapComponentType.FRIENDS, Constants.TapComponentType.ACHIEVEMENT
            , Constants.TapComponentType.CHAT, Constants.TapComponentType.LEADERBOARD
    };

    Action defaultAction = new Action() {
        @Override
        public void invoke(Object... args) {
            String componentName = (String) args[1];
            Toast.makeText(MainActivity.this, componentName + " invoke", Toast.LENGTH_SHORT).show();
        }
    };

    private void openMoment() {
        try {
            TapMoment.init(MainActivity.this, clientId, true);
            TapMoment.open(TapMoment.ORIENTATION_DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFriends() {
        Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(intent);
    }

    private void openArchievement() {
        if (TapLoginHelper.getCurrentAccessToken() == null) {
            Toast.makeText(this, "please login TapTap first", Toast.LENGTH_SHORT).show();
            return;
        }
        TapAchievement.showAchievementPage();
    }

    private void initTapArchievement() {

        TapAchievement.registerCallback(new AchievementCallback() {

            @Override
            public void onAchievementSDKInitSuccess() {

            }

            @Override
            public void onAchievementSDKInitFail(AchievementException exception) {
            }

            @Override
            public void onAchievementStatusUpdate(TapAchievementBean tapAchievementBean, AchievementException e) {
            }
        });

        TapAchievement.initData();
    }

    Action momentAction = new Action() {
        @Override
        public void invoke(Object... args) {
            openMoment();
        }
    };

    Action friendsAction = new Action() {
        @Override
        public void invoke(Object... args) {
            openFriends();
        }
    };

    Action archievementAction = new Action() {
        @Override
        public void invoke(Object... args) {
            openArchievement();
        }
    };

    private TapConfig generateTapConfig() {
        TapDBConfig tapDBConfig = new TapDBConfig();
        tapDBConfig.setGameVersion("v1.0.1");
        tapDBConfig.setChannel("Default");
        return new TapConfig.Builder()
                .withAppContext(this)
                .withTapDBConfig(tapDBConfig)
                .withClientId(clientId)
                .withClientToken("8V8wemqkpkxmAN7qKhvlh6v0pXc8JJzEZe3JFUnU")
                .withServerUrl("https://0rialmny.cloud.tds1.tapapis.cn")
                .withRegionType(TapRegionType.CN)
                .build();
    }

    private final Action[] actionArray = new Action[]{
            momentAction
            , friendsAction
            , archievementAction
            , defaultAction
            , defaultAction
    };

    private void initTapSdk() {
        TapBootstrap.init(MainActivity.this, generateTapConfig());
        if (TapLoginHelper.getCurrentAccessToken() != null) {
            initTapArchievement();
        }
    }

    private List<TapComponent> generateTestCase(int num) {
        List<TapComponent> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            TapComponent component = TapComponent.createDefault(componentTypeArray[i % 5], actionArray[i % 5]);
            result.add(component);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initTapSdk();
        final EditText numOfComponentsEditText = findViewById(R.id.numOfComponentsEditText);
        numOfComponentsEditText.setText(String.valueOf(10));
        final Button flatingButton = findViewById(R.id.floatingButton);
        flatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TapSDKSuite.isShowing()) {
                    try {
                        int numOfComponents = Integer.parseInt(numOfComponentsEditText.getText().toString());
                        popupFloatingWindow(generateTestCase(numOfComponents), flatingButton);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else {
                    TapSDKSuite.disable();
                    updateButtonPanelText(null, false);
                }
            }
        });

        Button tapLoginButton = findViewById(R.id.loginButton);
        tapLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TapBootstrap.loginWithTapTap(MainActivity.this, new Callback<TDSUser>() {
                    @Override
                    public void onSuccess(TDSUser tdsUser) {
                        initTapArchievement();

                    }

                    @Override
                    public void onFail(TapError tapError) {
                        System.out.println("login error" + tapError.toString());
                    }
                });
            }
        });

        TapMoment.setCallback(new TapMoment.TapMomentCallback() {
            @Override
            public void onCallback(int code, String msg) {
                if (code == TapMoment.CALLBACK_CODE_SCENE_EVENT) {
                    System.out.println( "event extra = " + msg);
                }
            }
        });
    }

    private void popupFloatingWindow(List<TapComponent> tapComponentList, View view) {
        TapSDKSuite.configComponents(tapComponentList);
        TapSDKSuite.enable(this);
        updateButtonPanelText(view, TapSDKSuite.isShowing());
    }

    private void updateButtonPanelText(View view, boolean showed) {
        Button tenBallButton = findViewById(R.id.floatingButton);
        if (showed) {
            ((Button) view).setText(R.string.disable_floating_window);
        } else {
            tenBallButton.setVisibility(View.VISIBLE);
            tenBallButton.setText(R.string.enable_floating_window);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        TapSDKSuite.disable();
        updateButtonPanelText(null, false);
    }
}