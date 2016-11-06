package co.cristiangarcia.bibliareinavalera.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import co.cristiangarcia.bibliareinavalera.R;
import co.cristiangarcia.bibliareinavalera.fragment.ATListFragment;
import co.cristiangarcia.bibliareinavalera.fragment.FrecListFragment;
import co.cristiangarcia.bibliareinavalera.fragment.NTListFragment;
import co.cristiangarcia.bibliareinavalera.util.ColorPickerDialogPreference;
import co.cristiangarcia.bibliareinavalera.util.DrawerLayoutHorizontalSupport;

public abstract class BaseActivity extends AppCompatActivity {
    private DrawerLayoutHorizontalSupport mDrawerLayout;
    private View mDrawerLeft;
    private ActionBarDrawerToggle mDrawerToggle;
    private TabLayout tab_menu;
    private Toolbar toolbar;
    private ViewPager viewPagerMenu;

    class ViewPagerMenuAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerMenuAdapter(FragmentManager manager) {
            super(manager);
        }

        public Fragment getItem(int position) {
            return (Fragment) this.mFragmentList.get(position);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return (CharSequence) this.mFragmentTitleList.get(position);
        }
    }

    public void onCreate(Bundle savedInstanceState, int contentView) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("modo_nocturno_preference", false) && contentView == R.layout.libro_main) {
            setTheme(ColorPickerDialogPreference.getStyle(PreferenceManager.getDefaultSharedPreferences(this).getString("theme_color_preference", "1")));
        } else {
            setTheme(ColorPickerDialogPreference.getStyle(PreferenceManager.getDefaultSharedPreferences(this).getString("theme_color_preference", "1")));
        }
        setContentView(contentView);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.mDrawerLeft = findViewById(R.id.left_drawer);
        this.viewPagerMenu = (ViewPager) findViewById(R.id.viewpager_menu);
        Log.v("viepager",this.viewPagerMenu.toString() );


        this.viewPagerMenu.setOffscreenPageLimit(3);
        this.viewPagerMenu.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (BaseActivity.this.mDrawerLayout == null) {
                    return;
                }
                if (position == 2) {
                    BaseActivity.this.mDrawerLayout.setverifyScrollChild(false);
                } else {
                    BaseActivity.this.mDrawerLayout.setverifyScrollChild(true);
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        setupViewPagerMenu(this.viewPagerMenu);
        this.tab_menu = (TabLayout) findViewById(R.id.tabs_menu);
        this.tab_menu.setupWithViewPager(this.viewPagerMenu);
        this.mDrawerLayout = (DrawerLayoutHorizontalSupport) findViewById(R.id.drawer_layout);
        this.mDrawerLayout.setInterceptTouchEventChildId(R.id.viewpager_menu);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, this.toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                if (BaseActivity.this.mDrawerLayout != null) {
                    BaseActivity.this.mDrawerLayout.setverifyScrollChild(false);
                }
            }

            public void onDrawerOpened(View drawerView) {
                if (BaseActivity.this.mDrawerLayout != null && BaseActivity.this.viewPagerMenu.getCurrentItem() != 2) {
                    BaseActivity.this.mDrawerLayout.setverifyScrollChild(true);
                }
            }
        };
        this.mDrawerLayout.addDrawerListener(this.mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        this.mDrawerToggle.syncState();
    }

    public void onBackPressed() {
        if (isNavigationDrawerOpen()) {
            closeNavigationDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void onStop() {
        super.onStop();
        if (isNavigationDrawerOpen()) {
            closeNavigationDrawer();
        }
    }

    public void closeNavigationDrawer() {
        if (this.mDrawerLayout != null && this.mDrawerLeft != null) {
            this.mDrawerLayout.closeDrawer(this.mDrawerLeft);
        }
    }

    public boolean isNavigationDrawerOpen() {
        return (this.mDrawerLayout == null || this.mDrawerLeft == null || !this.mDrawerLayout.isDrawerOpen(this.mDrawerLeft)) ? false : true;
    }

    private void setupViewPagerMenu(ViewPager viewPager) {
        ViewPagerMenuAdapter adapter = new ViewPagerMenuAdapter(getSupportFragmentManager());
        adapter.addFragment(ATListFragment.newInstance(), getString(R.string.antiguo));
        adapter.addFragment(NTListFragment.newInstance(), getString(R.string.nuevo));
        adapter.addFragment(FrecListFragment.newInstance(), getString(R.string.frecuentes));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }
}
