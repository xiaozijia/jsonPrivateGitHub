package com.order.food;



import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.order.food.base.BaseActivity;
import com.order.food.databinding.ActivityMainBinding;
import com.order.food.fragment.CarFragment;
import com.order.food.fragment.HomeFragment;
import com.order.food.fragment.MineFragment;
import com.order.food.fragment.OrderFragment;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private HomeFragment mHomeFragment;
    private CarFragment mCarFragment;
    private OrderFragment mOrderFragment;
    private MineFragment mMineFragment;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {
        mBinding.mainBottomNv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    selectedFragment(0);
                }else if (item.getItemId()==R.id.car){
                    selectedFragment(1);
                }else if (item.getItemId()==R.id.order){
                    selectedFragment(2);
                }else {
                    selectedFragment(3);
                }
                return true;
            }
        });


    }

    @Override
    protected void initData() {
        selectedFragment(0);
    }


    public void selectedFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        if (position == 0) {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                transaction.add(R.id.content, mHomeFragment);
            } else {
                transaction.show(mHomeFragment);
            }
        } else if (position == 1) {
            if (mCarFragment == null) {
                mCarFragment = new CarFragment();
                transaction.add(R.id.content, mCarFragment);
            } else {
                transaction.show(mCarFragment);
                mCarFragment.refreshData();
            }
        } else if (position == 2) {
            if (mOrderFragment == null) {
                mOrderFragment = new OrderFragment();
                transaction.add(R.id.content, mOrderFragment);
            } else {
                transaction.show(mOrderFragment);
                mOrderFragment.refreshData();
            }
        } else {
            if (mMineFragment == null) {
                mMineFragment = new MineFragment();
                transaction.add(R.id.content, mMineFragment);
            } else {
                transaction.show(mMineFragment);
            }
        }
        transaction.commit();

    }


    private void hideFragment(FragmentTransaction transaction) {
        if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }

        if (mCarFragment != null) {
            transaction.hide(mCarFragment);
        }

        if (mOrderFragment != null) {
            transaction.hide(mOrderFragment);
        }

        if (mMineFragment != null) {
            transaction.hide(mMineFragment);
        }
    }
}