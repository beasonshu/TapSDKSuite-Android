package com.example.floatingwindow.friends;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.floatingwindow.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.linecorp.linesdk.LoginDelegate;
import com.linecorp.linesdk.LoginListener;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.tapsdk.bootstrap.TapBootstrap;
import com.tapsdk.bootstrap.account.TDSUser;
import com.tapsdk.bootstrap.exceptions.TapError;
import com.tapsdk.friends.Callback;
import com.tapsdk.friends.FriendStatusChangedListener;
import com.tapsdk.friends.ListCallback;
import com.tapsdk.friends.TDSFriends;
import com.tapsdk.friends.entities.TDSFriendInfo;
import com.tapsdk.friends.entities.TDSRichPresence;
import com.tapsdk.friends.entities.ThirdPartyFriend;
import com.tapsdk.friends.entities.ThirdPartyFriendRequestConfig;
import com.tapsdk.friends.entities.ThirdPartyFriendResult;
import com.tapsdk.friends.exceptions.TDSFriendError;
import com.tapsdk.friends.utils.LogUtil;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.tds.common.entities.TapConfig;
import com.tds.common.entities.TapDBConfig;
import com.tds.common.models.TapRegionType;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leancloud.LCFriendshipRequest;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class FriendsActivity extends Activity {

    public static final String TAG = "Friends";
    private String userId;
    private EditText userIdTextView;
    private EditText friendIdEditText;
    private List<LCFriendshipRequest> lcFriendshipRequests;
    private String tip;
    private final boolean isRND = false;
    private CallbackManager callbackManager;
    private AccessToken facebookToken;
    private LoginDelegate line_loginDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.setIsDebug(true);
        FacebookSdk.setApplicationId("application id");
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.tapsdk_activity_friends);
        TapBootstrap.setPreferredLanguage(0);
        callbackManager = CallbackManager.Factory.create();


        TDSFriends.setShareLink("share link");
        bindView();
        TDSFriends.registerFriendStatusChangedListener(new FriendStatusChangedListener() {
            @Override
            public void onFriendAdd(TDSFriendInfo friendInfo) {
                super.onFriendAdd(friendInfo);
                toast("add friend :" + friendInfo.toString());
            }

            @Override
            public void onNewRequestComing(LCFriendshipRequest request) {
                super.onNewRequestComing(request);
                toast("onNewRequestComing :" + request.toString());
            }

            @Override
            public void onRequestAccepted(LCFriendshipRequest request) {
                super.onRequestAccepted(request);
                toast("onRequestAccepted :" + request.toString());

            }

            @Override
            public void onRequestDeclined(LCFriendshipRequest request) {
                super.onRequestDeclined(request);
                toast("onRequestDeclined :" + request.toString());

            }

            @Override
            public void onFriendOffline(String userId) {
                super.onFriendOffline(userId);
                toast("onFriendOffline :" + userId);
            }

            @Override
            public void onFriendOnline(String userId) {
                super.onFriendOnline(userId);
                toast("onFriendOnline :" + userId);
            }

            @Override
            public void onRichPresenceChanged(String userId, TDSRichPresence richPresence) {
                super.onRichPresenceChanged(userId, richPresence);
                toast("onRichPresenceChanged user = " + userId + " rich:" + richPresence.toString());
            }

            @Override
            public void onConnected() {
                super.onConnected();
                toast("onConnected ");

            }

            @Override
            public void onDisconnected() {
                super.onDisconnected();
                toast("onDisconnected ");
            }

            @Override
            public void onConnectError(int code, String msg) {
                super.onConnectError(code, msg);
                toast("onConnectError : code = " + code + " msg = " + msg);
            }

            @Override
            public void onSendFriendRequestToSharer(boolean isSuccess, String nickname, TDSFriendError errorMsg) {
                super.onSendFriendRequestToSharer(isSuccess, nickname, errorMsg);
                toast("onSendFriendRequestToSharer : isSuccess = " + isSuccess + " nickName = " + nickname + " errorMsg = " + errorMsg);

            }
        });


    }

    private void bindView() {
        findViewById(R.id.init_cn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TapDBConfig tapDBConfig = new TapDBConfig();
                tapDBConfig.setGameVersion("v1.0.1");
                tapDBConfig.setChannel("Default");
                TapConfig tdsConfig = new TapConfig.Builder()
                        .withAppContext(FriendsActivity.this)
                        .withTapDBConfig(tapDBConfig)
                        .withClientId("Client ID")
                        .withClientToken("Client Token")
                        .withServerUrl("Server Url")
                        .withRegionType(TapRegionType.CN)
                        .build();
                TapBootstrap.init(FriendsActivity.this, tdsConfig);
            }
        });

        findViewById(R.id.init_io).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TapDBConfig tapDBConfig = new TapDBConfig();
                tapDBConfig.setGameVersion("v1.0.1");
                tapDBConfig.setChannel("Default");
                TapConfig tdsConfig = new TapConfig.Builder()
                        .withAppContext(FriendsActivity.this)
                        .withTapDBConfig(tapDBConfig)
                        .withClientId("Client ID")
                        .withClientToken("Client Token")
                        .withServerUrl("Server Url")
                        .withRegionType(TapRegionType.IO)
                        .build();
                TapBootstrap.init(FriendsActivity.this, tdsConfig);
            }
        });

        Spinner spinner = findViewById(R.id.set_lang);
        List<String> langs = new ArrayList<>();
        langs.add("自动");
        langs.add("中文简体");
        langs.add("英文");
        langs.add("中文繁体");
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, langs));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TapBootstrap.setPreferredLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.login_tap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TapBootstrap.loginWithTapTap(FriendsActivity.this, new com.tapsdk.bootstrap.Callback<TDSUser>() {
                    @Override
                    public void onSuccess(TDSUser result) {
                        Toast.makeText(FriendsActivity.this, "succeed to login with Taptap.", Toast.LENGTH_SHORT).show();
                        Profile profile = TapLoginHelper.getCurrentProfile();
                        LogUtil.logd("tap openId = " + profile.getOpenid());
                    }

                    @Override
                    public void onFail(TapError error) {
                        Toast.makeText(FriendsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, TapLoginHelper.SCOPE_PUBLIC_PROFILE, "user_friends");
            }
        });

        LoginButton loginButton = findViewById(R.id.login_fackbook);
        loginButton.setPermissions("public_profile","user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookToken = loginResult.getAccessToken();
                LogUtil.logd(" fackbook token = " + loginResult.getAccessToken().getToken() + " id = " +
                        loginResult.getAccessToken().getUserId());
                com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
                if(profile != null) {
                    LogUtil.logd("facebook name = " + profile.getName() + " id = " + profile.getId() + " avatar = " + profile.getPictureUri());
                }else{
                    ProfileTracker profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(@org.jetbrains.annotations.Nullable com.facebook.Profile profile, @org.jetbrains.annotations.Nullable com.facebook.Profile profile1) {
                            if (profile1 != null) {
                                this.stopTracking();
                                com.facebook.Profile.setCurrentProfile(profile1);
                                LogUtil.logd("facebook name = " + profile1.getName() + " id = " + profile1.getId() + " avatar = " + profile1.getPictureUri());
                            }
                        }
                    };
                    profileTracker.startTracking();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NotNull FacebookException e) {
                LogUtil.logd("facebook loginFail error = " + e.getMessage());

            }
        });

        com.linecorp.linesdk.widget.LoginButton lineButton = findViewById(R.id.login_line);
        lineButton.setChannelId("1656655432");
        lineButton.enableLineAppAuthentication(true);
        lineButton.setAuthenticationParams(new LineAuthenticationParams.Builder()
        .scopes(Arrays.asList(Scope.PROFILE,Scope.FRIEND))
        .build());
        line_loginDelegate = LoginDelegate.Factory.create();
        lineButton.setLoginDelegate(line_loginDelegate);
        lineButton.addLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess(@androidx.annotation.NonNull LineLoginResult result) {
                if (result.getLineCredential() != null) {
                    String token = result.getLineCredential().getAccessToken().getTokenString();
                    LogUtil.logd(" line login success = " + token);
                    if (result.getLineProfile() != null) {
                        LogUtil.logd(" line profile = " + result.getLineProfile().getDisplayName() + " id = " +
                                result.getLineProfile().getUserId() + " picture = " + result.getLineProfile().getPictureUrl());
                    }
                }
            }

            @Override
            public void onLoginFailure(LineLoginResult result) {
                LogUtil.logd("line login fail error = " + result.getErrorData().getMessage());
            }
        });

        findViewById(R.id.anonyLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSUser.logInAnonymously().subscribe(new Observer<TDSUser>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TDSUser tdsUser) {
                        toast("succeed to login with Anonymously. info = " + tdsUser.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(FriendsActivity.this, "error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
        userIdTextView = findViewById(R.id.userIdTextView);
        friendIdEditText = findViewById(R.id.friendIdEditText);

        findViewById(R.id.getProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logd("currentuser = " + TDSUser.getCurrentUser().toString());
                userId = TDSUser.currentUser().getObjectId();
                userIdTextView.setText(String.valueOf("用户id:" + userId));
                Toast.makeText(FriendsActivity.this
                        , "get profile success", Toast.LENGTH_SHORT).show();
            }
        });

        final EditText rich_key1 = findViewById(R.id.rich_key1);
        final EditText rich_key2 = findViewById(R.id.rich_key2);
        final EditText rich_key3 = findViewById(R.id.rich_key3);
        final EditText rich_value1 = findViewById(R.id.rich_value1);
        final EditText rich_value2 = findViewById(R.id.rich_value2);
        final EditText rich_value3 = findViewById(R.id.rich_value3);
        findViewById(R.id.multiRich).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> richs = new HashMap<>();
                if (!TextUtils.isEmpty(rich_key1.getText().toString())) {
                    richs.put(rich_key1.getText().toString(), rich_value1.getText().toString());
                }
                if (!TextUtils.isEmpty(rich_key2.getText().toString())) {
                    richs.put(rich_key2.getText().toString(), rich_value2.getText().toString());
                }
                if (!TextUtils.isEmpty(rich_key3.getText().toString())) {
                    richs.put(rich_key3.getText().toString(), rich_value3.getText().toString());
                }
                TDSFriends.setRichPresences(richs, new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        toast("批量设置成功");
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("批量设置失败" + error.detailMessage);
                    }
                });
            }
        });


        findViewById(R.id.multiClearRich).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stringList = new ArrayList<>();
