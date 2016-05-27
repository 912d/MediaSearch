/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jackmusial
 */
public class TwitterTweet {
    public String created_at;
    public String text;
    public entities entities;
    public user user;
    
    public class entities {
        public List<hashtags> hashtags = new ArrayList<>();
        public List<urls> urls = new ArrayList<>();
                
        class hashtags {
           public String text;
        }
        
        class urls {
            public String url;
        }
    }
    
    public class user {
        public String name;
        public String location;
        public String description;
        public String profile_image_url_https;
    }

    @Override
    public String toString() {
        return "[created_at:" + created_at + ",\ntext:" + text + "\nhashtags:" 
                + entities.hashtags + ",\nurls:" + entities.urls + ",\n" + 
                "name:" + user.name + ",\nlocation:" + user.location + ",\n" +
                "description:" + user.description + ",\nprofileUrl:" + user.profile_image_url_https
                + "]";
    }
    
    /*public void setCreated_at(String created_at) {
    this.created_at = created_at;
    }
    
    public void setText(String text) {
    this.text = text;
    }
    
    public void setName(String name) {
    this.name = name;
    }
    
    public void setScreen_name(String screen_name) {
    this.screen_name = screen_name;
    }
    
    public void setLocation(String location) {
    this.location = location;
    }
    
    public void setDescription(String description) {
    this.description = description;
    }
    
    public void setUrl(String url) {
    this.url = url;
    }
    
    public void setProfile_image_url_https(String profile_image_url_https) {
    this.profile_image_url_https = profile_image_url_https;
    }
    
    public String getCreated_at() {
    return created_at;
    }
    
    public String getDescription() {
    return description;
    }
    
    public String getLocation() {
    return location;
    }
    
    public String getName() {
    return name;
    }
    
    public String getProfile_image_url_https() {
    return profile_image_url_https;
    }
    
    public String getScreen_name() {
    return screen_name;
    }
    
    public String getText() {
    return text;
    }
    
    public String getUrl() {
    return url;
    }*/
}