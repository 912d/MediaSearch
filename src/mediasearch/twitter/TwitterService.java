/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediasearch.twitter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author jackmusial
 */
public class TwitterService {
    private final String bearerToken;
    private final ArrayList<TwitterTweet> ret; 
    
    public TwitterService() {
        bearerToken = requestBearerToken(); 
        ret = new ArrayList<>();
    }
    
    private String requestBearerToken() {        
        HttpsURLConnection connection = basicAuthConnectionPost(TwitterData.OAUTH_TOKEN_URL);
        
        JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));
        if (obj != null) {
            String tokenType = (String)obj.get("token_type");
            String token = (String)obj.get("access_token");
            return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
        }
        
        if (connection != null) {
            connection.disconnect();
        }
        return null;
    }
    
    public ArrayList<TwitterTweet> search(String query) {
        try {
            return searchForTweetsAndParseJson(query);
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex.getMessage());
        }
        
        return null;
    }
    
    private ArrayList<TwitterTweet> searchForTweetsAndParseJson(String query) throws UnsupportedEncodingException {
        final String q = TwitterData.SEARCH_URL + URLEncoder.encode(query, "UTF-8");
        HttpsURLConnection connection = bearerAuthConnectionGet(q);
        if (connection != null) {
            connection.disconnect();
        }
        
        String readResponse = readResponse(connection);
        JSONObject obj = (JSONObject) JSONValue.parse(readResponse);
        return parseJsonIntoTweets(obj);
    }
    
    private ArrayList<TwitterTweet> parseJsonIntoTweets(JSONObject object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(String.class, new NullJsonStringConverter());
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        
        JSONArray array = (JSONArray) object.get("statuses");
        for(int i = 0; i < array.size(); i++) {
            JSONObject jSONObject = (JSONObject) array.get(i);
            TwitterTweet tweet = gson.fromJson(jSONObject.toString(), TwitterTweet.class);
            ret.add(tweet); 
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

    public long getRateLimitStatus() {
        HttpsURLConnection connection = bearerAuthConnectionGet(TwitterData.RATE_LIMIT_STATUS_URL);
        
        JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));
        if (obj != null) {
            JSONObject o = (JSONObject) obj.get("resources");
            o = (JSONObject) o.get("search");
            o = (JSONObject) o.get("/search/tweets");
            return (long) o.get("remaining");
        }
        
        return 0xdeadbeef;
    }    
    
    private String encodeKeys() {
	try {
            String encodedConsumerKey = URLEncoder.encode(TwitterData.CONSUMER_KEY, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(TwitterData.CONSUMER_SECRET, "UTF-8");
            String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
            byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
            return new String(encodedBytes);  
	}
	catch (UnsupportedEncodingException e) {
            return new String();
	}
    }
    
    public String getBearerToken() { return bearerToken;}
}