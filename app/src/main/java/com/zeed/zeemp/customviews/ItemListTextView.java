package com.zeed.zeemp.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.zeed.zeemp.R;

/**
 * Created by zeed on 17/08/2018.
 */

public class ItemListTextView extends android.support.v7.widget.AppCompatTextView {

    private Context context;

    public ItemListTextView(Context context) {
        super(context);
        init(context);
    }

    public ItemListTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemListTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setBackgroundColor(Color.parseColor("#ffffff"));
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_view));
        super.onDraw(canvas);
    }

    public void init(Context context) {
        this.context = context;
    }


}
