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
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

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

    /**
     * @param query our query to search twitter
     * @param searchtype indicate where we are looking for our query
     * @throws java.io.UnsupportedEncodingException
     */
    public TwitterSearch(String query, SEARCHTYPE searchtype) throws UnsupportedEncodingException {
        this.query = query;
        this.searchtype = searchtype;
        twitterService = new TwitterService();
        bearerToken = twitterService.getBearerToken();
        System.out.println(bearerToken);
    }    

    public long getRateLimitStatus() {
        return twitterService.getRateLimitStatus();
    }
    
    public void search() {
        ArrayList<TwitterTweet> search = twitterService.search(query);
        System.out.println(search);
        System.out.println(search.size());
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