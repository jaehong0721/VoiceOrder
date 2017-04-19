package com.rena21c.voiceorder.util;


import android.content.Context;
import android.content.Intent;

import com.rena21c.voiceorder.R;

public class LauncherUtil {
    public static void addLauncherIconToHomeScreen(Context context, Class cls) {
        Intent shortcutIntent = new Intent(context, cls);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        context.sendBroadcast(addIntent);
    }

}
