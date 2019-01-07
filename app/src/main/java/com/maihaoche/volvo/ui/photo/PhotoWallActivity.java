package com.maihaoche.volvo.ui.photo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.service.utils.FileUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityPhotoWallBinding;
import com.maihaoche.commonbiz.service.image.ImageUtil;
import com.maihaoche.volvo.ui.common.daomain.ChoosePicEvent;
import com.maihaoche.volvo.util.MediaScanner;
import com.maihaoche.commonbiz.service.permision.PermissionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;



/**
 * Created by wangshengru on 16/1/12.
 * 选择图片界面
 */
public class PhotoWallActivity extends BaseFragmentActivity {

    public static final int REQUEST_CAMERA = 19;
    public static final int REQUEST_PREVIEW_CODE = 28;
    public static final int REQUEST_CROP_CODE = 43;
    public static final int REQUEST_BATCH_CROP_CODE = 52;

    public static final String EXTRA_IS_MUTIPLE = "is_multiple";//是否多张
    public static final String EXTRA_IS_LIMIT_MAX = "is_limit_max";//是否限制最大张数

    public static final String EXTRA_MAX_PHOTO = "max_photo";//最大张数
    public static final String EXTRA_MEDIA_TYPE = "type";//类型
    public static final String EXTRA_TAG = "tag";//标志位
    public static final String IS_ONLY_FROM_CAMERA = "only_from_camera";//标志位

    public static final String EXTRA_IS_CROP = "is_crop"; // 是否调用裁剪
    public static final String EXTRA_CROP_SCALE = "crop_scale"; // 裁剪比例
    public static final String EXTRA_NEED_ROTATE = "need_rotate"; // 裁剪框是否能翻转
    public static final String EXTRA_NEED_UPLOAD = "need_upload"; // 是否上传七牛

    public static final String EXTRA_PREVIEW = "extra_preview"; //是否在选择图片界面显示底部预览按钮

    public static final String EXTRA_SELECTED_URLS_LIST = "selected_urls_list"; //选中list的url
    public static final String EXTRA_IS_COMMIT = "is_commit"; //选中list的url

    public static final String PATH_ALL = "all";

    public static final String RETURN_PATH = "path"; //返回图片路径

    public static final int TYPE_ALL = 0;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_VIDEO = 2;

    static final Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    static final String KEY_IMAGE_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
    static final String KEY_IMAGE_DATA = MediaStore.Images.Media.DATA;

    static final Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    static final String KEY_VIDEO_MIME_TYPE = MediaStore.Video.Media.MIME_TYPE;
    static final String KEY_VIDEO_DATA = MediaStore.Video.Media.DATA;
    public String mFirstPath;
    //扫描媒体路径
    public String mScanPath;
    public String mScanType;

    private TextView mTitleView;
    private TextView mRightView;
    private ImageView mLeftView;
    private GridView mPhotoWall;
    private TextView mPhotoPreview;
    private RelativeLayout mProgressBar;

    private View mPopChooseView;
    private View mPopView;
    private ListView mAlbumListView;

    private ProgressDialog mImageUploadDialog;

    private int mType;//类型
    private boolean isMultiple = true;
    private boolean mLimitMax = false;//是否限制最大张数
    private int mMaxCount = 9;//默认最大张数是9
    private boolean mPreview; //是否显示底部预览按钮
    private String mTag = "";//标志位

    private PhotoAlbumAdapter mAlbumAdapter;
    private PhotoWallAdapter mPhotoAdapter;
    private ArrayList<String> mSelectionList = new ArrayList<>();
    private ArrayList<String> mUploadList = new ArrayList<>();

    //选中的个数
    private int mSelectedCount = 0;
    private int mUploadSucCount = 0;
    private int mUploadFailCount = 0;

    private static final Object mLock = new Object();

    /**
     * 有上限多选的选择图片
     *
     * @param context  上下文
     * @param maxPhoto 最多照片数
     * @return 启动信息
     */
    public static Intent createIntentForMultiLimitPic(Context context, int maxPhoto) {
        Intent intent = new Intent(context, PhotoWallActivity.class);
        intent.putExtra(EXTRA_IS_MUTIPLE, true);
        intent.putExtra(EXTRA_IS_LIMIT_MAX, true);
        intent.putExtra(EXTRA_MAX_PHOTO, maxPhoto);
        intent.putExtra(EXTRA_MEDIA_TYPE, TYPE_PICTURE);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_wall);

