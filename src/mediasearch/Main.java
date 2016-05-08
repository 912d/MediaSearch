/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mediasearch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mediasearch.twitter.TwitterSearch;

/**
 *
 * @author jackmusial
 */
public class Main {
    
    public static void main(String[] args) {
        TwitterSearch search = new TwitterSearch("obama", TwitterSearch.SEARCHTYPE.TEXT);
        try {
            String search1 = search.search();
            System.out.println(search1);
            //RunInterval interval = new RunInterval(1, search);
            //interval.start();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
