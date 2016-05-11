/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediasearch.twitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
    protected static final String CONSUMER_KEY = "EgzK0RfsxfSiOIuaocz1dWl2i";
    protected static final String CONSUMER_SECRET = "mv2MSuA2AsQXzLoyyJCSm2e1WUlKcgw5VIHkkhI6xTrbxV2t7t";
    protected static final String ACCESS_TOKEN = "725253533071712256-l1F54xJlqmtlCfzZ9gN7Mgd09C5YFgg";
    protected static final String ACCESS_TOKEN_SECRET = "Kw7qX4XmXtXdtujBmWD8wA54myr9d8vBPUihRxBDn3bWX";
    private static final String OAUTH_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    private static final String RATE_LIMIT_STATUS_URL = "https://api.twitter.com/1.1/application/rate_limit_status.json?resources=search";
    protected final String BEARER_TOKEN;
    
    public TwitterService() {
        BEARER_TOKEN = requestBearerToken(); 
    }

    // Encodes the consumer key and secret to create the basic authorization key
    private static String encodeKeys() {
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
    
    private static HttpsURLConnection setUpConnection(String requestMethod, String endPointUrl, 
            String authorization, String authValue) {
        HttpsURLConnection connection = null;
        URL url; 
        try {
            url = new URL(endPointUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true); 
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Host", "api.twitter.com");
            connection.setRequestProperty("User-Agent", "NewSeedCloud");
            connection.setRequestProperty("Authorization", authorization + authValue);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); 
            //connection.setRequestProperty("Content-Length", "29");
            connection.setUseCaches(false);
            writeRequest(connection, "grant_type=client_credentials");
        } catch (MalformedURLException | ProtocolException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return connection;
    }
    
    // Constructs the request for requesting a bearer token and returns that token as a string
    private static String requestBearerToken() {
        String ret = "";
        HttpsURLConnection connection = setUpConnection("POST", OAUTH_TOKEN_URL, "Basic ", encodeKeys());
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
    
    private String getRateLimitStatus() {
        HttpsURLConnection connection = setUpConnection("GET",
                RATE_LIMIT_STATUS_URL, "Bearer ", BEARER_TOKEN);
        String ret = null;
        //json parsing
        
        return ret;
    }
    
    // Writes a request to a connection
    private static boolean writeRequest(HttpsURLConnection connection, String textBody) {
	try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            wr.write(textBody);
            wr.flush();
	}
	catch (IOException e) { return false; }
        return true;
    }
	
    // Reads a response for a given connection and returns it as a string.
    public static String readResponse(HttpsURLConnection connection) {
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
}