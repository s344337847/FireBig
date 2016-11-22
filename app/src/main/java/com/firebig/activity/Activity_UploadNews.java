package com.firebig.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firebig.Data.FBImageLoader;
import com.firebig.Data.FileUtil;
import com.firebig.Data.ImageHandle;
import com.firebig.app.AppContext;
import com.firebig.bean.BmobNews;
import com.firebig.util.AppUtil;
import com.firebig.util.CommandUtil;
import com.firebig.util.DataPort;
import com.firebig.util.KeyBoardUtils;
import com.firebig.util.StringUtils;
import com.firebig.widget.FBLargeImageDisPlay;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by FireBig-CH on 16-8-19.
 * 发送广播类
 */
public class Activity_UploadNews extends BaseActivity implements TextWatcher {

    /**
     * 组件
     */
    private EditText news_content_input;
    private TextView content_number;
    private ImageView news_image;
    private ImageView news_image_delete;
    private ProgressBar image_load_progressBar;
    private TextView four_font_text1;
    private TextView four_font_text2;
    private Switch four_font_btn;
    private boolean fourWordState = true;
    /**
     * 提示
     */
    private ProgressDialog dialog;
    /**
     * 临时图片路径
     */
    private String imagerandomName;

    /**
     * 广播按钮
     */
    private ImageView sendBtn;
    /**
     * 添加广播图片
     **/
    private int[] ids = {R.id.choose_camera, R.id.choose_thumb, R.id.choose_cancle};
    private RelativeLayout[] cbtns = new RelativeLayout[3];
    private RelativeLayout layout;
    // 选择，模式
    private int REQUEST_CODE_CAPTURE_CAMEIA = 2;
    private int REQUEST_CODE_PICK_IMAGE = 1;
    /**
     * 预览大图
     */
    private FBLargeImageDisPlay largeImageDisPlay;
    /**
     * 图片URI
     */
    private Uri uri;

    /**
     *
     */
    private Handlers handlers;

    private Bitmap bitmap;

