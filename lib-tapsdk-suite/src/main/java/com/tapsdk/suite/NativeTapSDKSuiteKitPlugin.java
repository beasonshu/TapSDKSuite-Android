package com.tapsdk.suite;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tapsdk.suite.component.TapComponent;
import com.tapsdk.suite.component.entity.ComponentSummary;
import com.tapsdk.suite.component.entity.ComponentSummaryListWrapper;
import com.tapsdk.suite.constants.Constants;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NativeTapSDKSuiteKitPlugin {

    private static final String GAME_OBJECT_NAME = "PluginBridge";

    public NativeTapSDKSuiteKitPlugin() {

    }

    private List<ComponentSummary> componentSummaryList;

    private final Set<Integer> defaultComponentTypeSet
            = new HashSet<>(Arrays.asList(Constants.TapComponentType.MOMENTS
            , Constants.TapComponentType.FRIENDS
            , Constants.TapComponentType.ACHIEVEMENT
            , Constants.TapComponentType.CHAT
            , Constants.TapComponentType.LEADERBOARD));

    private boolean showed = false;

    FloatingWidget widget = null;

    private final Gson gson = new GsonBuilder().create();

    private final Action bridgeAction = new Action() {
        @Override
        public void invoke(Object... args) {
            String componentName = "";
            int type = -1;
            if (args.length > 0) {
                type = (int) args[0];
            }
            if (args.length > 1) {
                componentName = String.valueOf(args[1]);
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", type);
                jsonObject.put("componentName", componentName);
                UnityPlayer.UnitySendMessage(GAME_OBJECT_NAME, "HandleFloatingWindowCallbackDataMsg", jsonObject.toString());
            } catch (Exception e) {
                String errorMsg;
                if (!TextUtils.isEmpty(e.getMessage())) {
                    errorMsg = e.getMessage();
                } else {
                    errorMsg = e.toString();
                }
                UnityPlayer.UnitySendMessage(GAME_OBJECT_NAME, "HandleException", errorMsg);
            }
        }
    };

    public void configComponents(String componentListJsonObject) {
        ComponentSummaryListWrapper wrapper = gson.fromJson(componentListJsonObject, new TypeToken<ComponentSummaryListWrapper>() {
        }.getType());
        componentSummaryList = wrapper.list;
    }

    public void enableFloatingWindow(Activity activity) {
        if (showed) return;
        showed = true;
        List<TapComponent> targetTapComponentList = new ArrayList<>();
        if (componentSummaryList != null) {
            for (ComponentSummary componentSummary : componentSummaryList) {
                TapComponent tapComponent;
                if (defaultComponentTypeSet.contains(componentSummary.type)) {
                    tapComponent = TapComponent.createDefault(componentSummary.type, bridgeAction);
                    if (!TextUtils.isEmpty(componentSummary.componentName)) tapComponent.componentName = componentSummary.componentName;
                    tapComponent.drawableName = componentSummary.drawableName;
                } else {
                    tapComponent = TapComponent.createCustom(componentSummary.type
                            , componentSummary.componentName, -1
                            , componentSummary.drawableName, bridgeAction);
                }
                targetTapComponentList.add(tapComponent);
            }
        }
        widget = new FloatingWidget.Builder()
                .withComponentList(targetTapComponentList)
                .build();
        widget.attach(activity);
    }

    public void disableFloatingWindow() {
        if (!showed) return;
        widget.detach();
        widget = null;
        showed = false;
    }
}
