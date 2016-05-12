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
 public void addIngredientToRecipe() {
   Ingredient newIngredient = new Ingredient("Ingredient 1");
   newIngredient.save();
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   goTo("http://localhost:4567/recipe/" + newRecipe.getId() + "/edit");
   fillSelect("#add-ingredient").withIndex(0);
   submit("#submit-ingredient");
   goTo("http://localhost:4567/recipe/" + newRecipe.getId());
   assertThat(pageSource()).contains("Ingredient 1");
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

 @Test
 public void removeIngredientFromRecipe() {
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   Ingredient testIngredient = new Ingredient("ingredient 1");
   testIngredient.save();
   newRecipe.addIngredient(testIngredient);
   goTo("http://localhost:4567/recipe/" + newRecipe.getId() +"/edit");
   fillSelect("#remove-ingredient").withText("ingredient 1");
   submit("#ingredient-remove");
   assertThat(pageSource()).doesNotContain("ingredient 1");
 }

 @Test
 public void addCategory() {
   goTo("http://localhost:4567/");
   click("a", withText("Add a Category"));
   fill("#category-name").with("category one");
   submit("#create-category");
   assertThat(pageSource()).contains("category one");
 }

 @Test
 public void individualCategoryDetail() {
   Category testCategory = new Category("test category");
   testCategory.save();
   goTo("http://localhost:4567/category/" + testCategory.getId());
   assertThat(pageSource()).contains("test category");
 }

 @Test
 public void EditCategory() {
   Category testCategory = new Category("test category");
   testCategory.save();
   goTo("http://localhost:4567/category/" + testCategory.getId());
   click("a", withText("Edit or Delete This Category"));
   assertThat(pageSource()).contains("Edit: test category");
 }

 @Test
 public void EditsTheCategory() {
   Category testCategory = new Category("test category");
   testCategory.save();
   goTo("http://localhost:4567/category/" + testCategory.getId());
   click("a", withText("Edit or Delete This Category"));
   fill("#category-name").with("new name");
   submit("#edit-category");
   assertThat(pageSource()).contains("new name");
 }

 @Test
 public void addsRecipeToCategory() {
   Category testCategory = new Category("test category");
   testCategory.save();
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   goTo("http://localhost:4567/category/" + testCategory.getId());
   click("a", withText("Edit or Delete This Category"));
   fill("#category-name").with("new name");
   fillSelect("#recipe").withText("Name1");
   submit("#edit-category");
   assertThat(pageSource()).contains("Name1");
 }

 @Test
 public void removesRecipeAssociation() {
   Category testCategory = new Category("test category");
   testCategory.save();
   Recipe newRecipe = new Recipe("Name1", "Instructions", 5);
   newRecipe.save();
   newRecipe.addCategory(testCategory);
   goTo("http://localhost:4567/category/" + testCategory.getId() +"/edit");
   fillSelect("#remove-recipe").withIndex(0);
   submit("#recipe-remove");
   assertThat(pageSource()).doesNotContain("Name1");
 }

 @Test
 public void deletesTheSpecificCategory() {
  Category testCategory = new Category("test category");
  testCategory.save();
  goTo("http://localhost:4567/category/" + testCategory.getId() +"/edit");
  submit("#delete-category");
  assertThat(pageSource()).doesNotContain("test category");
 }

 @Test
 public void findsRecipesSearchedFor() {
   Recipe testRecipe = new Recipe("goulash", "cook it up", 5);
   testRecipe.save();
   goTo("http://localhost:4567/");
   fill("#search-recipe").with("goul");
   submit("#search-button");
   assertThat(pageSource()).contains("goulash");
 }

 @Test
 public void findsRecipesSearchedFromRecipesPage() {
   Recipe testRecipe = new Recipe("goulash", "cook it up", 5);
   testRecipe.save();
   goTo("http://localhost:4567/list-recipes");
   fill("#search-recipe").with("goul");
   submit("#search-button");
   assertThat(pageSource()).contains("goulash");
 }

 @Test
 public void createIngredient() {
   goTo("http://localhost:4567/");
   click("a", withText("Add an Ingredient"));
   fill("#ingredient-name").with("ingredient 1");
   submit("#create-ingredient");
   assertThat(pageSource()).contains("ingredient 1");
 }

 @Test
 public void getSpecificIngredientPage() {
   Ingredient testIngredient = new Ingredient("ingredient 1");
   testIngredient.save();
   Recipe testRecipe = new Recipe("goulash", "cook it up", 5);
   testRecipe.save();
   testRecipe.addIngredient(testIngredient);
   goTo("http://localhost:4567/ingredient/" + testIngredient.getId());
   assertThat(pageSource()).contains("ingredient 1");
   assertThat(pageSource()).contains("goulash");
 }

}
