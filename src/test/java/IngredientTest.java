import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class IngredientTest {



  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Ingredient_initializesCorrectly_true() {
    Ingredient testIngredient = new Ingredient("Name 1");
    assertTrue(testIngredient instanceof Ingredient);
  }

  @Test public void getIngredientName_returnsIngredientName_String() {
    Ingredient testIngredient = new Ingredient("Name 1");
    assertEquals("Name 1", testIngredient.getIngredientName());
  }

  @Test
  public void all_initializesAsEmptyList() {
    assertEquals(Ingredient.all().size(), 0);
  }

  @Test
  public void save_assignsIdToInstance_int() {
    Ingredient testIngredient = new Ingredient("Name 1");
    testIngredient.save();
    Ingredient savedIngredient = Ingredient.all().get(0);
    assertEquals(testIngredient.getId(), savedIngredient.getId());
  }

  @Test
  public void equals_returnsTrueIfBothInstancesAreTheSame_true() {
    Ingredient testIngredient = new Ingredient("Name 1");
    Ingredient testIngredient2 = new Ingredient("Name 1");
    assertTrue(testIngredient2.equals(testIngredient));
  }

  @Test
  public void save_savesIntanceOfObjectToDatabase_true() {
    Ingredient testIngredient = new Ingredient("Name 1");
    testIngredient.save();
    Ingredient savedIngredient = Ingredient.all().get(0);
    assertTrue(testIngredient.equals(savedIngredient));
  }

  @Test
  public void findById_findsIngredientInstanceById_Ingredient() {
    Ingredient testIngredient = new Ingredient("Name 1");
    testIngredient.save();
    Ingredient foundIngredient = Ingredient.findById(testIngredient.getId());
    assertTrue(foundIngredient.equals(testIngredient));
  }

  @Test
  public void delete_deletesIngredientsFromIngredientsTable() {
    Ingredient testIngredient = new Ingredient("Name 1");
    testIngredient.save();
    testIngredient.delete();
    assertEquals(0, Ingredient.all().size());
  }

  @Test
  public void update_updatesIngredientProperties_String() {
    Ingredient testIngredient = new Ingredient("Name 1");
    testIngredient.save();
    testIngredient.update("Name 2");
    Ingredient updatedIngredient = Ingredient.findById(testIngredient.getId());
    assertEquals("Name 2", updatedIngredient.getIngredientName());
  }
}
