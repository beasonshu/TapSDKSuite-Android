package com.tapsdk.suite.component;

import com.tapsdk.suite.Action;
import com.tapsdk.suite.constants.Constants;

import java.util.HashMap;
import java.util.Map;

public class TapComponent {

    public String componentName;

    public int type;

    public int resourceId;

    // unity resource  from R.drawable pass file name
    public String drawableName;

    public Action customAction;

    public static Map<Integer, Class<? extends TapComponent>> dict = new HashMap<>();

    static {
        dict.put(Constants.TapComponentType.MOMENTS, TapMomentComponent.class);
        dict.put(Constants.TapComponentType.FRIENDS, TapFriendsComponent.class);
        dict.put(Constants.TapComponentType.ACHIEVEMENT, TapArchievementComponent.class);
        dict.put(Constants.TapComponentType.CHAT, TapChatComponent.class);
        dict.put(Constants.TapComponentType.LEADERBOARD, TapLeaderboard.class);
    }

    private TapComponent() {
    }

    /**
     * hide
     * @param type
     * @return
     */
    private static TapComponent createDefault(int type) {
        return createDefault(type, null);
    }

    public static TapComponent createDefault(int type, Action customAction) {
        TapComponent tapComponent = new TapComponent().createByClass(dict.get(type));
        if (tapComponent == null) return new TapMomentComponent();
        if (customAction != null) tapComponent.customAction = customAction;
        return tapComponent;
    }

    private TapComponent createByClass(Class<? extends TapComponent> kind) {
        TapComponentMaker<? extends TapComponent> maker = new TapComponentMaker<>(kind);
        try {
            return maker.create();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TapComponent createCustom(int type, String componentName, int resourceId, Action customAction) {
        return new TapComponent(type, componentName, resourceId, customAction);
    }

    public static TapComponent createCustom(int type, String componentName, int resourceId, String resourceName, Action customAction) {
        return new TapComponent(type, componentName, resourceId, resourceName, customAction);
    }

    protected TapComponent(int type, String componentName, int resourceId, Action customAction) {
        this(type, componentName, resourceId, "", customAction);
    }

    protected TapComponent(int type, String componentName, int resourceId, String drawableName, Action customAction) {
        this.type = type;
        this.componentName = componentName;
        this.resourceId = resourceId;
        this.drawableName = drawableName;
        this.customAction = customAction;
    }
}
