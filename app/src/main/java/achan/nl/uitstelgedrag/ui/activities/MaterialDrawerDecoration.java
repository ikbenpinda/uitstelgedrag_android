package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Profile;
import achan.nl.uitstelgedrag.persistence.gateways.UserGateway;

/**
 * Created by Etienne on 8-8-2016.
 */
public class MaterialDrawerDecoration implements Decoration {

    Base activity;

    public MaterialDrawerDecoration(Base activity) {
        this.activity = activity;
    }

    /**
     * Applies a MaterialDrawer implementation to the decoratable activity.
     */
    @Override
    public void decorate() {

        Profile user = new UserGateway(activity).loadProfile();

        if (user != null)
            initProfile(user);

        //if you want to update the items at a later time it is recommended to keep it in a variable.
        Activities
                overview = Activities.OVERVIEW,
                dayplanner = Activities.DAYPLANNER,
                tasks = Activities.TASKS,
                notes = Activities.NOTES,
                attendance = Activities.ATTENDANCELOG,
                //routine = Activities.ROUTINES,
                help = Activities.HELP,
                settings = Activities.SETTINGS,
                about = Activities.ABOUT;

        AccountHeader header = null;
        if (user != null)
             header = new AccountHeaderBuilder()
             .withActivity(activity)
             .withTextColor(Color.argb(150, 0, 0, 0))
             .withHeaderBackground(R.drawable.wallpapersmall)
             .addProfiles(
                new ProfileDrawerItem().withName(user.name).withEmail(user.email).withTextColor(Color.argb(150, 0, 0, 0))//.withIcon(activity.getResources().getDrawable(R.drawable.profile))
             )
             .withOnAccountHeaderListener((view, profile, currentProfile) -> false)
             .build();

        PrimaryDrawerItem
                overzicht = new PrimaryDrawerItem().withName(overview.name).withIdentifier(overview.id),
                planner = new PrimaryDrawerItem().withName(dayplanner.name).withIdentifier(dayplanner.id),
                taken = new PrimaryDrawerItem().withName(tasks.name).withIdentifier(tasks.id),
                notities = new PrimaryDrawerItem().withName(notes.name).withIdentifier(notes.id),
                aanwezigheid = new PrimaryDrawerItem().withName(attendance.name).withIdentifier(attendance.id);

        SecondaryDrawerItem
                handleiding = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(help.name).withIdentifier(help.id),
                instellingen = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(settings.name).withIdentifier(settings.id),
                over = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(about.name).withIdentifier(about.id);

        //create the drawer and remember the 'Drawer' result object.
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(activity.getToolbar())
                .addDrawerItems(
                        overzicht,
                        planner,
                        taken,
                        notities,
                        aanwezigheid,
                        new DividerDrawerItem(),
                        handleiding,
                        instellingen,
                        over
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    String name = ((PrimaryDrawerItem)drawerItem).getName().getText();

                    // The only alternative here is hardcoding the order of the elements?
                    Class destination = null;
                    for (Activities a : Activities.values()) {
                        if (a.name.equals(name))
                        destination = a.activity;
                    }

                    Log.i("Drawer", String.format("Item clicked: pos: %d, name: %s", position, name));
                    activity.startActivity(new Intent(activity.getBaseContext(), destination));
                    return true;
                });

        if (user != null)
            drawerBuilder.withAccountHeader(header);

        Drawer drawer = drawerBuilder.build();
        drawer.setSelection(activity.getCurrentActivity().id, false);

    }

    private void initProfile(Profile user) {
        if (user.cached_picture != null) {
            DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Glide.with(activity).load(user.cached_picture).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {

                }

                @Override
                public Drawable placeholder(Context ctx) {
                    return null;
                }

                @Override
                public Drawable placeholder(Context ctx, String tag) {
                    return null;
                }
            });
        } else if (user.picture_url != null || !user.picture_url.isEmpty()) {
            DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Glide.with(activity).load(user.picture_url).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {

                }

                @Override
                public Drawable placeholder(Context ctx) {
                    return null;
                }

                @Override
                public Drawable placeholder(Context ctx, String tag) {
                    return null;
                }
            });
        } else{
            // Use default profile picture in the header / do nothing.
        }
    }
}
