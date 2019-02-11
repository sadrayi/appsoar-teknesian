package ir.appsoar.teknesian.cards;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.utils.DecodeBitmapTask;

public class SliderCard extends RecyclerView.ViewHolder implements DecodeBitmapTask.Listener {

    private static int viewWidth = 0;
    private static int viewHeight = 0;

    private ImageView imageView;

    private DecodeBitmapTask task;

    public SliderCard(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
    }

    void setContent( final String resId) {
        if (viewWidth == 0) {
            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    viewWidth = itemView.getWidth();
                    viewHeight = itemView.getHeight();
                    loadBitmap(resId);
                }
            });
        } else {
            loadBitmap(resId);
        }
    }

    void clearContent() {
        if (task != null) {
            task.cancel(true);
        }
    }

    private void loadBitmap( String resId) {
        task = new DecodeBitmapTask(resId,itemView.getResources(), viewWidth, viewHeight, this);
        task.execute();
    }

    @Override
    public void onPostExecuted(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

}