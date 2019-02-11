package ir.appsoar.teknesian.Activity;

import android.graphics.PointF;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import java.text.DecimalFormat;

import ir.appsoar.teknesian.R;

public class ImageViewer extends AppCompatActivity {
    private TouchImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Bundle bundle=getIntent().getExtras();
/*        StfalconImageViewer.Builder<Image>(this, Uri.parse(bundle.getString("url"))) { bigImageView, image ->
                Glide.get().load(image.url).into(view)
        }..show()
        Glide.with(this).load(Uri.parse(bundle.getString("url"))).into(bigImageView);*/
        assert bundle != null;
        image = findViewById(R.id.img);
        Glide.with(this).load(Uri.parse(bundle.getString("url"))).into(image);


        //
        // Set the OnTouchImageViewListener which updates edit texts
        // with zoom and scroll diagnostics.
        //

    }
}
