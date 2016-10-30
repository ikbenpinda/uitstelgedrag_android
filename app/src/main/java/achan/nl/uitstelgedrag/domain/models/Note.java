package achan.nl.uitstelgedrag.domain.models;

import java.util.Date;

/**
 * Created by Etienne on 9-10-2016.
 */
public class Note {
    public int        id         = -1;
    public String     text;
    public Date       created;
    public Attachment attachment;
}
