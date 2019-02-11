package ir.appsoar.teknesian;


import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


class SwipeListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    public SwipeListener(Context ctx) {
        Context context = ctx;
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    // Interface
    private void onSwipeLeft() {
    }
    private void onSwipeRight() {
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final int SWIPE_MIN_DISTANCE = 120;
            final int SWIPE_MAX_OFF_PATH = 250;
            final int SWIPE_THRESHOLD_VELOCITY = 200;

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    onSwipeLeft();

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onSwipeRight();
                }
            } catch (Exception e) {
                // ...
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}