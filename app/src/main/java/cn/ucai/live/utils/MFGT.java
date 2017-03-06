package cn.ucai.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.ui.ChangeActivity;
import cn.ucai.live.ui.activity.LoginActivity;
import cn.ucai.live.ui.activity.MainActivity;
import cn.ucai.live.ui.activity.RegisterActivity;
import cn.ucai.live.ui.activity.StatementsActivity;


/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class MFGT {

    public static void finish(Activity context){
        context.finish();
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    public static void startActivity(Activity context,Class<?> clz){
        context.startActivity(new Intent(context,clz));
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }

    public static void startActivity(Activity context,Intent intent){
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }

    public static void gotoLogin(Context context){
        startActivity((Activity) context, LoginActivity.class);
    }
    public static void gotoRegister(Context context){
        startActivity((Activity) context, RegisterActivity.class);
    }
    public static void gotoLoginClearTask(Context context) {
        startActivity((Activity) context,new Intent(context,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    public static void gototMain(Activity activity) {
        startActivity(activity, new Intent(activity,MainActivity.class).putExtra(I.BACK_MAIN_FROM_CHAT,true));
    }

    public static void gotoChange(Activity activity) {
        startActivity(activity,ChangeActivity.class);
    }

    public static void gotoStateMents(Activity activity, int type) {
        startActivity(activity,new Intent(activity, StatementsActivity.class).putExtra(I.STATEMENTS_TYPE,type));
    }
}