        if (savedInstanceState != null) {
            isMultiple = savedInstanceState.getBoolean(EXTRA_IS_MUTIPLE);
            mType = savedInstanceState.getInt(EXTRA_MEDIA_TYPE);
            mLimitMax = savedInstanceState.getBoolean(EXTRA_IS_LIMIT_MAX);
            mMaxCount = savedInstanceState.getInt(EXTRA_MAX_PHOTO);
            mTag = savedInstanceState.getString(EXTRA_TAG);
            mScanPath = savedInstanceState.getString("path");
            mScanType = savedInstanceState.getString("scan_type");
            mPreview = savedInstanceState.getBoolean(EXTRA_PREVIEW);
        } else {
            Intent intent = getIntent();
            mType = intent.getIntExtra(EXTRA_MEDIA_TYPE, 0);
            isMultiple = intent.getBooleanExtra(EXTRA_IS_MUTIPLE, false);
            mMaxCount = intent.getIntExtra(EXTRA_MAX_PHOTO, 9);
            mLimitMax = intent.getBooleanExtra(EXTRA_IS_LIMIT_MAX, false);
            mTag = intent.getStringExtra(EXTRA_TAG);
            mPreview = intent.getBooleanExtra(EXTRA_PREVIEW, false);
        }

        mTitleView = $(R.id.title_text);
        mProgressBar = $(R.id.progress_view);
        mRightView = $(R.id.title_icon_right);
        mLeftView = $(R.id.title_icon_left);
        mPhotoWall = $(R.id.photo_wall_grid);

        mPopChooseView = $(R.id.photo_wall_pop_choose);
        mPopView = $(R.id.photo_wall_pop);
        mAlbumListView = $(R.id.album_list);
        mPhotoPreview = $(R.id.photo_txt_preview);

