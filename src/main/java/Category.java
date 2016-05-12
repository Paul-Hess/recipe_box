import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Category {

  private int id;
  private String category_name;

  @Override public boolean equals(Object otherCategory) {
    if(!(otherCategory instanceof Category)) {
      return false;
    } else {
      Category newCategory = (Category) otherCategory;
      return newCategory.getCategoryName().equals(this.getCategoryName()) &&
      newCategory.getId() == this.getId();
    }
  }

  public Category(String category_name) {
    this.category_name = category_name;
  }

  public String getCategoryName() {
    return this.category_name;
  }

  public List<Recipe> getRecipes() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT recipes.* FROM categories JOIN recipes_categories ON (categories.id = recipes_categories.category_id) JOIN recipes ON (recipes_categories.recipe_id = recipes.id) WHERE categories.id =:id;";
      return con.createQuery(sql)
        .addParameter("id", this.id)
        .executeAndFetch(Recipe.class);
    }
  }

  public List<Recipe> listAvailableRecipes() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT recipes.* FROM categories JOIN recipes_categories ON (categories.id = recipes_categories.category_id) JOIN recipes ON (recipes_categories.recipe_id != recipes.id) WHERE categories.id = :id;";
      return con.createQuery(sql)
        .addParameter("id", this.id)
        .executeAndFetch(Recipe.class);
    }
  }

  public int getId() {
    return this.id;
  }

  public static List<Category> all() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT id, category_name FROM categories;";
      return con.createQuery(sql)
      .executeAndFetch(Category.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories (category_name) VALUES (:category_name);";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("category_name", this.category_name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Category findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM categories WHERE id=:id;";
      return con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Category.class);
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM categories WHERE id=:id;";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void update(String new_category_name) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE categories SET category_name=:category_name WHERE id=:id;";
      con.createQuery(sql)
        .addParameter("category_name", new_category_name)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }
}
