package cn.ucai.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.R;
import cn.ucai.live.ui.activity.BaseActivity;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class ChangeActivity extends BaseActivity {
    @BindView(R.id.tv_change_balance)
    TextView tvChangeBalance;
    @BindView(R.id.target_layout)
    LinearLayout targetLayout;
    View loadingView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);
        loadingView = LayoutInflater.from(this).inflate(R.layout.rp_loading, targetLayout, false);
        targetLayout.addView(loadingView);
    }
}
