/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twittermongodbapp;

import com.mongodb.BasicDBList;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import java.util.ArrayList;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 *
 * @author aristeidis
 */
public class SaveTweets {
    static public void saveConnection(String date,String name,BasicDBObject doc,int flag,int count,DBCollection ConnectionCollection){
      BasicDBObject connectionDocument = new BasicDBObject("Connection",count);
                        connectionDocument.append("user", name);
                        connectionDocument.append("timestamp", date);
              if(flag == 1) connectionDocument.append("hashtag", doc);
              else if(flag == 2) connectionDocument.append("URL",doc);
                  else if(flag == 3) connectionDocument.append("mentioned_user", doc);
                      else if(flag == 4) connectionDocument.append("retweet_tweet", doc);
            ConnectionCollection.insert(connectionDocument);  
            System.out.println("C" + connectionDocument);
            
        
    }
    static public void saveTweets(DB db,twitter4j.Twitter twitter) throws TwitterException{
        DBCollection collection = db.getCollection("collection");
        DBCollection savedCollection = db.getCollection("savedCollection");
        DBCollection URLSCollection = db.getCollection("URLS");
        DBCollection HashtagsCollection = db.getCollection("HashtagsCollection");
        DBCollection MentionedUsersCollection = db.getCollection("MentionedUsersCollection");
        DBCollection TweetsCollection = db.getCollection("TweetsCollection");
        DBCollection ConnectionCollection = db.getCollection(" ConnectionCollection");
        BasicDBObject document0 = new BasicDBObject();
        savedCollection.remove(document0);
        HashtagsCollection.remove(document0);
        MentionedUsersCollection.remove(document0);
        TweetsCollection.remove(document0);
        ConnectionCollection.remove(document0);
        Status savedTweet ;
        
        //Read Tweets from Database
        System.out.println("Saved tweets: ");
         DBCursor cursor = collection.find();
          BasicDBObject hashtagsDocument = new BasicDBObject("Hashtags",1);
          BasicDBObject usersmainDocument = new BasicDBObject("Users",1);
          List<BasicDBObject> allHashtags = new ArrayList<>();
          List<BasicDBObject> allUsers = new ArrayList<>();
          int count=1;
         while (cursor.hasNext()) {
             
             
             DBObject obj = cursor.next(); 
             savedTweet = TwitterObjectFactory.createStatus(obj.toString());
             
                        String name = savedTweet.getUser().getScreenName();  
                        String date = savedTweet.getCreatedAt().toString();
          System.out.println("@" + savedTweet.getUser().getScreenName() + "---------- "+ savedTweet.getText());
          
                      HashtagEntity[] hashtagsEntities =savedTweet.getHashtagEntities();
                      for (int i = 0; i < hashtagsEntities.length; i++) {
                       BasicDBObject document1 = new BasicDBObject("hashtag",hashtagsEntities[i].getText());
                       
                         DBCursor cursor1 = HashtagsCollection.find(document1);
                         if(!cursor1.hasNext()) {
                             //BasicDBObject d =(BasicDBObject) cursor1.next();
                             HashtagsCollection.insert(document1);
                            // System.out.println(document1);
                            // System.out.println( "Not exist");
                         } 
                         else{
                            //System.out.println( "exist");
                         }
                         saveConnection(date,name,document1,1, count,ConnectionCollection);
                         count++;
                      }
                      
                      URLEntity[] urlEntities = savedTweet.getURLEntities();
                      if(savedTweet.getURLEntities().length>0){
                         for (int i = 0; i < savedTweet.getURLEntities().length; i++) {
                           if(urlEntities[i].getStart()<urlEntities[i].getEnd()){
                           BasicDBObject document1 = new BasicDBObject("url",urlEntities[i].getURL());
                           DBCursor cursor1 = HashtagsCollection.find(document1);
                           if(!cursor1.hasNext()) {
                             String completeURL = urlEntities[i].getExpandedURL();
                             document1.append("completeURL", completeURL);
                             URLSCollection.insert(document1);
                             //System.out.println(document1);
                            // System.out.println( "Not exist");
                           }
                           else{
                               // System.out.println( "exist");
                           }
                           saveConnection(date,name,document1,2, count,ConnectionCollection);
                           count++;
                           }
                         }
                      }
                      
                      UserMentionEntity[] mentionEntities = savedTweet.getUserMentionEntities();
                      for (int i = 0; i < mentionEntities.length; i++) {
                         BasicDBObject document2 = new BasicDBObject("user",mentionEntities[i].getText());
                           DBCursor cursor2 = MentionedUsersCollection.find(document2);
                           if(!cursor2.hasNext()) {
                             URLSCollection.insert(document2);
                             //System.out.println(document2);
                            // System.out.println( "Not exist");
                           }
                           else{
                                //System.out.println(document2);
                                //System.out.println( "exist");
                           }
                           saveConnection(date,name,document2,3, count,ConnectionCollection);
                           count++;
                         }
                         
                          String tweetText;
                          if(savedTweet.isRetweet()){
                              tweetText = savedTweet.getRetweetedStatus().getText();
                          }else{
                              tweetText = savedTweet.getText();
                          }
                          
                          BasicDBObject document2 = new BasicDBObject("tweet",tweetText);
                           DBCursor cursor2 = TweetsCollection.find(document2);
                           if(!cursor2.hasNext()) {
                               TweetsCollection.insert(document2);
                           }
                          if(savedTweet.isRetweet())
                          saveConnection(date,name,document2,4, count,ConnectionCollection);
                          count++;
                           
                           /*
                        BasicDBObject connectionDocument = new BasicDBObject("Connection",count)
                        .append("user", name)
                        .append("timestamp", date)
                        .append("tweet", document2)
                                ;
                        ConnectionCollection.insert(connectionDocument);
                        count++;*/
         }   
            
                    /* 
                     String name = savedTweet.getUser().getScreenName();
                     if(!allUsers.contains(new BasicDBObject("user",name)))
                        allUsers.add(new BasicDBObject("user",name));
                     
            
                     String tweetText = savedTweet.getText();
                     
                      HashtagEntity[] hashtagsEntities =savedTweet.getHashtagEntities();
                      BasicDBList hashtags = new BasicDBList();
                      List<BasicDBObject> milestones = new ArrayList<>();
                      for (int i = 0; i < hashtagsEntities.length; i++) {
                        hashtags.add(new BasicDBObject("hashtag",hashtagsEntities[i].getText()));
                        milestones.add(new BasicDBObject(hashtagsEntities[i].getText().toString(),hashtagsEntities[i].getText()));
                        if(!allHashtags.contains(new BasicDBObject("hashtag",hashtagsEntities[i].getText())))
                        allHashtags.add(new BasicDBObject("hashtag",hashtagsEntities[i].getText()));
                       // System.out.println("Hashtag: " + hashtagsEntities[i].getText());
                       
                      }
                      
                      int flag =1;
                      BasicDBList mentions = new BasicDBList();
                     UserMentionEntity[] mentionEntities = savedTweet.getUserMentionEntities();
                     if(savedTweet.isRetweet()){
                         for (int i = 1; i < mentionEntities.length; i++) {
                        mentions.add(new BasicDBObject("mention",mentionEntities[i].getText()));
                      //  System.out.println("Mention: " + mentionEntities[i].getText());
                       
                      }
                     }else{
                         for (int i = 0; i < mentionEntities.length; i++) {
                        mentions.add(new BasicDBObject("mention",mentionEntities[i].getText()));
                      //  System.out.println("Mention: " + mentionEntities[i].getText());
                       
                      }
                     }
                     BasicDBList shortURLS = new BasicDBList();
                      BasicDBList wholeURLS = new BasicDBList();
                      URLEntity[] urlEntities = savedTweet.getURLEntities();
                       for (int i = 0; i < savedTweet.getURLEntities().length; i++) {
                        shortURLS.add(new BasicDBObject("shortURL",urlEntities[i].getURL()));
                          wholeURLS.add(new BasicDBObject("wholeURL",urlEntities[i].getDisplayURL()));
                       //   System.out.println("shortURL: " + urlEntities[i].getURL());
                       //   System.out.println("wholeURL: " + urlEntities[i].getDisplayURL());
                       }
                     
                      
                      String date = savedTweet.getCreatedAt().toString();
                      //System.out.println("Date: " +date);
                    
                     Boolean rt=false;  
                    if(savedTweet.isRetweet()){ 
                        rt=true;
                    }
                    BasicDBObject document1 = new BasicDBObject("name",name);
                        DBCursor cursor1 = savedCollection.find(document1);
while(cursor1.hasNext()) {
   
    BasicDBObject document =(BasicDBObject) cursor1.next();
     System.out.println(document);
      System.out.println( "exist");
   
}    
                     BasicDBObject document = new BasicDBObject("name",name)
                     .append("tweetText",tweetText)
                     .append("Hashtags",hashtags) 
                     .append("ml",milestones) 
                     .append("Mentions",mentions)
                     .append("shortUrls",shortURLS)
                     .append("wholeUrls",wholeURLS)
                     .append("Date",date)
                     .append("IsReTweet",rt)
                     .append("tweet", obj.toString())
                      ;
                     savedCollection.insert(document);
                     
                     //System.out.println(savedCollection.find(document));
                 
                  //   System.out.println("paok2");
         }
         // savedCollection.insert(allHashtags);
         // savedCollection.insert(allUsers);
         // System.out.println(allHashtags.toString());
        for(BasicDBObject h:allHashtags) {
           // System.out.println(h.get("hashtag"));
            // prints [Tommy, tiger]
        }
        for(BasicDBObject u:allUsers) {
           // System.out.println(u.get("user"));
            // prints [Tommy, tiger]
        }
       //  System.out.println("Collected tweets :" + savedCollection.getCount()); 
*/
    }
    
}
