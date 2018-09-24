package com.recipes.ejb;

import com.recipes.entities.Category;
import com.recipes.entities.Recipe;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Data
@Model
public class RecipesBean {

  @EJB
  UserFacade userFacade;
  @EJB
  RecipeFacade recipeFacade;
  @Inject
  private UserBean userBean;

  private List<Recipe> recipeList;
  private Recipe toShow;
  private Recipe toCreate;

  @PostConstruct
  public void init() {
    recipeList = recipeFacade.findByUserAndMax(userBean.getUser().getId(), 10);
    toCreate = new Recipe();
  }

  public String formatCategories(List<Category> categoryList) {
    return StringUtils.join(categoryList, " - ");
  }

  public String resume(String text){
    return text.substring(0,100) + "...";
  }

  public String showRecipe(Recipe recipeToShow) {
    toShow = recipeToShow;
    return "recipe";
  }

  public void createRecipe(){
    toCreate.setUserId(userBean.getUser());
    //Por algún motivo el default de SQL no funcionaba
    toCreate.setCreatedAt(new Date());
    recipeFacade.create(toCreate);
    init();
  }
}
