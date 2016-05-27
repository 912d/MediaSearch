/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mediasearch;

import java.util.Timer;
import java.util.TimerTask;
import mediasearch.twitter.TwitterSearch;

/**
 *
 * @author jackmusial
 */
public class RunInterval {
    private final int MILISECONDS = 20000; //quantity of miliseconds in one hour 3600000
    private final int HOURS; 
    private final int TIME;
    private final Timer timer;
    private final TwitterSearch search;
    private int counter;
            
    public RunInterval(int HOURS, TwitterSearch search) {
        this.HOURS = HOURS;
        this.TIME = this.HOURS * MILISECONDS;
        timer = new Timer();
        this.search = search;
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
           @Override 
           public void run() {
                search.search();
           }
       }, 0, TIME); 
    }
    
    public Timer getTimer() { return this.timer; }         
    public TwitterSearch getSearch() { return search; }
}