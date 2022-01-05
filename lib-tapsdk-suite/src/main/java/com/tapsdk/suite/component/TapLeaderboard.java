package com.tapsdk.suite.component;

import com.tapsdk.suite.Action;
import com.tapsdk.suite.constants.Constants;
import com.tapsdk.tapsdk_suite.R;

class TapLeaderboard extends TapComponent {
    public TapLeaderboard() {
        super(Constants.TapComponentType.LEADERBOARD, Constants.TapComponentDefaultName.LEADERBOARD
                , R.drawable.tapsdk_suite_ic_leadboard_default, "", new Action() {
                    @Override
                    public void invoke(Object... args) {
                        // to implement
                    }
                });
    }
}
