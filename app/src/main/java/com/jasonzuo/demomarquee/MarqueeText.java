package com.jasonzuo.demomarquee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jasonzuo on 2015/7/16.
 */
public class MarqueeText extends TextView implements Runnable, TextWatcher {

    private int currentScrollX;// 当前滚动的位置
    private int textWidth;
    private float originTextWidth;

    private boolean isStop = false;

    private boolean isMeasure = false;
    private boolean isNeedScroll = false;
    private boolean isInSelSet = false;

    private String px100LengthString = "    ";

    public MarqueeText(Context context) {
        super(context, null);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setEllipsize(null);
        addTextChangedListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isMeasure) {
            getTextWidth();
            isMeasure = true;
            if (textWidth < getWidth()) {
                isNeedScroll = false;
            } else {
                isNeedScroll = true;
            }
        }
    }

    private void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        textWidth = (int) paint.measureText(str);

        while (paint.measureText(px100LengthString) < 100) {
            px100LengthString = px100LengthString.concat(" ");
        }
    }

    @Override
    public void run() {
        if (!isNeedScroll)
            return;

        currentScrollX += 1;
        scrollTo(currentScrollX, 0);
        if (isStop) {
            return;
        }

        if (getScrollX() >= (textWidth - originTextWidth)) {
            currentScrollX = -getWidth();
            scrollTo(0, 0);
            setEllipsize(TextUtils.TruncateAt.END);
        } else {
            postDelayed(this, 10);
        }
    }

    public void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        setEllipsize(null);
        post(this);
    }

    public void stopScroll() {
        isStop = true;
    }

    public void startFor0() {
        currentScrollX = 0;
        startScroll();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isInSelSet) {
            stopScroll();
            isMeasure = false;
            currentScrollX = 0;
            isInSelSet = true;
            String originText = getText().toString();
            originTextWidth = getPaint().measureText(originText);
            StringBuilder builder = new StringBuilder(originText)
                    .append(px100LengthString)
                    .append(originText)
                    .append(px100LengthString)
                    .append(originText);
            setText(builder.toString());
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    isInSelSet = false;
                    startScroll();
                }
            }, 200);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
}