package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                PagesCreatedFragment pagesCreatedFragment = new PagesCreatedFragment();
                return pagesCreatedFragment;

            case 1:
                PagesFollowedFragment pagesFollowedFragment = new PagesFollowedFragment();
                return pagesFollowedFragment;

            case 2:
                AssociationPagesFragment associationPagesFragment = new AssociationPagesFragment();
                return associationPagesFragment;

            case 3:
                GlobalPagesFragment globalPagesFragment = new GlobalPagesFragment();
                return globalPagesFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Created";

            case 1:
                return "Following";

            case 2:
                return "Associated";

            case 3:
                return "Global";

            default:
                return null;
        }
    }
}
