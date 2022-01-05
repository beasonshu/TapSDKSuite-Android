package com.tapsdk.suite.component;

import com.tapsdk.suite.Action;
import com.tapsdk.suite.constants.Constants;
import com.tapsdk.tapsdk_suite.R;

class TapFriendsComponent extends TapComponent {
    public TapFriendsComponent() {
        super(Constants.TapComponentType.FRIENDS, Constants.TapComponentDefaultName.FRIENDS
                , R.drawable.tapsdk_suite_ic_friends_default, "", new Action() {
                    @Override
                    public void invoke(Object... args) {
                        // to implement
                    }
                });
    }
}
