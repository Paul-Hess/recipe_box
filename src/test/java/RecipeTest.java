import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class RecipeTest {



  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Recipe_initializesCorrectly_true() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    assertTrue(testRecipe instanceof Recipe);
  }

  @Test public void getRecipeName_returnsRecipeName_String() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    assertEquals("Name 1", testRecipe.getRecipeName());
  }

  @Test
  public void getInstructions_returnsInstructions_String() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    assertEquals(testRecipe.getInstructions(), "instructions");
  }

  @Test
  public void getRating_returnsRating_int() {
    Recipe testRecipe = new Recipe("Name 1", "instrucitons", 5);
    assertEquals(5, testRecipe.getRating());
  }

  @Test
  public void all_initializesAsEmptyList() {
    assertEquals(Recipe.all().size(), 0);
  }

  @Test
  public void save_assignsIdToInstance_int() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    testRecipe.save();
    Recipe savedRecipe = Recipe.all().get(0);
    assertEquals(testRecipe.getId(), savedRecipe.getId());
  }

  @Test
  public void equals_returnsTrueIfBothINstancesAreTheSame_true() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    Recipe testRecipe2 = new Recipe("Name 1", "instructions", 5);
    assertTrue(testRecipe2.equals(testRecipe));
  }

  @Test
  public void save_savesIntanceOfObjectToDatabase_true() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    testRecipe.save();
    Recipe savedRecipe = Recipe.all().get(0);
    assertTrue(testRecipe.equals(savedRecipe));
  }

  @Test
  public void findById_findsRecipeInstanceById_Recipe() {
    Recipe testRecipe = new Recipe("Name 1", "instructions", 5);
    testRecipe.save();
    Recipe foundRecipe = Recipe.findById(testRecipe.getId());
    assertTrue(foundRecipe.equals(testRecipe));
  }

  @Test
  public void delete_deletesRecipeFromRecipesTable() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    testRecipe.delete();
    assertEquals(0, Recipe.all().size());
  }

  @Test
  public void update_updatesRecipeProperties_String() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    testRecipe.update("Name 2", "Instructions", 3);
    Recipe updatedRecipe = Recipe.findById(testRecipe.getId());
    assertEquals("Name 2", updatedRecipe.getRecipeName());
  }

  @Test
  public void addCategory_joinsRecipeAndCategory_true() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    Category testCategory = new Category("Category 1");
    testCategory.save();
    testRecipe.addCategory(testCategory);
    Category savedCategory = testRecipe.getCategories().get(0);
    assertTrue(testCategory.equals(savedCategory));
  }

  @Test
  public void addIngredient_joinsRecipeAndIngredient_true() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    Ingredient testIngredient = new Ingredient("Ingredient 1");
    testIngredient.save();
    testRecipe.addIngredient(testIngredient);
    Ingredient savedIngredient = testRecipe.getIngredients().get(0);
    assertTrue(testIngredient.equals(savedIngredient));
  }

  @Test
  public void listAvailableCategories_findsAllUnassignedCategories_List() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    Category testCategory = new Category("Category 1");
    testCategory.save();
    testRecipe.addCategory(testCategory);
    Category testCategory2 = new Category("Category 2");
    testCategory2.save();
    Category unAssignedCategory = testRecipe.listAvailableCategories().get(0);
    assertTrue(testCategory2.equals(unAssignedCategory));
  }

  @Test
  public void listAvailableCategories_findsAllUnassignedCategories_List2() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    Category testCategory = new Category("Category 1");
    testCategory.save();
    testRecipe.addCategory(testCategory);
    Category testCategory2 = new Category("Category 2");
    testCategory2.save();
    Category unAssignedCategory = testRecipe.listAvailableCategories().get(0);
    assertEquals("Category 2", unAssignedCategory.getCategoryName());
  }

  @Test
  public void removeCategory_removesCategoryAssociationFromJoinTable() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    Category testCategory = new Category("Category 1");
    testCategory.save();
    testRecipe.addCategory(testCategory);

    testRecipe.removeCategory(testCategory.getId());

    assertEquals(0, testRecipe.getCategories().size());
  }

  @Test
  public void removeIngredient_removesIngredientAssociationFromJoinTable() {
    Recipe testRecipe = new Recipe("Name 1", "Instructions", 5);
    testRecipe.save();
    Ingredient testIngredient = new Ingredient("Ingredient 1");
    testIngredient.save();
    testRecipe.addIngredient(testIngredient);

    testRecipe.removeIngredient(testIngredient.getId());

    assertEquals(0, testRecipe.getIngredients().size());
  }
}