//                stringList.add("score");
//                stringList.add("rank");
//                stringList.add("display");
                if (!TextUtils.isEmpty(rich_key1.getText().toString())) {
                    stringList.add(rich_key1.getText().toString());
                }
                if (!TextUtils.isEmpty(rich_key2.getText().toString())) {
                    stringList.add(rich_key2.getText().toString());
                }
                if (!TextUtils.isEmpty(rich_key3.getText().toString())) {
                    stringList.add(rich_key3.getText().toString());
                }
                TDSFriends.clearRichPresences(stringList, new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        toast("批量清除成功");
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("批量清除失败 = " + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.addFriendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.addFriend(friendIdEditText.getText().toString(), new com.tapsdk.friends.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(FriendsActivity.this
                                , "add friend success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        Toast.makeText(FriendsActivity.this
                                , error.detailMessage, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "addFriend error:" + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.checkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.checkFriendship(friendIdEditText.getText().toString(), new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            toast("当前是我的好友");
                        } else {
                            toast("当前不是我的好友");
                        }
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("检查失败 error = " + error.detailMessage);
                    }
                });
            }
        });

        final EditText input_name = findViewById(R.id.input_name);
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.searchUserByName(input_name.getText().toString(), new ListCallback<TDSFriendInfo>() {
                    @Override
                    public void onSuccess(List<TDSFriendInfo> result) {
                        toast("共搜索到 " + result.size() + " 个 info = " + result.toString());
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("搜素失败 error= " + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.deleteFriendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.deleteFriend(friendIdEditText.getText().toString(), new com.tapsdk.friends.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(FriendsActivity.this
                                , "delete friend success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        Toast.makeText(FriendsActivity.this
                                , error.detailMessage, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "deleteFriend error:" + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.onlineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.online(new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        toast("online success");
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("online fail error = " + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.offlineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.offline();
            }
        });

        findViewById(R.id.allRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.queryFriendRequestList(LCFriendshipRequest.STATUS_PENDING, 0, 20,
                        new ListCallback<LCFriendshipRequest>() {

                            @Override
                            public void onSuccess(List<LCFriendshipRequest> result) {
                                lcFriendshipRequests = result;
                                toast("queryFriendRequestList size = " + result.size() +
                                        " data = " + result.toString());
                            }

                            @Override
                            public void onFail(TDSFriendError error) {
                                toast("queryFriendRequestList fail error = " + error.detailMessage);
                            }
                        });
            }
        });

        findViewById(R.id.all_decline_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.queryFriendRequestList(LCFriendshipRequest.STATUS_DECLINED, 0, 20,
                        new ListCallback<LCFriendshipRequest>() {

                            @Override
                            public void onSuccess(List<LCFriendshipRequest> result) {
                                lcFriendshipRequests = result;
                                toast("queryFriendRequestList size = " + result.size() +
                                        " data = " + result.toString());
                            }

                            @Override
                            public void onFail(TDSFriendError error) {
                                toast("queryFriendRequestList fail error = " + error.detailMessage);
                            }
                        });
            }
        });

        findViewById(R.id.acceptRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequest(1);
            }
        });
        findViewById(R.id.declineRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequest(0);
            }
        });
        findViewById(R.id.deleteRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequest(-1);
            }
        });
        findViewById(R.id.getFollowingList).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TDSFriends.queryFriendList(0, 20, new ListCallback<TDSFriendInfo>() {
                    @Override
                    public void onSuccess(List<TDSFriendInfo> result) {
                        Log.d(TAG, "queryFriendList SIZE:" + result.size() + " data = " + result.toString());
                        Toast.makeText(FriendsActivity.this, "好友列表获取成功：" + result.size(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        Log.d(TAG, "queryFriendList error:" + error.detailMessage);
                        Toast.makeText(FriendsActivity.this, "好友列表获取失败：" + error.detailMessage, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        findViewById(R.id.friendInviteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDSFriends.generateFriendInvitationLink(new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.logd("share link = " + result);
                        Toast.makeText(FriendsActivity.this, "好友分享链接：" + result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        FriendsActivity.this.startActivity(intent);

                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        Toast.makeText(FriendsActivity.this, "好友分享链接：" + error.detailMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        final EditText typeEditTextView = (EditText) findViewById(R.id.typeEditTextView);
        final EditText valueEditTextView = (EditText) findViewById(R.id.valueEditTextView);
        findViewById(R.id.submitRichPresenceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeEditTextView == null
                        || valueEditTextView == null
                        || TextUtils.isEmpty(typeEditTextView.getText())
                ) {
                    Toast.makeText(FriendsActivity.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                TDSFriends.setRichPresence(typeEditTextView.getText().toString(),
                        valueEditTextView.getText().toString(), new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    Toast.makeText(FriendsActivity.this, "设置富信息成功", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFail(TDSFriendError error) {
                                Toast.makeText(FriendsActivity.this, "设置富信息失败：" + error.detailMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        findViewById(R.id.queryTapFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartyFriendRequestConfig config = new ThirdPartyFriendRequestConfig.Builder()
                        .platform(ThirdPartyFriendRequestConfig.PLATFORM_TAPTAP)
                        .pageSize(50)
                        .build();
                TDSFriends.queryThirdPartyFriendList(config, null, new Callback<ThirdPartyFriendResult>() {
                    @Override
                    public void onSuccess(ThirdPartyFriendResult result) {
                        List<ThirdPartyFriend> friendList = result.getFriendList();
                        StringBuilder builder = new StringBuilder();
                        if(friendList != null && friendList.size() > 0){
                            for(ThirdPartyFriend friend : friendList){
                                builder.append("tapName: ").append(friend.getUserName());
                                if(friend.getTdsFriendInfo() != null){
                                    TDSUser user = friend.getTdsFriendInfo().getUser();
                                    if(user != null){
                                        String nickName = (String) user.get("nickname");
                                        String objectId = user.getObjectId();
                                        String shortCode = (String) user.get("shortId");
                                        LogUtil.logd("friend name = " + friend.getUserName() + " objectId = " + objectId + " nickName = " + nickName + " shortId = " + shortCode);
                                    }else{
                                        LogUtil.logd(" friend name = " + friend.getUserName() + " has no tdsInfo");
                                    }

                                    builder.append(" tdsName: ").append(friend.getTdsFriendInfo().getUser().get("nickname"));
                                }
                                builder.append("\n");
                            }
                        }
                        String friendInfos = builder.toString();
                        if(friendInfos.length() == 0){
                            friendInfos = "暂无数据";
                        }
                        toast(friendInfos);
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("query error = " + error.code + " msg = " + error.detailMessage);
                        LogUtil.loge(" get friendList error = " + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.queryNextTapFriend).setVisibility(View.GONE);
        findViewById(R.id.queryTapFriendWithoutCache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartyFriendRequestConfig config = new ThirdPartyFriendRequestConfig.Builder()
                        .platform(ThirdPartyFriendRequestConfig.PLATFORM_TAPTAP)
                        .pageSize(50)
                        .cachePolicy(ThirdPartyFriendRequestConfig.CachePolicy.ONLY_NETWORK)
                        .build();

                TDSFriends.queryThirdPartyFriendList(config, null, new Callback<ThirdPartyFriendResult>() {
                    @Override
                    public void onSuccess(ThirdPartyFriendResult result) {
                        List<ThirdPartyFriend> friendList = result.getFriendList();
                        StringBuilder builder = new StringBuilder();
                        if(friendList != null && friendList.size() > 0){
                            for(ThirdPartyFriend friend : friendList){
                                builder.append("tapName: ").append(friend.getUserName());
                                if(friend.getTdsFriendInfo() != null){
                                    TDSUser user = friend.getTdsFriendInfo().getUser();
                                    if(user != null){
                                        String nickName = (String) user.get("nickname");
                                        String objectId = user.getObjectId();
                                        String shortCode = (String) user.get("shortId");
                                        LogUtil.logd("friend name = " + friend.getUserName() + " objectId = " + objectId + " nickName = " + nickName + " shortId = " + shortCode);
                                    }else{
                                        LogUtil.logd(" friend name = " + friend.getUserName() + " has no tdsInfo");
                                    }

                                    builder.append(" tdsName: ").append(friend.getTdsFriendInfo().getUser().get("nickname"));
                                }
                                builder.append("\n");
                            }
                        }
                        String friendInfos = builder.toString();
                        if(friendInfos.length() == 0){
                            friendInfos = "暂无数据";
                        }
                        toast(friendInfos);
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        toast("query error = " + error.code + " msg = " + error.detailMessage);
                        LogUtil.loge(" get friendList error = " + error.detailMessage);
                    }
                });
            }
        });

        findViewById(R.id.followTapUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tdsId = findViewById(R.id.et_friend_id);
                if(!TextUtils.isEmpty(tdsId.getText().toString())){
                    TDSUser tdsUser = new TDSUser();
                    tdsUser.setObjectId(tdsId.getText().toString());
                    TDSFriends.followTapUser(tdsUser, new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if(result){
                                toast("tap 关注成功");
                            }else{
                                toast("tap 关注失败");
                            }
                        }

                        @Override
                        public void onFail(TDSFriendError error) {
                            toast("tap 关注失败 error = " + error.detailMessage);
                        }
                    });
                }
            }
        });

        findViewById(R.id.bt_search_by_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shortId = ((EditText)findViewById(R.id.et_find_short)).getText().toString();
                if(shortId.length() > 0){
                    TDSFriends.searchUserByShortCode(shortId, new Callback<TDSFriendInfo>() {
                        @Override
                        public void onSuccess(TDSFriendInfo result) {
                            String nick = (String) result.getUser().get("nickname");
                            String shortID = (String) result.getUser().get("shortId");
                            toast("find user =" + nick + " shortCode = " + shortID);
                        }

                        @Override
                        public void onFail(TDSFriendError error) {
                            toast("find fail error = " + error.detailMessage);
                        }
                    });
                }
            }
        });

        findViewById(R.id.bt_add_by_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shortId = ((EditText)findViewById(R.id.et_find_short)).getText().toString();
                if(shortId.length() > 0){
                    TDSFriends.addFriendByShortCode(shortId, null, new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            toast("add success");
                        }

                        @Override
                        public void onFail(TDSFriendError error) {
                            toast("add fail" + error.detailMessage);
                        }
                    });
                }
            }
        });

        findViewById(R.id.bt_query_fackbook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphRequest request = GraphRequest.newMyFriendsRequest(facebookToken,
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(@org.jetbrains.annotations.Nullable JSONArray jsonArray, @org.jetbrains.annotations.Nullable GraphResponse graphResponse) {
                                LogUtil.logd("fackbook friends = " + jsonArray);
                            }
                        });
                request.executeAsync();
            }
        });

    }

    private void toast(String msg) {
        Log.d(TAG, msg);
        Toast.makeText(FriendsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void handleRequest(int action) {
        String requestID = ((EditText) findViewById(R.id.requestid)).getText().toString();
        if (requestID.length() == 0) {
            toast("申请用户id为空");
            return;
        }
        if (lcFriendshipRequests == null || lcFriendshipRequests.size() == 0) {
            toast("未获取申请列表或列表为空");
            return;
        }
        LCFriendshipRequest request = null;
        for (LCFriendshipRequest r : lcFriendshipRequests) {
            if (r.getSourceUser().getObjectId().equals(requestID)) {
                request = r;
                break;
            }
        }
        if (request == null) {
            toast("未发现对应好友申请");
        } else {

            Callback<Boolean> callback = new Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        toast(tip + "成功");
                    } else {
                        toast(tip + "失败");
                    }
                }

                @Override
                public void onFail(TDSFriendError error) {
                    toast(tip + " 错误 = " + error.detailMessage);
                }
            };
            switch (action) {
                case -1:
                    tip = "删除申请";
                    TDSFriends.deleteFriendRequest(request, callback);
                    break;
                case 0:
                    tip = "拒绝申请";
                    TDSFriends.declineFriendRequest(request, callback);
                    break;
                case 1:
                    tip = "同意申请";
                    TDSFriends.acceptFriendRequest(request, null, callback);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            line_loginDelegate.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
