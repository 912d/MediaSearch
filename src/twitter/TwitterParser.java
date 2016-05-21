/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author jackmusial
 */
public class TwitterParser {
    private final String bearerToken;
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    public TwitterParser() {
        bearerToken = requestBearerToken(); 
    }
    
    public static class TweetsList {
        @Key("statuses") 
        private List<Tweet> tweetsList;

        public List<Tweet> getTweetsList() {
            return tweetsList;
        }
    }
    
    public static class Tweet extends GenericJson {
        @Key("created_at")
        private String createdAt;

        public String getCreatedAt() {
            return createdAt;
        }
        
        @Key
        private String text;
        
        public String getText() {
            return text;
        }
        
        @Key
        private Entities entities;
        
        public Entities getEntities() {
            return entities;
        }
    }
    
    public static class Entities {
        @Key
        private List<Hashtags> hashtags;
        
        @Key
        private List<Symbols> symbols;
        
        @Key
        private List<UserMentions> userMentions;
        
        @Key
        private List<Urls> urls;
        
        @Key
        private List<Media> media;
        
    }
    
    private String requestBearerToken() {
        String ret = null;
        HttpsURLConnection connection = basicAuthConnectionPost(TwitterData.OAUTH_TOKEN_URL);
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
    
    public int getRateLimitStatus() {
        HttpRequest httpRequest = requestFa
        
        HttpsURLConnection connection = bearerAuthConnectionGet(TwitterData.RATE_LIMIT_STATUS_URL);
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