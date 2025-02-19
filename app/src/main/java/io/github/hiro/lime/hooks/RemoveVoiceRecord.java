package io.github.hiro.lime.hooks;

import android.content.Context;
import android.content.pm.PackageManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.hiro.lime.LimeOptions;
public class RemoveVoiceRecord implements IHook {

    @Override
    public void hook(LimeOptions limeOptions, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!limeOptions.RemoveVoiceRecord.checked) return;

        XposedBridge.hookAllMethods(
                loadPackageParam.classLoader.loadClass(Constants.RemoveVoiceRecord_Hook_a.className),
                "run",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);

                    }
                }
        );

    }
}