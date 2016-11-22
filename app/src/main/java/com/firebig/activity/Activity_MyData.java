package com.firebig.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebig.Data.FileUtil;
import com.firebig.Data.ImageHandle;
import com.firebig.app.AppContext;
import com.firebig.bean.Bmob_MyUser;
import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;
import com.firebig.util.KeyBoardUtils;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBLargeImageDisPlay;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by FireBig-CH on 16-8-17.
 * 我的资料类:
 * 1.内容包括头像、账号、昵称、和签名(在2016/10/09 进行隐藏,不显示)，安全方面的修改密码和找回密码不考虑
 * 2.生成的临时图片保存在手机目录FireBig/Photo下
 * Bug(2016/10/09):
 * 1.生成的临时图片无法删除
 */
public class Activity_MyData extends BaseActivity implements View.OnLayoutChangeListener {

    private int[] layids = {R.id.data_header, R.id.data_account, R.id.data_name, R.id.data_singture};
    private RelativeLayout[] layouts = new RelativeLayout[layids.length];
    private int[] textids = {R.id.text01, R.id.text02, R.id.text03};
    private TextView[] tvs = new TextView[textids.length];
    private int[] edids = {R.id.userdata_input01, R.id.userdata_input02, R.id.userdata_input03};
    private EditText[] eds = new EditText[edids.length];
    private LinearLayout layout;
    // 修改密码
    private TextView updatePass;
    // save editview input string
    private String saveinput01;
    // toolbar
    private TextView update;
    // data
    private Bmob_MyUser myUser;
    //
    private ProgressDialog pgd;
    // 头像
    private SimpleDraweeView header;
    // 退出按钮
    private LinearLayout exit;
    // 选取头像类型(拍照or相册)
    private int REQUEST_CODE_CAPTURE_CAMEIA = 2;
    private int REQUEST_CODE_PICK_IMAGE = 1;
    /**
     * 更换头像弹出菜单
     **/
    private int[] tvids = {R.id.userdata_pop_tv1, R.id.userdata_pop_tv2, R.id.userdata_pop_tv3, R.id.userdata_pop_tv4};
    private TextView[] pop_tvs = new TextView[tvids.length];
    private LinearLayout userdata_pop;
    private LinearLayout userdata_pop_layout;
    /**
     * 临时图片
     */
    private File mTempDir;

    /**
     * 保存临时图片到路径
     */
    private String SAVED_IMAGE_DIR_PATH = Environment.getExternalStorageDirectory() + File.separator + "Temp" + File.separator;

    /**
     * 图片名
     */
    private String tempFileName;

    /**
     * 拍照的临时图片Uri
     */
    private Uri input_uri;

    /**
     * 临时拍照图片保存名
     */
    private String temp_PhotoName;

    /**
     * 加载提示
     */
    private ProgressDialog dialog;

    /**
     * 大图显示
     */
    private FBLargeImageDisPlay largeImageDisPlayl;

    @Override
    public void initUI() {
        exit = (LinearLayout) findViewById(R.id.exit_layout);
        exit.setOnClickListener(this);
        layout = (LinearLayout) findViewById(R.id.mydata_layout);
        layout.addOnLayoutChangeListener(this);
        for (int i = 0; i < layids.length; i++) {
            layouts[i] = (RelativeLayout) findViewById(layids[i]);
            layouts[i].setOnClickListener(this);
        }

        for (int i = 0; i < textids.length; i++) {
            tvs[i] = (TextView) findViewById(textids[i]);
            tvs[i].setOnClickListener(this);
        }

        for (int i = 0; i < edids.length; i++) {
            eds[i] = (EditText) findViewById(edids[i]);
            eds[i].setOnClickListener(this);
        }

        updatePass = (TextView) findViewById(R.id.data_updatepass);
        updatePass.setOnClickListener(this);

        update = (TextView) findViewById(R.id.userdata_update);
        update.setOnClickListener(this);
        update.setVisibility(View.GONE);
        header = (SimpleDraweeView) findViewById(R.id.userdata_header);
        header.setOnClickListener(this);
        dialog = new ProgressDialog(this);
    }