    public class Handlers extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                setImageViewBitmap();
            }
        }
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
            String out_file_path = CommandUtil.Temp_IM_PATH;
            File dir = new File(out_file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            imagerandomName = CommandUtil.Temp_IM_PATH + System.currentTimeMillis() + ".png";
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagerandomName)));
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        }
    }

    private void setImageViewBitmap() {
        news_image.setImageBitmap(bitmap);
        news_image_delete.setVisibility(View.VISIBLE);
        image_load_progressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            //imagerandomName = CommandUtil.Temp_IM_PATH + System.currentTimeMillis() + ".png";
            // 把图片保存到软件目录
            image_load_progressBar.setVisibility(View.VISIBLE);
            uri = data.getData();
            imagerandomName = AppUtil.getRealFilePath(this, uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bitmap = FileUtil.getBitmapFromApp(imagerandomName, 100, 100);
                    handlers.sendEmptyMessage(0);
                }
            }).start();
            //FileUtil.saveImage(photo, imagerandomName);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            // 将缩略图显示在UI
            image_load_progressBar.setVisibility(View.VISIBLE);
            uri = Uri.fromFile(new File(imagerandomName));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bitmap = FileUtil.getBitmapFromApp(imagerandomName, 100, 100);
                    handlers.sendEmptyMessage(0);
                }
            }).start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initUI() {
        news_content_input = (EditText) findViewById(R.id.upload_newsinput);
        news_content_input.addTextChangedListener(this);
        content_number = (TextView) findViewById(R.id.news_content_number);
        news_image = (ImageView) findViewById(R.id.upload_news_image);
        image_load_progressBar = (ProgressBar) findViewById(R.id.upload_news_image_load_progressBar);
        news_image_delete = (ImageView) findViewById(R.id.upload_news_image_delete);
        news_image_delete.setVisibility(View.GONE);
        news_image_delete.setOnClickListener(this);
        four_font_text1 = (TextView) findViewById(R.id.mynews_list_item_show1);
        four_font_text2 = (TextView) findViewById(R.id.mynews_list_item_show2);
        four_font_btn = (Switch) findViewById(R.id.mynews_list_item_switch);
    }

    @Override
    public void loadData() {
        four_font_text1.setText(getResources().getString(R.string.uploadnews_font01));
        four_font_text2.setText(getResources().getString(R.string.uploadnews_font02));
        news_image.setOnClickListener(this);
        four_font_btn.setChecked(fourWordState);
        four_font_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // false off and true on
                fourWordState = b;
            }
        });
        handlers = new Handlers();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_uploadnews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.uploadnews_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLayoutInflater().inflate(R.layout.toolbar_sendnews, toolbar);
        sendBtn = (ImageView) findViewById(R.id.upload_news_toolbar_send);
        sendBtn.setOnClickListener(this);
        sendBtn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.upload_news_image) {
            if (news_image_delete.getVisibility() == View.VISIBLE) {
                DisplayLarge();
                return;
            }
            showChoose();
        } else if (id == R.id.choose_cancle) {
            hideChoose();
        } else if (id == R.id.choose_camera) {
            hideChoose();
            getPictureFromCam();
        } else if (id == R.id.choose_thumb) {
            hideChoose();
            selectPicture();
        } else if (id == R.id.upload_news_image_delete) {
            news_image_delete.setVisibility(View.GONE);
            news_image.setImageResource(R.drawable.ic_send_news_addimage);
            // 删除上次生成的图片
            if (imagerandomName != null)
                deleteTemp(imagerandomName);
        } else if (id == R.id.upload_news_toolbar_send) {
            uploadData();
        }
    }

    /**
     * 预览大图
     */
    private void DisplayLarge() {
        if (largeImageDisPlay == null) {
            largeImageDisPlay = new FBLargeImageDisPlay(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(largeImageDisPlay.getRootView(), params);
            largeImageDisPlay.Show(imagerandomName);
        } else {
            largeImageDisPlay.Show(imagerandomName);
        }
    }

    /**
     * 上传数据
     */
    private void uploadData() {
        dialog = new ProgressDialog(this);
        // 隐藏软键盘
        KeyBoardUtils.closeKeybord(news_content_input, Activity_UploadNews.this);
        // 显示上传提示
        dialog.setMessage("正在上传数据...");
        dialog.show();
        if (news_image_delete.getVisibility() == View.VISIBLE) {
            uploadFile();
        } else {
            BmobNews bmobNews = new BmobNews();
            bmobNews.setAccount(AppContext.getINSTANCE().UserAccount);
            bmobNews.setFour_word_state(fourWordState);
            bmobNews.setNewscontent(news_content_input.getText().toString());
            bmobNews.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        //广播成功
                        dialog.dismiss();
                        Activity_UploadNews.this.setResult(CommandUtil.RESULCODE_REFRESH);
                        finish();
                    } else {
                        AppContext.getINSTANCE().showBmobException(Activity_UploadNews.this, e);
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * 上传图片
     */
    private void uploadFile() {
        // 生成小图（拍照图片太大，压缩图片后进行上传）
        ImageHandle imageHandle = new ImageHandle(imagerandomName);
        new Thread(imageHandle).start();
        imageHandle.setImagePathListener(new DataPort.handleImage() {
            @Override
            public void done(final String LargeImage_Path, final String smallImage_Path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 上传两张图片。。。。一张缩略图，一张大图
                        final String[] filePaths = new String[2];
                        filePaths[0] = smallImage_Path;
                        filePaths[1] = LargeImage_Path;
                        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                            @Override
                            public void onSuccess(List<BmobFile> list, List<String> urls) {
                                if (urls.size() == filePaths.length) {
                                    BmobNews bmobNews = new BmobNews();
                                    bmobNews.setAccount(AppContext.getINSTANCE().UserAccount);
                                    bmobNews.setFour_word_state(fourWordState);
                                    bmobNews.setNewscontent(news_content_input.getText().toString());
                                    // 大图
                                    bmobNews.setNewsimage(list.get(1));
                                    // 直接获取缩略图下载地址
                                    bmobNews.setThumbnail_url(urls.get(0));
                                    bmobNews.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                // 删除临时图片
                                                deleteTemp(LargeImage_Path);
                                                deleteTemp(smallImage_Path);
                                                deleteTemp(imagerandomName);
                                                dialog.dismiss();
                                                Activity_UploadNews.this.setResult(CommandUtil.RESULCODE_REFRESH);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {

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
     * 显示选择图片
     */

    private void showChoose() {
        if (layout == null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view = LayoutInflater.from(this).inflate(R.layout.upload_news_chooseimage, null);
            addContentView(view, params);
            layout = (RelativeLayout) view.findViewById(R.id.choose_r);
            for (int i = 0; i < ids.length; i++) {
                cbtns[i] = (RelativeLayout) view.findViewById(ids[i]);
                cbtns[i].setOnClickListener(this);
            }
        }
        layout.setVisibility(View.VISIBLE);
        cbtns[0].startAnimation(AnimationUtils.loadAnimation(this, R.anim.choose_btn_photo));
        cbtns[1].startAnimation(AnimationUtils.loadAnimation(this, R.anim.choose_btn_picture));
        cbtns[2].startAnimation(AnimationUtils.loadAnimation(this, R.anim.choose_btn_cancle));
    }

    /**
     * 隐藏选择图片
     */
    private void hideChoose() {
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1, 0);
        alphaAnimation2.setDuration(200);
        layout.startAnimation(alphaAnimation2);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.choose_btn_photo_hide);
        cbtns[0].startAnimation(animation);
        cbtns[1].startAnimation(AnimationUtils.loadAnimation(this, R.anim.choose_btn_picture_hide));
        cbtns[2].startAnimation(AnimationUtils.loadAnimation(this, R.anim.choose_btn_cancle_hide));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 删除生成的图片
     */
    private void deleteTemp(@NonNull String Url) {
        // 删除临时图片
        File file = new File(Url);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (charSequence.length() > 0)
            sendBtn.setVisibility(View.VISIBLE);
        else
            sendBtn.setVisibility(View.GONE);

        if (charSequence.length() > 150) {
            news_content_input.setText(news_content_input.getText().toString().substring(0, 150));
            toast(getResources().getString(R.string.upload_ts01));
            news_content_input.setSelection(150);
            content_number.setText("150/150");
        } else {
            content_number.setText(charSequence.length() + "/150");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
