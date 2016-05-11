import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class CategoryTest {



  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Category_initializesCorrectly_true() {
    Category testCategory = new Category("Name 1");
    assertTrue(testCategory instanceof Category);
  }

  @Test public void getCategoryName_returnsCategoryName_String() {
    Category testCategory = new Category("Name 1");
    assertEquals("Name 1", testCategory.getCategoryName());
  }

  @Test
  public void all_initializesAsEmptyList() {
    assertEquals(Category.all().size(), 0);
  }

  @Test
  public void save_assignsIdToInstance_int() {
    Category testCategory = new Category("Name 1");
    testCategory.save();
    Category savedCategory = Category.all().get(0);
    assertEquals(testCategory.getId(), savedCategory.getId());
  }

  @Test
  public void equals_returnsTrueIfBothInstancesAreTheSame_true() {
    Category testCategory = new Category("Name 1");
    Category testCategory2 = new Category("Name 1");
    assertTrue(testCategory2.equals(testCategory));
  }

  @Test
  public void save_savesIntanceOfObjectToDatabase_true() {
    Category testCategory = new Category("Name 1");
    testCategory.save();
    Category savedCategory = Category.all().get(0);
    assertTrue(testCategory.equals(savedCategory));
  }

  @Test
  public void findById_findsCategoryInstanceById_Category() {
    Category testCategory = new Category("Name 1");
    testCategory.save();
    Category foundCategory = Category.findById(testCategory.getId());
    assertTrue(foundCategory.equals(testCategory));
  }

  @Test
  public void delete_deletesCategoryFromCategorysTable() {
    Category testCategory = new Category("Name 1");
    testCategory.save();
    testCategory.delete();
    assertEquals(0, Category.all().size());
  }

  @Test
  public void update_updatesCategoryProperties_String() {
    Category testCategory = new Category("Name 1");
    testCategory.save();
    testCategory.update("Name 2");
    Category updatedCategory = Category.findById(testCategory.getId());
    assertEquals("Name 2", updatedCategory.getCategoryName());
  }
}
