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
import java.net.MalformedURLException;
import java.net.URL;
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
    private ConfigurationBuilder configurationBuilder;
    private String query;
    private TwitterService twitterService;

    /**
     * default constructor
     */
    public TwitterSearch() {
        this("Obama", SEARCHTYPE.TEXT);
    }
    
    /**
     * @param query our query to search twitter
     * @param searchtype indicate where we are looking for our query
     */
    public TwitterSearch(String query, SEARCHTYPE searchtype) {
        this.query = query;
        this.searchtype = searchtype;
        database = new HashMap<>();
        twitterService = new TwitterService();
        bearerToken = twitterService.getBEARER_TOKEN();
        System.out.println(bearerToken);
    }    

    // Fetches the first tweet from a given user's timeline
    public String search() throws IOException {
        //ADD url obfuscation etc
        String endPointUrl ="https://api.twitter.com/1.1/search/tweets.json?q=%23" + getQuery();
                //"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=twiterapi&count=2";
                
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
            System.out.println("");
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
    
    /*public boolean Search() throws TwitterException {
        setup();
        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = tf.getInstance();
        try {
            Query queries = new Query(getQuery());
            QueryResult result;
            do {
                result = twitter.search(queries);
                List<Status> tweets = result.getTweets();
                tweets.stream().forEach((tweet) -> {
                    switch (searchtype) {
                        case HASHTAG:
                        case TEXT:
                            String t = tweet.getText();
                            database.put(tweet.getUser().getScreenName(),
                                    tweet.getText());
                            break;
                        case USERNAME:
                            t = tweet.getUser().getScreenName();
                            database.put(tweet.getUser().getScreenName(),
                                    tweet.getText());
                    }
                });
            } while ((queries = result.nextQuery()) != null);
        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
            return false;
        }
        return true;
    } */
    
    /**
     * setup twitter configuration builder
     */
    public void setup() { 
        configurationBuilder = new ConfigurationBuilder();
        ConfigurationBuilder setOAuthAccessTokenSecret;
        setOAuthAccessTokenSecret = configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterService.getCONSUMER_KEY())
                .setOAuthConsumerSecret(twitterService.getCONSUMER_SECRET())
                .setOAuthAccessToken(twitterService.getACCESS_TOKEN())
                .setOAuthAccessTokenSecret(twitterService.getACCESS_TOKEN_SECRET());
    }
    
    public int getCounter() { return counter; }
    public HashMap<String, String> getDatabase() { return database; }
    public SEARCHTYPE getSearchtype() { return searchtype; }
    public String getBearerToken() { return bearerToken; }
    public String getQuery() { return this.query; }   
}