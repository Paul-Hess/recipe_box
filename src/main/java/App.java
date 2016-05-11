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
      } else if (Category.all().size() > 0 && recipe.getCategories().size() < 2){
        model.put("allCategories", Category.all());
      }
      model.put("recipe-categories", recipe.getCategories());
      model.put("template", "templates/edit-recipe.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipe/:id/edit", (request, response) -> {
      String recipeName = request.queryParams("recipe_name");
      String instructions = request.queryParams("instructions");
      int rating = Integer.parseInt(request.queryParams("rating"));

      int categoryId = Integer.parseInt(request.queryParams("category"));
      Category category = Category.findById(categoryId);

      int recipeId = Integer.parseInt(request.params(":id"));
      Recipe recipe = Recipe.findById(recipeId);

      recipe.update(recipeName, instructions, rating);
      recipe.addCategory(category);

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
      model.put("recipes", Recipe.all());
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
  }
}
