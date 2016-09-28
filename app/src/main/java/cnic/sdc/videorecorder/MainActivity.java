package cnic.sdc.videorecorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements VideoItemFragment.OnListFragmentInteractionListener {

    VideoItemFragment vfgs;

    static final int VIDEO_GET_RCODE = 1;   //录制视频的request code

    // 分辨率
    static final PredefinedCaptureConfigurations.CaptureResolution[] resolutions = new PredefinedCaptureConfigurations.CaptureResolution[]{
            PredefinedCaptureConfigurations.CaptureResolution.RES_480P,
            PredefinedCaptureConfigurations.CaptureResolution.RES_720P,
            PredefinedCaptureConfigurations.CaptureResolution.RES_1080P
    };

    // 视频质量
    final PredefinedCaptureConfigurations.CaptureQuality[] qualitys = new PredefinedCaptureConfigurations.CaptureQuality[]{
            PredefinedCaptureConfigurations.CaptureQuality.LOW,
            PredefinedCaptureConfigurations.CaptureQuality.MEDIUM,
            PredefinedCaptureConfigurations.CaptureQuality.HIGH
    };

    int RESOLUTION_POS = 1;     //分辨率index
    int QUALITY_POS = 1;        //视频质量idex
    int DURATION = -1;          //持续时间(秒)
    int FILESIZE = -1;          //文件大小(MB)
    boolean SHOWTIMER = false;  //是否在录制视频的同时显示计时器

    String filename = null; //视频文件

    // 默认分辨率
    PredefinedCaptureConfigurations.CaptureResolution RESOLUTION = resolutions[RESOLUTION_POS];

    // 默认视频质量
    PredefinedCaptureConfigurations.CaptureQuality QUALITY = qualitys[QUALITY_POS];

    // 设置视频录制参数
    Intent intent_seting;
    SharedPreferences settings;

    protected BottomSheetLayout bottomSheetLayout;  // 响应用户长按事件弹出操作菜单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);

        //获取一个Fragment
        vfgs = VideoItemFragment.newInstance();

        //将该Fragment添加到activity_main中
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, vfgs).commit();

        intent_seting = new Intent();
        intent_seting.setClass(MainActivity.this, SetsActivity.class);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

    }

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.videoset:
                startActivity(intent_seting);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 录制视频后的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case VIDEO_GET_RCODE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    filename = null;
                } else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
                    filename = null;
                }
                filename = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);

                vfgs.addItem(new VideoItem(ItemType.VIDEO, filename));

                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private CaptureConfiguration createCaptureConfiguration() {

        try {
            RESOLUTION_POS = Integer.parseInt(settings.getString("resolution_list", "-1"));
        }catch (NumberFormatException e){
            RESOLUTION_POS = -1;
        }
        if(RESOLUTION_POS > -1){
            RESOLUTION = resolutions[RESOLUTION_POS];
        }

        try {
            QUALITY_POS = Integer.parseInt(settings.getString("quality_list", "-1"));
        }catch (NumberFormatException e){
            RESOLUTION_POS = -1;
        }
        if(QUALITY_POS > -1){
            QUALITY = qualitys[QUALITY_POS];
        }

        try {
            DURATION = Integer.parseInt(settings.getString("duration_value", "-1"));
        }catch (NumberFormatException e){
            DURATION = -1;
        }
        if(DURATION == -1){
            DURATION = CaptureConfiguration.NO_DURATION_LIMIT;
        }

        try {
            FILESIZE = Integer.parseInt(settings.getString("filesize_value", "-1"));
        }catch (NumberFormatException e){
            FILESIZE = -1;
        }
        if(FILESIZE == -1){
            FILESIZE = CaptureConfiguration.NO_FILESIZE_LIMIT;
        }

        SHOWTIMER = settings.getBoolean("showtimer_switch", false);

        CaptureConfiguration config = new CaptureConfiguration(RESOLUTION, QUALITY, DURATION, FILESIZE, SHOWTIMER);

        return config;
    }

    @Override
    public void onFragmentClick(VideoItem item) {
        switch (item.item_type){
            case VIDEO:     //播放视频
                Uri fp = Uri.parse(item.path);
                File f = new File(fp.toString());
                if(f.exists()){
                    playVideo(fp.toString());
                }
                break;

            case BUTTON:    //录制视频
                startVideoCaptureActivity();
                break;

            default:
                break;
        }

    }

    @Override
    public void onFragmentLongClick(VideoItem item) {
        switch (item.item_type){
            case VIDEO:
                MenuSheetView menuSheetView =
                        new MenuSheetView(MainActivity.this, MenuSheetView.MenuType.LIST, "操作...", new MenuSheetView.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (bottomSheetLayout.isSheetShowing()) {
                                    bottomSheetLayout.dismissSheet();
                                }
                                ConfirmDialog confirm = new ConfirmDialog(MainActivity.this, "确认", "删除后不可恢复，确定删除？", "确定", "取消") {
                                    @Override
                                    public void ok_action() {
                                        //执行删除操作
                                    }

                                    @Override
                                    public void no_action() {

                                    }
                                };
                                confirm.show();
                                return true;
                            }
                        });
                menuSheetView.inflateMenu(R.menu.video_opt);
                bottomSheetLayout.showWithSheetView(menuSheetView);
                break;

            case BUTTON:
                break;

            default:
                break;
        }
    }

    /**
     * 播视频
     */
    private void playVideo(String fpath){
        if (fpath == null) return;
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra("path", fpath);
        startActivity(intent);
    }

    /**
     * 录视频
     */
    private void startVideoCaptureActivity(){
        final CaptureConfiguration config = createCaptureConfiguration();
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = date_format.format(new Date()) + ".mp4";
        Intent intent = new Intent(MainActivity.this, VideoCaptureActivity.class);
        intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
        intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
        startActivityForResult(intent, VIDEO_GET_RCODE);
    }
}
