package achan.nl.uitstelgedrag.domain.models;

import java.io.File;
import java.io.Serializable;

/**
 * User profile model for the Facebook API.
 * Created by Etienne on 13-8-2016.
 */
public class Profile implements Serializable{

    public String name;
    public String email;
    public String picture_url;
    public String cover_url;

    public File cached_picture;
    public File cached_cover;

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", picture_url='" + picture_url + '\'' +
                ", cover_url='" + cover_url + '\'' +
                ", cached_picture=" + cached_picture +
                ", cached_cover=" + cached_cover +
                '}';
    }
}
