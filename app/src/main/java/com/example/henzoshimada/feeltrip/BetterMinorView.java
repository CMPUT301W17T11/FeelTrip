package com.example.henzoshimada.feeltrip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akiniyalocts.minor.MinorView;

/**
 * Created by Brett on 2017-04-01.
 * Full credit of this code goes to com.akiniyalocts.minor.MinorView, I take none
 */
public class BetterMinorView extends MinorView {
    private int selectedTitleColor = -1;
    private int titleColor = -1;
    private TextView titleTextView;
    private FrameLayout notificationView;
    private TextView notificationTextView;
    private View iconView;
    private LayoutParams params;
    private float inactiveSize;

    /**
     * Instantiates a new Better minor view.
     *
     * @param context the context
     */
    public BetterMinorView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Better minor view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public BetterMinorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    /**
     * Instantiates a new Better minor view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public BetterMinorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    /**
     * Instantiates a new Better minor view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    public BetterMinorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, com.akiniyalocts.minor.R.styleable.MinorView, 0, 0);
        int iconViewRes = a.getResourceId(com.akiniyalocts.minor.R.styleable.MinorView_minor_icon_view, -1);
        String title = a.getString(com.akiniyalocts.minor.R.styleable.MinorView_minor_title);
        this.titleColor = a.getColor(com.akiniyalocts.minor.R.styleable.MinorView_minor_title_text_color, ContextCompat.getColor(context, 17170435));
        this.selectedTitleColor = a.getColor(com.akiniyalocts.minor.R.styleable.MinorView_minor_title_selected_color, -1);
        LinearLayout minorLayout = new LinearLayout(this.getContext());
        minorLayout.setOrientation(1);
        minorLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2, 1.0F));
        minorLayout.setGravity(1);
        minorLayout.setPadding(5, 5, 5, 5);
        this.setClickable(true);
        if(iconViewRes != -1) {
            this.iconView = inflate(context, iconViewRes, (ViewGroup)null);
            minorLayout.addView(this.iconView, this.getLayoutParamsForIconView());
            if(title != null) {
                this.titleTextView = new TextView(context);
                this.titleTextView.setLayoutParams(this.getLayoutParamsForIconView());
                this.titleTextView.setText(title);
                this.titleTextView.setTextColor(this.titleColor);
                this.titleTextView.setClickable(false);
                this.titleTextView.setFocusable(false);
                if(a.getBoolean(com.akiniyalocts.minor.R.styleable.MinorView_minor_selected, false) && this.selectedTitleColor != -1) {
                    this.titleTextView.setTextColor(this.selectedTitleColor);
                }

                this.titleTextView.setGravity(1);
                minorLayout.addView(this.titleTextView);
                this.inactiveSize = this.titleTextView.getTextSize();
            }

            minorLayout.requestLayout();
            this.addView(minorLayout);
            this.initNotificationView();
            a.recycle();
        } else {
            throw new IllegalArgumentException("You must specify a view for MinorView to inflate as an icon. Use app:minor_icon_view=@layout/your_view");
        }
    }

    private LayoutParams getLayoutParamsForIconView() {
        if(this.params == null) {
            this.params = new LayoutParams(-2, -2);
        }

        return this.params;
    }

    public void selected() {
        if(this.titleTextView != null && this.selectedTitleColor != -1) {
            this.titleTextView.setTextColor(this.selectedTitleColor);
            this.setScaleX(1.1f);
            this.setScaleY(1.1f);
            if(this.iconView instanceof TextView) {
                ((TextView)this.iconView).setTextColor(this.selectedTitleColor);
            }
            if(this.iconView instanceof ImageView) {
                ((ImageView)this.iconView).setColorFilter(this.selectedTitleColor);
            }
            this.invalidate();
        }

    }

    private void initNotificationView() {
        if(this.notificationView == null) {
            this.notificationView = (FrameLayout)inflate(this.getContext(), com.akiniyalocts.minor.R.layout.minor_notification, (ViewGroup)null);
            if(this.notificationTextView == null) {
                this.notificationTextView = (TextView)this.notificationView.findViewById(com.akiniyalocts.minor.R.id.minor_notification_text);
            }
        }

        LayoutParams params = new LayoutParams(-2, -2, 53);
        params.setMargins(20, 15, 5, 5);
        this.notificationView.setPadding(5, 5, 5, 5);
        this.notificationView.setLayoutParams(params);
        this.addView(this.notificationView);
        this.notificationView.setVisibility(4);
        this.invalidate();
    }

    public void addNotification(int notificationCount) {
        if(this.notificationView != null) {
            this.notificationView.setVisibility(0);
        }

        if(notificationCount <= 99) {
            if(this.notificationTextView != null) {
                this.notificationTextView.setText(String.valueOf(notificationCount));
            }
        } else if(this.notificationTextView != null) {
            this.notificationTextView.setText("*");
        }

    }

    public void clearNotification() {
        if(this.notificationView != null) {
            this.notificationView.setVisibility(4);
        }

    }

    public void unselected() {
        if(this.titleTextView != null && this.iconView != null && this.titleColor != -1) {
            this.titleTextView.setTextColor(this.titleColor);
            this.setScaleX(1f);
            this.setScaleY(1f);
            ((ImageView) this.iconView).setColorFilter(this.titleColor);
            this.invalidate();
        }
    }

    /**
     * Sets icon.
     *
     * @param drawable the drawable
     */
    public void setIcon(int drawable) {
        if(this.iconView != null) {
            if(this.iconView instanceof ImageView) {
                ((ImageView) this.iconView).setImageResource(drawable);
                ((ImageView) this.iconView).setColorFilter(null);
            }
            this.invalidate();
        }
    }

    /**
     * Sets icon color.
     *
     * @param color the color
     */
    public void setIconColor(int color) {
        if(this.iconView != null) {
            if(this.iconView instanceof ImageView) {
                ((ImageView)this.iconView).setColorFilter(color);
            }
            this.invalidate();
        }
    }

    /**
     * Sets text color.
     *
     * @param color the color
     */
    public void setTextColor(int color) {
        if(this.titleTextView != null) {
            this.titleTextView.setTextColor(color);
            if(this.iconView instanceof TextView) {
                ((TextView)this.iconView).setTextColor(color);
            }
            this.invalidate();
        }
    }

    /**
     * Sets text.
     *
     * @param title the title
     */
    public void setText(String title) {
        if(title != null) {
            this.titleTextView.setText(title);
        }
    }


}
