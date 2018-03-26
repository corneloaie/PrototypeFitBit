package rocks.nxfifteen.poc.myapplication;

import android.util.Base64;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.revoke.TokenTypeHint;

import java.nio.charset.Charset;

public class Fitbit20ServiceImpl extends OAuth20Service {

    Fitbit20ServiceImpl(DefaultApi20 api, OAuthConfig config) {
        super(api, config);
    }

    //encode the ApiKey and ApiSecret in a Base64 Format for encryption purposes
    private String AuthorizationBasic() {
        OAuthConfig config = this.getConfig();
        String preEncodeValue = String.format("%s:%s", config.getApiKey(), config.getApiSecret());
        byte[] byteValue = preEncodeValue.getBytes(Charset.forName("UTF-8"));
        return new String(Base64.encode(byteValue, Base64.DEFAULT));
    }

    @Override
    protected OAuthRequest createAccessTokenRequest(String code) {
        //System.out.println(getApi().getAccessTokenEndpoint());

        //HERE IS THE LINK WITH FITBITAPI20
        OAuthRequest request = new OAuthRequest(Verb.POST, getApi().getAccessTokenEndpoint());
        OAuthConfig config = this.getConfig();

        request.addParameter("client_id", config.getApiKey());
        request.addParameter("client_secret", config.getApiSecret());
        request.addParameter("code", code);
        request.addParameter("redirect_uri", config.getCallback());
        request.addParameter("scope", config.getScope());
        request.addParameter("grant_type", "authorization_code");
        //this is non-OAuth2 standard, but Fitbit requires it
        request.addHeader("Authorization", "Basic " + AuthorizationBasic());
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");

//        System.out.println(request.getHeaders());
//        System.out.println(request.getUrl());
//        System.out.println(request.getQueryStringParams());
//        System.out.println(request.getBodyParams());
//        System.out.println(request.getCompleteUrl());

        return request;
    }

    @Override
    protected OAuthRequest createRevokeTokenRequest(String tokenToRevoke, TokenTypeHint tokenTypeHint) {
        OAuthRequest request = new OAuthRequest(Verb.POST, getApi().getRevokeTokenEndpoint());

        request.addParameter("token",tokenToRevoke);

        request.addHeader("Authorization", "Basic " + AuthorizationBasic());
        //unclear if needed to include this
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");

        return request;
    }
}

