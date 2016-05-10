package com.deus_tech.aria.dashboard;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deus_tech.aria.R;

/**
 * Created by user on 3/19/2016.
 */
public class WearableListItemLayout extends LinearLayout implements WearableListView.OnCenterProximityListener {
    private  float mFadedTextAlpha;
    private  int mUnselectedCircleColor, mSelectedCircleColor;
    private float mBigCircleRadius;
    private float mSmallCircleRadius;
    CircledImageView imageView;
    TextView textView;
    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFadedTextAlpha=40/100f;
        mUnselectedCircleColor = getResources().getColor(R.color.grey);
        mSelectedCircleColor = getResources().getColor(R.color.dark_blue);
        mBigCircleRadius=getResources().getDimension(R.dimen.large_circle_radius);
        mSmallCircleRadius=getResources().getDimension(R.dimen.small_circle_radius);
        setClipChildren(false);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView=(CircledImageView)findViewById(R.id.circle);
        textView=(TextView)findViewById(R.id.name);
    }

    @Override
    public void onCenterPosition(boolean b) {
       textView.setAlpha(1f);
       //imageView.setAlpha(1f);
       imageView.setCircleRadius(mBigCircleRadius);
       imageView.setCircleColor(mSelectedCircleColor);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        textView.setAlpha(mFadedTextAlpha);
       // imageView.setAlpha(mFadedTextAlpha);
        imageView.setCircleRadius(mSmallCircleRadius);
        imageView.setCircleColor(mUnselectedCircleColor );
    }
}
