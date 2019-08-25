package com.ocram.qichwadic.presentation.ui.activity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.ocram.qichwadic.BuildConfig;
import com.ocram.qichwadic.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.ivQichwa20Fb) ImageView ivQichwa20Fb;
    @BindView(R.id.ivQichwa20Yt) ImageView ivQichwa20Yt;
    @BindView(R.id.ivQichwa20Mail) ImageView ivQichwa20Mail;
    @BindView(R.id.tvWebsite) TextView tvWebsite;
    @BindView(R.id.tvProjectCollab) TextView tvProjectCollab;
    @BindView(R.id.tvRateApp) TextView tvRateApp;

    private final String QICHWA_FB_ID = "556776684487945";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ivQichwa20Fb.setOnClickListener(view -> openFbPageIntent(QICHWA_FB_ID));
        tvProjectCollab.setText(HtmlCompat.fromHtml(getString(R.string.about_project_collaboration), HtmlCompat.FROM_HTML_MODE_LEGACY));
        tvProjectCollab.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick({R.id.ivQichwa20Yt, R.id.tvWebsite, R.id.tvRateApp})
    public void openWebItem(View v){
        String uri;
        switch (v.getId()){
            case R.id.ivQichwa20Yt: uri = "https://www.youtube.com/channel/UCZ5kIwvo7DlN9qdrQrjUOkg";break;
            case R.id.tvRateApp: uri = "market://details?id=com.ocram.qichwadic";break;
            default: uri = "https://www.qichwa.net";
        }
        openActionViewIntent(uri);
    }

    @OnClick({R.id.ivQichwa20Mail})
    public void openMailSender(View v){
        openEmailIntent(BuildConfig.DEV_EMAIL, getString(R.string.contact_subject));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initViews() {
        setTitle(getString(R.string.nav_about));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}