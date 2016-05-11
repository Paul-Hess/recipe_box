import static org.fluentlenium.core.filter.FilterConstructor.*;
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;


public class AppTest extends FluentTest{

  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
   return webDriver;
 }

 @ClassRule
 public static ServerRule server = new ServerRule();

 @Rule
public DatabaseRule database = new DatabaseRule();

 @Test
 public void rootTest() {
   goTo("http://localhost:4567/");
   assertThat(pageSource()).contains("Recipes");
 }

 @Test
 public void addRecipe() {
   goTo("http://localhost:4567/");
   click("a", withText("Add a Recipe"));
   fill("#recipe_name").with("name 1");
   fill("#instructions").with("instruction example");
   fillSelect("#rating").withText("4");
   submit("#create-recipe");
   assertThat(pageSource()).contains("name 1");
 }

 @Test
 public void editRecipe() {
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   goTo("http://localhost:4567/recipe/" + newRecipe.getId());
   click("a", withText("Edit or Delete This Recipe"));
   assertThat(pageSource()).contains("Edit Recipe Name");
 }

 @Test
 public void isEditedRecipe() {
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   Category testCategory = new Category("category name1");
   testCategory.save();
   goTo("http://localhost:4567/recipe/" + newRecipe.getId());
   click("a", withText("Edit or Delete This Recipe"));
   fill("#recipe_name").with("new name");
   fill("#instructions").with("new instructions");
   fillSelect("#category").withIndex(0);
   fillSelect("#rating").withText("3");
   submit("#edit-recipe");
   assertThat(pageSource()).contains("new name");
   assertThat(pageSource()).contains("category name1");
 }

 @Test
 public void deleteRecipe() {
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   goTo("http://localhost:4567/recipe/" + newRecipe.getId() +"/edit");
   submit("#delete-recipe");
   assertThat(pageSource()).doesNotContain("Name1");
 }

 @Test
 public void removeCategoryFromRecipe() {
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   Category testCategory = new Category("category 1");
   testCategory.save();
   newRecipe.addCategory(testCategory);
   goTo("http://localhost:4567/recipe/" + newRecipe.getId() +"/edit");
   fillSelect("#remove-category").withText("category 1");
   submit("#category-remove");
   assertThat(pageSource()).doesNotContain("category 1");
 }

}
