package com.example.liuqun.newsdaily.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
 * 注册界面
 */
public class FragmentRegister extends Fragment {

    private View view;
    private EditText editTextEmail,editTextName,editTextPwd;
    private Button btn_register;
    private CheckBox checkBox;
    private UserManager userManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_register, container, false);

        editTextEmail = (EditText) view.findViewById(R.id.editText_email);
        editTextName = (EditText) view.findViewById(R.id.editText_name);
        editTextPwd = (EditText) view.findViewById(R.id.editText_pwd);
        btn_register = (Button) view.findViewById(R.id.button_register);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox1);

        btn_register.setOnClickListener(clickListener);
        return view;
    }

    private View.OnClickListener clickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!checkBox.isChecked()){
                Toast.makeText(getActivity(), "没有同意协议条款!", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            String email =editTextEmail.getText().toString().trim();
            String name =editTextName.getText().toString().trim();
            String pwd =editTextPwd.getText().toString().trim();
            if (!CommonUtil.verifyEmail(email)){
                Toast.makeText(getActivity(), "请求输入正确的邮箱格式", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (TextUtils.isEmpty(name)){
                Toast.makeText(getActivity(), "请输入用户昵称", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (pwd.length()<6||pwd.length()>16){
                Toast.makeText(getActivity(), "密码长度错误", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (!CommonUtil.verifyPassword(pwd)){
                Toast.makeText(getActivity(), "请输入6~16位数字和字母组合的密码", Toast
                        .LENGTH_SHORT).show();
                return;
            }
            if (userManager == null) {
                userManager =UserManager.getInstance(getActivity());
                userManager.register(getActivity(),listener,errorListener,
                        CommonUtil.VERSION_CODE+"",name,pwd,email);
            }
        }
    };

    Response.Listener<String> listener =new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            BaseEntity<Register> register = ParserUser.parserRigister(response);
            String result =null;
            String explain =null;
            Register data =register.getData();
            int status =Integer.parseInt(register.getStatus());
            if (status == 0){
                result =data.getResult().trim();
                explain =data.getExplain();
                if (result.equals("0")){
                    //保存用户信息
                    SharedPreferencesUtils.saveRegister(getActivity(),register);
                    startActivity(new Intent(getActivity(),ActivityUser.class));
                    //增加动画
                    getActivity().overridePendingTransition(R.anim
                            .anim_activity_right_in,R.anim.anim_activity_bottom_out);
                    //更新右侧界面
                    ((MainActivity)getActivity()).changeFragmentUser();
                }
                Toast.makeText(getActivity(), explain, Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener =new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), "未知的错误导致注册失败,请重试", Toast.LENGTH_SHORT)
                    .show();
        }
    };
}
