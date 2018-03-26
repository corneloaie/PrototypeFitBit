package rocks.nxfifteen.poc.myapplication;

//import com.github.scribejava.apis.service.LinkedIn20ServiceImpl;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Fitbit's OAuth2 client's implementation
 * source: https://dev.fitbit.com/docs/oauth2/
 */
public class FitbitApi20 extends DefaultApi20 {

    @SuppressWarnings("WeakerAccess")
    protected FitbitApi20() {}

    private static class InstanceHolder {
        private static final FitbitApi20 INSTANCE = new FitbitApi20();
    }

    @SuppressWarnings("WeakerAccess")
    public static FitbitApi20 instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.fitbit.com/oauth2/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://www.fitbit.com/oauth2/authorize";
    }

    @Override
    public String getRevokeTokenEndpoint() {
        return "https://api.fitbit.com/oauth2/revoke";
    }

    @Override
    public OAuth20Service createService(OAuthConfig config) {
        return new Fitbit20ServiceImpl(this, config);
    }
}