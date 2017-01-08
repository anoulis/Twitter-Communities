/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twittermongodbapp;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

/**
 *
 * @author aristeidis
 */
public class CollectTweets {
    
    static public void collectTweets(DB db,ConfigurationBuilder cf1,twitter4j.Twitter twitter){
        TwitterStream twitterStream = new TwitterStreamFactory(cf1.build())
         .getInstance();
    
         DBCollection collection = db.getCollection("collection");
         //BasicDBObject document0 = new BasicDBObject();
       // collection.remove(document0);
        StatusListener listener = new StatusListener() {

        @Override
        public void onStatus(Status status) {
            String json = DataObjectFactory.getRawJSON(status);
            DBObject doc = (DBObject)JSON.parse(json);
            try {
                collection.insert(doc);
            } catch (Exception e) {
                System.out.println("MongoDB Connection Error : " + e.getMessage());
            }
            
            System.out.println("Collected tweets :" + collection.getCount());
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
       
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice arg0) {
                  // TODO Auto-generated method stub

        }

        @Override
        public void onScrubGeo(long arg0, long arg1) {

        }

        @Override
        public void onStallWarning(StallWarning arg0) {
            // TODO Auto-generated method stub
            System.out.println(arg0);
        }

        @Override
        public void onTrackLimitationNotice(int arg0) {
            // TODO Auto-generated method stub
            System.out.println(arg0);
        }
        };
        getTopTrends(twitter,twitterStream,listener);
    }
    
    static public void getTopTrends(twitter4j.Twitter twitter,TwitterStream twitterStream, StatusListener listener){
        FilterQuery filterQuery = new FilterQuery();
        
        Timer t = new Timer( );
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Trends trends;
                try {
                    trends = twitter.getPlaceTrends(23424833);//trends = twitter.getPlaceTrends(1);(23424833)
                    String[] keywords = new String[trends.getTrends().length];
                    System.out.println("Top Trends in Greece");
                    for (int i = 0; i < trends.getTrends().length; i++) {
                        keywords[i]=trends.getTrends()[i].getName();
                        System.out.println(keywords[i]);
                    }
                    filterQuery.track(keywords);
                    filterQuery.language(new String[]{"el"});
                    twitterStream.addListener(listener);
                    twitterStream.filter(filterQuery);
                } catch (TwitterException ex) {
                    Logger.getLogger(TwitterMongoDBApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 1000,360000);
        
    }
    
}
