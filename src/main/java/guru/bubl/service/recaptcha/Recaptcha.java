package guru.bubl.service.recaptcha;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MultivaluedMap;

public class Recaptcha {

    private final WebResource resource = Client.create(
            new DefaultApacheHttpClientConfig()).resource("https://www.google.com");

    @Inject
    @Named("skipRecaptcha")
    Boolean skipRecaptcha;

    @Inject
    @Named("googleRecaptchaKey")
    String googleRecaptchaKey;

    public RecaptchaResult getResult(JSONObject info) {
        if (skipRecaptcha) {
            return new RecaptchaResult(
                    true,
                    1.0
            );
        }
        MultivaluedMap recaptchaFormData = new MultivaluedMapImpl();
        recaptchaFormData.add("secret", googleRecaptchaKey);
        recaptchaFormData.add("response", info.optString("recaptchaToken", ""));
        ClientResponse recaptchaResponse = resource.path("recaptcha").path("api").path("siteverify").post(
                ClientResponse.class,
                recaptchaFormData
        );
        JSONObject recaptchaResult = recaptchaResponse.getEntity(JSONObject.class);
        return RecaptchaResult.isSuccessAndScore(
                recaptchaResult.optBoolean("success", false),
                recaptchaResult.optDouble("score", 0)
        );
    }
}
