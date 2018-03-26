package rocks.nxfifteen.poc.myapplication;

import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this code you use in the Sign Up Button/ Activity
        getApiCodeFromFitbit();
    }

    public void getApiCodeFromFitbit() {
        NxFitbitHelper.sendUserToAuthorisation(this);
    }

}
