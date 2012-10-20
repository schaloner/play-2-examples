/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import models.ToDo;
import org.codehaus.jackson.JsonNode;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class Application extends Controller
{
    public static Result index()
    {
        return ok(index.render());
    }

    public static Result getAll()
    {
        List<ToDo> toDos = ToDo.findAll();
        return ok(Json.toJson(toDos));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create()
    {
        JsonNode json = request().body().asJson();
        ToDo incoming = Json.fromJson(json, ToDo.class);

        incoming.save();

        return ok(Json.toJson(incoming));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id)
    {
        // the ID parameter isn't used here, since it's already in the JSON body
        // PUTs should have an identifier, you could use it to confirm against the model you're sending, etc
        JsonNode json = request().body().asJson();
        ToDo incoming = Json.fromJson(json, ToDo.class);

        incoming.update();

        return ok(Json.toJson(incoming));
    }

    public static Result delete(Long id)
    {
        ToDo toDo = ToDo.findById(id);
        if (toDo != null)
        {
            toDo.delete();
        }
        return noContent();
    }
}