/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twittermongodbapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author aristeidis
 */
public class JaccardSimilarity {

    public final float findSimilarity(List<String> l1, List<String> l2) {
        int inter = 0;
        Set<String> union = new HashSet<String>();
        union.addAll(l1);
        union.addAll(l2);
        
        if (l1.isEmpty() || l2.isEmpty()) {
            return 0;
        }
        if (l1.isEmpty() && l2.isEmpty()) {
            return 1;
        }
        for (String key : union) {
            if (l1.contains(key) && l2.contains(key)) {
                inter++;
            }
        }
        if (inter != 0) {
            return (float) (inter / union.size());
        } else {
            return 0;
        }
    }

}
