package achan.nl.uitstelgedrag.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import achan.nl.uitstelgedrag.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A custom view for color picking.
 * To auto-style according to the theme, use the Attrs AttributeSet.
 *
 * Created by Etienne on 4-11-2017.
 */
public class ColorPicker extends LinearLayout {

    public static final int SELECTION_NOT_SET = -1;
    public static final int SELECTION_DEFAULT_COLOR = 0;

    // FIXME/IMPROVEMENT - Convert to dynamic listview.

    @LayoutRes int viewLayout = R.layout.provisional_color_picker;

    @BindView(R.id.provisional_label_color_picker_card_10) CardView card_10;
    @BindView(R.id.provisional_label_color_picker_card_00) CardView card_00;
    @BindView(R.id.provisional_label_color_picker_card_0A) CardView card_0A;
    @BindView(R.id.provisional_label_color_picker_card_0B) CardView card_0B;
    @BindView(R.id.provisional_label_color_picker_card_0C) CardView card_0C;
    @BindView(R.id.provisional_label_color_picker_card_0D) CardView card_0D;
    @BindView(R.id.provisional_label_color_picker_card_0E) CardView card_0E;
    @BindView(R.id.provisional_label_color_picker_card_0F) CardView card_0F;
    @BindView(R.id.provisional_label_color_picker_card_0G) CardView card_0G;
    @BindView(R.id.provisional_label_color_picker_card_0H) CardView card_0H;

    CardView[] cards;

    LayoutInflater inflater;
    Context context;

//    int[] colors;
    int selectedColor = SELECTION_NOT_SET;

    OnSelectionChangedListener callback;
    OnClickListener onSelectionChangedListener = v -> {
        selectedColor = ((CardView) v).getCardBackgroundColor().getDefaultColor();
        // todo - set border.
        if (callback != null)
            callback.execute();
        Log.i("ColorPicker", "Selected color: " + selectedColor);
    };

    // drawable for border
    // inflation
    // convert to list
    // getSelected
    // getColors
    // setColors

    public ColorPicker(Context context) {
        this(context, null);
    }

    private void setListeners() {
        card_00.setOnClickListener(onSelectionChangedListener);
        card_10.setOnClickListener(onSelectionChangedListener);
        card_0A.setOnClickListener(onSelectionChangedListener);
        card_0B.setOnClickListener(onSelectionChangedListener);
        card_0C.setOnClickListener(onSelectionChangedListener);
        card_0D.setOnClickListener(onSelectionChangedListener);
        card_0E.setOnClickListener(onSelectionChangedListener);
        card_0F.setOnClickListener(onSelectionChangedListener);
        card_0G.setOnClickListener(onSelectionChangedListener);
        card_0H.setOnClickListener(onSelectionChangedListener);
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        inflater = LayoutInflater.from(context); // Same as getLayoutInflater / getSystemService(LAYOUT_INFLATER).
        View view = inflater.inflate(viewLayout, this, true);
        ButterKnife.bind(this, view);

        setListeners();

        // Array needs to be filled after view-binding or values will be null.
        cards = new CardView[]{
                card_10,
                card_00,
                card_0A,
                card_0B,
                card_0C,
                card_0D,
                card_0E,
                card_0F,
                card_0G,
                card_0H
        };

    }
//
//    todo - public ColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    todo - public ColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

//    public void setColors(int[] colors){
//        this.colors = colors;
//    }

    /**
     * Returns the latest selected color or -1 if not set.
     * Returns the color as getDefaultColor on the background color state list.
     */
    public int getSelectedColor(){
        // Default color is always in the first position.
        return selectedColor == cards[SELECTION_DEFAULT_COLOR].getCardBackgroundColor().getDefaultColor()?
                    SELECTION_NOT_SET : selectedColor;
    }

    /**
     * Sets the selected color to that of the card at this position.
     * @param position
     */
    public void setSelected(int position){
        // todo - set border.
        selectedColor = cards[position].getCardBackgroundColor().getDefaultColor();
    }

    public void setColor(int position, int color){
        cards[position].setCardBackgroundColor(color);
    }

//    public String getSelectedColorAsHex(){
//        // VERIFY TODO - possible? -> Integer.toHex does not count alpha correctly.
//    }

//    public int getSelectedColorAsRes(){
//        // VERIFY TODO - possible? -> Res-matching against list?
//    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener callback){
        this.callback = callback;
    }

    public interface OnSelectionChangedListener{
        void execute();
    }
}
