package achan.nl.uitstelgedrag.domain.models;

/**
 * Created by Etienne on 25-10-2016.
 */
public class Attachment {

    public static final String ATTACHMENT_TYPE_AUDIO = "audio";
    public static final String ATTACHMENT_TYPE_VIDEO = "video";
    public static final String ATTACHMENT_TYPE_PHOTO = "photo";
    // ... todo - stringdef

    public int id = -1;
    public String path;
    public String type;
}
