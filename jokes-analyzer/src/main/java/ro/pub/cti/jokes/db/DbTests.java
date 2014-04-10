package ro.pub.cti.jokes.db;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;

/**
 *
 * @author vlad.paunescu
 */
public class DbTests {

    public List<Category> listCategorieses() {
        Session session = JokesHibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Category> result = session.createQuery("from Category").list();
        session.getTransaction().commit();
        return result;
    }
    
    public List<Joke> getJokes(Subcategory subcat){
        Set<JokeCategory> jokeCategories = subcat.getJokeCategories();
        List <Joke> jokes = new LinkedList<Joke>();
        for(JokeCategory jokeCategory : jokeCategories){
           jokes.add(jokeCategory.getJoke());
        }
        return jokes;
    }
    
    public static void main(String[] args){
        DbTests dbTests = new DbTests();
        List<Category> categories = dbTests.listCategorieses();
        for (Category category : categories){
            System.out.println(category.getName());
            Set<Subcategory> subcategories = category.getSubcategories();
            for(Subcategory subcat: subcategories){
                System.out.println("\t" + subcat.getName());
                List<Joke> jokes = dbTests.getJokes(subcat);
                for(Joke joke : jokes){
                    System.out.println("\t\t" + joke.getTitle());
                }
            }
        }
        JokesHibernateUtil.getSessionFactory().close();
        
    }
}

