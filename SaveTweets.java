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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
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
        DBCollection collection = db.getCollection("collection2");
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

            // BasicDBList allHashtags = new BasicDBList();
            List<String> allHashtags = new ArrayList<>();
            List<String> allsortURLS = new ArrayList<>();
            List<String> allcompleteURLS = new ArrayList<>();
            //BasicDBList allURLS = new BasicDBList();
            List<String> allmentionedUsers = new ArrayList<>();
            List<String> allTweets = new ArrayList<>();

            if (exist) {
                allHashtags = (ArrayList) theObj.get("Hashtags");
            }
            HashtagEntity[] hashtagsEntities = savedTweet.getHashtagEntities();
            for (HashtagEntity hashtagsEntitie : hashtagsEntities) {
                if (!allHashtags.contains(hashtagsEntitie.getText())) {
                    allHashtags.add(hashtagsEntitie.getText());
                }
                saveConnection(date, name, new BasicDBObject("hashtag", hashtagsEntitie.getText()), 1, count, ConnectionCollection);
                count++;
            }
            document.append("Hashtags", allHashtags);

            if (exist) {
                allsortURLS = (ArrayList) theObj.get("sortUrls");
                allcompleteURLS = (ArrayList) theObj.get("completeUrls");
            }
            URLEntity[] urlEntities = savedTweet.getURLEntities();
            if (savedTweet.getURLEntities().length > 0) {
                for (int i = 0; i < savedTweet.getURLEntities().length; i++) {
                    if (urlEntities[i].getStart() < urlEntities[i].getEnd()) {

                        BasicDBObject document1 = new BasicDBObject("url", urlEntities[i].getURL());
                        String completeURL = urlEntities[i].getExpandedURL();
                        document1.append("completeURL", completeURL);
                        if (!allsortURLS.contains(document1)) {
                            allsortURLS.add(urlEntities[i].getURL());
                            allcompleteURLS.add(urlEntities[i].getExpandedURL());
                        }
                        saveConnection(date, name, document1, 2, count, ConnectionCollection);
                        count++;
                    }
                }
            }
            document.append("sortUrls", allsortURLS);
            document.append("completeUrls", allsortURLS);

            if (exist) {
                allmentionedUsers = (ArrayList) theObj.get("Mentions");
            }
            UserMentionEntity[] mentionEntities = savedTweet.getUserMentionEntities();
            for (UserMentionEntity mentionEntitie : mentionEntities) {
                BasicDBObject document2 = new BasicDBObject("mentioned_user", mentionEntitie.getText());
                if (!allmentionedUsers.contains(mentionEntitie.getText())) {
                    allmentionedUsers.add(mentionEntitie.getText());
                }
                saveConnection(date, name, document2, 3, count, ConnectionCollection);
                count++;
            }
            document.append("Mentions", allmentionedUsers);

            if (exist) {
                allTweets = (ArrayList) theObj.get("Tweets");
            }
            String tweetText = " ";
            if (savedTweet.isRetweet()) {
                tweetText = savedTweet.getRetweetedStatus().getText();
            } else {
                tweetText = savedTweet.getText();
            }
            BasicDBObject document2 = new BasicDBObject("Tweet", tweetText);
            if (!allTweets.contains(tweetText)) {
                allTweets.add(tweetText);
            }
            saveConnection(date, name, document2, 4, count, ConnectionCollection);
            count++;
            document.append("Tweets", allTweets);

            if (exist) {
                savedCollection.remove(searchDocument);
            }
            savedCollection.insert(document);
            // System.out.println(document);
        }

        JaccardSimilarity js = new JaccardSimilarity();
        List<Map<List<String>, List<Float>>> list = new ArrayList<Map< List<String>, List<Float>>>();//This is the final list you need
        Map<List<String>, List<Float>> map1 = new HashMap<List<String>, List<Float>>();//This is one instance of the   map you want to store in the above map
        List<String> usersNames = new ArrayList<String>();
        List<Float> usersResults = new ArrayList<Float>();

        BasicDBObject doc1;
        BasicDBObject doc2;
        DBCursor cursor1;
        DBCursor cursor2;
        DBObject obj1;
        DBObject obj2;
        List<String> tl1 = new ArrayList();
        List<String> tl2 = new ArrayList();

        float countSimilarity = 0;

        for (int i = 0; i < allUsers.size(); i++) {
            for (int j = i + 1; j < allUsers.size(); j++) {
                //System.out.println(i+" "+j);
                System.out.print(allUsers.get(i) + " " + allUsers.get(j) + " ");
                usersNames.add(allUsers.get(i));
                usersNames.add(allUsers.get(j));
                doc1 = new BasicDBObject("User", allUsers.get(i));
                doc2 = new BasicDBObject("User", allUsers.get(i));
                cursor1 = savedCollection.find(doc1);
                cursor2 = savedCollection.find(doc2);
                if (cursor1.hasNext() && cursor2.hasNext()) {
                    obj1 = cursor1.next();
                    obj2 = cursor2.next();
                    tl1 = (ArrayList) obj1.get("Hashtags");
                    tl2 = (ArrayList) obj2.get("Hashtags");
                    countSimilarity = countSimilarity + js.findSimilarity(tl1, tl2);
                    usersResults.add(js.findSimilarity(tl1, tl2));
                    System.out.print(js.findSimilarity(tl1, tl2) + " ");
                    tl1 = (ArrayList) obj1.get("Mentions");
                    tl2 = (ArrayList) obj2.get("Mentions");
                    countSimilarity = countSimilarity + js.findSimilarity(tl1, tl2);
                    usersResults.add(js.findSimilarity(tl1, tl2));
                    System.out.print(js.findSimilarity(tl1, tl2) + " ");
                    tl1 = (ArrayList) obj1.get("Tweets");
                    tl2 = (ArrayList) obj2.get("Tweets");
                    countSimilarity = countSimilarity + js.findSimilarity(tl1, tl2);
                    usersResults.add(js.findSimilarity(tl1, tl2));
                    System.out.print(js.findSimilarity(tl1, tl2) + " ");
                    tl1 = (ArrayList) obj1.get("sortUrls");
                    tl2 = (ArrayList) obj2.get("sortUrls");
                    countSimilarity = countSimilarity + js.findSimilarity(tl1, tl2);
                    usersResults.add(js.findSimilarity(tl1, tl2));
                    System.out.print(js.findSimilarity(tl1, tl2) + " ");
                    usersResults.add((float) (countSimilarity / 4));
                    System.out.print((float) (countSimilarity / 4) + " ");
                    System.out.println();
                    countSimilarity = 0;

                }

            }
            map1.put(usersNames, usersResults);
        }
        list.add(map1);

        //System.out.println(savedCollection.count());

        /*
       // Read every Element Example
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
        
        DBCursor cursor2 = savedCollection.find();
         List<String> list = new ArrayList();
               
        while (cursor2.hasNext()) {
            BasicDBObject document2 = (BasicDBObject) cursor2.next();
              String bj = (String) document2.get("User");
            System.out.println(bj);
             for (int i = 0; i < list.size(); i++) {
                   // String bj = (String) list.get(i);
                    // System.out.println(bj.getString("hashtag"));
                      System.out.println(bj);
                }
         */
    }
}
