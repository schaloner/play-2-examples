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

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ResponseBody;
import models.Beer;
import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.concurrent.Callable;

import static play.test.Helpers.*;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class BeersTest
{
    private static final int PORT = 3333;

    @Before
    public void setUp()
    {
        RestAssured.port = PORT;
    }

    @After
    public void tearDown()
    {
        RestAssured.reset();
    }

    @Test
    public void testGetAll_emptyDatabase()
    {
        running(testServer(PORT), new Runnable()
        {
            @Override
            public void run()
            {
                String body = RestAssured.expect()
                                               .statusCode(200)
                                               .when()
                                               .get("/")
                                               .andReturn()
                                               .body()
                                               .asString();
                JsonNode node = Json.parse(body);
                Assert.assertTrue(node.isArray());
                Assert.assertEquals(0,
                                    node.size());
            }
        });
    }


    @Test
    public void testGetAll_populatedDatabase()
    {
        running(testServer(PORT), new Runnable()
        {
            @Override
            public void run()
            {
                RestAssured.given()
                           .contentType(ContentType.JSON)
                           .content("{\"name\":\"Westmalle\"}")
                           .expect()
                           .statusCode(200)
                           .when()
                           .post("/");
                RestAssured.given()
                           .contentType(ContentType.JSON)
                           .content("{\"name\":\"Delirium\"}")
                           .expect()
                           .statusCode(200)
                           .when()
                           .post("/");

                String body = RestAssured.expect()
                                               .statusCode(200)
                                               .when()
                                               .get("/")
                                               .andReturn()
                                               .body()
                                               .asString();
                JsonNode node = Json.parse(body);
                Assert.assertTrue(node.isArray());
                Assert.assertEquals(2,
                                    node.size());
            }
        });
    }

    @Test
    public void testGetBeer_emptyDatabase()
    {
        running(testServer(PORT), new Runnable()
        {
            @Override
            public void run()
            {
                RestAssured.expect()
                           .statusCode(404)
                           .when()
                           .get("/Westmalle");
            }
        });
    }

    @Test
    public void testGetBeer_present()
    {
        running(testServer(PORT), new Runnable()
        {
            @Override
            public void run()
            {
                RestAssured.given()
                           .contentType(ContentType.JSON)
                           .content("{\"name\":\"Westmalle\"}")
                           .expect()
                           .statusCode(200)
                           .when()
                           .post("/");

                Beer beer = RestAssured.expect()
                                       .statusCode(200)
                                       .when()
                                       .get("/Westmalle")
                                       .andReturn()
                                       .body()
                                       .as(Beer.class);
                Assert.assertEquals("Westmalle",
                                    beer.name);
            }
        });
    }

    @Test
    public void testUpdate()
    {
        running(testServer(PORT), new Runnable()
        {
            @Override
            public void run()
            {
                Beer beer = RestAssured.given()
                                       .contentType(ContentType.JSON)
                                       .content("{\"name\":\"Westmale\"}")
                                       .expect()
                                       .statusCode(200)
                                       .when()
                                       .post("/")
                                       .andReturn()
                                       .body()
                                       .as(Beer.class);

                beer.name = "Westmalle";
                RestAssured.given()
                           .contentType(ContentType.JSON)
                           .content(Json.toJson(beer).toString())
                           .expect()
                           .statusCode(200)
                           .when()
                           .put("/" + beer.id)
                           .andReturn()
                           .body()
                           .asString();

                String body = RestAssured.expect()
                                         .statusCode(200)
                                         .when()
                                         .get("/")
                                         .andReturn()
                                         .body()
                                         .asString();
                JsonNode node = Json.parse(body);
                Assert.assertTrue(node.isArray());
                Assert.assertEquals(1,
                                    node.size());
                Assert.assertEquals("Westmalle",
                                    node.get(0).get("name").asText());
            }
        });
    }
}
