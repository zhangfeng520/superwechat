package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.Fragments.BoutiqueFragment;
import cn.ucai.fulicenter.Fragments.CategoryFragment;
import cn.ucai.fulicenter.Fragments.NewGoodsFragment;
import cn.ucai.fulicenter.R;

/**
 * Created by sks on 2016/8/1.
 */
public class FuliCenterMainActivity extends BaseActivity {
    private static final String TAG = FuliCenterMainActivity.class.getSimpleName();
    RadioButton rbNewGoods,rbBoutique,rbCategory,rbCart,rbContact;
    TextView tvCartHint;
    RadioButton[] rbArr;
    Fragment[] mFragments;
    int index;
    int currentIndex;
    ViewPager mvpGoods;
    GoodsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main);
        initFragment();
        initView();
        setListener();
    }

    private void setListener() {
        mvpGoods.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setRadioButtonStatus(0);
                        break;
                    case 1:
                        setRadioButtonStatus(1);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initFragment() {
        mFragments = new Fragment[3];
        mFragments[0] = new NewGoodsFragment();
        mFragments[1] = new BoutiqueFragment();
        mFragments[2] = new CategoryFragment();

    }
    class GoodsAdapter extends FragmentPagerAdapter {
        Fragment[] fragments;
        public GoodsAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    private void initView() {
        rbNewGoods = (RadioButton) findViewById(R.id.rbGoodNews);
        rbBoutique = (RadioButton) findViewById(R.id.rbBoutique);
        rbCategory = (RadioButton) findViewById(R.id.rbCategory);
        rbCart = (RadioButton) findViewById(R.id.rbCart);
        rbContact = (RadioButton) findViewById(R.id.rbContact);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);
        rbArr = new RadioButton[5];
        rbArr[0] = rbNewGoods;
        rbArr[1] = rbBoutique;
        rbArr[2] = rbCategory;
        rbArr[3] = rbCart;
        rbArr[4] = rbContact;
        mvpGoods = (ViewPager) findViewById(R.id.vpGoods);
        mAdapter = new GoodsAdapter(getSupportFragmentManager(), mFragments);
        mvpGoods.setAdapter(mAdapter);
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.rbGoodNews:
                index=0;
                mvpGoods.setCurrentItem(0);
                break;
            case R.id.rbBoutique:
                index=1;
                mvpGoods.setCurrentItem(1);
                break;
            case R.id.rbCategory:
                index=2;
                mvpGoods.setCurrentItem(2);
                break;
            case R.id.rbCart:
                index=3;
                break;
            case R.id.rbContact:
                index=4;
                break;
        }
        if (index != currentIndex) {
            setRadioButtonStatus(index);
        }
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
        currentIndex = index;
    }

    private void setRadioButtonStatus(int index) {
        for(int i=0;i<rbArr.length;i++) {
            if (index == i) {
                rbArr[i].setChecked(true);
            } else {
                rbArr[i].setChecked(false);
            }
        }
    }
}
