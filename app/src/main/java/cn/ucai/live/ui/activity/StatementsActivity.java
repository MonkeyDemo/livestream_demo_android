package cn.ucai.live.ui.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.model.RechargeStatements;
import cn.ucai.live.utils.MFGT;

public class StatementsActivity extends AppCompatActivity {

    int fragmentType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements);
        getIntent().getIntExtra(I.STATEMENTS_TYPE,0);
        if (fragmentType==0){
            MFGT.finish(this);
        }else{
            setFragment();
        }
    }

    private void setFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        switch (fragmentType){
            case I.STATEMENTS_TYPE_GIVING:
                ft.add(R.id.layout_content,new GiveStatementsFragment());
                break;
            case I.STATEMENTS_TYPE_RECEIVE:
                ft.add(R.id.layout_content,new ReceiveStatementsFragment());
                break;
            case I.STATEMENTS_TYPE_RECHARGE:
               // ft.add(new RechargeStatementsFragment(),"recharge");
                break;
        }
        ft.commit();
    }
}
