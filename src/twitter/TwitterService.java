/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author jackmusial
 */
public final class TwitterService {
    private final String CONSUMER_KEY = "EgzK0RfsxfSiOIuaocz1dWl2i";
    private final String CONSUMER_SECRET = "mv2MSuA2AsQXzLoyyJCSm2e1WUlKcgw5VIHkkhI6xTrbxV2t7t";
    private final String ACCESS_TOKEN = "725253533071712256-l1F54xJlqmtlCfzZ9gN7Mgd09C5YFgg";
    private final String ACCESS_TOKEN_SECRET = "Kw7qX4XmXtXdtujBmWD8wA54myr9d8vBPUihRxBDn3bWX";
    private final String OAUTH_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    private final String RATE_LIMIT_STATUS_URL = "https://api.twitter.com/1.1/application/rate_limit_status.json?resources=search";
    private final String bearerToken;
    
    public TwitterService() {
        bearerToken = requestBearerToken(); 
    }

    private String encodeKeys() {
	try {
            String encodedConsumerKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");
            String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
            byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
            return new String(encodedBytes);  
	}
	catch (UnsupportedEncodingException e) {
            return new String();
	}
    }
    
    public int getRateLimitStatus() {
        HttpsURLConnection connection = bearerAuthConnectionGet(RATE_LIMIT_STATUS_URL);
        int ret = 0;
        JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));
        if (obj != null) {
            JSONObject o = (JSONObject) obj.get("resources");
            o = (JSONObject) o.get("search");
            o = (JSONObject) o.get("/search/tweets");
            ret = (int) o.get("remaining");
        }
        return ret;
    }
    
    private String requestBearerToken() {
        String ret = null;
        HttpsURLConnection connection = basicAuthConnectionPost(OAUTH_TOKEN_URL);
        JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));
        if (obj != null) {
            String tokenType = (String)obj.get("token_type");
            String token = (String)obj.get("access_token");
            ret = ((tokenType.equals("bearer")) && (token != null)) ? token : "";
        }
        if (connection != null) {
            connection.disconnect();
        }
        return ret;
    }
    
    public HttpsURLConnection bearerAuthConnectionGet(String url) {
        HttpsURLConnection connection = null;
        try { 
            connection = setUpConnection("GET", url, "Bearer ", getBearerToken());
        } catch  (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return connection;
    }
    
    public HttpsURLConnection basicAuthConnectionPost(String url) {
        HttpsURLConnection connection = null;
        try { 
            connection = setUpConnection("POST", url, "Basic ", encodeKeys());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return connection;
    }
    
    private HttpsURLConnection setUpConnection(String requestMethod, String endPointUrl, 
            String authorization, String authValue) throws IOException {
        URL url = new URL(endPointUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Host", "api.twitter.com");
        connection.setRequestProperty("User-Agent", "NewSeedCloud");
        connection.setRequestProperty("Authorization", authorization + authValue);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); 
        //connection.setRequestProperty("Content-Length", "29");
        connection.setUseCaches(false);
        if (!authorization.contains("Bearer ")) {
            writeRequest(connection, "grant_type=client_credentials"); 
        }
        return connection;
    }
    
    public boolean writeRequest(HttpsURLConnection connection, String textBody) {
	try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            wr.write(textBody);
            wr.flush();
	}
	catch (IOException e) { return false; }
        return true;
    }
	
    public String readResponse(HttpsURLConnection connection) {
	try {
            StringBuilder str = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = br.readLine()) != null) {
                str.append(line).append(System.getProperty("line.separator"));
            }
            return str.toString();
	}
	catch (IOException e) { return new String(); }
    }

    public String getBearerToken() { return bearerToken;}
}