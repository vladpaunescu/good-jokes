package ro.pub.cti.jokes.analyzer.db;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author vlad.paunescu
 */
public class DbTests {

    public List<Categories> listCategorieses() {
        Session session = JokesHibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Categories> result = session.createQuery("from Categories").list();
        session.getTransaction().commit();
        return result;
    }
    
    public static void main(String[] args){
        DbTests dbTests = new DbTests();
        List<Categories> categories = dbTests.listCategorieses();
        for (Categories category : categories){
            System.out.println(category.getName());
        }
        
    }
}

