package com.policyboss.policybosspro.utils.FirebasePushNotification;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {


    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"policyboss-pro\",\n" +
                    "  \"private_key_id\": \"ed5a101f54f062444e0f7d2f01cf8812e87e0d85\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXbw0yUj5PyAcH\\nPW12HePI1kCp/L4HgigQxabf828gCxMl2Lvo78XBEMqI5NHMVxgz5geUnnAs2ujW\\ni1pQerL9Xti87LlBIzD1OiYumNpR9WWKNKKc9Sd5m3774bEQUvKAIXEmqIb7FbG2\\n5Uz+fbugzaPZbd6qjmbiPZPUs0wiYtuddaYX9BRyCF0GnS2w/cdhxo3rSX6tJygd\\nyPwdY81TrT7fUPch+/DQmGCvx8LdHW6+P2NnKy+6ToRM6NRVJQMvJZF5hyMzH/7v\\nowfPRdYvNqGjh4h7YSvW5KtRFsi/DX9sKfqA7vagqtfe2cz5YGlGohNcu6qWL2rm\\nzr+LroQDAgMBAAECggEAHH/r7eKIk59Do1suNKKfcfbQqK1GXkdinvVz2IYKDe/H\\npMHLa6VkhFdSprzZtylVBr9uAcxntClm1KnZT3goI4MsKzIqE1Y6KozNDsv5mp8i\\ngwYw7HU+/qYDGgh5uGbD+NVSZEInVS3aCl/RQ7wKCdNp2MvSsiqmtkF8hvVPiNDj\\n749IwO3TlQ740NJlQGCbmkJliU/bW6mCq/jp3BOGsmEXTz+OSnYK6Me+DWDuez3s\\naIvTWHUR/QqhPt6yPgnVKPSajp7D73nOOt7Wvsa5iB/1C28l0GKL5tzBir3HX3t4\\nLi6v7oANuy9pAI5AziC6+x9561jYLIzQTv+x3WQX0QKBgQC8DtEr+YhQ+dSbGerW\\nJt6tGPSlJAv6vxqHxKM6xEp54jyVp+4OLZWwh5ERecG1r7SFgJzdF5siiFTNpGV7\\nTZeQoZCcVNZtuJfjQaVxBI+ZcbCcmZOA7CRlJXGMWTT0S75UH/zdY79lmOnw8o2z\\nPbCnbz7rwzWtWe5w8NBuqQ6K5QKBgQDOJO3yClvlSGlEpdpwuIi1L79GZGogRXQ7\\nR25HaK2r0pSzAYc97YsLDP1e9kiOMNjKjd8M4mvC7eJWMkpJx5UE58pvKTqjqlEH\\nXdoFl+m/EW2reF6Fg7CWLcMldFjXH1shofZGZKCDjg9v/nQBkNCnEdNhKKNuPdot\\nOzFQvPmcxwKBgDs8I5DJabGv79LnB/xqg+tcF/tIbmmN9mpbetrQ53Ca3WB6Y9ad\\nbzAKk5xAkM6MyLzgdQiDHf9zl9qqdLixCROqgGVtp5kp8tXHYLhHqn+3utyo1zhd\\nqV0evTRrCO4GSa4v4TNO7wOudcnKbO3PeaxTsysJ8wdmc1+pys2AfK8tAoGBAMQB\\npSWg5+PIyB73vA21yHbO4YYi8C2jHHNI5fiL9aYMYksaRueBN9XYEgn7Dt30YA2o\\ne88acvAzlCP1SWGBnZW+d2LextzbkpP4jtcGrMB559dEakt0/bCt7oFscRSKwfHm\\nt+4Su/SaXN9WCCN8+IgCkQQuycaGlS6wu6FTLdv9AoGAbLS8a3+l81ILTHiuTgIb\\ncikZn2dsVsudeDN14dltG+appoPgI3fnE8FLLjEy9Fc8lv8iI55tTxJ+H95YmfBQ\\n8slNZyGgCWVlWJYso1ieLPwwJL0F5/ycVmyD7b+B6jHQGtN2tk5fJKDgi2hGQs2z\\n9UUGbMXFLdrAEX++3uCI3jM=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-16551@policyboss-pro.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"106443527664032529340\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-16551%40policyboss-pro.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}
