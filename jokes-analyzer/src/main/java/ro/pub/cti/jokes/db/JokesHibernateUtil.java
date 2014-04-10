package ro.pub.cti.jokes.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author vlad.paunescu
 */
public class JokesHibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            
            final ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

            configuration.setSessionFactoryObserver(
                    new SessionFactoryObserver() {
                        @Override
                        public void sessionFactoryCreated(SessionFactory factory) {
                        }

                        @Override
                        public void sessionFactoryClosed(SessionFactory factory) {
                            ((StandardServiceRegistryImpl) sr).destroy();
                        }
                    }
            );
            
            sessionFactory = configuration.buildSessionFactory(sr);
            Session session = sessionFactory.openSession();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
