package cs160.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class RepInfoWatchActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_info_watch);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final Intent fromWatchEntry = getIntent();
        final Bundle repBundles = fromWatchEntry.getExtras();

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ViewPager repsViewPager = (ViewPager) findViewById(R.id.reps_view_pager);
                Log.d("RepInfoWatchActivity", repBundles.toString());
                RepsViewPagerAdapter adapter = new RepsViewPagerAdapter(
                        RepInfoWatchActivity.this.getSupportFragmentManager(),
                        repBundles);
                repsViewPager.setAdapter(adapter);

                /*
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragments.size(); i++) {
                    Log.d("RepInfoWatchActivity", "setting data");
                    ((RepFragment) fragments.get(i)).setBundle(reps.getBundle("bundle" + Integer.toString(i)));
                }
                */
            }
        });




    }

    public class RepsViewPagerAdapter extends FragmentPagerAdapter {

        protected Bundle reps;

        public RepsViewPagerAdapter(FragmentManager fm, Bundle reps) {
            super(fm);
            if (fm.getFragments() != null) {
                fm.getFragments().clear();
            }
            this.reps = reps;
        }

        @Override
        public Fragment getItem(int i) {
            RepFragment fragment = RepFragment.newInstance(reps.getBundle("bundle" + Integer.toString(i)));
            return fragment;
        }

        @Override
        public int getCount() {
            return reps.getInt("size");
        }

    }
}


