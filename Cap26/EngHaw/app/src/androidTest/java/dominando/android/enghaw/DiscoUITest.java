package dominando.android.enghaw;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DiscoUITest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp(){
        mActivityRule.getActivity().deleteDatabase(DiscoDbHelper.DB_NAME);
    }

    @Test
    public void testAdicionarFavorito() {
        final String RECYCLER_WEB = "web";
        final String RECYCLER_FAVORITO = "fav";
        final String DISCO_CLICADO = "Minuano";

        onView(withTagValue(is((Object) RECYCLER_WEB)))
                .perform(scrollTo(hasDescendant(withText(DISCO_CLICADO))));
        onView(withTagValue(is((Object) RECYCLER_WEB)))
                .perform(actionOnItem(hasDescendant(withText(DISCO_CLICADO)), click()));
        onView(withId(R.id.txtTitulo)).check(matches(withText(DISCO_CLICADO)));
        onView(withId(R.id.fabFavorito)).perform(click());
        pressBack();
        onView(withId(R.id.viewPager)).perform(swipeLeft());
        onView(withTagValue(is((Object) RECYCLER_FAVORITO)))
                .check(matches(hasDescendant(withText(DISCO_CLICADO))));
    }
}

