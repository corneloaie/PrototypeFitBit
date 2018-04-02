package rocks.nxfifteen.poc.myapplication;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by Gebruiker on 23/03/2018.
 */

public class sketchone extends PApplet {

    private static final String TAG = "SketchActivity";

    int margin, graphHeight;
    float xSpacer;
    PVector[] positions;
    NxFitbitHelper fitbitsketch;

    public sketchone(NxFitbitHelper nxFitbitHelper) {
        this.fitbitsketch = nxFitbitHelper;
    }

    public void setup() {

        background(20);
        try {
            loadData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        background(20);
        for (int i = 0; i < positions.length; i++) {
            ellipse(positions[i].x, positions[i].y, 15, 15);
        }
    }


    @SuppressLint("NewApi")
    public void loadData() throws InterruptedException, ExecutionException, IOException, JSONException {

        //NO DATA ARE CALLED, POSITIONS PVECTOR IS EMPTY: Why not is the question..
        //this allows you to use all the methods in the helper class - request token with code
        //Parsing Activity Time Series to test EndPoint
        JSONObject activityTimeSeriesDistancesMonth = this.fitbitsketch.getDistances2802period1month();
        JSONArray activityTimeSeriesDistances = activityTimeSeriesDistancesMonth.getJSONArray("activities-distance");
        String teststring = activityTimeSeriesDistances.getJSONObject(0).getString("value");
        //NONE OF THESE TWO IS SHOWN IN THE LOGCAT
        Log.d(TAG, "Sketchone Check : " + teststring);
        System.out.println("Sketchone" + teststring);

        // use a for loop to retrieve all data from the heart rate statistics from 30/01 to 28/02
        // this list you can use as input for the Processing sketch
        ArrayList<Float> distancelistsketchcrunch = new ArrayList<Float>();
        for (int i = 0; i < activityTimeSeriesDistances.length(); i++) {
            String distancessketchcrunch = activityTimeSeriesDistances.getJSONObject(i).getString("value");
            Float distancesnumbersketchcrunch = Float.parseFloat(distancessketchcrunch);
            distancelistsketchcrunch.add(distancesnumbersketchcrunch);
        }
        System.out.println("DistanceSketch List : " + distancelistsketchcrunch);

        ArrayList<Float> distancelistoriginal = new ArrayList<Float>();
        for (int i = 0; i < activityTimeSeriesDistances.length(); i++) {
            String distancesoriginal = activityTimeSeriesDistances.getJSONObject(i).getString("value");
            Float distancesnumbersoriginal = Float.parseFloat(distancesoriginal);
            distancelistoriginal.add(distancesnumbersoriginal);
        }

        ArrayList<String> datetimelistsketch = new ArrayList<String>();
        for (int i = 0; i < activityTimeSeriesDistances.length(); i++) {
            String datetimesketch = (activityTimeSeriesDistances.getJSONObject(i).getString("dateTime"));
            String dateadaptedsketch = datetimesketch.substring(8, 10);
            datetimelistsketch.add(dateadaptedsketch);
        }
        System.out.println("DateTimeSketch List : " + datetimelistsketch);

        //calculating positions on the screen
        margin = 25;
        graphHeight = (height - margin) - margin;
        xSpacer = (width - margin - margin) / (distancelistoriginal.size() - 1);

        //Need to get the minimum and maximum value out of ArrayList
        Collections.sort(distancelistsketchcrunch, Collections.reverseOrder());
        float overallMin = distancelistsketchcrunch.get(0);
        float overallMax = distancelistsketchcrunch.get(distancelistsketchcrunch.size() - 1);

        positions = new PVector[distancelistsketchcrunch.size()];
        for (int i = 0; i < distancelistsketchcrunch.size(); i++) {
            float adjDistance = map(distancelistsketchcrunch.get(i), overallMin, overallMax, 0, graphHeight);
            float yPos = height - margin - adjDistance;
            float xPos = margin + (xSpacer * i);

            positions[i] = new PVector(xPos, yPos);
        }
    }
}
