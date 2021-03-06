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
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.view.ViewCompat;

import com.tencent.mobileqq.widget.BounceScrollView;

import java.text.ParseException;
import java.util.ArrayList;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.hook.JumpController;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.UiThread;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Utils.dip2px;

@SuppressLint("Registered")
public class JefsRulesActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private EditText rulesEt;
    private TextView rulesTv;
    private LinearLayout layoutDisplay;
    private LinearLayout layoutEdit;
    private final JumpController jmpctl = JumpController.get();
    private boolean currEditMode;

    @Override
    public boolean doOnCreate(Bundle savedState) {
        super.doOnCreate(savedState);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setId(R.id.rootMainLayout);
        bounceScrollView.addView(mainLayout, MATCH_PARENT, WRAP_CONTENT);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout _tmp = ViewBuilder.newListItemHookSwitchInit(this, "?????????", "??????????????????????????????", JumpController.get());
        mainLayout.addView(_tmp, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0));

        int __10 = dip2px(this, 10);
        int __5 = dip2px(this, 5);

        TextView _tv_rules = new TextView(this);
        _tv_rules.setTextSize(16);
        _tv_rules.setText("??????:");
        _tv_rules.setTextColor(ResUtils.skin_black);
        mainLayout.addView(_tv_rules, ViewBuilder.newLinearLayoutParams(WRAP_CONTENT, WRAP_CONTENT, __10));

        {
            layoutDisplay = new LinearLayout(this);
            layoutDisplay.setOrientation(LinearLayout.VERTICAL);
            layoutDisplay.setId(R.id.jefsRulesDisplayLayout);
            {
                String appLabel = Utils.getHostAppName();
                TextView _tmp_1 = new TextView(this);
                _tmp_1.setTextColor(ResUtils.skin_gray3);
                _tmp_1.setText("?????????????????????????????? \"????????????" + appLabel + " ??????????????????\" ?????????, " +
                        "???????????????????????????" + appLabel + "????????????????????????APP????????????????????????(???????????????)");
                layoutDisplay.addView(_tmp_1, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __5));
            }
            rulesTv = new TextView(this);
            rulesTv.setTextSize(14);
            rulesTv.setPadding(__5, __5, __5, __5);
            rulesTv.setHorizontallyScrolling(true);
            rulesTv.setTextColor(ResUtils.skin_black);
            layoutDisplay.addView(rulesTv, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __5));

            Button editBtn = new Button(this);
            editBtn.setId(R.id.jefsRulesEditButton);
            editBtn.setOnClickListener(this);
            editBtn.setText("????????????");
            ResUtils.applyStyleCommonBtnBlue(editBtn);
            layoutDisplay.addView(editBtn, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __10));

            mainLayout.addView(layoutDisplay, MATCH_PARENT, WRAP_CONTENT);
        }
        {
            layoutEdit = new LinearLayout(this);
            layoutEdit.setOrientation(LinearLayout.VERTICAL);
            layoutEdit.setId(R.id.jefsRulesEditLayout);
            {
                TextView _tmp_1 = new TextView(this);
                _tmp_1.setTextColor(ResUtils.skin_gray3);
                _tmp_1.setText("???????????????????????????????????????\n" +
                        "??????????????? * ??? **\n" +
                        "????????????: ??????,?????????;\n????????????????????????, ??????????????????, \n???: ??????,?????????1,?????????2,?????????3;\n" +
                        "?????????A/D/Q??????: ??????(A), ??????(D), ????????????(Q), ????????????3???(P/C/A)\n" +
                        "?????????(P): ???  A,P:com.tencent.mm;    ?????????????????????\n" +
                        "?????????(C): ???  D,C:de.robv.android.xposed.installer/.WelcomeActivity;  ???????????????Xposed????????????\n" +
                        "?????????(A): ???  Q,A:android.intent.action.DIAL;    ?????????????????????????????????\n" +
                        "???????????????: aa.bbb.*.dddd ???????????? aa.bbb.ccccc.dddd ???????????? aa.bbb.cc.ee.dddd\n" +
                        " aa.bbb.**.dddd ???????????? aa.bbb.cc.dddd ??? aa.bbb.cc.ee.dddd ???????????? aa.bbb.dddd");
                layoutEdit.addView(_tmp_1, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __5));
            }
            rulesEt = new EditText(this);
            rulesEt.setHorizontallyScrolling(true);
            rulesEt.setTextSize(16);
            rulesEt.setPadding(__5, __5, __5, __5);
            //rulesEt.setBackgroundDrawable(new HighContrastBorder());
            ViewCompat.setBackground(rulesEt,new HighContrastBorder());
            rulesEt.setTextColor(ResUtils.skin_black);
            rulesEt.setTypeface(Typeface.MONOSPACE);
            layoutEdit.addView(rulesEt, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __10));

            Button saveBtn = new Button(this);
            saveBtn.setId(R.id.jefsRulesSaveButton);
            saveBtn.setOnClickListener(this);
            saveBtn.setText("??????");
            ResUtils.applyStyleCommonBtnBlue(saveBtn);
            layoutEdit.addView(saveBtn, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __10));

            {
                RelativeLayout _rl_tmp = new RelativeLayout(this);

                TextView cancelBtn = new TextView(this);
                cancelBtn.setTextColor(ResUtils.skin_black);
                cancelBtn.setTextSize(16);
                cancelBtn.setId(R.id.jefsRulesCancelButton);
                cancelBtn.setGravity(Gravity.CENTER);
                cancelBtn.setPadding(__10, __5, __10, __10 / 2);
                cancelBtn.setText("??????");
                cancelBtn.setTextColor(ResUtils.skin_blue);
                cancelBtn.setOnClickListener(this);

                TextView resetBtn = new TextView(this);
                resetBtn.setTextColor(ResUtils.skin_black);
                resetBtn.setTextSize(16);
                resetBtn.setId(R.id.jefsRulesResetButton);
                resetBtn.setGravity(Gravity.CENTER);
                resetBtn.setPadding(__10, __5, __10, __10 / 2);
                resetBtn.setText("??????????????????");
                resetBtn.setTextColor(ResUtils.skin_blue);
                resetBtn.setOnClickListener(this);

                RelativeLayout.LayoutParams _rlp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                _rlp_l.leftMargin = __10;
                _rlp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                _rl_tmp.addView(resetBtn, _rlp_l);

                RelativeLayout.LayoutParams _rlp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                _rlp_r.rightMargin = __10;
                _rlp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                _rl_tmp.addView(cancelBtn, _rlp_r);

                layoutEdit.addView(_rl_tmp, MATCH_PARENT, WRAP_CONTENT);
            }
            mainLayout.addView(layoutEdit, MATCH_PARENT, WRAP_CONTENT);
        }

        goToDisplayMode();

        setContentView(bounceScrollView);

        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("????????????");
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jefsRulesEditButton: {
                goToEditMode();
                break;
            }
            case R.id.jefsRulesCancelButton: {
                confirmLeaveEditMode();
                break;
            }
            case R.id.jefsRulesResetButton: {
                confirmResetRules();
                break;
            }
            case R.id.jefsRulesSaveButton: {
                checkAndSaveRules(rulesEt.getText().toString());
                break;
            }
        }
    }

    @Override
    public void doOnBackPressed() {
        if (currEditMode) {
            CustomDialog.create(this).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("??????", null).setTitle("??????").setMessage("??????????????????????????????")
                    .setCancelable(true).show();
        } else {
            super.doOnBackPressed();
        }
    }

    private void goToEditMode() {
        currEditMode = true;
        rulesTv.setText("");
        rulesEt.setText(jmpctl.getRuleString());
        layoutDisplay.setVisibility(View.GONE);
        layoutEdit.setVisibility(View.VISIBLE);
    }

    private void goToDisplayMode() {
        currEditMode = false;
        rulesEt.setText("");
        rulesTv.setText(jmpctl.getRuleString());
        layoutEdit.setVisibility(View.GONE);
        layoutDisplay.setVisibility(View.VISIBLE);
    }

    private void confirmLeaveEditMode() {
        CustomDialog.create(this).setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToDisplayMode();
            }
        }).setNegativeButton("??????", null).setTitle("????????????").setMessage("??????????????????????????????")
                .setCancelable(true).show();
    }

    private void confirmResetRules() {
        CustomDialog.create(this).setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jmpctl.setRuleString(JumpController.DEFAULT_RULES);
                goToDisplayMode();
            }
        }).setNegativeButton("??????", null).setTitle("????????????").setMessage("?????????????????????, ????????????????????????")
                .setCancelable(true).show();
    }

    @UiThread
    private void checkAndSaveRules(String rules) {
        try {
            ArrayList<JumpController.Rule> ruleList = JumpController.parseRules(rules);
            jmpctl.setRuleString(rules);
            goToDisplayMode();
        } catch (ParseException e) {
            CustomDialog.createFailsafe(this).setPositiveButton("??????", null).setTitle("????????????").setMessage(e.toString())
                    .setCancelable(true).show();
        }
    }
}
