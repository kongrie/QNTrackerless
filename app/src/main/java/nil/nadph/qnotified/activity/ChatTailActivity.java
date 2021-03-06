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
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.view.ViewCompat;

import com.tencent.mobileqq.widget.BounceScrollView;

import java.text.SimpleDateFormat;
import java.util.Date;

import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.dialog.RikkaCustomMsgTimeFormatDialog;
import nil.nadph.qnotified.hook.ChatTailHook;
import nil.nadph.qnotified.hook.FakeBatteryHook;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTION_CHAT_TAIL_FRIENDS_ACTIVITY;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTION_CHAT_TAIL_TROOPS_ACTIVITY;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class ChatTailActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private static final int R_ID_APPLY = 0x300AFF81;
    private static final int R_ID_DISABLE = 0x300AFF82;
    private static final int R_ID_PERCENT_VALUE = 0x300AFF83;
    private static final int R_ID_REGEX_VALUE = 0x300AFF84;
    public static final String delimiter = "#msg#";
    private static int battery = 0;
    private static String power = "?????????";

    TextView tvStatus;

    private final boolean mMsfResponsive = false;
    private TextView __tv_chat_tail_groups, __tv_chat_tail_friends, __tv_chat_tail_time_format;

    public static int getBattery() {
        return battery;
    }

    public static String getPower() {
        if (FakeBatteryHook.get().isEnabled()) {
            return FakeBatteryHook.get().isFakeBatteryCharging() ? "?????????" : "?????????";
        }
        return power;
    }

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        if (!FakeBatteryHook.get().isEnabled()) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            registerReceiver(new BatteryReceiver(), filter);//??????BroadcastReceiver
        }
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(ChatTailActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        //LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(ChatTailActivity.this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(ChatTailActivity.this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(ChatTailActivity.this, "?????????????????????????????????????????????"));
        ChatTailHook ct = ChatTailHook.get();
        boolean enabled = ct.isEnabled();
        RelativeLayout _s;
        LinearLayout _t;
        ll.addView(_t = subtitle(ChatTailActivity.this, ""));
        tvStatus = (TextView) _t.getChildAt(0);
        ll.addView(subtitle(ChatTailActivity.this, "???????????????????????????????????????\\n"));

        ll.addView(_s = newListItemButton(this, "??????????????????", "????????????????????????????????????", "N/A", clickToProxyActAction(ACTION_CHAT_TAIL_TROOPS_ACTIVITY)));
        __tv_chat_tail_groups = _s.findViewById(R_ID_VALUE);
        ll.addView(_s = newListItemButton(this, "?????????????????????", "???????????????????????????????????????", "N/A", clickToProxyActAction(ACTION_CHAT_TAIL_FRIENDS_ACTIVITY)));
        __tv_chat_tail_friends = _s.findViewById(R_ID_VALUE);
        ll.addView(_s = newListItemButton(this, "??????????????????", "??????QN?????????Q???\"??????????????????????????????\"?????????", RikkaCustomMsgTimeFormatDialog.getTimeFormat(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToastShort(ChatTailActivity.this, "??????QN?????????Q???\"??????????????????????????????\"?????????");
            }
        }));
        __tv_chat_tail_time_format = _s.findViewById(R_ID_VALUE);
        ll.addView(subtitle(ChatTailActivity.this, "???????????????"));
        ll.addView(subtitle(ChatTailActivity.this, "????????????(??????????????????): "));
        LinearLayout _a, _b, _c, _d, _e, _f, _g;
        ll.addView(_a = subtitle(ChatTailActivity.this, delimiter + "         : ????????????"));
        ll.addView(_b = subtitle(ChatTailActivity.this, "#model#   : ????????????"));
        ll.addView(_c = subtitle(ChatTailActivity.this, "#brand#   : ????????????"));
        ll.addView(_d = subtitle(ChatTailActivity.this, "#battery# : ????????????"));
        ll.addView(_e = subtitle(ChatTailActivity.this, "#power#   : ??????????????????"));
        ll.addView(_f = subtitle(ChatTailActivity.this, "#time#    : ????????????"));
        ll.addView(_g = subtitle(ChatTailActivity.this, "\\n       : ??????"));
        int _5dp = dip2px(ChatTailActivity.this, 5);
        EditText pct = createEditText(R_ID_PERCENT_VALUE, _5dp,
                ct.getTailCapacity().replace("\n", "\\n"),
                ChatTailActivity.delimiter + " ????????????????????????");
        _a.setOnClickListener(v -> pct.setText(pct.getText() + delimiter));
        _b.setOnClickListener(v -> pct.setText(pct.getText() + "#model#"));
        _c.setOnClickListener(v -> pct.setText(pct.getText() + "#brand#"));
        _d.setOnClickListener(v -> pct.setText(pct.getText() + "#battery#"));
        _e.setOnClickListener(v -> pct.setText(pct.getText() + "#power#"));
        _f.setOnClickListener(v -> pct.setText(pct.getText() + "#time#"));
        _g.setOnClickListener(v -> pct.setText(pct.getText() + "\\n"));
        ll.addView(pct, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        ll.addView(newListItemSwitchFriendConfigNext(this, "????????????",
                "???????????????????????????????????????????????????(????????????" + Utils.getHostAppName() + ")",
                ConfigItems.qn_chat_tail_regex, false));
        ll.addView(createEditText(R_ID_REGEX_VALUE, _5dp, ChatTailHook.getTailRegex(),
                "????????????????????????????????????(????????????)"),
                newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT,
                        2 * _5dp, _5dp, 2 * _5dp, _5dp));
        ll.addView(newListItemSwitchFriendConfigNext(this, "????????????", "???????????????????????????(????????????" + Utils.getHostAppName() + ")", ConfigItems.qn_chat_tail_global, false));
        Button apply = new Button(ChatTailActivity.this);
        apply.setId(R_ID_APPLY);
        apply.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(apply);
        ll.addView(apply, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button dis = new Button(ChatTailActivity.this);
        dis.setId(R_ID_DISABLE);
        dis.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(dis);
        dis.setText("??????");
        ll.addView(dis, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        showStatus();
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("???????????????");
        return true;
    }

    private EditText createEditText(int id, int _5dp, String text, String hint) {
        EditText pct = new EditText(ChatTailActivity.this);
        pct.setId(id);
        pct.setInputType(TYPE_CLASS_TEXT);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(ChatTailActivity.this, 18));
        //pct.setBackgroundDrawable(null);
        ViewCompat.setBackground(pct,null);
        pct.setGravity(Gravity.CENTER);
        pct.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        //pct.setBackgroundDrawable(new HighContrastBorder());
        ViewCompat.setBackground(pct,new HighContrastBorder());
        pct.setHint(hint);
        pct.setText(text);
        pct.setSelection(pct.getText().length());
        //if (pct.getText() == null) pct.setText("");
        //??????????????????????????????????????????????????????????????????????????????Android?????????????????????????????????
        return pct;
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_BATTERY_CHANGED: {
                    int current = intent.getExtras().getInt("level");//??????????????????
                    int total = intent.getExtras().getInt("scale");//???????????????
                    int percent = current * 100 / total;
                    ChatTailActivity.battery = percent;
                }
                case Intent.ACTION_POWER_DISCONNECTED: {
                    ChatTailActivity.power = "?????????";
                }
                case Intent.ACTION_POWER_CONNECTED: {
                    ChatTailActivity.power = "?????????";
                }
            }

        }
    }

    @Override
    public void doOnResume() {
        super.doOnResume();
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        String str = cfg.getString(ConfigItems.qn_chat_tail_troops);
        int n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_chat_tail_groups.setText(n + "??????");
        str = cfg.getString(ConfigItems.qn_chat_tail_friends);
        n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_chat_tail_friends.setText(n + "?????????");
    }

    private void showStatus() {
        ChatTailHook ct = ChatTailHook.get();
        boolean enabled = ct.isEnabled();
        String desc = "????????????: ";
        if (enabled) {
            if (!ChatTailHook.isRegex() || !ChatTailHook.isPassRegex("????????????"))
                desc += "?????????: \n" + ct.getTailCapacity()
                        .replace(ChatTailActivity.delimiter, "????????????")
                        .replace("#model#", Build.MODEL)
                        .replace("#brand#", Build.BRAND)
                        .replace("#battery#", battery + "")
                        .replace("#power#", ChatTailActivity.getPower())
                        .replace("#time#", new SimpleDateFormat(RikkaCustomMsgTimeFormatDialog.getTimeFormat()).format(new Date()));
            else
                desc += "?????????: \n????????????";
        } else {
            desc += "??????";
        }
        tvStatus.setText(desc);
        Button apply, disable;
        apply = ChatTailActivity.this.findViewById(R_ID_APPLY);
        disable = ChatTailActivity.this.findViewById(R_ID_DISABLE);
        if (!enabled) {
            apply.setText("???????????????");
        } else {
            apply.setText("??????");
        }
        if (!enabled) {
            disable.setVisibility(View.GONE);
        } else {
            disable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        switch (v.getId()) {
            case R_ID_APPLY:
                //if (mMsfResponsive) {
                doUpdateTailCfg();
                logi("isRegex:" + ChatTailHook.isRegex());
                logi("isPassRegex:" + ChatTailHook.isPassRegex("????????????"));
                logi("getTailRegex:" + ChatTailHook.getTailRegex());
               /* } else {
                    final Dialog waitDialog = CustomDialog.create(this).setCancelable(true).setTitle("?????????")
                            .setMessage("?????? :MSF ????????????").show();
                    SyncUtils.enumerateProc(this, SyncUtils.PROC_MSF, 3000, new SyncUtils.EnumCallback() {
                        private boolean mFinished = false;

                        @Override
                        public void onResponse(SyncUtils.EnumRequestHolder holder, SyncUtils.ProcessInfo process) {
                            if (mFinished) return;
                            mFinished = true;
                            mMsfResponsive = true;
                            waitDialog.dismiss();
                            doUpdateTailCfg();
                        }

                        @Override
                        public void onEnumResult(SyncUtils.EnumRequestHolder holder) {
                            if (mFinished) return;
                            mFinished = true;
                            mMsfResponsive = holder.result.size() > 0;
                            waitDialog.dismiss();
                            if (mMsfResponsive) {
                                doUpdateTailCfg();
                            } else {
                                CustomDialog.create(ChatTailActivity.this).setTitle("????????????")
                                        .setCancelable(true).setPositiveButton("??????", null)
                                        .setMessage("????????????:\n" + getApplication().getPackageName() + ":MSF ??????????????????\n" +
                                                "????????????QQ????????????,????????????????????????????????????\n" +
                                                "??????????????????(?????????)??????,???????????????????????????????????? ??????-6.0.2(1907) ,??????????????????,???????????????").show();
                            }
                        }
                    });
                }

                */
                break;
            case R_ID_DISABLE:
                cfg.putBoolean(ChatTailHook.qn_chat_tail_enable, false);
                try {
                    cfg.save();
                } catch (Exception e) {
                    Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "??????:" + e.toString(), Toast.LENGTH_LONG);
                    log(e);
                }
                showStatus();
        }
    }

    private void doUpdateTailCfg() {
        ChatTailHook ct = ChatTailHook.get();
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        EditText pct;
        pct = ChatTailActivity.this.findViewById(R_ID_PERCENT_VALUE);
        String val = pct.getText().toString();
        if (Utils.isNullOrEmpty(val)) {
            Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "??????????????????", Toast.LENGTH_SHORT);
            return;
        }
        if (!val.contains(ChatTailActivity.delimiter)) {
            Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "????????????????????????" + ChatTailActivity.delimiter + "", Toast.LENGTH_SHORT);
            return;
        }
        ct.setTail(val);
        val = ((EditText) ChatTailActivity.this.findViewById(R_ID_REGEX_VALUE)).getText().toString();
        if (!Utils.isNullOrEmpty(val)) {
            ChatTailHook.setTailRegex(val);
        }
        if (!ct.isEnabled()) {
            cfg.putBoolean(ChatTailHook.qn_chat_tail_enable, true);
            try {
                cfg.save();
                //  boolean success = true;
                // if (!ct.isInited()) success = ct.init();
                //SyncUtils.requestInitHook(ct.getId(), ct.getEffectiveProc());
                //  if (!success)
                //   Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "???????????????: ????????????????????????", Toast.LENGTH_SHORT);
            } catch (Exception e) {
                Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "??????:" + e.toString(), Toast.LENGTH_LONG);
                log(e);
            }
        }
        showStatus();
    }

}
