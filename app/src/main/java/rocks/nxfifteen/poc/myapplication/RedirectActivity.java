package rocks.nxfifteen.poc.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import processing.android.PFragment;
import processing.core.PApplet;

public class RedirectActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static TextView data;
    private PApplet sketch;
    NxFitbitHelper nxFitbitHelper;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (sketch != null){
            sketch.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (sketch != null){
            sketch.onNewIntent(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect); // For now this will work, but once everything is fine change this to what ever layout your using once a user has completed authorisation

        //retrieve parameter from the Intent
        Uri returnUrl = getIntent().getData();
        if (returnUrl != null) {

            //this allows you to use all the methods in the helper class - request token with code
            this.nxFitbitHelper = new NxFitbitHelper();
            this.nxFitbitHelper.requestAccessTokenFromIntent(returnUrl);

            //get user profile using helper function
            try {
                JSONObject responseProfile = this.nxFitbitHelper.getUserProfile();
                Log.d(TAG, "From JSON encodedId: " + responseProfile.getJSONObject("user").getString("encodedId"));
                Log.d(TAG, "From JSON fullName: " + responseProfile.getJSONObject("user").getString("fullName"));

                JSONObject weightprofile = this.nxFitbitHelper.getWeight();
                Log.d(TAG, "From JSON weight:  " + weightprofile.getJSONObject("user").getString("weight"));

                JSONObject activity2402Profile = this.nxFitbitHelper.getActivity2402();
                Log.d(TAG, "From JSON distance 24/02:  " + activity2402Profile.getJSONArray("activities").getJSONObject(0).getString("distance"));

                JSONObject activity2802Profile = this.nxFitbitHelper.getActivity2802Test();
                Log.d(TAG, "From JSON duration 28/02:  " + activity2802Profile.getJSONArray("activities").getJSONObject(0).getString("duration"));
                //Log.d(TAG, "From JSON moderateActive distance 28/02:  " + activity2802Profile.getJSONObject("goals").getString("distance"));
                //Log.d(TAG, "From JSON moderateActive distance 28/02:  " + activity2802Profile.getJSONObject("summary").getJSONArray("distances").getJSONObject(4).getString("distance"));

                JSONObject activitiesHeart2802 = this.nxFitbitHelper.getHeart2802();
                Log.d(TAG, "From JSON Heart Statistics from 28/02:  " + activitiesHeart2802.getJSONArray("activities-heart").getJSONObject(0).getJSONObject("value").getJSONArray("heartRateZones").getJSONObject(1).getString("name"));
                Log.d(TAG, "From JSON Heart Statistics from 28/02:  " + activitiesHeart2802.getJSONArray("activities-heart").getJSONObject(0).getJSONObject("value").getJSONArray("heartRateZones").getJSONObject(1).getString("minutes"));
                Log.d(TAG, "From JSON Heart Statistics from 28/02:  " + activitiesHeart2802.toString());
                Log.d(TAG, "From JSON Heart Statistics from 28/02:  " + activitiesHeart2802.getJSONObject("activities-heart-intraday").getJSONArray("dataset").getJSONObject(0).getString("value"));

                //parsing heartrate statistics to test EndPoint
                data = (TextView) findViewById(R.id.toon_de_text);
                JSONArray heartstatistic = activitiesHeart2802.getJSONArray("activities-heart");
                String heartstats = heartstatistic.toString();
                String heart = activitiesHeart2802.getJSONArray("activities-heart").getJSONObject(0).getJSONObject("value").getJSONArray("heartRateZones").getJSONObject(1).getString("name");
                //data.setText(heartstats);

                /* TEST FOR LENGTH OF JSONArray, RESULT = 30
                int number = activityTimeSeriesDistances.length();
                String numberdisplay = String.valueOf(number);
                data.setText(numberdisplay);
                */

                //Get Heart Rate Time Series for an Activity on a Particular Day
                //STEP 1: get the Activity out
                JSONObject jsonactivity2802 = this.nxFitbitHelper.getActivity2802Test();
                JSONArray jsonactivity = jsonactivity2802.getJSONArray("activities");
                String jsonactivitystring = jsonactivity.toString();
                System.out.println("activity28/02 " + jsonactivitystring);
                //STEP 2: get originalStartTime and originalDuration out
                //STEP 3: transform these to a workable start and end time - mind new day..
                //STEP 4: retrieve an array of heart rates in this time period
                //STEP 5: use this array in a processing sketch

                //Parsing Activity Time Series to test EndPoint
                JSONObject activityTimeSeriesDistancesMonth = this.nxFitbitHelper.getDistances2802period1month();
                Log.d(TAG, "From JSON Heart Statistics from 28/02:  " + activityTimeSeriesDistancesMonth.toString());
                JSONArray activityTimeSeriesDistances = activityTimeSeriesDistancesMonth.getJSONArray("activities-distance");
                String distancestats = activityTimeSeriesDistances.toString();
                data.setText(distancestats);
                System.out.println("Length of Array : " + activityTimeSeriesDistances.length());

                // use a for loop to retrieve all data from the heart rate statistics from 30/01 to 28/02
                // this list you can use as input for the Processing sketch

                List<Float> distancelist = new ArrayList<Float>();
                for (int i=0; i < activityTimeSeriesDistances.length(); i++){
                    String distances = activityTimeSeriesDistances.getJSONObject(i).getString("value") ;
                    Float distancesnumber = Float.parseFloat(distances);
                    distancelist.add (distancesnumber);
                }
                System.out.println("Distance List : " + distancelist);

                List<String> datetimelist = new ArrayList<String>();
                for (int i=0; i < activityTimeSeriesDistances.length(); i++){
                    //change format datetime
                    String datetime = (activityTimeSeriesDistances.getJSONObject(i).getString("dateTime"));
                    String dateadapted = datetime.substring(8,10);
                    datetimelist.add (dateadapted);
                }
                System.out.println("DateTime List : " + datetimelist);
                String element = datetimelist.get(2);
                Integer numberelement = Integer.parseInt(element);
                System.out.println("List Item : " + numberelement);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Query profile variable directly
            Log.d(TAG, "From class encodedId: " + this.nxFitbitHelper.getFieldFromProfile("encodedId"));
            Log.d(TAG, "From class fullName: " + this.nxFitbitHelper.getFieldFromProfile("fullName"));

            // Query field Helper class
            Log.d(TAG, "From helper class encodedId: " + this.nxFitbitHelper.getFieldFrom("Profile", "encodedId"));
            Log.d(TAG, "From helper class fullName: " + this.nxFitbitHelper.getFieldFrom("Profile", "fullName"));

            String name = this.nxFitbitHelper.getFieldFrom("Profile", "fullName");
            System.out.println("Name = " + name);

            // Get user activities by calling end point directly
            try {
                @SuppressLint("SimpleDateFormat")
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();

                JSONObject responseProfile = this.nxFitbitHelper.makeApiRequest("user/-/activities/date/" + dateFormat.format(date) + ".json");
                Log.d(TAG, "steps: " + responseProfile.getJSONObject("summary").getString("steps"));
                String number_steps = responseProfile.getJSONObject("summary").getString("steps");
                System.out.println("Steps taken = " + number_steps);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setId(R.id.SketchFrame);
            sketch = new sketchone(nxFitbitHelper);
            PFragment fragment = new PFragment(sketch);
            fragment.setView(frameLayout,this);

        } else {
            Log.d(TAG, "Something is wrong with the return value from this.nxFitbitHelper. getIntent().getData() is NULL?");
        }
    }
}
