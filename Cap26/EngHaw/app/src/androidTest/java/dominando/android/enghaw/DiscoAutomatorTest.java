package dominando.android.enghaw;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class DiscoAutomatorTest {
    public static final String PACOTE_APP = "dominando.android.enghaw";
    public static final String MAIN_ACTIVITY = PACOTE_APP +".MainActivity";
    private static final int LAUNCH_TIMEOUT = 5000;
    private Context mContext;
    private UiDevice mUIDevice;
    @Before
    public void startMainActivity() {
        mContext = InstrumentationRegistry.getContext();
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mUIDevice.pressHome();
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mUIDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
        final Intent intent = mContext.getPackageManager()
                .getLaunchIntentForPackage(PACOTE_APP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.wait(Until.hasObject(By.pkg(MAIN_ACTIVITY).depth(0)), LAUNCH_TIMEOUT);
    }
    @Test
    public void testFavorito(){
        try {
            UiSelector recyclerViewSelector = new UiSelector().className(RecyclerView.class);
            UiScrollable recyclerViewWeb = new UiScrollable(recyclerViewSelector);
            int count = 0;
            UiSelector selector = new UiSelector().className(TextView.class).text("Minuano");
            while (!recyclerViewWeb.getChild(selector).exists() && count < 3){
                recyclerViewWeb.scrollForward();
                count++;
            }
            if (count == 3) fail("Não achou Minuano");
            UiObject linha = recyclerViewWeb.getChild(selector);
            linha.clickAndWaitForNewWindow(2000);
            mUIDevice.findObject(new UiSelector().resourceId(PACOTE_APP +":id/fabFavorito")).click();
            mUIDevice.pressBack();
            UiSelector viewPagerSelector = new UiSelector().resourceId(PACOTE_APP + ":id/viewPager");
            UiObject viewPager = mUIDevice.findObject(viewPagerSelector);
            viewPager.swipeLeft(50);
            UiObject recyclerViewDb = viewPager
                    .getChild(new UiSelector().index(1))
                    .getChild(recyclerViewSelector);
            assertTrue(recyclerViewDb.getChild(selector).exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    private String getLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}

