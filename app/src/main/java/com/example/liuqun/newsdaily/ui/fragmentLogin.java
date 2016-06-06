package com.example.liuqun.newsdaily.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.SharedPreferencesUtils;
import com.example.liuqun.newsdaily.model.biz.UserManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserUser;
import com.example.liuqun.newsdaily.model.entity.BaseEntity;
import com.example.liuqun.newsdaily.model.entity.Register;

/**
 * 登录界面
 */
public class FragmentLogin extends Fragment {
    private View view;
    private EditText editTextNickname,editTextPwd;
    private Button btn_register,btn_login,btn_forgetPass;
    private UserManager userManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_login, container, false);
        editTextNickname = (EditText) view.findViewById(R.id.edittext_nickname);
        editTextPwd = (EditText) view.findViewById(R.id.edittext_pwd);
        btn_register = (Button) view.findViewById(R.id.button_register);
        btn_forgetPass = (Button) view.findViewById(R.id.button_forgetPass);
        btn_login = (Button) view.findViewById(R.id.button_login);

        btn_register.setOnClickListener(clickListener);
        btn_forgetPass.setOnClickListener(clickListener);
        btn_login.setOnClickListener(clickListener);
        return view;
    }

    private View.OnClickListener clickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_login:
                    String name =editTextNickname.getText().toString().trim();
                    String pwd =editTextPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(name)){
                        Toast.makeText(getActivity(), "请输入用户名", Toast
                                .LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(pwd)){
                        Toast.makeText(getActivity(), "密码不能为空", Toast
                                .LENGTH_SHORT).show();
                        return;
                    }
                    if (pwd.length()<6 || pwd.length() >16){
                        Toast.makeText(getActivity(), "密码长度错误", Toast
                                .LENGTH_SHORT).show();
                        return;
                    }

                    if (userManager ==null){
                        userManager =UserManager.getInstance(getActivity());
                    }
                    userManager.login(getActivity(),listener,errorListener,
                            CommonUtil.VERSION_CODE+"",name,pwd,"0");
                    break;
                case R.id.button_register:
                    ((MainActivity)getActivity()).showFragmentRegister();
                    break;
                case R.id.button_forgetPass:
                    ((MainActivity)getActivity()).showFragmentForgetPass();
                    break;
            }
        }
    };
        public Response.Listener<String> listener =new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                BaseEntity<Register> register = ParserUser.parserRigister
                        (response);
                int status =Integer.parseInt(register.getStatus());
                String result ="";
                if (status == 0){
                    result ="登录成功";
                    SharedPreferencesUtils.saveRegister(getActivity(),register);
                    startActivity(new Intent(getActivity(),ActivityUser.class));
                    //增加动画
                    getActivity().overridePendingTransition(R.anim
                            .anim_activity_right_in,R.anim.anim_activity_bottom_out);
                }else if (status == -3){
                    result ="用户名或密码错误";
                }else {
                    result ="登录失败";
                }
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        };

    public Response.ErrorListener errorListener =new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), "登录异常", Toast.LENGTH_SHORT).show();
        }
    };

}
