package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * IO layer shortcuts for handling file storage persistence..
 *
 * Created by Etienne on 13-8-2016.
 */
public class IOGateway {

    Context context;

    public IOGateway(Context context) {
        this.context = context;
    }

    /**
     * Writes a given object to the internal storage.
     * @param filename
     * @param object
     */
    public void write(String filename, Object object){
        try {
            ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
            out.writeObject(object);
        } catch (IOException e) {
            Log.e("IOGateway", "Error writing object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads an object from the internal storage.
     * @param filename
     * @return
     */
    public Object read(String filename){
        Object object = null;
        try {
            ObjectInputStream in = new ObjectInputStream(context.openFileInput(filename));
            object = in.readObject();
        } catch (IOException e) {
            Log.e("IOGateway", "Error reading object: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e("IOGateway", "Error reading object: " + e.getMessage());
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Writes a given object to the supplied path.
     * @param filename path/filename
     * @param object
     */
    public static void writeToPublicArea(String filename, Object object) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(filename)))) {
            out.writeObject(object);
        } catch (Exception e){
            Log.e("IOGateway", "Error writing object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads an object from the supplied filename.
     * @param filename path/filename
     * @return
     */
    public static Object loadFromPublicArea(String filename) {

        Object result = null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))){
            result = in.readObject();
        } catch (Exception e){
            Log.e("IOGateway", "Error reading object: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
