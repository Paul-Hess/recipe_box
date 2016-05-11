import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Recipe {

  private int id;
  private String recipe_name;
  private String instructions;
  private int rating;

  @Override public boolean equals(Object otherRecipe) {
    if(!(otherRecipe instanceof Recipe)) {
      return false;
    } else {
      Recipe newRecipe = (Recipe) otherRecipe;
      return newRecipe.getRecipeName().equals(this.getRecipeName()) &&
      newRecipe.getInstructions().equals(this.getInstructions()) &&
      newRecipe.getRating() == this.getRating() &&
      newRecipe.getId() == this.getId();
    }
  }

  public Recipe(String recipe_name, String instructions, int rating) {
    this.recipe_name = recipe_name;
    this.instructions = instructions;
    this.rating = rating;
  }

  public String getRecipeName() {
    return this.recipe_name;
  }

  public String getInstructions() {
    return this.instructions;
  }

  public int getRating() {
    return this.rating;
  }

  public int getId() {
    return this.id;
  }

  public static List<Recipe> all() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT id, instructions, rating, recipe_name FROM recipes;";
      return con.createQuery(sql)
      .executeAndFetch(Recipe.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO recipes (instructions, rating, recipe_name) VALUES (:instructions, :rating, :recipe_name);";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("instructions", this.instructions)
        .addParameter("rating", this.rating)
        .addParameter("recipe_name", this.recipe_name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Recipe findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM recipes WHERE id=:id;";
      return con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Recipe.class);
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteRecipe = "DELETE FROM recipes WHERE id=:id;";
      con.createQuery(deleteRecipe)
        .addParameter("id", id)
        .executeUpdate();

      String deleteJoin = "DELETE FROM recipes_categories WHERE id=:id;";
      con.createQuery(deleteJoin)
        .addParameter("id", id)
        .executeUpdate();
    }

  }

  public void update(String new_recipe_name, String new_instructions, int new_rating) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE recipes SET recipe_name=:recipe_name, instructions=:instructions, rating=:rating WHERE id=:id;";
      con.createQuery(sql)
        .addParameter("recipe_name", new_recipe_name)
        .addParameter("instructions", new_instructions)
        .addParameter("rating", new_rating)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void addCategory(Category newCategory) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO recipes_categories (recipe_id, category_id) VALUES (:recipe_id, :category_id);";
      con.createQuery(sql)
        .addParameter("recipe_id", this.id)
        .addParameter("category_id", newCategory.getId())
        .executeUpdate();
    }
  }

  public List<Category> getCategories() {
    try(Connection con = DB.sql2o.open()) {
      String joinQuery = "SELECT category_id FROM recipes_categories WHERE recipe_id=:recipe_id;";
      List<Integer> categoryIds = con.createQuery(joinQuery)
        .addParameter("recipe_id", this.id)
        .executeAndFetch(Integer.class);

      List<Category> categories = new ArrayList<Category>();

      for(Integer categoryId : categoryIds) {
        String categoryQuery = "SELECT * FROM categories WHERE id =:category_id;";
        Category category = con.createQuery(categoryQuery)
          .addParameter("category_id", categoryId)
          .executeAndFetchFirst(Category.class);
          categories.add(category);
      }
      return categories;
    }
  }

  public void addIngredient(Ingredient newIngredient) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO recipes_ingredients (recipe_id, ingredient_id) VALUES (:recipe_id, :ingredient_id);";
      con.createQuery(sql)
        .addParameter("recipe_id", this.id)
        .addParameter("ingredient_id", newIngredient.getId())
        .executeUpdate();
    }
  }


  public List<Ingredient> getIngredients() {
    try(Connection con = DB.sql2o.open()) {
      String joinQuery = "SELECT ingredients.* FROM recipes JOIN recipes_ingredients ON (recipes.id = recipes_ingredients.recipe_id) JOIN ingredients ON (recipes_ingredients.ingredient_id = ingredients.id) WHERE  recipes.id = :id;";
      return con.createQuery(joinQuery)
        .addParameter("id" , this.id)
        .executeAndFetch(Ingredient.class);
    }
  }

  public List<Category> listAvailableCategories() {
    try(Connection con = DB.sql2o.open()) {
      String joinQuery = "SELECT categories.* FROM recipes JOIN recipes_categories ON (recipes.id = recipes_categories.recipe_id) JOIN categories ON (recipes_categories.category_id != categories.id) WHERE recipes.id = :id;";
      return con.createQuery(joinQuery)
        .addParameter("id", this.id)
        .executeAndFetch(Category.class);
    }
  }

  public void removeCategory(int categoryId) {
    try(Connection con = DB.sql2o.open()) {
      String removeQuery = "DELETE FROM recipes_categories WHERE category_id=:category_id AND recipe_id=:recipe_id;";
      con.createQuery(removeQuery)
        .addParameter("category_id", categoryId)
        .addParameter("recipe_id", this.id)
        .executeUpdate();
    }
  }

  public void removeIngredient(int ingredientId) {
    try(Connection con = DB.sql2o.open()) {
      String removeQuery = "DELETE FROM recipes_ingredients WHERE ingredient_id=:ingredient_id AND recipe_id=:recipe_id;";
      con.createQuery(removeQuery)
        .addParameter("ingredient_id", ingredientId)
        .addParameter("recipe_id", this.id)
        .executeUpdate();
    }
  }
}
