package com.tapsdk.suite.component;

import com.tapsdk.suite.Action;
import com.tapsdk.suite.constants.Constants;
import com.tapsdk.tapsdk_suite.R;

class TapMomentComponent extends TapComponent {
    public TapMomentComponent() {
        super(Constants.TapComponentType.MOMENTS, Constants.TapComponentDefaultName.MOMENTS
                , R.drawable.tapsdk_suite_ic_moment_default, "", new Action() {
            @Override
            public void invoke(Object... args) {
                // to implement
            }
        });
    }
}
