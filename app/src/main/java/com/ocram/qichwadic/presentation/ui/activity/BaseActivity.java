package com.ocram.qichwadic.presentation.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ocram.qichwadic.BuildConfig;
import com.ocram.qichwadic.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    protected Unbinder unbinder;
    @Nullable @BindView(R.id.mToolbar) protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUGGABLE){
            this.activateStrictMode();
        }
        setContentView(getLayoutId());
        setupToolbar();
        bindViews();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected void activateStrictMode(){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    private void bindViews(){
        unbinder = ButterKnife.bind(this);
    }

    protected void setTitle(String title) {
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupToolbar(){
        toolbar = this.findViewById(R.id.mToolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void openEmailIntent(String toEmail, String subject){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + toEmail));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)));
    }

    protected void openActionViewIntent(String uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    protected void openFbPageIntent(String fbPageId){
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + fbPageId));
        } catch (Exception e) {
            intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + fbPageId));
        }
        startActivity(intent);
    }

    protected void openShareIntent(String textToShare) {
        final String TEXT_TYPE = "text/plain";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType(TEXT_TYPE);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)));
    }
}
