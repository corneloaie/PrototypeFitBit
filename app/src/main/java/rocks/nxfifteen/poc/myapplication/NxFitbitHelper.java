package rocks.nxfifteen.poc.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.support.customtabs.CustomTabsIntent;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

class NxFitbitHelper {
    @SuppressWarnings("FieldCanBeLocal")
    private String clientid = "CLIENT/ID";
    @SuppressWarnings("FieldCanBeLocal")
    private String clientSecret = "CLIENT/SECRET";
    @SuppressWarnings("FieldCanBeLocal")
    private String apiCallback = "YOURCALLBACK";
    @SuppressWarnings("FieldCanBeLocal")
    private String apiScope = "weight activity heartrate sleep profile";
    private JSONObject apiValueProfile, apiValueActivity, apiValueActivity2802, apiValueActivity2802New, apiValueDistancesPeriod, apiValueHeart2802;
    private String authCode;
    private OAuth20Service service;
    private OAuth2AccessToken accessToken, refreshToken;
    private Date date;

    //Constructor, allows for all kinds of network access, application will not block. This is ok since this flow is only run once as long as the scope stays the same.
    NxFitbitHelper() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    //Points the application to Chrome Custom Tabs to get the Code
    static void sendUserToAuthorisation(Context callingContext) {
        NxFitbitHelper helperClass = new NxFitbitHelper();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(callingContext, Uri.parse(helperClass.getAuthorizationUrl()));
    }

    //Method creates the OAuth20Service if none is already running
    private String getAuthorizationUrl() {
        return getService().getAuthorizationUrl();
    }

    //works with OAuth20Service, gathers all parameters to make the call
    private OAuth20Service createService() {
        return new ServiceBuilder(clientid)
                .apiSecret(clientSecret)
                .scope(apiScope) // replace with desired scope
                .callback(apiCallback)  //your callback URL to store and handle the authorization code sent by Fitbit
                .build(FitbitApi20.instance());
    }

    //Creates service if necessary
    private OAuth20Service getService() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    //You extract the Code from the "code" variable in the returned Uri
    private void setAuthCodeFromIntent(Uri returnUrl) {
        authCode = returnUrl.getQueryParameter("code");
        System.out.println("Auth Code set to " + authCode);
    }

    //You get the Token with the Access Code
    private void requestAccessToken() {
        try {
            OAuth20Service serviceCall = getService();
            accessToken = serviceCall.getAccessToken(authCode);
            System.out.println("Access Token=" + accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //Get access Code and get Token with Code
    void requestAccessTokenFromIntent(Uri returnUrl) {
        setAuthCodeFromIntent(returnUrl);
        requestAccessToken();
    }

    /*No need for a refresh code flow since
     Alternatively, your application could redirect the user to the authentication flow.
     If the user is signed in and the scopes requested match the previously granted scopes
     The user will be redirected to your redirect URI with a new access token without being prompted.*/

    //REVOKE ACCESS TOKEN STILL TO CODE / TRY YOURSELF AND HAVE IT CHECKED
    //Revoke Access Token
    private void revokeAccessToken (){

    }

    //FROM HERE ONWARDS API CALLS FOR DATA
    //METHOD 1 API CALL
    //Create Service with the Application Parameters and returns JSON data, this is structure of general call
    JSONObject makeApiRequest(String endPointUrl) throws InterruptedException, ExecutionException, IOException {
        OAuth20Service serviceCall = getService();

        final OAuthRequest activityrequest = new OAuthRequest(Verb.GET, "https://api.fitbit.com/1/" + endPointUrl);
        serviceCall.signRequest(accessToken, activityrequest);

        final Response response;
        response = serviceCall.execute(activityrequest);

        try {
            return new JSONObject(response.getBody());
        } catch (JSONException e) {
            return null;
        }
    }

    //Example request for the user profile
    JSONObject getUserProfile() throws InterruptedException, ExecutionException, IOException {
        if (apiValueProfile == null) {
            apiValueProfile = makeApiRequest("user/-/profile.json");
        }
        return apiValueProfile;
    }

    //Example request for weight
    JSONObject getWeight() throws InterruptedException, ExecutionException, IOException {
        if (apiValueProfile == null){
            apiValueProfile = makeApiRequest("user/-/body/log/weight/goal.json");
        }
        return apiValueProfile;
    }

    //THIS ONE FAILS
    JSONObject getActivity2802() throws InterruptedException, ExecutionException, IOException {
        if (apiValueActivity2802 == null) {
            apiValueActivity2802 = makeApiRequest("user/-/activities/date/2018-02-28.json");
        }
        return apiValueActivity2802;
    }

    //Get Activity date 28/2/2018
    //First thing you get back is object, {}, so JSONObject
    //For this user, so we use "-"
    JSONObject getActivity2802Test() throws InterruptedException, ExecutionException, IOException {
        if (apiValueActivity2802New == null){
            apiValueActivity2802New = makeApiRequest("user/-/activities/list.json&afterDate=2018-02-28&offset=0&limit=2&sort=asc");
        }
        return apiValueActivity2802New;
    }

    //Example request from 01/01/2018
    JSONObject getActivity2402() throws InterruptedException, ExecutionException, IOException {
        if (apiValueActivity == null) {
            apiValueActivity = makeApiRequest("user/-/activities/list.json&afterDate=2018-02-24&offset=0&limit=2&sort=asc");
        }
        return apiValueActivity;
    }

    JSONObject getHeart2802() throws InterruptedException, ExecutionException, IOException {
        if (apiValueHeart2802 == null){
            apiValueHeart2802 = makeApiRequest("user/-/activities/heart/date/2018-02-28/1d.json");
        }
        return apiValueHeart2802;
    }

    //ACTIVITY TIME SERIES
    //Activity Time Series for the Distance Variable
    JSONObject getDistances2802period1month() throws InterruptedException, ExecutionException, IOException {
        if (apiValueDistancesPeriod == null){
            apiValueDistancesPeriod = makeApiRequest("user/-/activities/distance/date/2018-02-28/30d.json");
        }
        return apiValueDistancesPeriod;
    }

    //Again get the UserProfile, then get the "user" JSON and extract a value from one of the underlying variables
    //METHOD 2 API CALL
    String getFieldFromProfile(String jsonFieldName) {
        String jsonFieldValue = "An error occurred";
        if (apiValueProfile == null) {
            try {
                apiValueProfile = makeApiRequest("user/-/profile.json");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonFieldValue = apiValueProfile.getJSONObject("user").getString(jsonFieldName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonFieldValue;
    }

    //Extract a field value from the JSON from return
    String getFieldFrom(String apiSource, String jsonFieldName) {
        String jsonFieldValue = "An error occurred";

        switch (apiSource) {
            case "Profile":
                jsonFieldValue = getFieldFromProfile(jsonFieldName);
                break;
            default:
                jsonFieldValue = "No helper class for " + apiSource;
                break;
        }

        return jsonFieldValue;
    }
}
