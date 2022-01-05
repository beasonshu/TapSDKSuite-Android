package com.tapsdk.suite.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tapsdk.suite.component.TapComponent;
import com.tapsdk.suite.util.DrawableUtil;
import com.tapsdk.suite.util.UIUtil;
import com.tapsdk.tapsdk_suite.BuildConfig;
import com.tapsdk.tapsdk_suite.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FloatingRootView extends FrameLayout {

    private final static String TAG = "FloatingRootView";

    private final static int COMPONENT_INTERVAL = 64;
    private final static int COMPONENT_HALF_INTERVAL = 32;

    List<Integer[]> componentPositionList = Collections.emptyList();

    Integer[] ovalCenterPosition;

    Integer[] ovalCenterPositionDp;

    Integer[] ovalAxis;

    int startY;

    private int prevScrolly = Integer.MIN_VALUE;

    private LogoBar logoBar = null;

    private Animation componentIconAppearAnimation;
    private Animation componentTextAppearAnimation;

    private Animation componentIconDisappearAnimation;
    private Animation componentTextDisappearAnimation;

    private Animation logoAppearAnimation;
    private Animation logDisappearAnimation;

    private boolean appearAnimationStarted = false;

    private boolean disappearAnimationStarted = false;

    public interface ComponentsCollapseListener {
        void onCollapse();
    }

    ComponentsCollapseListener componentsCollapseListener;

    public void setComponentsCollapseListener(ComponentsCollapseListener listener) {
        this.componentsCollapseListener = listener;
    }

    public FloatingRootView(Context context) {
        super(context);
    }

    public FloatingRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static FloatingRootView createDefault(Context context, Integer[] ovalCenterPositionDp, Integer[] ovalAxis, Integer[] ovalCenterPosition, int startY) {
        FloatingRootView floatingRootView
                = (FloatingRootView) LayoutInflater.from(context).inflate(R.layout.tapsdk_suite_view_floating_root, null, false);
        floatingRootView.ovalCenterPositionDp = ovalCenterPositionDp;
        floatingRootView.ovalAxis = ovalAxis;
        floatingRootView.ovalCenterPosition = ovalCenterPosition;
        floatingRootView.startY = startY;
        return floatingRootView;
    }

    public void attach(List<TapComponent> tapComponentList, int orientation) {
        Context context = getContext();
        this.componentIconAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.tapsdk_suite_anim_component_ic_scale_in);
        this.componentIconDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.tapsdk_suite_anim_component_ic_scale_out);
        this.componentTextAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.tapsdk_suite_anim_component_text_alpha_in);
        this.componentTextDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.tapsdk_suite_anim_component_text_alpha_out);
        this.logoAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.tapsdk_suite_anim_logo_alpha_in);
        this.logDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.tapsdk_suite_anim_logo_alpha_out);

        setupCustomScrollView();
        setupComponentListViews(tapComponentList);
        setupCenterProgressBar(tapComponentList.size(),orientation);
        showComponents(true);
        registerListener();
    }

    private void registerListener() {
        findViewById(R.id.floatingOuterAreaView).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showComponents(false);
                    }
                }
        );
    }

    private void setupCenterProgressBar(int numOfComponents, int orientation) {
        Context context = getContext();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMargins(UIUtil.dp2px(context, 17), 0, 0, 0);
        logoBar = new LogoBar(getContext(), this, lp, numOfComponents);
    }

    private void setupCustomScrollView() {
        final FrameLayout frameLayout = findViewById(R.id.componentContainerFrameLayout);
        final CustomScrollView customScrollView = findViewById(R.id.customScrollView);
        customScrollView.setCustomOnScrollChangedListener(new CustomScrollView.CustomOnScrollChangedListener() {
            @Override
            public void onScroll(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int changeY = scrollY - oldScrollY;
                if (prevScrolly == Integer.MIN_VALUE || prevScrolly != oldScrollY) {
                    updateComponents(changeY);
                    refreshProgress(changeY, frameLayout.getHeight() - customScrollView.getHeight());
                }
                prevScrolly = oldScrollY;
            }
        });
    }

    private int extractRealIndex(String tag) {
        int result = -1;
        try {
            result = Integer.parseInt(tag);
        } catch (Exception e) {
            // not expected here
        }
        return result;
    }

    private void updateComponents(int dy) {
        Context context = getContext();

        ovalCenterPositionDp[1] += dy;
        for (int i = 0; i < componentPositionList.size(); i++) {
            Integer[] position = componentPositionList.get(i);
            position[0] = getOvalX(position[1], ovalCenterPositionDp[0], ovalCenterPositionDp[1], ovalAxis[1], ovalAxis[0]) - UIUtil.dp2px(context, 26);
        }
        ViewGroup viewGroup = findViewById(R.id.componentContainerFrameLayout);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            LinearLayout componentView = (LinearLayout) viewGroup.getChildAt(i);
            int realIndex = extractRealIndex((String) componentView.getTag());
            Integer[] position = componentPositionList.get(realIndex);
            FrameLayout.LayoutParams lp = (LayoutParams) componentView.getLayoutParams();
            lp.setMargins(position[0], position[1], 0, 0);
            componentView.setLayoutParams(lp);
        }
    }

    private void refreshProgress(int dy, int totalMove) {
        logoBar.refreshProgress(dy, totalMove);
    }

    private static Integer getOvalX(int y, int centerX, int centerY, int axisA, int axisB) {
        int relativeY = y - centerY;
        int targetX = (int) Math.ceil(Math.sqrt((double) (axisB * axisB) * (1 - (double) (relativeY * relativeY) / (axisA * axisA))));
        return targetX + centerX;
    }

    private Integer[] getComponentPosition(int sourceY) {
        Context context = getContext();
        int y = UIUtil.dp2px(context, sourceY);
        Integer[] result = new Integer[]{0, y};
        result[0] = getOvalX(y, ovalCenterPositionDp[0], ovalCenterPositionDp[1], ovalAxis[1], ovalAxis[0]) - UIUtil.dp2px(context, 26);
        return result;
    }

    private List<Integer[]> generateInitialComponentPositionList(int num) {
        List<Integer[]> result = new ArrayList<>();
        int centerY = ovalCenterPosition[1];
        switch (num) {
            case 1:
            case 3:
            case 5:
                Integer[] startPos = getComponentPosition(centerY);
                result.add(startPos);
                for (int i = 1; i < (num / 2 + 1); i++) {
                    result.add(getComponentPosition(centerY - i * COMPONENT_INTERVAL));
                    result.add(getComponentPosition(centerY + i * COMPONENT_INTERVAL));
                }
                break;
            case 2:
            case 4:
                for (int i = 0; i < num / 2; i++) {
                    result.add(getComponentPosition(centerY - COMPONENT_HALF_INTERVAL - i * COMPONENT_INTERVAL));
                    result.add(getComponentPosition(centerY + COMPONENT_HALF_INTERVAL + i * COMPONENT_INTERVAL));
                }
                break;
            default:
                for (int i = 0; i < num; i++) {
                    result.add(getComponentPosition(startY + i * COMPONENT_INTERVAL));
                }
                break;
        }
        Collections.sort(result, new Comparator<Integer[]>() {
            @Override
            public int compare(Integer[] t1, Integer[] t2) {
                return t1[1] - t2[1];
            }
        });
        return result;
    }

    private void setupComponentListViews(List<TapComponent> tapComponentList) {
        Context context = getContext();
        if (context == null) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException(TAG + " context is null");
            }
            return;
        }
        int size = tapComponentList.size();
        if (size == 0) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException(TAG + " component size can't be zero");
            }
            return;
        }
        componentPositionList = generateInitialComponentPositionList(size);
        FrameLayout containerView = findViewById(R.id.componentContainerFrameLayout);
        for (int i = 0; i < tapComponentList.size(); i++) {
            Integer[] position = componentPositionList.get(i);
            final TapComponent tapComponent = tapComponentList.get(i);
            LinearLayout componentLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.tapsdk_suite_item_view_single_component, null, false);
            componentLinearLayout.setTag("" + i);
            TextView componentTitleTextView = componentLinearLayout.findViewById(R.id.componentTitleTextView);
            if (tapComponent.componentName != null && tapComponent.componentName.length() != 0) {
                componentTitleTextView.setText(tapComponent.componentName);
            }

            ImageView componentIconImageView = componentLinearLayout.findViewById(R.id.componentIconImageView);
            int resourceId = 0;
            if (!TextUtils.isEmpty(tapComponent.drawableName) && DrawableUtil.getDrawable(context, tapComponent.drawableName) > 0) {
                resourceId = DrawableUtil.getDrawable(context, tapComponent.drawableName);
            } else if (tapComponent.resourceId > 0) {
                resourceId = tapComponent.resourceId;
            }
            componentIconImageView.setImageResource(resourceId);

            componentLinearLayout.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tapComponent.customAction != null) {
                            tapComponent.customAction.invoke(tapComponent.type, tapComponent.componentName);
                            showComponents(false);
                        }
                    }
                }
            );
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(UIUtil.dp2px(context, 300), UIUtil.dp2px(context, 52));
            lp.setMargins(position[0], position[1], 0, 0);
            containerView.addView(componentLinearLayout, lp);
        }
    }

    public void showComponents(final boolean visible) {
        CustomScrollView customScrollView = findViewById(R.id.customScrollView);
        customScrollView.setVisibility(View.VISIBLE);
        logoBar.setOnLogoClickListener(new LogoBar.OnLogoClickListener() {
            @Override
            public void onClick() {
                showComponents(false);
            }
        });
        if (visible) {
            applyAppearAnimation();
        } else {
            applyDisappearAnimation();
        }
    }

    private void applyAppearAnimation() {
        if (appearAnimationStarted) {
            return;
        }
        appearAnimationStarted = true;

        final FrameLayout containerView = findViewById(R.id.componentContainerFrameLayout);
        final CustomScrollView customScrollView = findViewById(R.id.customScrollView);
        for (int i = 0; i < containerView.getChildCount(); i++) {
            LinearLayout componentLayout = containerView.findViewWithTag("" + i);
            componentLayout.findViewById(R.id.componentIconImageView).startAnimation(componentIconAppearAnimation);
            componentLayout.findViewById(R.id.componentTitleTextView).startAnimation(componentTextAppearAnimation);
        }

        logoBar.playAnimation(logoAppearAnimation);
        componentIconAppearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appearAnimationStarted = true;
                customScrollView.setForbid(true);
                logoBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appearAnimationStarted = false;
                customScrollView.setForbid(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void applyDisappearAnimation() {
        if (disappearAnimationStarted) {
            return;
        }
        disappearAnimationStarted = true;
        final FrameLayout containerView = findViewById(R.id.componentContainerFrameLayout);
        final CustomScrollView customScrollView = findViewById(R.id.customScrollView);
        for (int i = 0; i < containerView.getChildCount(); i++) {
            LinearLayout componentLayout = containerView.findViewWithTag("" + i);
            componentLayout.findViewById(R.id.componentIconImageView).startAnimation(componentIconDisappearAnimation);
            componentLayout.findViewById(R.id.componentTitleTextView).startAnimation(componentTextDisappearAnimation);
        }
        logoBar.playAnimation(logDisappearAnimation);
        componentIconDisappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                customScrollView.setForbid(false);
                for (int i = 0; i < containerView.getChildCount(); i++) {
                    LinearLayout componentLayout = containerView.findViewWithTag("" + i);
                    componentLayout.findViewById(R.id.componentTitleTextView).setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (componentsCollapseListener != null) componentsCollapseListener.onCollapse();
                disappearAnimationStarted = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void onDetach() {

    }
}
