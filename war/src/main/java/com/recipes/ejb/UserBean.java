package com.recipes.ejb;

import com.recipes.entities.User;
import lombok.Data;
import org.jasypt.util.password.BasicPasswordEncryptor;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Data
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable {

    private static final BasicPasswordEncryptor ENCRYPTOR = new BasicPasswordEncryptor();

    @EJB
    UserFacade userFacade;

    private User user;
    private String inputPassword;
    private boolean userLogged;

    @PostConstruct
    public void init() {
        user = new User();
        userLogged = false;
    }

    public String doLogin() {
        return Optional.ofNullable(userFacade.findByUsername(user.getName()))
              .filter(dbUser -> correctPassword(dbUser.getHashedPassword()))
              .map(correctUser -> {
                  user = correctUser;
                  userLogged = true;
                  return "recipes";
              })
              .orElseGet(() -> {
                  //TODO: Do ajax stuff
                  return "login";
              });
    }

    private boolean correctPassword(String hashedPassword) {
        return ENCRYPTOR.checkPassword(inputPassword, hashedPassword);
    }

    public String doRegister() {
        return Optional.ofNullable(userFacade.findByUsername(user.getName()))
              .map(user -> {
                  //TODO: Ajax stuff
                  return "register";
              }).orElseGet(() -> {
                  user.setHashedPassword(ENCRYPTOR.encryptPassword(inputPassword));
                  userFacade.create(user);
                  return "listRecipes";
              });
    }

    public void checkLogged() throws IOException {
        if (!userLogged) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/login.xhtml");
        }
    }
}
