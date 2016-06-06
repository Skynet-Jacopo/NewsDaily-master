package com.example.liuqun.newsdaily.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.model.biz.UserManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserUser;
import com.example.liuqun.newsdaily.model.entity.BaseEntity;
import com.example.liuqun.newsdaily.model.entity.Register;

/**
 * 忘记密码界面
 */
public class FragmentForgetPass extends Fragment {
    //邮箱编辑框
    private EditText editEmail;
    //确认按钮
    private Button btnCommit;
    //用户管理器
    private UserManager userManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgetpass, container,
                false);
        editEmail = (EditText) view.findViewById(R.id.edit_email);
        btnCommit = (Button) view.findViewById(R.id.btn_commit);

        btnCommit.setOnClickListener(listener);
        return view;
    }

    private View.OnClickListener listener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_commit){
                String email =editEmail.getText().toString().trim();
                if (!CommonUtil.verifyEmail(email)){
                    Toast.makeText(getActivity(), "请输入正确的邮箱格式", Toast
                            .LENGTH_SHORT)
                            .show();
                    return;
                }
                if (userManager == null) {
                    userManager =UserManager.getInstance(getActivity());
                    userManager.forgetPass(getActivity(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            LogUtil.d(LogUtil.TAG,"执行忘记密码操作,返回信息:" +response);
                            BaseEntity<Register> register = ParserUser
                                    .parserRigister(response);
                            int status =Integer.parseInt(register.getStatus());
                            String result ="";
                            if (status == 0){
                                Register data =register.getData();
                                result =data.getExplain();
                                if (data.getResult().trim().equals("0")){
                                    ((MainActivity)getActivity()).showFragmentLogin();
                                    //增加动画
                                    getActivity().overridePendingTransition(R
                                            .anim.anim_activity_right_in,R
                                            .anim.anim_activity_bottom_out);
                                }else if (data.getResult().trim().equals
                                        ("-2")){
                                    editEmail.requestFocus();
                                }
                                Toast.makeText(getActivity(), result, Toast
                                        .LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "请求失败!", Toast
                                    .LENGTH_SHORT).show();
                        }
                    },CommonUtil.VERSION_CODE+"",email);
                }
            }
        }
    };
}
