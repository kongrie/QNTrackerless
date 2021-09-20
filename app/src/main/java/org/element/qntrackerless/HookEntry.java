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
package org.element.qntrackerless;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nil.nadph.qnotified.StartupHook;
import nil.nadph.qnotified.util.Utils;

public class HookEntry implements IXposedHookLoadPackage {
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
    public static final String PACKAGE_NAME_SELF = "org.element.qntrackerless";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        switch (lpparam.packageName) {
            case PACKAGE_NAME_SELF:
                XposedHelpers.findAndHookMethod("nil.nadph.qnotified.util.Utils", lpparam.classLoader, "getActiveModuleVersion", XC_MethodReplacement.returnConstant(Utils.QN_VERSION_NAME));
                break;
            case PACKAGE_NAME_QQ:
            case PACKAGE_NAME_TIM:
                StartupHook.getInstance().doInit(lpparam.classLoader);
                break;
        }
    }
}
