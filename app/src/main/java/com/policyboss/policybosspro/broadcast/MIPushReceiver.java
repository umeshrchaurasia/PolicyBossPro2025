package com.policyboss.policybosspro.broadcast;

import android.content.Context;

import com.webengage.sdk.android.PushUtils;
import com.webengage.sdk.android.WebEngage;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MIPushReceiver extends PushMessageReceiver {
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        super.onReceivePassThroughMessage(context, miPushMessage);
        String content = miPushMessage.getContent();
        WebEngage.get().receive(PushUtils.prepareMap(content));
    }


    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        super.onCommandResult(context, message);
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                String region = MiPushClient.getAppRegion(context);
                WebEngage.get().setXiaomiRegistrationID(MiPushClient.getRegId(context), region);
            }
        }
    }

}
