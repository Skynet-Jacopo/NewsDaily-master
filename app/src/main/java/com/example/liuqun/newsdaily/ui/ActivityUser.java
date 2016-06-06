package com.example.liuqun.newsdaily.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.liuqun.newsdaily.R;
import com.example.liuqun.newsdaily.common.CommonUtil;
import com.example.liuqun.newsdaily.common.LoadImage;
import com.example.liuqun.newsdaily.common.LogUtil;
import com.example.liuqun.newsdaily.common.SharedPreferencesUtils;
import com.example.liuqun.newsdaily.common.SystemUtils;
import com.example.liuqun.newsdaily.model.biz.UserManager;
import com.example.liuqun.newsdaily.model.biz.parser.ParserUser;
import com.example.liuqun.newsdaily.model.entity.BaseEntity;
import com.example.liuqun.newsdaily.model.entity.LoginLog;
import com.example.liuqun.newsdaily.model.entity.Register;
import com.example.liuqun.newsdaily.model.entity.User;
import com.example.liuqun.newsdaily.ui.adapter.LoginLogAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ActivityUser extends Activity implements LoadImage.ImageLoadListener {

    private LinearLayout      layout;
    private ImageView         imageView;
    private ImageView         imageView_back;
    private TextView          tvName;
    private TextView          integralTextView;
    private TextView          commentTextView;
    private ListView          logListView;
    private Button            btn_exit;
    private LoginLogAdapter   adapter;
    private SharedPreferences sharedPreferences;
    private Bitmap            bitmap , alterBitmap;
    private LoadImage         loadImage;
    private PopupWindow       popupWindow;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        layout = (LinearLayout) findViewById(R.id.layout);
        imageView = (ImageView) findViewById(R.id.icon);
        imageView_back = (ImageView) findViewById(R.id.imageView_back);
        tvName = (TextView) findViewById(R.id.name);
        integralTextView = (TextView) findViewById(R.id.integral);
        commentTextView = (TextView) findViewById(R.id.comment_count);
        logListView = (ListView) findViewById(R.id.list);
        btn_exit = (Button) findViewById(R.id.btn_exit);

        adapter = new LoginLogAdapter(this, new ArrayList<LoginLog>());
        logListView.setAdapter(adapter);

        //第一个参数:上下文,第二个参数:ImageLoadListener的接口实现对象
        //创建图片请求LoadImage对象
        loadImage = new LoadImage(this, this);

        initData();

        sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        tvName.setText(sharedPreferences.getString("uname", "瑞兹"));
        String localpath = sharedPreferences.getString("localpic", null);
        if (localpath != null) {
            bitmap = BitmapFactory.decodeFile(localpath);
            imageView.setImageBitmap(bitmap);
        }
        imageView.setOnClickListener(onClickListener);
        btn_exit.setOnClickListener(onClickListener);
        imageView_back.setOnClickListener(onClickListener);
        initpopupwindow();
    }

    private void initpopupwindow() {
        View contentView = getLayoutInflater().inflate(R.layout
                .item_pop_selectpic, null);
        //设置popupwindow视图
        popupWindow = new PopupWindow(contentView, ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        LinearLayout photo_take = (LinearLayout) contentView.findViewById(R.id.photo_take);
        LinearLayout photo_sel  = (LinearLayout) contentView.findViewById(R.id.photo_sel);
        photo_take.setOnClickListener(onClickListener);
        photo_sel.setOnClickListener(onClickListener);
    }

    /**
     * 请求用户中心数据
     */
    private void initData() {
        String token = SharedPreferencesUtils.getToken(this);
        UserManager.getInstance(this).getUserInfo(this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.d("请求用户中心返回字符串", response);
                BaseEntity<User> user = ParserUser.parserUser(response);
                if (Integer.parseInt(user.getStatus()) != 0) {
                    Toast.makeText(ActivityUser.this, "请求用户中心失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                //保存用户数据到本地 : 用户昵称, 用户头像地址
                SharedPreferencesUtils.saveUser(ActivityUser.this, user);
                //显示数据更新UI
                User userCore = user.getData();
                tvName.setText(userCore.getUid());
                //更新积分,发帖数
                integralTextView.setText("积分:" + userCore.getIntegration());
                commentTextView.setText(userCore.getComnum() + "");
                //更新登录记录信息
                adapter.appendData(userCore.getLoginlog(), true);
                adapter.update();
                //获取用户头像地址
                String portrait = userCore.getPortrait();
                if (!TextUtils.isEmpty(portrait)) {
                    //此方法内部优化判断缓存中是否有图片,若有则返回
                    //否则判断本地文件是否有图片存在,若有则返回
                    //反之,则请求网络数据,最后会回调下面的imageloadOk();
                    loadImage.geBitmap(portrait, imageView);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
// TODO: 6/4/2016 明儿再做
            }
        }, CommonUtil.VERSION_CODE + "", token, SystemUtils.getIMEI(this));
        // TODO: 6/4/2016
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icon://点击用户头像,底部弹起popupwindow
                    // TODO: 6/5/2016 这里可以尝试一下改改参数会发生什么
                    popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                    break;
                case R.id.imageView_back:
                    startActivity(new Intent(ActivityUser.this, MainActivity
                            .class));
                    finish();
                    break;
                case R.id.photo_take://拍照
                    popupWindow.dismiss();
                    takePhoto();
                    break;
                case R.id.photo_sel://从相册选择
                    popupWindow.dismiss();
                    selectPhoto();
                    break;
                case R.id.btn_exit://退出登录
                    SharedPreferencesUtils.clearUser(ActivityUser.this);
                    startActivity(new Intent(ActivityUser.this, MainActivity.class));
                    finish();
                    break;
            }
        }
    };

    /**
     * 跳转到系统的拍照功能
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    /**
     * 跳转到系统相册,选择照片
     */
    private void selectPhoto() {
        final Intent intent = getPhotoPickIntent();
        startActivityForResult(intent, 200);
    }

    /**
     * 封装请求Gallery的intent
     *
     * @return intent
     */
    private static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");//设置裁剪功能
        intent.putExtra("aspectX", 1);//宽高比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);//宽高值
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);//返回裁剪结果
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: 6/5/2016 这里不是很明白
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {//判断请求是否成功
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                save(bitmap);//缓存用户选择的图片
            }
        } else if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK){
                bitmap =data.getParcelableExtra("data");
                save(bitmap);//缓存用户选择的图片
            }
        }
    }

    /**
     * 缓存用户上传的图片
     * @param bitmap
     */
    private void save(Bitmap bitmap) {
        if (bitmap ==null)
            return;
        roundPic();
        File dir =new File(Environment.getExternalStorageDirectory(),"azynews");
        dir.mkdirs();
        file = new File(dir,"userpic.jpg");

        try {
            OutputStream stream =new FileOutputStream(file);
            if (alterBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)){
                //上传图片
                UserManager.getInstance(this).changPhoto(this,
                        SharedPreferencesUtils.getToken(this),file,listener,
                        errorListener);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪图片
     */
    private void roundPic() {
        // TODO: 6/5/2016 关于Bitmap的使用
        Bitmap backBp =BitmapFactory.decodeResource(getResources(),R.drawable
                .userbg);
        alterBitmap =Bitmap.createBitmap(backBp.getWidth(),backBp.getHeight()
                ,backBp.getConfig());
        Canvas canvas =new Canvas(alterBitmap);
        Paint paint =new Paint();
        paint.setAntiAlias(true);//设置抗锯齿(消除锯齿,普通显示效果)
        canvas.drawBitmap(backBp,new Matrix(),paint);//matrix矩阵,基质,母体
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//xfer转送；转移；传递（等于transfer）
        bitmap =Bitmap.createScaledBitmap(bitmap,backBp.getWidth(),backBp
                .getHeight(),true);
        canvas.drawBitmap(bitmap,new Matrix(),paint);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Response.Listener<String> listener =new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            BaseEntity<Register> entity =ParserUser.parserUploadImage(response);
            if (entity.getData().getResult().equals("0")){
                //保存用户头像本地的路径
                SharedPreferencesUtils.saveUserLocalIcon(ActivityUser.this,
                        file.getAbsolutePath());
                imageView.setImageBitmap(alterBitmap);
            }
        }
    };

    private Response.ErrorListener errorListener =new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };
    //此方法是ImageLoadListener接口的实现方法,用于网络请求后回调返回bitmap对象
    @Override
    public void imageLoadOk(Bitmap bitmap, String url) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
