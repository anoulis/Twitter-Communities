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

    static public void saveConnection(String date, String name, BasicDBObject doc, int flag, int count, DBCollection ConnectionCollection) {
        BasicDBObject connectionDocument = new BasicDBObject("Connection", count);
        connectionDocument.append("user", name);
        connectionDocument.append("timestamp", date);
        switch (flag) {
            case 1:
                connectionDocument.append("hashtag", doc);
                break;
            case 2:
                connectionDocument.append("URL", doc);
                break;
            case 3:
                connectionDocument.append("mentioned_user", doc);
                break;
            case 4:
                connectionDocument.append("retweet_tweet", doc);
                break;
            default:
                break;
        }
        ConnectionCollection.insert(connectionDocument);
    }

    static public void saveTweets(DB db, twitter4j.Twitter twitter) throws TwitterException {
        DBCollection collection = db.getCollection("collection");
        DBCollection savedCollection = db.getCollection("savedCollection");
        DBCollection ConnectionCollection = db.getCollection(" ConnectionCollection");
        BasicDBObject document0 = new BasicDBObject();
        savedCollection.remove(document0);
        ConnectionCollection.remove(document0);
        Status savedTweet;

        //Read Tweets from Database
        System.out.println("Saved tweets: ");
        DBCursor cursor = collection.find();
        List<String> allUsers = new ArrayList<>();
        int count = 1;

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            savedTweet = TwitterObjectFactory.createStatus(obj.toString());

            String name = savedTweet.getUser().getScreenName();
            String date = savedTweet.getCreatedAt().toString();
            boolean exist = false;

            BasicDBObject document = new BasicDBObject("User", name);
            BasicDBObject searchDocument = new BasicDBObject("User", name);
            DBObject theObj = null;

            if (!allUsers.contains(name)) {
                allUsers.add(name);
            } else {
                exist = true;
                DBCursor cursor2 = savedCollection.find(searchDocument);
                if (cursor2.hasNext()) {
                    theObj = cursor2.next();
                }
            }

            BasicDBList allHashtags = new BasicDBList();
            BasicDBList allURLS = new BasicDBList();
            BasicDBList allmentionedUsers = new BasicDBList();
            BasicDBList allTweets = new BasicDBList();

            if (exist) {
                allHashtags = (BasicDBList) theObj.get("Hashtags");
            }
            HashtagEntity[] hashtagsEntities = savedTweet.getHashtagEntities();
            for (HashtagEntity hashtagsEntitie : hashtagsEntities) {
                if (!allHashtags.contains(new BasicDBObject("hashtag", hashtagsEntitie.getText()))) {
                    allHashtags.add(new BasicDBObject("hashtag", hashtagsEntitie.getText()));
                }
                saveConnection(date, name, new BasicDBObject("hashtag", hashtagsEntitie.getText()), 1, count, ConnectionCollection);
                count++;
            }
            document.append("Hashtags", allHashtags);

            if (exist) {
                allURLS = (BasicDBList) theObj.get("Urls");
            }
            URLEntity[] urlEntities = savedTweet.getURLEntities();
            if (savedTweet.getURLEntities().length > 0) {
                for (int i = 0; i < savedTweet.getURLEntities().length; i++) {
                    if (urlEntities[i].getStart() < urlEntities[i].getEnd()) {
                        BasicDBObject document1 = new BasicDBObject("url", urlEntities[i].getURL());
                        String completeURL = urlEntities[i].getExpandedURL();
                        document1.append("completeURL", completeURL);
                        if (!allURLS.contains(document1)) {
                            allURLS.add(document1);
                        }
                        saveConnection(date, name, document1, 2, count, ConnectionCollection);
                        count++;
                    }
                }
            }
            document.append("Urls", allURLS);

            if (exist) {
                allmentionedUsers = (BasicDBList) theObj.get("Mentions");
            }
            UserMentionEntity[] mentionEntities = savedTweet.getUserMentionEntities();
            for (UserMentionEntity mentionEntitie : mentionEntities) {
                BasicDBObject document2 = new BasicDBObject("mentioned_user", mentionEntitie.getText());
                if (!allmentionedUsers.contains(document2)) {
                    allmentionedUsers.add(document2);
                }
                saveConnection(date, name, document2, 3, count, ConnectionCollection);
                count++;
            }
            document.append("Mentions", allmentionedUsers);

            if (exist) {
                allTweets = (BasicDBList) theObj.get("Tweets");
            }
            String tweetText = " ";
            if (savedTweet.isRetweet()) {
                tweetText = savedTweet.getRetweetedStatus().getText();
            } else {
                tweetText = savedTweet.getText();
            }
            BasicDBObject document2 = new BasicDBObject("Tweet", tweetText);
            if (!allTweets.contains(document2)) {
                allTweets.add(document2);
            }
            saveConnection(date, name, document2, 4, count, ConnectionCollection);
            count++;
            document.append("Tweets", allTweets);

            if (exist) {
                savedCollection.remove(searchDocument);
            }
            savedCollection.insert(document);
        }
        /*
        Read every Element Example
        DBCursor cursor2 = savedCollection.find(searchDocument);
        if (cursor2.hasNext()) {
            theObj = cursor2.next();
        //String l =  ( String) cursor2.one().get("exist").toString();
        }

        BasicDBList list = new BasicDBList();
        list = (BasicDBList) theObj.get("Hashtags");
        BasicDBList l2 = new BasicDBList();
        for (int i = 0; i < list.size(); i++) {
            BasicDBObject bj = (BasicDBObject) list.get(i);
            System.out.println(bj.getString("hashtag"));
        }
         */
    }
}
