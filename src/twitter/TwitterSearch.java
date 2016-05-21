/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

/**
 *
 * @author jackmusial
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TwitterSearch {
    
    public enum SEARCHTYPE {
        TEXT,
        USERNAME,
        HASHTAG
    }
    
    private static int counter;
    private final SEARCHTYPE searchtype;
    private final String bearerToken;
    private final String query;
    private final TwitterService twitterService;
    private final String SEARCH_URL;

    /**
     * @param query our query to search twitter
     * @param searchtype indicate where we are looking for our query
     * @throws java.io.UnsupportedEncodingException
     */
    public TwitterSearch(String query, SEARCHTYPE searchtype) throws UnsupportedEncodingException {
        this.query = query;
        this.searchtype = searchtype;
        SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=" 
                + URLEncoder.encode(query, "UTF-8");
        twitterService = new TwitterService();
        bearerToken = twitterService.getBearerToken();
        System.out.println(bearerToken);
    }    

    private int getRateLimitStatus() {
        return twitterService.getRateLimitStatus();
    }
    
    public ArrayList<TwitterTweet> search() {
        HttpsURLConnection connection = twitterService.bearerAuthConnectionGet(SEARCH_URL);
        ArrayList<TwitterTweet> ret = new ArrayList<>();
        JSONObject obj = (JSONObject) JSONValue.parse(readResponse(connection));
        
        if (obj == null) return new ArrayList<>();
        
        JSONArray array = (JSONArray) obj.get("statuses");
        array.stream().forEach((_item) -> {
            JSONObject object = (JSONObject) _item;
            ArrayList<String> data = new ArrayList<>();
            data.add((String) object.get("created_at"));
            data.add((String) object.get("text"));
            if (object.containsKey("urls")) {
                JSONObject o = (JSONObject) object.get("entities");
            }
        });
        System.out.println(array);

        if (connection != null) {
            connection.disconnect();
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
    
    public int getCounter() { return counter; }
    public SEARCHTYPE getSearchtype() { return searchtype; }
}