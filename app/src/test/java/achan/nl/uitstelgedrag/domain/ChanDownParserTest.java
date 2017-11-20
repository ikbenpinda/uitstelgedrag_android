package achan.nl.uitstelgedrag.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;

import static org.junit.Assert.*;

/**
 * Created by Etienne on 13-11-2017.
 */
public class ChanDownParserTest {

    ChanDownParser parser;

    @Before
    public void initialize(){
        parser = new ChanDownParser();
    }

    @After
    public void destroy(){
        parser = null;
    }

    @Test
    public void testDelimiting() throws Exception{
        String input = "A B C D E F";
        assertTrue(parser.parseLabels(input, null).size() > 0);
    }

    @Test
    public void testParsingMultiLabel() throws Exception {
        String input1 = "A, B"; // sanity check
        String input2 = "A, B, "; // check for correct trimming.
        String input3 = "A, A, B"; // check for duplicates

        List<Label> results;

        results = parser.parseLabels(input1, null);
        assertTrue(results.size() == 2);
        assertTrue(results.get(0).title.equals("A"));
        assertTrue(results.get(1).title.equals("B"));

        results = parser.parseLabels(input2, null);
        assertTrue(results.size() == 2);
        assertTrue(results.get(0).title.equals("A"));
        assertTrue(results.get(1).title.equals("B"));

        results = parser.parseLabels(input3, null);
        assertTrue("Duplicates? results.size() = " + results.size(),
                results.size() == 2);
        assertTrue(results.get(0).title.equals("A"));
        assertTrue(results.get(1).title.equals("B"));
    }

    @Test
    public void testParsingSingleLabel() throws Exception {
        String input1 = "";
        String input2 = "A, ";
        String input3 = ",";

        // Sanity check
        assertTrue(parser.parseLabels(null, null).isEmpty());
        assertTrue(parser.parseLabels(input1, null).isEmpty());

        List<Label> labels = parser.parseLabels(input2, null);

        assertTrue(labels.size() == 1);
        assertTrue(labels.get(0).title.equals("A"));

        assertTrue(parser.parseLabels(input3, null).isEmpty());
    }

    @Test
    public void testCrossReferencing() throws Exception{
        String input = "A, B";

        Label existingLabel = new Label();
        existingLabel.title = "A";
        existingLabel.id = 99;
        ArrayList<Label> existingLabels = new ArrayList<>();
        existingLabels.add(existingLabel);

        List<Label> results = parser.parseLabels(input, existingLabels);
        assertTrue(results.size() == 2);
        assertTrue(results.get(0).id == 99);

        existingLabels = new ArrayList<>();
        Label existingLabel1 = new Label();
        existingLabel1.title = "A";
        existingLabel1.id = 99;
        Label existingLabel2 = new Label();
        existingLabel2.title = "B";
        existingLabel2.id = 98;
        Label existingLabel3 = new Label();
        existingLabel3.title = "C";
        existingLabel3.id = 97;
        Label existingLabel4 = new Label();
        existingLabel4.title = "D";
        existingLabel4.id = 96;
        existingLabels.add(existingLabel1);
        existingLabels.add(existingLabel2);
        existingLabels.add(existingLabel3);
        existingLabels.add(existingLabel4);

        String inputExtended = "A, B, C, D, E, F, G, H, I, ";
        results = parser.parseLabels(inputExtended, existingLabels);
        assertTrue(results.size() == 9);
        assertTrue(results.get(0).id == 99);
        assertTrue(results.get(1).id == 98);
        assertTrue(results.get(2).id == 97);
        assertTrue(results.get(3).id == 96);
        assertTrue(results.get(4).id == -1);
    }

}