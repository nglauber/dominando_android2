package dominando.android.hotel;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.RatingBar;
import org.hamcrest.Matcher;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
public class RatingBarAction implements ViewAction {
    int rating;
    @Override
    public Matcher<View> getConstraints() {
        Matcher <View> isRatingBarConstraint = isAssignableFrom(RatingBar.class);
        return isRatingBarConstraint;
    }
    @Override
    public String getDescription() {
        return "Ação personalizada para RatingBar";
    }
    @Override
    public void perform(UiController uiController, View view) {
        RatingBar ratingBar = (RatingBar) view;
        ratingBar.setRating(rating);
    }
    public static RatingBarAction setRating(int value) {
        RatingBarAction ratingBarAction = new RatingBarAction();
        ratingBarAction.rating = value;
        return ratingBarAction;
    }
}


