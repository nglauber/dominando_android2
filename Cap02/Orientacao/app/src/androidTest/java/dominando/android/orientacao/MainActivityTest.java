package dominando.android.orientacao;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
    MainActivity mActivity;
    Context mContext;
    @Before
    public void setUp() throws Exception {
        mContext = getTargetContext();
        mActivity = mActivityRule.getActivity();
    }
    @Test
    public void adicionarTextoTest() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.edt.setText("Texto 1");
                mActivity.meuBotaoClick(null);
            }
        });
        getInstrumentation().waitForIdleSync();

        ListView listView = (ListView)mActivity.findViewById(R.id.listView1);
        assertThat(listView.getCount(), is(1));
    }
}



