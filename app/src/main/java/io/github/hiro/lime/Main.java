package io.github.hiro.lime;

import android.content.res.XModuleResources;


import androidx.annotation.NonNull;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.hiro.lime.hooks.AddRegistrationOptions;
import io.github.hiro.lime.hooks.AutomaticBackup;
import io.github.hiro.lime.hooks.BlockTracking;

import io.github.hiro.lime.hooks.CheckHookTargetVersion;
import io.github.hiro.lime.hooks.Constants;
import io.github.hiro.lime.hooks.CrashGuard;
import io.github.hiro.lime.hooks.EmbedOptions;
import io.github.hiro.lime.hooks.IHook;

import io.github.hiro.lime.hooks.KeepUnread;
import io.github.hiro.lime.hooks.KeepUnreadLSpatch;
import io.github.hiro.lime.hooks.ModifyRequest;
import io.github.hiro.lime.hooks.ModifyResponse;
import io.github.hiro.lime.hooks.OutputRequest;
import io.github.hiro.lime.hooks.OutputResponse;
import io.github.hiro.lime.hooks.PreventMarkAsRead;
import io.github.hiro.lime.hooks.PreventUnsendMessage;
import io.github.hiro.lime.hooks.RedirectWebView;
import io.github.hiro.lime.hooks.RemoveAds;
import io.github.hiro.lime.hooks.RemoveFlexibleContents;
import io.github.hiro.lime.hooks.RemoveIconLabels;
import io.github.hiro.lime.hooks.RemoveIcons;
import io.github.hiro.lime.hooks.RemoveNotification;
import io.github.hiro.lime.hooks.RemoveReplyMute;
import io.github.hiro.lime.hooks.Ringtone;
import io.github.hiro.lime.hooks.SendMuteMessage;
import io.github.hiro.lime.hooks.SpoofAndroidId;
import io.github.hiro.lime.hooks.SpoofUserAgent;
import io.github.hiro.lime.hooks.Test;
import io.github.hiro.lime.hooks.UnsentRec;
import io.github.hiro.lime.hooks.Archived;
import io.github.hiro.lime.hooks.ReadChecker;
import io.github.hiro.lime.hooks.NaviColor;


public class Main implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {
    public static String modulePath;

    public static XSharedPreferences xModulePrefs;
    public static XSharedPreferences xPackagePrefs;
    public static XSharedPreferences xPrefs;
    public static LimeOptions limeOptions = new LimeOptions();

    static final IHook[] hooks = new IHook[]{
           new CrashGuard(),
            new OutputResponse(),
            new ModifyRequest(),
            new CheckHookTargetVersion(),
            new SpoofAndroidId(),
            new SpoofUserAgent(),
            new AddRegistrationOptions(),
            new EmbedOptions(),
            new RemoveIcons(),
            new RemoveIconLabels(),
            new RemoveAds(),
            new RemoveFlexibleContents(),
            new RemoveReplyMute(),
            new RedirectWebView(),
            new PreventMarkAsRead(),
            new PreventUnsendMessage(),
            new SendMuteMessage(),
            new KeepUnread(),
            new BlockTracking(),
            new ModifyResponse(),
            new OutputRequest(),
            new Archived(),
            new UnsentRec(),
            new Ringtone(),
            new ReadChecker(),
            new NaviColor(),
            new KeepUnreadLSpatch(),
            new AutomaticBackup(),
            new RemoveNotification(),
    };

    public void handleLoadPackage(@NonNull XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals(Constants.PACKAGE_NAME)) return;

        xModulePrefs = new XSharedPreferences(Constants.MODULE_NAME, "options");
        xPackagePrefs = new XSharedPreferences(Constants.PACKAGE_NAME, Constants.MODULE_NAME + "-options");
        if (xModulePrefs.getBoolean("unembed_options", false)) {
            xPrefs = xModulePrefs;
        } else {
            xPrefs = xPackagePrefs;
        }
        for (LimeOptions.Option option : limeOptions.options) {
            option.checked = xPrefs.getBoolean(option.name, option.checked);
        }

        for (IHook hook : hooks) {
            hook.hook(limeOptions, loadPackageParam);
        }
    }

    @Override
    public void handleInitPackageResources(@NonNull XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(Constants.PACKAGE_NAME))
            return;

        XModuleResources xModuleResources = XModuleResources.createInstance(modulePath, resparam.res);

/*
        resparam.res.setReplacement(Constants.PACKAGE_NAME, "color", "setting_background", Color.parseColor("#000000")); // 背景デフォルトを白色に設定
        XposedBridge.log("Replacing color 'background_default' with #FFFFFF");

        resparam.res.setReplacement(Constants.PACKAGE_NAME, "color", "background_material_light", Color.parseColor("#000000")); // 背景ライトを白色に設定
        XposedBridge.log("Replacing color 'background_material_light' with #FFFFFF");
*/



        if (limeOptions.removeIconLabels.checked) {
            resparam.res.setReplacement(Constants.PACKAGE_NAME, "dimen", "main_bnb_button_height", xModuleResources.fwd(R.dimen.main_bnb_button_height));
            resparam.res.setReplacement(Constants.PACKAGE_NAME, "dimen", "main_bnb_button_width", xModuleResources.fwd(R.dimen.main_bnb_button_width));
            resparam.res.hookLayout(Constants.PACKAGE_NAME, "layout", "app_main_bottom_navigation_bar_button", new XC_LayoutInflated() {
                @Override
                public void handleLayoutInflated(XC_LayoutInflated.LayoutInflatedParam liparam) throws Throwable {
                    liparam.view.setTranslationY(xModuleResources.getDimensionPixelSize(R.dimen.gnav_icon_offset));
                }
            });
        }

        if (limeOptions.removeServiceLabels.checked) {
            resparam.res.setReplacement(Constants.PACKAGE_NAME, "dimen", "home_tab_v3_service_icon_size", xModuleResources.fwd(R.dimen.home_tab_v3_service_icon_size));
        }
    }



    @Override
    public void initZygote(@NonNull StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
    }
}
