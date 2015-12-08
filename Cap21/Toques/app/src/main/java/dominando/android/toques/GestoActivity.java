package dominando.android.toques;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
public class GestoActivity extends AppCompatActivity {
    private static final String TAG = "DominandoAndroid";
    GestureDetectorCompat mDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesto);
        mDetector = new GestureDetectorCompat(this, mGestureListener);
        mDetector.setOnDoubleTapListener(mDoubleTapListener);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
    GestureDetector.OnDoubleTapListener mDoubleTapListener =
            new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                    Log.d(TAG, "onSingleTapConfirmed");
                    return true;
                }
                @Override
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    Log.d(TAG, "onDoubleTap");
                    return true;
                }
                @Override
                public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                    Log.d(TAG, "onDoubleTapEvent");
                    return true;
                }
            };
    GestureDetector.OnGestureListener mGestureListener =
            new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    Log.d(TAG, "onDown");
                    return true;
                }
                @Override
                public void onShowPress(MotionEvent motionEvent) {
                    Log.d(TAG, "onShowPress");
                }
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    Log.d(TAG, "onSingleTapUp");
                    return true;
                }
                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2,
                                        float v, float v2) {
                    Log.d(TAG, "onScroll");
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent motionEvent) {
                    Log.d(TAG, "onLongPress");
                }
                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2,
                                       float v, float v2) {
                    Log.d(TAG, "onFling");
                    return true;
                }
            };
}
