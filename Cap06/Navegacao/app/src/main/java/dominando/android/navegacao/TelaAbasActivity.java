package dominando.android.navegacao;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class TelaAbasActivity extends AppCompatActivity {
    ViewPager mViewPager;
    TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_abas);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        AbasPagerAdapter pagerAdapter = new AbasPagerAdapter(this,
                getSupportFragmentManager());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout = (TabLayout)findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}

