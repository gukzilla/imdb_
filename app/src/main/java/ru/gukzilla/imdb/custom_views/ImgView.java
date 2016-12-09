package ru.gukzilla.imdb.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by evgeniy on 09.12.16.
 */

public class ImgView extends ImageView {

    public ImgView(Context context) {
        super(context);
    }

    public ImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec / 2);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec / 2);
    }
}
