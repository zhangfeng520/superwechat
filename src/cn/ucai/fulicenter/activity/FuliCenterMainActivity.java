package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.Fragments.BoutiqueFragment;
import cn.ucai.fulicenter.Fragments.CartFragment;
import cn.ucai.fulicenter.Fragments.CategoryFragment;
import cn.ucai.fulicenter.Fragments.NewGoodsFragment;
import cn.ucai.fulicenter.Fragments.PersonalCenterFragment;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;

/**
 * Created by sks on 2016/8/1.
 */
public class FuliCenterMainActivity extends BaseActivity {
    private static final String TAG = FuliCenterMainActivity.class.getSimpleName();
    RadioButton rbNewGoods, rbBoutique, rbCategory, rbCart, rbContact;
    TextView tvCartHint;
    RadioButton[] rbArr;
    Fragment[] mFragments;
    int index;
    int currentIndex;
    int haha;
    ViewPager mvpGoods;
    GoodsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main);
        initFragment();
        initView();
        setListener();
        setRadioButtonStatus(0);
        updateCount();
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
                    case 2:
                        setRadioButtonStatus(2);
                        break;
                    case 3:
                        setRadioButtonStatus(3);
                        break;
                    case 4:
                        if (FuliCenterApplication.getInstance().getUser() == null) {
                            startActivity(new Intent(FuliCenterMainActivity.this, LoginActivity.class));
                        }
                        setRadioButtonStatus(4);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void initFragment() {
        mFragments = new Fragment[5];
        mFragments[0] = new NewGoodsFragment();
        mFragments[1] = new BoutiqueFragment();
        mFragments[2] = new CategoryFragment();
        mFragments[3] = new CartFragment();
        mFragments[4] = new PersonalCenterFragment();

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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public Parcelable saveState() {
            return super.saveState();
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

    int action01;
    StringBuilder builder = new StringBuilder("");

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.rbGoodNews:
                index = 0;
                break;
            case R.id.rbBoutique:
                index = 1;
                break;
            case R.id.rbCategory:
                index = 2;
                break;
            case R.id.rbCart:
                if (FuliCenterApplication.getInstance().getUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class).putExtra("action", currentIndex));
                } else {
                    index = 3;
                }
                break;
            case R.id.rbContact:
                if (FuliCenterApplication.getInstance().getUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class).putExtra("action", currentIndex));
                } else {
                    index = 4;
                }
                break;
        }
//        if (index != 4) {
//            builder.append(index);
//        }
//        Log.e(TAG, "builder=" + builder);
//        String s = builder.toString();
//        Log.e(TAG, "s=" + s);
//        action01 = Integer.parseInt(builder.substring(s.length()-1));
        mvpGoods.setCurrentItem(index);
        if (index != haha) {
            setRadioButtonStatus(index);
        }

        if (index != 4 && index != 3) {
            currentIndex = index;
        }
        haha = 100;
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
    }

    private void setRadioButtonStatus(int index) {
        for (int i = 0; i < rbArr.length; i++) {
            if (index == i) {
                rbArr[i].setChecked(true);
            } else {
                rbArr[i].setChecked(false);
            }
        }
    }

    int action;

    @Override
    protected void onResume() {
        super.onResume();

//        if (FuliCenterApplication.getInstance().getUser() != null) {
//        } else {
//            setRadioButtonStatus(action);
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        action = getIntent().getIntExtra("action", 0);
        FuliCenterApplication.getInstance().setAction(action);
        Log.e(TAG, "returnaction=" + action);
        if (FuliCenterApplication.getInstance().getUser() != null) {
            mvpGoods.setCurrentItem(4);
        } else {
            int yy = FuliCenterApplication.getInstance().getAction();
            mvpGoods.setCurrentItem(yy);
        }
    }

    /**
     * 发送广播，在用户退出后购物车数量清空，实时更新
     */
    class UpdateCountReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = FuliCenterApplication.getInstance().getCartCount();
            if (count > 0) {
                tvCartHint.setText(String.valueOf(count));
                tvCartHint.setVisibility(View.VISIBLE);
            } else {
                tvCartHint.setVisibility(View.GONE);
            }
            if (FuliCenterApplication.getInstance().getUser() == null) {
                tvCartHint.setVisibility(View.GONE);
            }
        }
    }
    UpdateCountReceiver mReceiver;

    private void updateCount() {
        mReceiver = new UpdateCountReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        filter.addAction("update");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    //    String fragmentName;
//    public Handler handler =new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg !=null) {
//                switch (msg.what) {
//                    case 100:
//                        fragmentName = SettingsFragment.class.getName();
//                        replaceFragment(R.id.fragment1, fragmentName);
//                        break;
//                    case 200:
//                        fragmentName = PersonalCenterFragment.class.getName();
//                        replaceFragment(R.id.fragment2,fragmentName);
//                        break;
//                }
//            }
//        }
//    };
//    protected void replaceFragment(int viewResource, String fragmentName) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = Fragment.instantiate(this, fragmentName);
//        ft.replace(viewResource, fragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.commit();
//        getSupportFragmentManager().executePendingTransactions();
//    }
}
