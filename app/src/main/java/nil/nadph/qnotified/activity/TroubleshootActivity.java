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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tencent.mobileqq.widget.BounceScrollView;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import me.singleneuron.activity.BugReportActivity;
import me.singleneuron.data.CardMsgCheckResult;
import me.singleneuron.hook.DebugDump;
import me.singleneuron.util.KotlinUtilsKt;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.config.EventRecord;
import nil.nadph.qnotified.config.FriendRecord;
import nil.nadph.qnotified.remote.GetUserStatusResp;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTION_EXFRIEND_LIST;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTIVITY_PROXY_ACTION;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class TroubleshootActivity extends IphoneTitleBarActivityCompat {
    @Override
    public boolean doOnCreate(Bundle savedInstanceState) {
        super.doOnCreate(savedInstanceState);
        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;
        ll.addView(subtitle(this, "?????????????????????????????????bug?????????[????????????]?????????????????????"));
        ll.addView(newListItemButton(this, "????????????", "?????????????????????????????????????????????", null, clickToCleanCache()));
        ll.addView(subtitle(this, "???????????????(?????????)"));
        ll.addView(newListItemButton(this, "??????????????????", "???????????????????????????", null, clickToReset()));
        ll.addView(newListItemButton(this, "??????[?????????]???????????????", "????????????????????????????????????[?????????]?????????????????????", null, clickToWipeDeletedFriends()));
        ll.addView(newListItemButton(this, "???????????????????????????", "????????????????????????????????????????????????", null, clickToWipeAllFriends()));
        ll.addView(subtitle(this, ""));
        ll.addView(subtitle(this, "??????"));
        ll.addView(newListItemButton(this, "??????BUG??????", null, null, clickToProxyActAction(BugReportActivity.class)));
        ll.addView(subtitle(this, ""));
        ll.addView(subtitle(this, "???????????????????????????????????????????????????????????????????????????"));
        ll.addView(subtitle(this, "??????"));
        ll.addView(newListItemHookSwitchInit(this, "????????????", "????????????", DebugDump.INSTANCE));
        ll.addView(newListItemButton(this, "????????????????????????????????????", null, null, new View.OnClickListener() {
            final String LAST_TRACE_HASHCODE_CONFIG = "lastTraceHashcode";
            final String LAST_TRACE_DATA_CONFIG = "lastTraceDate";
            @Override
            public void onClick(View v) {
                try {
                    ConfigManager configManager = ConfigManager.getDefaultConfig();
                    configManager.remove(LAST_TRACE_DATA_CONFIG);
                    configManager.remove(LAST_TRACE_HASHCODE_CONFIG);
                    configManager.save();
                } catch (Exception e) {
                    Utils.runOnUiThread(() -> Toast.makeText(Utils.getApplication(),e.toString(),Toast.LENGTH_LONG).show());
                    Utils.log(e);
                }
            }
        }));
        ll.addView(newListItemButton(this, "?????????????????????", null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KotlinUtilsKt.checkCardMsg("");
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TroubleshootActivity.this, R.style.MaterialDialog);
                EditText editText = new EditText(TroubleshootActivity.this);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.setView(editText)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String msg = editText.getText().toString();
                                CardMsgCheckResult result = KotlinUtilsKt.checkCardMsg(msg);
                                Toast.makeText(TroubleshootActivity.this,result.toString(),Toast.LENGTH_LONG).show();
                            }
                        }).create().show();
            }
        }));
        ll.addView(newListItemButton(this, "??????X5????????????", "???????????????????????????", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Class<?> browser = Class.forName("com.tencent.mobileqq.activity.QQBrowserDelegationActivity");
                    Intent intent = new Intent(TroubleshootActivity.this, browser);
                    intent.putExtra("fling_action_key", 2);
                    intent.putExtra("fling_code_key", TroubleshootActivity.this.hashCode());
                    intent.putExtra("useDefBackText", true);
                    intent.putExtra("param_force_internal_browser", true);
                    intent.putExtra("url", "http://debugx5.qq.com/");
                    startActivity(intent);
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "?????? Looper", "????????????", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quitLooper();
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "abort()", "????????????", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long libc = Natives.dlopen("libc.so", Natives.RTLD_NOLOAD);
                    if (libc == 0) {
                        throw new RuntimeException("dlopen libc.so failed");
                    }
                    long abort = Natives.dlsym(libc, "abort");
                    if (abort == 0) {
                        String msg = Natives.dlerror();
                        if (msg != null) {
                            throw new RuntimeException(msg);
                        } else {
                            throw new RuntimeException("dlsym 'abort' failed");
                        }
                    }
                    Natives.call(abort);
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "((void(*)())0)();", "???????????????, ????????????", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Natives.load(TroubleshootActivity.this);
                    Natives.call(0L);
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "*((int*)0)=0;", "???????????????, ????????????", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Natives.load(TroubleshootActivity.this);
                    Natives.memset(0, 0, 1);
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "????????????", "??????????????????", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent inner = new Intent(getApplication(), ExfriendListActivity.class);
                    inner.putExtra(ACTIVITY_PROXY_ACTION, ACTION_EXFRIEND_LIST);
                    Intent wrapper = new Intent();
                    wrapper.setClassName(getApplication().getPackageName(), ActProxyMgr.STUB_DEFAULT_ACTIVITY);
                    wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, inner);
                    PendingIntent pi = PendingIntent.getActivity(getApplication(), 0, wrapper, 0);
                    NotificationManager nm = (NotificationManager) Utils.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification n = ExfriendManager.getCurrent().createNotiComp(nm, "Ticker", "Title", "Content", new long[]{100, 200, 200, 100}, pi);
                    nm.notify(ExfriendManager.ID_EX_NOTIFY, n);
                } catch (Throwable e) {
                    CustomDialog.createFailsafe(TroubleshootActivity.this).setCancelable(true).setPositiveButton(getString(android.R.string.ok), null)
                            .setTitle(getShort$Name(e)).setMessage(Log.getStackTraceString(e)).show();
                }
            }
        }));
        ll.addView(subtitle(this, ""));

        ll.addView(subtitle(this, "???????????????"));
        for (int i = 1; i <= DexKit.DEOBF_NUM_C; i++) {
            try {
                String tag = DexKit.a(i);
                String orig = DexKit.c(i);
                if (orig == null) continue;
                orig = orig.replace("/", ".");
                String shortName = Utils.getShort$Name(orig);
                String currName = "(void*)0";
                DexMethodDescriptor md = DexKit.getMethodDescFromCache(i);
                if (md != null) {
                    currName = md.toString();
                } else {
                    Class<?> c = DexKit.loadClassFromCache(i);
                    if (c != null) currName = c.getName();
                }
                ll.addView(subtitle(this, "  [" + i + "]" + shortName + "\n" + orig + "\n= " + currName));
            } catch (Throwable e) {
                ll.addView(subtitle(this, "  [" + i + "]" + e.toString()));
            }
        }
        for (int ii = 1; ii <= DexKit.DEOBF_NUM_N; ii++) {
            int i = 20000 + ii;
            try {
                String tag = DexKit.a(i);
                String orig = DexKit.c(i);
                if (orig == null) continue;
                orig = orig.replace("/", ".");
                String shortName = Utils.getShort$Name(orig);
                String currName = "(void*)0";
                DexMethodDescriptor md = DexKit.getMethodDescFromCache(i);
                if (md != null) {
                    currName = md.toString();
                } else {
                    Class<?> c = DexKit.loadClassFromCache(i);
                    if (c != null) currName = c.getName();
                }
                ll.addView(subtitle(this, "  [" + i + "]" + shortName + "\n" + orig + "\n= " + currName));
            } catch (Throwable e) {
                ll.addView(subtitle(this, "  [" + i + "]" + e.toString()));
            }
        }

        ll.addView(subtitle(this, "SystemClassLoader\n" + ClassLoader.getSystemClassLoader()
                + "\nContext.getClassLoader()\n" + this.getClassLoader()
                + "\nThread.getContextClassLoader()\n" + Thread.currentThread().getContextClassLoader()
                + "\nInitiator.getHostClassLoader()\n" + Initiator.getHostClassLoader()));
        long ts = Utils.getBuildTimestamp();
        ll.addView(subtitle(this, "Build Time: " + (ts > 0 ? new Date(ts).toString() : "unknown")));
        String info;
        try {
            Natives.load(this);
            info = "pagesize=" + Natives.getpagesize() + ", sizeof(void*)=" + Natives.sizeofptr() + ", addr="
                    + Long.toHexString(Natives.dlopen("libnatives.so", Natives.RTLD_NOLOAD));
        } catch (Throwable e3) {
            log(e3);
            info = e3.toString();
        }
        ll.addView(subtitle(this, info));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setTitle("????????????");
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }

    public View.OnClickListener clickToWipeDeletedFriends() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ExfriendManager exm = ExfriendManager.getCurrent();
                            Iterator it = exm.getEvents().entrySet().iterator();
                            while (it.hasNext()) {
                                EventRecord ev = (EventRecord) ((Map.Entry) it.next()).getValue();
                                if (exm.getPersons().get(ev.operand).friendStatus == FriendRecord.STATUS_FRIEND_MUTUAL)
                                    it.remove();
                            }
                            exm.saveConfigure();
                            showToast(TroubleshootActivity.this, TOAST_TYPE_SUCCESS, "????????????", Toast.LENGTH_SHORT);
                        } catch (Throwable e) {
                        }
                    }
                });
                dialog.setNegativeButton("??????", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("??????????????????????????????(" + getLongAccountUin() + ")?????? ????????? ?????????????????????(?????????????????????).?????????bug?????????????????????????????????,????????????????????????,????????????????????????.\n?????????????????????");
                dialog.setTitle("????????????");
                dialog.show();
            }
        };
    }

    public static void quitLooper() throws Exception {
        Looper looper = Looper.getMainLooper();
        MessageQueue queue = (MessageQueue) iget_object_or_null(looper, "mQueue");
        iput_object(queue, "mQuitAllowed", true);
        looper.quit();
    }

    public View.OnClickListener clickToWipeAllFriends() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ExfriendManager exm = ExfriendManager.getCurrent();
                            exm.getConfig().getFile().delete();
                            exm.getConfig().reinit();
                            exm.reinit();
                            showToast(TroubleshootActivity.this, TOAST_TYPE_SUCCESS, "????????????", Toast.LENGTH_SHORT);
                        } catch (Throwable e) {
                        }
                    }
                });
                dialog.setNegativeButton("??????", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("??????????????????????????????(" + getLongAccountUin() + ")?????? ?????? ?????????????????????,?????????????????????????????????.????????????????????????????????????bug??????????????????,?????????????????????????????????????????? ????????????????????????????????? .\n?????????????????????");
                dialog.setTitle("????????????");
                dialog.show();
            }
        };
    }

    public View.OnClickListener clickToCleanCache() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ConfigManager cfg = ConfigManager.getCache();
                            cfg.getAllConfig().clear();
                            cfg.getFile().delete();
                            System.exit(0);
                        } catch (Throwable e) {
                            log(e);
                        }
                    }
                });
                dialog.setNegativeButton("??????", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("??????????????????,????????????????????????????\n????????????????????????3??????????????????" + Utils.getHostAppName() + ".");
                dialog.setTitle("????????????");
                dialog.show();
            }
        };
    }

    public View.OnClickListener clickToReset() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ConfigManager cfg = ConfigManager.getDefaultConfig();
                            cfg.getAllConfig().clear();
                            cfg.getFile().delete();
                            System.exit(0);
                        } catch (Throwable e) {
                            log(e);
                        }
                    }
                });
                dialog.setNegativeButton("??????", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("????????????????????????????????????????????????,??????????????????????????????,??????????????????????????????.????????????????????????3??????????????????" + Utils.getHostAppName() + ".\n?????????????????????");
                dialog.setTitle("????????????");
                dialog.show();
            }
        };
    }

}
