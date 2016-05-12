import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Ingredient {

  private int id;
  private String ingredient_name;

  @Override public boolean equals(Object otherIngredient) {
    if(!(otherIngredient instanceof Ingredient)) {
      return false;
    } else {
      Ingredient newIngredient = (Ingredient) otherIngredient;
      return newIngredient.getIngredientName().equals(this.getIngredientName()) &&
      newIngredient.getId() == this.getId();
    }
  }

  public Ingredient(String ingredient_name) {
    this.ingredient_name = ingredient_name;
  }

  public String getIngredientName() {
    return this.ingredient_name;
  }

  public int getId() {
    return this.id;
  }

  public static List<Ingredient> all() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT id, ingredient_name FROM ingredients;";
      return con.createQuery(sql)
      .executeAndFetch(Ingredient.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO ingredients (ingredient_name) VALUES (:ingredient_name);";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("ingredient_name", this.ingredient_name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Ingredient findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM ingredients WHERE id=:id;";
      return con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Ingredient.class);
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM ingredients WHERE id=:id;";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void update(String new_ingredient_name) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE ingredients SET ingredient_name=:ingredient_name WHERE id=:id;";
      con.createQuery(sql)
        .addParameter("ingredient_name", new_ingredient_name)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public List<Recipe> getRecipes() {
    try(Connection con = DB.sql2o.open()) {
      String joinQuery = "SELECT recipes.* FROM ingredients JOIN recipes_ingredients ON (ingredients.id = recipes_ingredients.ingredient_id) JOIN recipes ON (recipes_ingredients.recipe_id = recipes.id) WHERE ingredients.id = :id;";
      return con.createQuery(joinQuery)
        .addParameter("id", this.id)
        .executeAndFetch(Recipe.class);
    }
  }


}
