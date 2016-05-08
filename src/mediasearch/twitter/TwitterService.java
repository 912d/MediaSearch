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
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final String END_POINT_URL = "https://api.twitter.com/oauth2/token";
    private final String BEARER_TOKEN;
    
    public TwitterService() {
        BEARER_TOKEN = requestBearerToken();
    }

    // Encodes the consumer key and secret to create the basic authorization key
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
    
    // Constructs the request for requesting a bearer token and returns that token as a string
    public String requestBearerToken() {
        HttpsURLConnection connection = null;
        String encodedCredentials = encodeKeys();
        System.out.println(encodedCredentials);
        String ret = "";
        try {
            URL url = new URL(END_POINT_URL); 
            connection = (HttpsURLConnection) url.openConnection();   
            connection.setDoOutput(true);
            connection.setDoInput(true); 
            connection.setRequestMethod("POST"); 
            connection.setRequestProperty("Host", "api.twitter.com");
            connection.setRequestProperty("User-Agent", "NewSeedCloud");
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); 
            connection.setRequestProperty("Content-Length", "29");
            connection.setUseCaches(false);
            writeRequest(connection, "grant_type=client_credentials");

            // Parse the JSON response into a JSON mapped object to fetch fields from.
            JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));

            if (obj != null) {
                String tokenType = (String)obj.get("token_type");
                String token = (String)obj.get("access_token");
                ret = ((tokenType.equals("bearer")) && (token != null)) ? token : "";
            }
        }
        catch (MalformedURLException e) {} 
        catch (IOException ex) {
            Logger.getLogger(TwitterService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (connection != null) {
                    connection.disconnect();
            }
        }
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
    
    public String getACCESS_TOKEN_SECRET() { return ACCESS_TOKEN_SECRET; }
    public String getCONSUMER_KEY() { return CONSUMER_KEY; }
    public String getCONSUMER_SECRET() { return CONSUMER_SECRET; }
    public String getACCESS_TOKEN() { return ACCESS_TOKEN; }
    public String getEND_POINT_URL() { return END_POINT_URL; }   
    public String getBEARER_TOKEN() { return BEARER_TOKEN; }
}