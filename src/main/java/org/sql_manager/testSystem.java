package org.sql_manager;

import javax.persistence.*;
import java.util.List;


public class testSystem {
    private static final EntityManagerFactory entityManagerFactory = Persistence
            .createEntityManagerFactory("JavaFX_socket_mini_chat");
    public static void main(String[] args){

        addUser("hibernate","test");
        getAllUsers();
        entityManagerFactory.close();
    }

    public static void addUser(String login, String passwd){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            user user1 = new user();
            user1.setLogin(login);
            user1.setPasswd(passwd);
            entityManager.persist(user1);
            entityTransaction.commit();
        }catch (Exception e){
            if(e != null) {
            entityTransaction.rollback();
            }else {
                e.printStackTrace();
            }
        }finally{
            entityManager.close();
        }
    }

    public static void getuser(String login){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String query = "SELECT c FROM TEST c WHERE c.login = :userLogin";
        TypedQuery<user> typedQuery =  entityManager.createQuery(query,user.class);
        typedQuery.setParameter("userLogin",login);
        user userFromDB = null;
        try {
            userFromDB = typedQuery.getSingleResult();
            System.out.println(userFromDB.getLogin());
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            entityManager.close();
        }
    }

    public static void getAllUsers(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String query = "SELECT c FROM TEST c WHERE c.login IS NOT NULL";
        TypedQuery<user> typedQuery = entityManager.createQuery(query,user.class);
        List<user> users;
        try {
            users = typedQuery.getResultList();
            users.forEach(user -> System.out.println(user.getLogin()));
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            entityManager.close();
        }
    }
}
