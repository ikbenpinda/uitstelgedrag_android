package achan.nl.uitstelgedrag.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import achan.nl.uitstelgedrag.R;

/**
 * TODO: document your custom view class.
 */
public class CategoryView extends TextView {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public CategoryView(Context context) {
        super(context);
        init(null, 0);
    }

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CategoryView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.CategoryView_example_String);
        mExampleColor = a.getColor(
                R.styleable.CategoryView_example_Color,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.CategoryView_example_Dimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.CategoryView_example_Drawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.CategoryView_example_Drawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) { // override to implement Material Chips.
        super.onDraw(canvas);

        // note - paint/ how to draw
        Paint textPaint = new Paint(Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(getCurrentTextColor());
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));

        // note - canvas/ what to draw
        String text = "Sample text.";
        RectF ovalStarting = new RectF(0, 0, 90, 180);
        RectF chip = new RectF(ovalStarting.right, ovalStarting.top, 200, 20);
        //todo - dynamic / Measure pass #1.
        RectF ovalTrailing = new RectF(chip.right, chip.top, 90, 180);

        canvas.drawArc(ovalStarting, 90, 180, true, backgroundPaint);
        canvas.drawRect(chip, backgroundPaint);
        canvas.drawText(text, chip.left, chip.top, textPaint);
        canvas.drawArc(ovalTrailing, 270, 180, true, backgroundPaint);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    // todo - custom view methods, other than TextView.
}
