package achan.nl.uitstelgedrag.ui;

import achan.nl.uitstelgedrag.R;

/**
 * Created by Etienne on 10/01/17.
 */
public enum Themes {
    LIGHT(0, R.style.AppTheme_Light, "Light"),
    DARK(1, R.style.AppTheme, "Dark"),
    AUTO(2, 0, "Auto");
    //    SOLARIZED(3, R.style.AppTheme_Solarized),

    public int id;
    public int style;
    public String name;

    Themes(int id, int style, String name) {
        this.id = id;
        this.style = style;
        this.name = name;
    }
}
