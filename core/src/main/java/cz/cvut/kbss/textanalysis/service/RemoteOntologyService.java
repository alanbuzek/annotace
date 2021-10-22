/*
 * Annotace
 * Copyright (C) 2021 Czech Technical University in Prague
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
package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RemoteOntologyService extends AbstractOntologyService {

    @Autowired
    public RemoteOntologyService(final LemmatizerApi lemmatizer) {
        super(lemmatizer);
    }

    public Model readOntology(URI uri, String userName, String password) {
        final HttpClientBuilder httpClient = HttpClientBuilder.create();
        if (userName != null) {
            final CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password)
            );
            httpClient.setDefaultCredentialsProvider(provider);
        }
        final Model model = ModelFactory.createDefaultModel();
        try (final CloseableHttpResponse response = httpClient.build().execute(new HttpGet(uri))) {
            final HttpEntity entity = response.getEntity();
            final String entityString = EntityUtils.toString(entity);
            if (entity != null) {
                model.read(new StringReader(entityString), null, FileUtils.langTurtle);
            }
        } catch (IOException e) {
            log.error("Error getting the ontology : ", e);
        }
        return model;
    }
}