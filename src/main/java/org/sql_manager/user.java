package org.sql_manager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "TEST")

public class user implements Serializable {
    private static final long serialVersionUID = 1L;


    @Column(name = "login",nullable = false)
    private String login;

    @Column(name = "passwd",nullable = false)
    private String passwd;

    public String getLogin(){
        return login;
    }

    public String getPasswd(){
        return passwd;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswd(String passwd){
        this.passwd = passwd;
    }
}
