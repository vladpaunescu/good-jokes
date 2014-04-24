/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.pub.cti.jokes.analyzer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import ro.pub.cti.jokes.db.Category;
import ro.pub.cti.jokes.db.DbConnection;
import ro.pub.cti.jokes.db.Joke;
import ro.pub.cti.jokes.db.JokesHibernateUtil;
import ro.pub.cti.jokes.db.Subcategory;

/**
 *
 * @author Vlad
 */
public class Statistics {

    List<Category> categories;
    DbConnection connection;
    int subcategoriesCount;
    int jokesCount;
    HashMap<Subcategory, Integer> jokesMap;
    TreeMap<Subcategory, Integer> topJokesCount;

    public Statistics(DbConnection connection) {
        categories = connection.listCategorieses();
        this.connection = connection;
        subcategoriesCount = 0;
        jokesCount = 0;
    }

    public void countJokes() {
        jokesMap = new HashMap<>();
        ValueComparator vc = new ValueComparator(jokesMap);
        topJokesCount = new TreeMap<>(vc);
        for (Category category : categories) {
            System.out.println(category.getName());
            Set<Subcategory> subcategories = category.getSubcategories();
            subcategoriesCount += subcategories.size();
            for (Subcategory subcat : subcategories) {
                List<Joke> jokes = connection.getJokes(subcat);
                jokesMap.put(subcat, jokes.size());
                jokesCount += jokes.size();
            }
        }
        System.out.println("Subcategories count " + subcategoriesCount);
        System.out.println("Jokes count " + jokesCount);
        System.out.println("Categories with most jokes");

        topJokesCount.putAll(jokesMap);
        for (Subcategory topCategory : topJokesCount.keySet()) {
            System.out.println(topCategory.getName() + " "
                    + jokesMap.get(topCategory));
        }
    }

    public static void main(String[] args) {
        Statistics stats = new Statistics(new DbConnection());
        stats.countJokes();
        
        JokesHibernateUtil.getSessionFactory().close();
    }
    
}

class ValueComparator implements Comparator<Subcategory> {

    Map<Subcategory, Integer> base;

    public ValueComparator(Map<Subcategory, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(Subcategory a, Subcategory b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
