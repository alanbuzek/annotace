/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * © 2019 GitHub, Inc.
 */
package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.QueryResult;
import cz.cvut.kbss.textanalysis.service.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
public class OntologyController {

    @Autowired
    private OntologyService ontologyService;

    @RequestMapping("/ontology")
    public List<QueryResult> getAnnotationResult(@RequestParam("ontologyUrl") Set<URI> ontologyUrl) {
        List<QueryResult> queryResultList;
        //Model model = ontologyService.readOntologyFromFile("C:/Projects/OPPPR/services/textanalysis/src/main/resources/glosar.ttl");
        queryResultList = ontologyService.analyzeModel(ontologyUrl);

        return queryResultList;
    }


}
