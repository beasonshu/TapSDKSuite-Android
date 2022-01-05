package com.tapsdk.suite.component;

import com.tapsdk.suite.Action;
import com.tapsdk.suite.constants.Constants;
import com.tapsdk.tapsdk_suite.R;

class TapArchievementComponent extends TapComponent {
    public TapArchievementComponent() {
        super(Constants.TapComponentType.ACHIEVEMENT, Constants.TapComponentDefaultName.ACHIEVEMENT
                , R.drawable.tapsdk_suite_ic_achievement_default, "", new Action() {
                    @Override
                    public void invoke(Object... args) {
                        // to implement
                    }
                });
    }
}