    @Override
    public void loadData() {
        myUser = BmobUser.getCurrentUser(Bmob_MyUser.class);
        if (myUser.getHeader_thumbnail_url() != null) {
            header.setImageURI(myUser.getHeader_thumbnail_url());
        }

        tvs[0].setText(myUser.getUsername());
        tvs[1].setText(myUser.getShowname());
        tvs[2].setText(StringUtils.friendly_time(myUser.getCreatedAt()));

        eds[1].setHint(tvs[1].getText());
        eds[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveinput01 = charSequence.toString();
                if (saveinput01.equals(tvs[1].getText().toString())) {
                    update.setVisibility(View.GONE);
                } else {
                    update.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // 创建临时图片
        mTempDir = new File(Environment.getExternalStorageDirectory(), "Temp");
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_userdata);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLayoutInflater().inflate(R.layout.toolbar_userdata, toolbar);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.exit_layout) {
            exitLogin();
        }
        if (id == layids[0]) {
            // 更换头像
            showPop();
        }
        if (id == layids[1]) {
            hideIM();
        }
        if (id == layids[2]) {
            tvs[1].setVisibility(View.INVISIBLE);
            eds[1].setVisibility(View.VISIBLE);
            KeyBoardUtils.openKeybord(eds[1], this);
        }
        if (id == R.id.userdata_update) {
            updateData();
        }
        if (id == R.id.userdata_pop_tv2) {// 图库选择
            hidePop();
            selectPicture();
        }
        if (id == R.id.userdata_pop_tv3) { // 拍照
            hidePop();
            getPictureFromCam();
        }
        if (id == R.id.userdata_pop_tv4) {// 取消
            hidePop();
        }
        if (id == R.id.data_updatepass) {
            // 修改密码接口
            toast("暂时不能修改密码，正在努力建设中...");
        }
        if (id == R.id.userdata_header) {// 显示头像大图
            showLargeHeader();
        }

    }

