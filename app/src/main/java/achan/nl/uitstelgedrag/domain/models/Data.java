package achan.nl.uitstelgedrag.domain.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Static functions and constants class.
 *
 * Created by Etienne on 26-3-2016.
 */
public class Data {

    public static List<Task> list = new ArrayList<>();

    public static List<Task> createList(){ // FIXME: 26-3-2016 Persistence.
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Schoenen bestellen"));
        tasks.add(new Task("Nagels knippen"));
        tasks.add(new Task("Wasmand kopen"));
        tasks.add(new Task("Huiswerk maken"));
        list = tasks;
        return list;
    }

    public static List<Task> updateList(List<Task> tasks){
        list = tasks;
        return list;
    }

    public static boolean save(){
        Log.i("UITSTELGEDRAG", "Saving to internal database.");
        return false;
    }

    public static boolean load(){
        Log.i("UITSTELGEDRAG", "Loading internal database.");
        return false;
    }

    public static boolean delete(){
        Log.i("UITSTELGEDRAG", "Deleting from internal database.");
        return false;
    }

    public static boolean update(){
        Log.i("UITSTELGEDRAG", "Updating internal database.");
        return false;
    }

    public static boolean sync(){
        Log.i("UITSTELGEDRAG", "Syncing internal and external database.");
        return false;
    }
}
