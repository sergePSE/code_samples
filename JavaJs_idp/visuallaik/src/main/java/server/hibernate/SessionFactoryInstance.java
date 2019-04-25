package server.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;

public class SessionFactoryInstance {
    public static Session getSession(HttpServletRequest request){

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                //(SessionFactory) request.getServletContext().getAttribute("SessionFactory");

        Session session = sessionFactory.openSession();
        return session;
    }
}
