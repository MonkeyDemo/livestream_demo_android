package cn.ucai.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.model.net.NetDao;
import cn.ucai.live.data.model.net.OnCompleteListener;
import cn.ucai.live.ui.activity.BaseActivity;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.MFGT;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class ChangeActivity extends BaseActivity {
    @BindView(R.id.tv_change_balance)
    TextView tvChangeBalance;
    @BindView(R.id.target_layout)
    LinearLayout targetLayout;
    View loadingView;
    int change;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);
        loadingView = LayoutInflater.from(this).inflate(R.layout.rp_loading, targetLayout, false);
        targetLayout.addView(loadingView);
        setChange();
        initData();
    }

    private void initData() {
        NetDao.loadChange(this, EMClient.getInstance().getCurrentUser(), new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                boolean success = false;
                if (s != null) {
                    Result resule = ResultUtils.getResultFromJson(s, Wallet.class);
                    if (resule != null && resule.isRetMsg()) {
                        Wallet wallet = (Wallet) resule.getRetData();
                        if (wallet != null) {
                            success = true;
                            change = wallet.getBalance();
                            PreferenceManager.getInstance().setCurrentUserChange(change);
                            setChange();
                        }
                    }
                }
                if (!success) {
                    PreferenceManager.getInstance().setCurrentUserChange(0);
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                CommonUtils.showShortToast(error);
                loadingView.setVisibility(View.GONE);
            }
        });
    }

    private void setChange() {
        int change = PreferenceManager.getInstance().getCurrentUserChange();
        tvChangeBalance.setText("￥" + new DecimalFormat("#0.00").format(Float.valueOf(change)));
    }


    @OnClick({R.id.tv_change_details, R.id.tv_my_bankcard,R.id.tv_my_red_packet_records})
    public void checkStatements(View view) {
        switch (view.getId()) {
            case R.id.tv_change_details:
                MFGT.gotoStateMents(this, I.STATEMENTS_TYPE_RECHARGE);
                break;
            case R.id.tv_my_bankcard:
                MFGT.gotoStateMents(this, I.STATEMENTS_TYPE_GIVING);
                break;
            case R.id.tv_my_red_packet_records:
                MFGT.gotoStateMents(this, I.STATEMENTS_TYPE_RECEIVE);
                break;
        }
    }
}
