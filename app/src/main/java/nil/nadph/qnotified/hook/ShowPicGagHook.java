/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.hook;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator._TroopPicEffectsController;
import static nil.nadph.qnotified.util.Utils.*;

public class ShowPicGagHook extends BaseDelayableHook {

    public static final String qn_gag_show_pic = "qn_gag_show_pic";
    private static final ShowPicGagHook self = new ShowPicGagHook();
    private boolean inited = false;

    private ShowPicGagHook() {
    }

    public static ShowPicGagHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method showPicEffect = null;
            for (Method m : _TroopPicEffectsController().getDeclaredMethods()) {
                Class[] argt = m.getParameterTypes();
                if (argt.length > 2 && argt[1].equals(Bitmap.class)) {
                    showPicEffect = m;
                    break;
                }
            }
            XposedBridge.hookMethod(showPicEffect, new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    param.setResult(null);
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isValid() {
        Application app = getApplication();
        return app == null || !isTim(app);
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_gag_show_pic, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_gag_show_pic);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
