package dominando.android.hotel;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class HotelCrudTest {
    @Rule
    public ActivityTestRule<HotelActivity> mActivityRule = new ActivityTestRule(HotelActivity.class);
    @Test
    public void crudTest() {
        inserir();
        editar();
        excluir();
    }

    private void inserir() {
        onView(withId(R.id.fabAdd)).perform(click());
        onView(withId(R.id.txtNome)).perform(typeText("Hotel de Teste"));
        onView(withId(R.id.txtEndereco))
                .perform(typeText("Rua tal"));
        onView(withId(R.id.rtbEstrelas)).perform(RatingBarAction.setRating(2));
        onView(withId(R.id.txtEndereco))
                .perform(pressImeActionButton());
    }

    private void editar() {
        onData(withRowString(HotelSQLHelper.COLUNA_NOME, "Hotel de Teste"))
                .perform(click());
        onView(withId(R.id.acao_editar)).perform(click());
        onView(withId(R.id.txtNome)).perform(replaceText("Hotel Modificado"));
        onView(withId(R.id.txtEndereco)).perform(replaceText("Rua modificada"));
        onView(withId(R.id.txtEndereco)).perform(pressImeActionButton());
        pressBack();
    }
    private void excluir(){
        onData(withRowString(HotelSQLHelper.COLUNA_NOME, "Hotel Modificado"))
                .perform(longClick());
        onView(withId(R.id.acao_delete)).perform(click());
        onView(withId(android.R.id.list)).perform(swipeDown());
    }
}
