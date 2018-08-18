package com.zeed.zeemp.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zeed.zeemp.R;

/**
 * Created by teamapt on 17/08/2018.
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
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_itwm_view));
        super.onDraw(canvas);
    }

    public void init(Context context) {
        this.context = context;
    }


}
