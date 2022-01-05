package com.tapsdk.suite.component;

import com.tapsdk.suite.Action;
import com.tapsdk.suite.constants.Constants;
import com.tapsdk.tapsdk_suite.R;

class TapChatComponent extends TapComponent {
    public TapChatComponent() {
        super(Constants.TapComponentType.CHAT, Constants.TapComponentDefaultName.CHAT
                , R.drawable.tapsdk_suite_ic_chat_default, "", new Action() {
                    @Override
                    public void invoke(Object... args) {
                        // to implement
                    }
                });
    }
}
