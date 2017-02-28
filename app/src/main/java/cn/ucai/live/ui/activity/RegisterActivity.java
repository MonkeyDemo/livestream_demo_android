package cn.ucai.live.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.net.NetDao;
import cn.ucai.live.data.model.net.OnCompleteListener;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;
import cn.ucai.live.utils.MD5;
import cn.ucai.live.utils.ResultUtils;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @BindView(R.id.email)
    EditText etUsername;
    @BindView(R.id.password)
    EditText etPassword;
    @BindView(R.id.usernick)
    EditText etUsernick;
    @BindView(R.id.password_confirm)
    EditText etConfirmpassword;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ProgressDialog pd;
    String username,usernick,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                usernick = etUsernick.getText().toString().trim();
                String confirm_pwd = etConfirmpassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    CommonUtils.showShortToast(getResources().getString(R.string.User_name_cannot_be_empty));
                    etUsername.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(usernick)) {
                    CommonUtils.showShortToast(getResources().getString(R.string.Nick_name_cannot_be_empty));
                    etUsernick.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    CommonUtils.showShortToast(getResources().getString(R.string.Password_cannot_be_empty));
                    etPassword.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(confirm_pwd)) {
                    CommonUtils.showShortToast(getResources().getString(R.string.Confirm_password_cannot_be_empty));
                    etConfirmpassword.requestFocus();
                    return;
                } else if (!password.equals(confirm_pwd)) {
                    CommonUtils.showShortToast(getResources().getString(R.string.Two_input_password));
                    return;
                }

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage(getResources().getString(R.string.Is_the_registered));
                    pd.show();

                    registerAppServer(username, usernick, password);

                }

            }
        });
    }


    private void registerAppServer(final String username, String nickname, final String pwd) {
        //注册自己服务器的账号
        NetDao.register(this, username, nickname, pwd, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                L.e(TAG, "result s=" + s);
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, null);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            // //注册成功后调用环信注册
                            registerEMServer(username, pwd);
                        } else {
                            pd.dismiss();
                            if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                CommonUtils.showShortToast(getResources().getString(R.string.User_already_exists));
                            } else {
                                CommonUtils.showShortToast(getResources().getString(R.string.Registration_failed));
                            }
                        }
                    } else {
                        pd.dismiss();
                        CommonUtils.showShortToast(getResources().getString(R.string.Registration_failed));
                    }
                } else {
                    pd.dismiss();
                    CommonUtils.showShortToast(getResources().getString(R.string.Registration_failed));
                }
            }

            @Override
            public void onError(String error) {
                pd.dismiss();
                CommonUtils.showShortToast(getResources().getString(R.string.Registration_failed));
                L.e(TAG, "error = " + error);
            }
        });
    }

    private void registerEMServer(final String username, final String pwd) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(pwd));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // save current user
                            LiveHelper.getInstance().setCurrentUserName(username);
                            CommonUtils.showShortToast(getResources().getString(R.string.Registered_successfully));
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    unRegisterAppServer(username);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                CommonUtils.showShortToast(getResources().getString(R.string.network_anomalies));
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                CommonUtils.showShortToast( getResources().getString(R.string.User_already_exists));
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                CommonUtils.showShortToast(getResources().getString(R.string.registration_failed_without_permission));
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                CommonUtils.showShortToast(getResources().getString(R.string.illegal_user_name));
                            } else {
                                CommonUtils.showShortToast( getResources().getString(R.string.Registration_failed));
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void unRegisterAppServer(String username) {
        NetDao.unRegister(this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.e(TAG, "unRegister!");
            }

            @Override
            public void onError(String error) {
                L.e(TAG, "error = " + error);
            }
        });
    }

}