    /**
     * exit Login back to loginActivity
     */
    private void exitLogin() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认要退出登陆吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO 清除当前登陆信息
                BmobUser.logOut();
                //BmobIMHandle.getInstance().disConnect();
                AppContext.getINSTANCE().LOGIN = false;
                AppContext.getINSTANCE().UserAccount = null;
                setResult(CommandUtil.RESULCODE_FINISH);
                goToActivity(Activity_Login.class, null, true);
                Activity_MyData.this.setResult(CommandUtil.RESULCODE_FINISH);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 从相册选择一张图片
     */
    public void selectPicture() {
        toast("请选择一张图片!");
        Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
        localIntent.addCategory("android.intent.category.OPENABLE");
        localIntent.setType("image/jpeg");
        startActivityForResult(localIntent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 拍照获取一张图片
     */
    public void getPictureFromCam() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            String out_file_path = SAVED_IMAGE_DIR_PATH;
            File dir = new File(out_file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            temp_PhotoName = SAVED_IMAGE_DIR_PATH + System.currentTimeMillis() + ".jpg";
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(temp_PhotoName)));
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            // 图库选择
            CropPicture(data.getData());
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            // 拍照
            Uri input = Uri.fromFile(new File(temp_PhotoName));
            CropPicture(input);
        }
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            // 返回裁剪图片
            handleCroped();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理裁剪后的图片
     * 1.压缩图片
     * 2.上传图片
     */
    private void handleCroped() {
        dialog.setMessage("正在上传...");
        dialog.show();
        // 生成缩略图
        ImageHandle imageHandle = new ImageHandle(mTempDir + "/" + tempFileName);
        imageHandle.setThumbnailImagePixel(100, 100);
        imageHandle.setLargeImagePixel(400, 400);
        new Thread(imageHandle).start();
        imageHandle.setImagePathListener(new DataPort.handleImage() {
            @Override
            public void done(final String LargeImage_Path, final String smallImage_Path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 上传两张图片,一张缩略图,一张大图
                        final String[] filePaths = new String[2];
                        filePaths[0] = smallImage_Path;
                        filePaths[1] = LargeImage_Path;
                        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                            @Override
                            public void onSuccess(final List<BmobFile> list, List<String> urls) {
                                if (urls.size() == filePaths.length) {
                                    Bmob_MyUser myUser = new Bmob_MyUser();
                                    myUser.setAvater(list.get(1));
                                    myUser.setHeader_thumbnail_url(list.get(0).getFileUrl());
                                    myUser.update(Activity_MyData.this.myUser.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                updateAvater(list);
                                                dialog.dismiss();
                                            } else {
                                                dialog.dismiss();
                                                toast("ERROR:" + e.getErrorCode() + "-" + e.toString());
                                            }
                                            FileUtil.deleteTemp(mTempDir + "/" + tempFileName);
                                            FileUtil.deleteTemp(smallImage_Path);
                                            FileUtil.deleteTemp(LargeImage_Path);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onProgress(int i, int i1, int i2, int i3) {

                            }

                            @Override
                            public void onError(int i, String s) {
                                dialog.dismiss();
                            }
                        });

                    }
                });
            }
        });
    }

    /**
     * 压缩图片并显示到UI
     */
    public void updateAvater(List<BmobFile> list) {
        myUser.setAvater(list.get(1));
        myUser.setHeader_thumbnail_url(list.get(0).getFileUrl());
        header.setImageURI(Uri.parse(list.get(0).getFileUrl()));
        setResult(CommandUtil.RESULCODE_UPDATECACHE);
    }

    /**
     * 开始裁剪图片
     *
     * @param inputUri
     */
    private void CropPicture(Uri inputUri) {
        tempFileName = "Temp_" + String.valueOf(System.currentTimeMillis()) + ".png";
        File cropFile = new File(mTempDir, tempFileName);
        Uri outputUri = Uri.fromFile(cropFile);
        new Crop(inputUri).output(outputUri).asSquare().start(this);
    }

    /**
     * 显示弹出框
     */
    public void showPop() {
        if (userdata_pop_layout == null) {
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view = LayoutInflater.from(this).inflate(R.layout.userdata_pop, null);
            addContentView(view, params);
            for (int i = 0; i < tvids.length; i++) {
                pop_tvs[i] = (TextView) view.findViewById(tvids[i]);
                pop_tvs[i].setOnClickListener(this);
            }
            userdata_pop = (LinearLayout) view.findViewById(R.id.userdata_pop);
            userdata_pop_layout = (LinearLayout) view.findViewById(R.id.userdata_pop_layout);
            userdata_pop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.userdata_pop_show01));
            userdata_pop_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hidePop();
                    return true;
                }
            });
        } else {
            userdata_pop.startAnimation(AnimationUtils.loadAnimation(this, R.anim.userdata_pop_show01));
            userdata_pop_layout.setVisibility(View.VISIBLE);
            userdata_pop_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.userdata_pop_show02));
        }

    }

    /**
     * 隐藏弹出框
     */
    private void hidePop() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.userdata_pop_hide01);
        userdata_pop.startAnimation(animation);
        userdata_pop_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.userdata_pop_hide02));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                userdata_pop_layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 显示头像大图
     */
    private void showLargeHeader() {
        if (myUser.getAvater().getFileUrl() == null) {
            showPop();
            return;
        }
        if (largeImageDisPlayl == null) {
            largeImageDisPlayl = new FBLargeImageDisPlay(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(largeImageDisPlayl.getRootView(), params);
            showLargeHeader();
        } else {
            largeImageDisPlayl.setUrl(myUser.getAvater().getFileUrl(), myUser.getAvater().getFilename());
            largeImageDisPlayl.Show();
        }
    }

    /**
     * 更改资料
     */
    private void updateData() {
        hideIM();
        pgd = new ProgressDialog(this);
        pgd.setMessage(getResources().getString(R.string.suggest_upload_loading));
        pgd.show();
        Bmob_MyUser myUser1 = new Bmob_MyUser();
        if (saveinput01 != null)
            myUser1.setShowname(saveinput01);
        myUser1.update(myUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                pgd.dismiss();
                if (e != null) {
                    AppContext.getINSTANCE().showBmobException(Activity_MyData.this, e);
                    return;
                }
                update.setVisibility(View.GONE);
                if (saveinput01 != null) {
                    tvs[1].setText(saveinput01);
                    myUser.setShowname(saveinput01);
                    setResult(CommandUtil.RESULCODE_UPDATECACHE);
                }
            }
        });

    }

    /**
     * 隐藏键盘
     */
    private void hideIM() {
        eds[1].setVisibility(View.INVISIBLE);
        tvs[1].setVisibility(View.VISIBLE);
        KeyBoardUtils.closeKeybord(eds[1], this);
        KeyBoardUtils.closeKeybord(eds[2], this);
    }

    @Override
    protected void onStop() {
        hideIM();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mTempDir.exists()) {
            mTempDir.delete();
        }
        super.onDestroy();
    }

    /**
     * 监听最外层layout的高度变化，来判断键盘到显示和隐藏
     */
    @Override
    public void onLayoutChange(View view, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > screen_height / 3)) {
            hideIM();
        }
    }
}
