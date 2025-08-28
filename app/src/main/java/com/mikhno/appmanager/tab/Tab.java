package com.mikhno.appmanager.tab;

import com.google.android.material.tabs.TabLayout;

public class Tab extends TabLayout.Tab {
    private final TabLayout.Tab tab;
    private final String rn;

    public Tab(TabLayout tabLayout, String text, String rn) {
        this.tab = tabLayout.newTab();
        this.rn = rn;
        this.tab.setText(text);
    }

    public String getRunnable() {
        return rn;
    }

    public TabLayout.Tab getTab() {
        return tab;
    }
}