        $(R.id.take_picture).setOnClickListener(v -> takePicture());
        $(R.id.title_text).setOnClickListener(v -> chooseAlbum());
        $(R.id.photo_wall_pop).setOnClickListener(v -> dismissPop());
        $(R.id.photo_wall_pop_choose).setOnClickListener(v -> dismissChoosePop());
        $(R.id.title_icon_right).setOnClickListener(v -> commit());
        $(R.id.title_icon_left).setOnClickListener(v -> backAction());
        initView();
        if(getIntent().getBooleanExtra(IS_ONLY_FROM_CAMERA,false)){
            takePicture();
//            finish();
        }else{
            refreshWall(PATH_ALL);
        }

    }

    private void backAction() {
        this.finish();
    }

    private void dismissChoosePop() {
        mPopChooseView.setVisibility(View.GONE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_IS_MUTIPLE, isMultiple);
        outState.putInt(EXTRA_MEDIA_TYPE, mType);

        outState.putBoolean(EXTRA_IS_LIMIT_MAX, mLimitMax);
        outState.putInt(EXTRA_MAX_PHOTO, mMaxCount);
        outState.putString(EXTRA_TAG, mTag);
        outState.putString("path", mScanPath);
        outState.putString("scan_type", mScanType);
        outState.putBoolean(EXTRA_PREVIEW, mPreview);
    }


    void takePicture() {
        dismissChoosePop();
        PermissionHandler.checkCamera(PhotoWallActivity.this, granted -> {
            if (granted) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                File file = ImageUtil.getOutputMediaFile();
                mScanPath = file.getPath();
                LogUtil.e("takePicture path = " + mScanPath);
                mScanType = "image/jpg";
                Uri contentUri = FileProvider.getUriForFile(this, FileUtil.FILE_PROVIDER_NAME1, file);

                //兼容版本处理，因为 intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) 只在5.0以上的版本有效
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip =
                            ClipData.newUri(getContentResolver(), "A photo", contentUri);
                    intent.setClipData(clip);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, contentUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                AlertToast.show("请开启相关权限");
            }
        });
    }

    /**
     * 选择相册
     */
    void chooseAlbum() {
        ArrayList<PhotoAlbumLVItem> list = getAlbums();
        if (list != null && list.size() > 0) {
            mAlbumAdapter = new PhotoAlbumAdapter(this);
            mAlbumAdapter.addData(list);
            mAlbumListView.setAdapter(mAlbumAdapter);
            mPopView.setVisibility(View.VISIBLE);
            mAlbumListView.setOnItemClickListener((parent, view, position, id) -> {
                refreshWall(mAlbumAdapter.getItem(position).getPathName());
                dismissPop();
            });
        } else {
            AlertToast.show("未找到其他相册");
        }
    }

    void dismissPop() {
        mPopView.setVisibility(View.GONE);
        mAlbumAdapter.clear();
    }

    void refreshWall(String path) {
        PermissionHandler.checkReadExternalStorage(this, granted -> {
            if (granted) {
                if (path.equals(PATH_ALL)) {
                    new AsyncLoadImagePaths().execute("");
                } else {
                    new AsyncLoadImagePaths().execute(path);
                }
            } else {
                AlertToast.show("权限被禁用,请到系统设置中授权");
                backAction();
            }
        });

    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath) {

        File folder = new File(folderPath);
        File[] allFiles = folder.listFiles();

        if (allFiles == null || allFiles.length == 0) {
            return null;
        }

        Arrays.sort(allFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }
        });

        ArrayList<String> imageFilePaths = new ArrayList<>();
        for (int i = allFiles.length - 1; i >= 0; i--) {
            if (isImage(allFiles[i].getName())) {
                imageFilePaths.add(folderPath + File.separator + allFiles[i].getName());
            }
        }

        imageFilePaths.add(0, "");
        return imageFilePaths;
    }


    void initView() {
        mTitleView.setText("选择相册");
        mLeftView.setImageResource(R.drawable.topbar_icon_close);
        if (isMultiple) {
            mRightView.setVisibility(View.VISIBLE);
            setRightViewText();
        }

        if (mPreview) {
            mPhotoPreview.setVisibility(View.VISIBLE);
            photoPreviewState();
            //预览图片
            mPhotoPreview.setOnClickListener(v -> {
                Intent intent = new Intent(PhotoWallActivity.this, ImagePreviewActivity.class);
                intent.putStringArrayListExtra(ImagePreviewActivity.EXTRA_SIZE_URLS_LIST, mSelectionList);
                intent.putStringArrayListExtra(ImagePreviewActivity.EXTRA_SELECTED_URLS_LIST, mSelectionList);
                startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            });
        } else {
            mPhotoPreview.setVisibility(View.GONE);
        }

        mTitleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_pull_normal, 0);
        mTitleView.setCompoundDrawablePadding(SizeUtil.dip2px(4f));
        mTitleView.setPadding(SizeUtil.dip2px(18f), 0, 0, 0);

        mPhotoWall.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                if (mType == TYPE_PICTURE && !(isMultiple && mLimitMax && mSelectedCount == mMaxCount))
                    takePicture();
            } else {
                String path = mPhotoAdapter.getItem(position);
                if (isMultiple) {
                    if (mSelectionList.indexOf(path) >= 0) {
                        view.findViewById(R.id.photo_wall_check).setVisibility(View.GONE);
                        mSelectionList.remove(path);
                        mSelectedCount--;
                    } else {
                        if (!(mLimitMax && mSelectedCount == mMaxCount)) {
                            view.findViewById(R.id.photo_wall_check).setVisibility(View.VISIBLE);
                            mSelectionList.add(path);
                            mSelectedCount++;
                        }
                    }
                    setRightViewText();
                } else {
                    mSelectionList.add(path);
                    mSelectedCount++;
                    commit();
                }
                if (mPreview) {
                    photoPreviewState();
                }
            }
        });
    }

    /**
     * 更新预览按钮的状态
     */
    private void photoPreviewState() {
        if (mSelectionList.size() > 0) {
            mPhotoPreview.setEnabled(true);
        } else {
            mPhotoPreview.setEnabled(false);
        }
    }

    /**
     * 设置右边文字选中数量
     */
    private void setRightViewText() {
        if (mLimitMax) {
            mRightView.setText(getResources().getString(R.string.title_text_import_limit, String.valueOf(mSelectedCount), String.valueOf(mMaxCount)));
        } else {
            mRightView.setText(getResources().getString(R.string.title_text_import, String.valueOf(mSelectedCount)));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {

            if(getIntent().getBooleanExtra(IS_ONLY_FROM_CAMERA,false)){
                finish();
            }else{
                return;
            }


        }
        switch (requestCode) {
            case REQUEST_CAMERA:
                File file = new File(mScanPath);
                if (file.exists()) {
                    MediaScanner.scan(mScanPath, mScanType);
                    mSelectionList.add(mScanPath);
                    mSelectedCount++;
                    commit();
                }
                break;
            case REQUEST_CROP_CODE:
            case REQUEST_BATCH_CROP_CODE:
                setResult(RESULT_OK, data);
                finish();
                break;
            case REQUEST_PREVIEW_CODE: //在预览中回调回来的处理 ，预览中勾选的图片进行初始化勾选
                mSelectionList.clear();
                ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(EXTRA_SELECTED_URLS_LIST);
                boolean isCommit = data.getBooleanExtra(EXTRA_IS_COMMIT, false);
                mSelectionList.addAll(stringArrayListExtra);
                mSelectedCount = mSelectionList.size();
                setRightViewText();
                mPhotoAdapter.addUpSelectionMap(mSelectionList);
                if (isCommit) {
                    commit();
                }
                break;
            default:
        }
    }

    /**
     * 提交选择，有两种方式回调
     * 1.onActivityResult intent的path中
     * 2.ChoosePicEvent事件
     */
    private void commit() {

        Intent intent = new Intent();
        if(mSelectionList == null || mSelectionList.size() <=0){
            AlertToast.show("请至少选择一张图片");
            return;
        }
        if (isMultiple) {
            intent.putStringArrayListExtra(RETURN_PATH, mSelectionList);
        } else {
            intent.putExtra(RETURN_PATH, mSelectionList.get(0));
        }
        setResult(RESULT_OK, intent);
        //专门为chooseImage准备的事件
        ChoosePicEvent choosePicEvent = new ChoosePicEvent();
        choosePicEvent.tag = mTag;
        if (isMultiple) {
            choosePicEvent.type = ChoosePicEvent.TYPE_MULTI;
            choosePicEvent.pathList.addAll(mSelectionList);
        } else {
            choosePicEvent.type = ChoosePicEvent.TYPE_SINGLE;
            choosePicEvent.path = mSelectionList.get(0);
        }
        RxBus.getDefault().post(choosePicEvent);
        finish();

//        if (mSelectionList != null && mSelectionList.size() > 0) {
//            if (getIntent().getBooleanExtra(EXTRA_IS_CROP, false)) {
//                if (isMultiple) {
//                    doBatchCropPhoto(mSelectionList);
//                } else {
//                    doCropPhoto(mSelectionList.get(0));
//                }
//            } else {
//                if (getIntent().getBooleanExtra(EXTRA_NEED_UPLOAD, false)) {
//                    mUploadList.clear();
//                    updateDialog();
//                    for (String path : mSelectionList) {
//                        ImageUploader.upload(this, path, true, new ImageUploader.Callback() {
//                            @Override
//                            public void success(String qiniuPath) {
//                                updateForOneImage(true, qiniuPath);
//                            }
//
//                            @Override
//                            public void fail() {
//                                updateForOneImage(false, "");
//                            }
//                        });
//                    }
//                } else {
//
//                }
//            }
//        }
    }

    private void updateForOneImage(boolean suc, String url) {
        if (suc) {
            mUploadSucCount++;
            mUploadList.add(url);
        } else {
            mUploadFailCount++;
        }
        updateDialog();
    }

    private void updateDialog() {
        if (isFinishing()) return;
        if (mImageUploadDialog == null) {
            mImageUploadDialog = new ProgressDialog(this);
            mImageUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置圆形进度条
            mImageUploadDialog.setCancelable(false);
            mImageUploadDialog.setCanceledOnTouchOutside(false);
            mImageUploadDialog.setTitle("请稍后");
        }
        if (mUploadSucCount + mUploadFailCount >= mSelectedCount) {
            if (mImageUploadDialog.isShowing()) {
                mImageUploadDialog.dismiss();
            }
            if (mUploadSucCount > 0) {
                Intent intent = new Intent();
                if (isMultiple) {
                    intent.putStringArrayListExtra(RETURN_PATH, mUploadList);
                } else {
                    intent.putExtra(RETURN_PATH, mUploadList.get(0));
                }
                setResult(RESULT_OK, intent);
            }
            finish();
        } else {
            mImageUploadDialog.setMessage(String.format(
                    "图片上传中（%s/%s）...", String.valueOf(mUploadSucCount), String.valueOf(mSelectedCount)));
            mImageUploadDialog.show();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mRightView.setText(getResources().getString(R.string.title_text_import, String.valueOf(mSelectedCount)));
        refreshWall(PATH_ALL);
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths() {
        ArrayList<String> imageFilePaths = new ArrayList<>();
        synchronized (mLock) {

            ContentResolver mContentResolver = getContentResolver();
            if (mContentResolver != null) {

                //只查询jpg和jpeg的图片,按最新修改排序
                Cursor cursor = mContentResolver.query(mImageUri,
                        new String[]{KEY_IMAGE_DATA},
                        KEY_IMAGE_MIME_TYPE + "=? or " +
                                KEY_IMAGE_MIME_TYPE + "=? or " +
                                KEY_IMAGE_MIME_TYPE + "=? or " +
                                KEY_IMAGE_MIME_TYPE + "=? or " +
                                KEY_IMAGE_MIME_TYPE + "=? or " +
                                KEY_IMAGE_MIME_TYPE + "=?",
                        new String[]{
                                "image/bmp", "image/jpg",
                                "image/png", "image/jpeg",
                                "image/gif", "image/webp"},
                        MediaStore.Images.Media.DATE_MODIFIED);


                if (cursor != null) {
                    System.out.print("-----------------" + cursor.getCount());
                    if (cursor.moveToLast()) {
                        //路径缓存，防止多次扫描同一目录
                        HashSet<String> cachePath = new HashSet<>();


                        while (true) {


                            // 获取图片的路径
                            String imagePath = cursor.getString(0);

                            if (TextUtils.isEmpty(imagePath)) {
                                if (!cursor.moveToPrevious()) {
                                    break;
                                }
                                continue;
                            }

                            File parentFile = new File(imagePath).getParentFile();
                            String parentPath = parentFile.getAbsolutePath();
                            //不扫描重复路径

                            if (!parentFile.exists()) {
                                if (!cursor.moveToPrevious()) {
                                    break;
                                }
                                continue;
                            }
                            if (!cachePath.contains(parentPath)) {
                                cachePath.add(parentPath);
                                File folder = new File(parentPath);
                                File[] allFiles = folder.listFiles();
                                if (allFiles == null || allFiles.length == 0) {
                                    continue;
                                }

                                Arrays.sort(allFiles, (f1, f2) -> {
                                    long diff = f1.lastModified() - f2.lastModified();
                                    if (diff > 0) {
                                        return 1;
                                    } else if (diff == 0) {
                                        return 0;
                                    } else {
                                        return -1;
                                    }
                                });

                                for (int i = allFiles.length - 1; i >= 0; i--) {
                                    if (isImage(allFiles[i].getName())) {
                                        imageFilePaths.add(parentPath + File.separator + allFiles[i].getName());
                                    }
                                }

                            }


                            if (!cursor.moveToPrevious()) {
                                break;
                            }
                        }
                    }
                    cursor.close();
                }
            }

            imageFilePaths.add(0, "");
        }
        return imageFilePaths;
    }


    private ArrayList<PhotoAlbumLVItem> getAlbums() {
        ArrayList<PhotoAlbumLVItem> albumList = null;
        synchronized (mLock) {
            ContentResolver mContentResolver = getContentResolver();
            // 只查询jpg和png的图片
            Cursor cursor = mContentResolver.query(mImageUri,
                    new String[]{KEY_IMAGE_DATA},
                    KEY_IMAGE_MIME_TYPE + "=? or " +
                            KEY_IMAGE_MIME_TYPE + "=? or " +
                            KEY_IMAGE_MIME_TYPE + "=? or " +
                            KEY_IMAGE_MIME_TYPE + "=? or " +
                            KEY_IMAGE_MIME_TYPE + "=? or " +
                            KEY_IMAGE_MIME_TYPE + "=?",
                    new String[]{
                            "image/jpg", "image/jpeg",
                            "image/png", "image/bmp",
                            "image/gif", "image/webp"},
                    MediaStore.Images.Media.DATE_MODIFIED);

            int currentImageCount = 0;
            if (cursor != null) {

                if (cursor.moveToLast()) {
                    //路径缓存，防止多次扫描同一目录
                    HashSet<String> cachePath = new HashSet<>();

                    while (true) {

                        // 获取图片的路径
                        String imagePath = cursor.getString(0);
                        File parentFile = new File(imagePath).getParentFile();
                        String parentPath = parentFile.getAbsolutePath();
                        //不扫描重复路径

                        if (!parentFile.exists()) {
                            if (!cursor.moveToPrevious()) {
                                break;
                            }
                            continue;
                        }
                        LogUtil.i("path = " + parentPath);
                        if (!cachePath.contains(parentPath)) {
                            LogUtil.e("path = " + parentPath);
                            if (albumList == null) {
                                albumList = new ArrayList<>();
                            }
                            albumList.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile),
                                    getFirstImagePath(parentFile)));
                            cachePath.add(parentPath);
                            mFirstPath = getFirstImagePath(parentFile);
                            currentImageCount += getImageCount(parentFile);
                        }


                        if (!cursor.moveToPrevious()) {
                            break;
                        }
                    }
                }
                cursor.close();
            }
            if (currentImageCount > 0) {
                PhotoAlbumLVItem allItem = new PhotoAlbumLVItem(PATH_ALL, currentImageCount, mFirstPath);
                albumList.add(0, allItem);
            }
        }
        return albumList;
    }

    /**
     * 获取目录中图片的个数。
     */
    private int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        if (files == null) {
            return 0;
        }
        for (File file : files) {
            if (isImage(file.getName())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return null;
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            if (isImage(file.getName())) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * 执行裁剪操作
     */
//    private void doCropPhoto(String path) {
//        String scale = getIntent().getStringExtra(EXTRA_CROP_SCALE);
//        boolean isNeedRotate = getIntent().getBooleanExtra(EXTRA_NEED_ROTATE, false);
//        boolean isNeedUpload = getIntent().getBooleanExtra(EXTRA_NEED_UPLOAD, false);
//        Intent intent = PhotoCropActivity.createIntent(this, path, scale, isNeedRotate, isNeedUpload);
//        startActivityForResult(intent, REQUEST_CROP_CODE);
//    }

//    private void doBatchCropPhoto(ArrayList<String> paths) {
//        String scale = getIntent().getStringExtra(EXTRA_CROP_SCALE);
//        Intent intent = PhotoBatchCropActivity.getInstance(this, paths, scale);
//        startActivityForResult(intent, REQUEST_BATCH_CROP_CODE);
//    }

    @Override
    public void onBackPressed() {
        if (mPopView.getVisibility() == View.VISIBLE) {
            mPopView.setVisibility(View.GONE);
        } else if (mPopChooseView.getVisibility() == View.VISIBLE) {
            dismissChoosePop();
        } else {
            super.onBackPressed();
        }
    }

    class AsyncLoadImagePaths extends AsyncTask<String, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            if (TextUtils.isEmpty(params[0])) {
                return getLatestImagePaths();
            } else {
                return getAllImagePathsByFolder(params[0]);
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            mPhotoAdapter = new PhotoWallAdapter(PhotoWallActivity.this, mSelectionList);
            mPhotoAdapter.addData(strings);
            mPhotoWall.setAdapter(mPhotoAdapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 判断该文件是否是一个图片。
     */
    public boolean isImage(String fileName) {
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".bmp")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".webp");
    }

    /**
     * 判断该文件是否是一个短视频。
     */
    public boolean isVideo(String fileName) {
        return fileName.endsWith(".mp4");
    }


}
