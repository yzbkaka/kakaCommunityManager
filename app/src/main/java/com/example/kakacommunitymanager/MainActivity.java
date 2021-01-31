package com.example.kakacommunitymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kakacommunitymanager.community.ManagerActivity;
import com.example.kakacommunitymanager.constant.HttpUtil;
import com.example.kakacommunitymanager.constant.StringUtil;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.kakacommunitymanager.constant.Constant.BASE_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextInputLayout loginName;

    private TextInputLayout loginPassword;

    private Button loginButton;

    private TextView loginRegisterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        loginName = (TextInputLayout) findViewById(R.id.login_name);
        loginPassword = (TextInputLayout) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String name = loginName.getEditText().getText().toString();
        String password = loginPassword.getEditText().getText().toString();
        if (StringUtil.isBlank(name)) {
            loginName.setErrorEnabled(true);
            loginName.setError("用户名不能为空");
        }
        if (StringUtil.isBlank(password)) {
            loginPassword.setErrorEnabled(true);
            loginPassword.setError("密码不能为空");
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("username", name)
                .add("password", password)
                .add("rememberMe", "true")
                .build();
        HttpUtil.OkHttpPOST(BASE_ADDRESS + "/login", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if (responseData.contains("ticket")) {
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请求登录成功", Toast.LENGTH_SHORT).show();
                        }
                    }));
                    Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请求登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            }
        });
    }
}
