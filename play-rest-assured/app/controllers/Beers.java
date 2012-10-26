package controllers;

import models.Beer;
import org.codehaus.jackson.JsonNode;
import play.*;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

public class Beers extends Controller
{
    public static Result getAll()
    {
        return ok(Json.toJson(Beer.getAll()));
    }

    public static Result get(String name)
    {
        Beer beer = Beer.getByName(name);
        Result result;

        if (beer == null)
        {
            result = notFound();
        }
        else
        {
            result = ok(Json.toJson(beer));
        }
        return result;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create()
    {
        JsonNode json = request().body().asJson();
        Beer beer = Json.fromJson(json, Beer.class);

        beer.save();

        return ok(Json.toJson(beer));

    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(String name)
    {
        JsonNode json = request().body().asJson();
        Beer beer = Json.fromJson(json, Beer.class);

        beer.update();

        return ok(Json.toJson(beer));
    }

    public static Result delete(String name)
    {
        Beer beer = Beer.getByName(name);
        Result result;

        if (beer == null)
        {
            result = notFound();
        }
        else
        {
            beer.delete();
            result = noContent();
        }
        return result;
    }
}