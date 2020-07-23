package com.jht.chimera.io;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IO_main";

    private PackageInfo app;

    private TabLayout fragmentTab;
    private ViewPager2 viewPager;

    private TextView text_appVersion;
    private TextView text_ioVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentTab = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        text_appVersion = findViewById(R.id.text_app_version);
        text_ioVersion = findViewById(R.id.text_io_version);

        fragmentTab.setTabMode(TabLayout.MODE_FIXED);
        fragmentTab.setTabGravity(TabLayout.GRAVITY_FILL);

        CollectionAdapter viewPagerAdapter = new CollectionAdapter(getSupportFragmentManager(),getLifecycle());

        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(fragmentTab,viewPager,(tab, position) -> tab.setText(viewPagerAdapter.getPageTitle(position))).attach();

        //Get App Version
        try {
            app = getPackageManager().getPackageInfo("com.jht.chimera.chimeraio", 0);
            runOnUiThread(() -> text_appVersion.setText(app.versionName));
//            text_appVersion.setText(app.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        findViewById(R.id.fab_close).setOnClickListener(view -> {
            this.finish();
            onDestroy();
            System.exit(0);
        });
    }


}

