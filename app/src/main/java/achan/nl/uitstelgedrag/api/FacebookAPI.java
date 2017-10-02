
package achan.nl.uitstelgedrag.api;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.File;

import achan.nl.uitstelgedrag.domain.models.Profile;
import achan.nl.uitstelgedrag.persistence.UserRepository;
import achan.nl.uitstelgedrag.persistence.gateways.UserGateway;

/**
 * Facebook SDK and Graph API facade.
 *
 * Created by Etienne on 10-8-2016.
 */
public class FacebookAPI {

    Context context;

    public CallbackManager callbackManager;

    public static final String PERMISSIONS_READ_PROFILE = "public_profile"; // id, name, picture.
    public static final String PERMISSIONS_READ_EMAIL   = "email";
    //public static final String PERMISSIONS_READ_COVER   = (see API guide);

    public static final String GRAPH_USER_ID         = "id";
    public static final String GRAPH_NAME            = "name";
    public static final String GRAPH_EMAIL           = "email";
    public static final String GRAPH_PROFILE_PICTURE = "picture";
    //public static final String GRAPH_COVER_PHOTO     = "cover";
    //public static final String ADDRESS_HOME = ;
    //public static final String ADDRESS_WORK = ;
    //public static final String ADDRESS_SCHOOL = ;

    public FacebookAPI(Context baseContext) {
        this.context = baseContext;
        callbackManager = CallbackManager.Factory.create();
    }

    public void initializeLoginButton(LoginButton button, FacebookCallback loginResult, String... permissions) {

        // It's important to always pass in a consistent set of permissions
        // to this method, or manage the setting of permissions outside
        // of the LoginButton class altogether
        // (by using the LoginManager explicitly).
        button.setReadPermissions(permissions);
        button.registerCallback(callbackManager, loginResult);

        Log.i("Facebook API", "Button initialized!");
    }

    public static String[] getPermissions() {
        return new String[]{PERMISSIONS_READ_PROFILE, PERMISSIONS_READ_EMAIL};
    }

    public FacebookCallback<LoginResult> getReadProfileCallback() {
        return new FacebookCallback<LoginResult>() {

            {
                Log.i("CallbackManager", "Initialized!");
            }

            @Override
            public void onSuccess(LoginResult loginResult) { // FIXME handle already-logged-in state: check for existing cache, refresh, etc.
                Log.i("Facebook API", "Login successful!");
                getProfile(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w("FacebookAPI", "Canceled request!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FacebookAPI", "Error handling request: " + error.getMessage());
                error.printStackTrace();
            }
        };
    }

    public Profile getProfile(AccessToken token) {

        UserRepository persistence = new UserGateway(context);

        // Get profile picture, name, email, and cover photo.
        GraphRequest me = GraphRequest.newMeRequest(token, (object, response) -> { // FIXME major refactoring and AssetRepository.
            Log.i("Facebook API", "GraphRequest completed!");
            Log.i("Facebook API", "Response = " + response.getRawResponse());

            try {

                String  name = response.getJSONObject().getString(GRAPH_NAME),
                        email = response.getJSONObject().getString(GRAPH_EMAIL),
                        picture_url = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                        //cover_url = response.getJSONObject().getJSONObject(GRAPH_COVER_PHOTO).getString("source");

                Log.i("Facebook API", String.format("Data:\n\t name = %s, \n\t email = %s, \n\t picture-url = %s, \n\t cover-url = %s", name, email, picture_url, /*cover_url*/null));

                File    cached_picture = Glide.with(context).load(picture_url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        //cached_cover = Glide.with(context).load(cover_url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

                Log.i("Facebook API", String.format("Retrieved pictures: profile_picture = %s, cover_photo = %s", cached_picture, /*cached_cover*/null));

                Profile profile = new Profile();

                profile.name = name;
                profile.email = email;
                profile.picture_url = picture_url;
                //profile.cover_url = cover_url;
                profile.cached_picture = cached_picture;
                //profile.cached_cover = cached_cover;

                persistence.saveProfile(profile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", String.format("%s,%s,%s,%s"/*,%s"*/,GRAPH_USER_ID, GRAPH_NAME, GRAPH_EMAIL, GRAPH_PROFILE_PICTURE/*, GRAPH_COVER_PHOTO*/));
        me.setParameters(parameters);
        me.executeAndWait();

        return persistence.loadProfile();
    }
}
