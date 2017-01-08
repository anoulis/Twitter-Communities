/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twittermongodbapp;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.store;
import org.bson.Document;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.Location;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;


/**
 *
 * @author aristeidis
 */

public class TwitterMongoDBApp {

    /**
     * @param args the command line arguments
     * @throws twitter4j.TwitterException
     */
    public static void init(ConfigurationBuilder cf){
        cf.setDebugEnabled(true);
        cf.setJSONStoreEnabled(true);
        cf.setOAuthConsumerKey("SkmxKecZHTxfKmGcQLh7tTufm");
        cf.setOAuthConsumerSecret("HPWFJ6jVrVvuCQd4qT1C0b9q3QqifU27mbcuW3M6f8f81IGGQn");
        cf.setOAuthAccessToken("719105233322905600-FTnAnBZBFZ4AADDTpNyJNPNidsVNvvv");
        cf.setOAuthAccessTokenSecret("D6yIzAKqBLrGLAdIsXk5XvdUehudkgUqO5S7xrWcYp5pu");
    }
    
    public static void main(String[] args) throws TwitterException {
        
       ConfigurationBuilder cf1 = new ConfigurationBuilder();
       ConfigurationBuilder cf2 = new ConfigurationBuilder();
       init(cf1);
       init(cf2);
       
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );	
        DB db = mongoClient.getDB("tweetsTest");
        
        TwitterFactory tf = new TwitterFactory(cf2.build());
        twitter4j.Twitter twitter = tf.getInstance();
        /*
       
         System.out.println("Select option :");
         System.out.println("Collect tweets from Greece: 1");
         System.out.println("Save these tweets: 2");
         System.out.println("Exit: 0");
          Scanner scanner = new Scanner(System.in);
         int input = scanner.nextInt();
         if(input==1){
             CollectTweets ct = new CollectTweets();
             ct.collectTweets(db,cf1,twitter);
         }else if(input==2){
             SaveTweets st = new SaveTweets();
             st.saveTweets(db,twitter);
         }else if (input==0){
             mongoClient.close(); 
             return ;
         }else{
              System.out.println("Select property");
              System.out.println("Select option");
         System.out.println("Collect 20 tweets from Greece: 1");
         System.out.println("Save these tweets: 2");
         System.out.println("Exit : 0");
         }
        */
        SaveTweets st = new SaveTweets();
             st.saveTweets(db,twitter);
    }
    
}
