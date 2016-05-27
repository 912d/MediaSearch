/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mediasearch;

import java.io.UnsupportedEncodingException;
import twitter.TwitterSearch;

/**
 *
 * @author jackmusial
 */
public class Main {
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        TwitterSearch search = new TwitterSearch("#NASA", TwitterSearch.SEARCHTYPE.TEXT);
        search.search();
        System.out.println(search.getRateLimitStatus());
        //RunInterval interval = new RunInterval(1, search);
        //interval.start();
        
    }

}
