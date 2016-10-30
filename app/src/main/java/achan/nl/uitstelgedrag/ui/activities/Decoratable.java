package achan.nl.uitstelgedrag.ui.activities;

/**
 * Decouples the framework.
 *
 * Created by Etienne on 8-8-2016.
 */
public interface Decoratable {

    /**
     * See: Decorator pattern.
     *
     * Decouples the window from the additional views like the toolbar, drawer, etc.
     * Decorations can be added by supplying a decoration.
     */
    void addDecoration(Decoration decoration);

}
