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
package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
public class ToDo extends Model
{
    @Id
    public Long id;

    public String title;

    public Boolean completed;

    @Column(name = "ITEM_ORDER", nullable = true)
    public Long order;

    @Column(nullable = true)
    public Long created;

    private static final Finder<Long, ToDo> FIND = new Finder<Long, ToDo>(Long.class,
                                                                 ToDo.class);

    public static ToDo findById(Long id)
    {
        return FIND.byId(id);
    }

    public static ToDo findByTitle(String title)
    {
        List<ToDo> matches = FIND.where()
                                 .eq("title", title)
                                 .setMaxRows(1)
                                 .findList();
        return matches.isEmpty() ? null : matches.get(0);
    }

    public static List<ToDo> findAll()
    {
        return FIND.all();
    }
}
