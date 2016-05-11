/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediasearch.twitter;

/**
 *
 * @author jackmusial
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSearch {
    
    public enum SEARCHTYPE {
        TEXT,
        USERNAME,
        HASHTAG
    }
    
    private static HashMap<String, String> database;
    private static int counter;
    private final SEARCHTYPE searchtype;
    private final String bearerToken;
    private final ConfigurationBuilder configurationBuilder;
    private final String query;
    private final TwitterService twitterService;
    private final String endPointUrl;

    /**
     * @param query our query to search twitter
     * @param searchtype indicate where we are looking for our query
     * @throws java.io.UnsupportedEncodingException
     */
    public TwitterSearch(String query, SEARCHTYPE searchtype) throws UnsupportedEncodingException {
        this.query = query;
        this.searchtype = searchtype;
        database = new HashMap<>();
        endPointUrl = "https://api.twitter.com/1.1/search/tweets.json?q=" + 
                URLEncoder.encode(query, "UTF-8");
        twitterService = new TwitterService();
        bearerToken = twitterService.BEARER_TOKEN;
        configurationBuilder = new ConfigurationBuilder();
        System.out.println(bearerToken);
    }    

    // Fetches the first tweet from a given user's timeline
    public String search() throws IOException {
	HttpsURLConnection connection = null;
        String ret = "";
	try {
            URL url = new URL(endPointUrl); 
            connection = (HttpsURLConnection) url.openConnection();           
            connection.setDoOutput(true);
            connection.setDoInput(true); 
            connection.setRequestMethod("GET"); 
            connection.setRequestProperty("Host", "api.twitter.com");
            connection.setRequestProperty("User-Agent", "NewSeedCloud");
            connection.setRequestProperty("Authorization", "Bearer " + getBearerToken());
            connection.setUseCaches(false);

            // Parse the JSON response into a JSON mapped object to fetch fields from.
            JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));
            //TODO add parsing of json
            System.out.println(obj);
            /*if (obj != null) {
                String tweet = ((JSONObject)obj.get(0)).get("text").toString();
                ret = (tweet != null) ? tweet : "";
            }*/
	}
	catch (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified.", e);
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
	}
        return ret;
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
    
    /**
     * setup twitter configuration builder
     */
    public void setup() { 
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(TwitterService.CONSUMER_KEY)
                .setOAuthConsumerSecret(TwitterService.CONSUMER_SECRET)
                .setOAuthAccessToken(TwitterService.ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TwitterService.ACCESS_TOKEN_SECRET);
    }
    
    public int getCounter() { return counter; }
    public HashMap<String, String> getDatabase() { return database; }
    public SEARCHTYPE getSearchtype() { return searchtype; }
    public String getBearerToken() { return bearerToken; }
    public String getQuery() { return this.query; }   
}