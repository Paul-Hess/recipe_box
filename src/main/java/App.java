import java.util.Map;
import java.util.HashMap;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/add-recipe", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/add-recipe.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/add-recipe", (request, response) -> {
      String recipeName = request.queryParams("recipe_name");
      String instructions = request.queryParams("instructions");
      int rating = Integer.parseInt(request.queryParams("rating"));
      Recipe newRecipe = new Recipe(recipeName, instructions, rating);
      newRecipe.save();
      response.redirect("/recipe/" + newRecipe.getId());
      return null;
    });

    get("/recipe/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Recipe recipe = Recipe.findById(Integer.parseInt(request.params(":id")));
      model.put("recipe", recipe);
      model.put("ingredients", recipe.getIngredients());
      model.put("categories", recipe.getCategories());
      model.put("template", "templates/recipe.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/recipe/:id/edit", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Recipe recipe = Recipe.findById(Integer.parseInt(request.params(":id")));
      model.put("recipe", recipe);
      if(recipe.listAvailableCategories().size() > 0) {
        model.put("allCategories", recipe.listAvailableCategories());
      } else {
        model.put("allCategories", Category.all());
      }

      if(recipe.listAvailableIngredients().size() > 0) {
        model.put("allIngredients", recipe.listAvailableIngredients());
      } else {
        model.put("allIngredients", Ingredient.all());
      }

      model.put("allCategoriesSize", Category.all().size());
      model.put("allIngredientsSize", Ingredient.all().size());
      model.put("recipe-categories", recipe.getCategories());
      model.put("template", "templates/edit-recipe.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipe/:id/edit", (request, response) -> {
      String recipeName = request.queryParams("recipe_name");
      String instructions = request.queryParams("instructions");
      int rating = Integer.parseInt(request.queryParams("rating"));


      int recipeId = Integer.parseInt(request.params(":id"));
      Recipe recipe = Recipe.findById(recipeId);

      recipe.update(recipeName, instructions, rating);

      String categoryIdString = request.queryParams("category");
      if(categoryIdString != null) {
        int categoryId = Integer.parseInt(categoryIdString);
        Category category = Category.findById(categoryId);
        recipe.addCategory(category);
      }

      response.redirect("/recipe/" + recipeId);
      return null;
    });

    post("/recipe/:id/delete", (request, response) -> {
      Recipe recipe = Recipe.findById(Integer.parseInt(request.params(":id")));
      recipe.delete();

      response.redirect("/list-recipes");
      return null;
    });

    get("/list-recipes", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("recipes", Recipe.sortByRating());
      model.put("template", "templates/list-recipes.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipe/:recipe_id/remove-category", (request, response) -> {
      int categoryId = Integer.parseInt(request.queryParams("remove-category"));
      Recipe recipe = Recipe.findById(Integer.parseInt(request.params(":recipe_id")));

      recipe.removeCategory(categoryId);
      response.redirect("/recipe/" + recipe.getId());
      return null;
    });

    get("/add-category", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/add-category.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/add-category", (request, response) -> {
      String categoryName = request.queryParams("category-name");
      Category newCategory = new Category(categoryName);
      newCategory.save();
      response.redirect("/categories");
      return null;
    });

    get("/categories", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("categories", Category.all());
      model.put("template", "templates/categories.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/category/:category_id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      int id = Integer.parseInt(request.params(":category_id"));
      Category currentCategory = Category.findById(id);
      model.put("category", currentCategory);
      model.put("recipes", currentCategory.getRecipes());
      model.put("template", "templates/category.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/category/:category_id/edit", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      int id = Integer.parseInt(request.params(":category_id"));
      Category currentCategory = Category.findById(id);

      if(currentCategory.listAvailableRecipes().size() > 0) {
        model.put("allRecipes", currentCategory.listAvailableRecipes());
      } else {
        model.put("allRecipes", Recipe.all());
      }
      model.put("allRecipesSize", Recipe.all().size());
      model.put("category", currentCategory);
      model.put("template", "templates/edit-category.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/category/:category_id/edit", (request, response) -> {
      int id = Integer.parseInt(request.params(":category_id"));
      Category currentCategory = Category.findById(id);

      String newCategoryName = request.queryParams("category-name");

      currentCategory.update(newCategoryName);

      String recipeIdString = request.queryParams("recipe");

      if(recipeIdString != null) {
        int recipeId = Integer.parseInt(recipeIdString);
        Recipe recipe = Recipe.findById(recipeId);
        recipe.addCategory(currentCategory);
      }

      response.redirect("/category/" + id);
      return null;
    });

    post("/category/:category_id/remove-recipe", (request, response) -> {
      int id = Integer.parseInt(request.params(":category_id"));
      Category category = Category.findById(id);
      Recipe recipe = Recipe.findById(Integer.parseInt(request.queryParams("remove-recipe")));

      recipe.removeCategory(id);

      response.redirect("category/" + id);
      return null;
    });

    post("/category/:category_id/delete", (request, response) -> {
      int id = Integer.parseInt(request.params(":category_id"));
      Category category = Category.findById(id);
      category.delete();

      response.redirect("/categories");
      return null;
    });

    post("/search-recipe", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String searchString = request.queryParams("search-recipe");

      model.put("query", searchString);
      model.put("recipes", Recipe.search(searchString));
      model.put("template", "templates/search-results.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());


    get("/add-ingredient", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/add-ingredient.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());
    post("/add-ingredient", (request, response) -> {
      String ingredientName = request.queryParams("ingredient-name");

      Ingredient newIngredient = new Ingredient(ingredientName);
      newIngredient.save();
      response.redirect("/ingredients");
      return null;
    });

    get("/ingredients", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("ingredients", Ingredient.all());
      model.put("template", "templates/ingredients.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipe/:recipe_id/add-ingredient", (request, response) -> {
      int recipeId = Integer.parseInt(request.params(":recipe_id"));

      Recipe recipe = Recipe.findById(recipeId);
      Ingredient ingredient = Ingredient.findById(Integer.parseInt(request.queryParams("add-ingredient")));

      recipe.addIngredient(ingredient);

      response.redirect("/recipe/" + recipeId + "/edit");
      return null;
    });

    post("/recipe/:recipe_id/remove-ingredient", (request, response) -> {
      int ingredientId = Integer.parseInt(request.queryParams("remove-ingredient"));
      Recipe recipe = Recipe.findById(Integer.parseInt(request.params(":recipe_id")));

      recipe.removeIngredient(ingredientId);
      response.redirect("/recipe/" + recipe.getId());
      return null;
    });

    get("/ingredient/:ingredient_id", (request, reqponse) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      int id = Integer.parseInt(request.params("ingredient_id"));
      Ingredient ingredient = Ingredient.findById(id);
      model.put("ingredient", ingredient);
      model.put("recipes", ingredient.getRecipes());
      model.put("template", "templates/ingredient.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

   }
}
