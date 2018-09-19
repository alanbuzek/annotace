package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import cz.cvut.kbss.textanalysis.model.QueryResult;
import java.io.IOException;
import java.net.URL;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


@Service
public class OntologyService {

    @Autowired
    private MorphoDitaService morphoDitaService;

    private static final Logger LOG = LoggerFactory.getLogger(OntologyService.class);
    public OntologyService() {
    }

    public Model readOntologyFromFile(String filename) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        File file = new File(filename);
        FileReader reader = new FileReader(file);
        model.read(reader,null, FileUtils.langTurtle);
        //model.write(System.out, "RDF/JSON");

        return model;
    }

    public Model readOntology(URL url) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        model.read(url.openStream(),null, FileUtils.langTurtle);
        //model.write(System.out, "RDF/JSON");
        return model;
    }

    public List<QueryResult> analyzeModel(URL url) throws IOException {
        return analyzeModel(readOntology(url));
    }

    public List<QueryResult> analyzeModel(Model model) {
        List<QueryResult> queryResultList = new ArrayList<>();
        LOG.debug("Analyzing ontologie model to get all labels");
        RDFNode s;
        RDFNode o;
        ResultSet resultSet;
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "SELECT ?s ?o WHERE {" +
                "?s rdfs:label ?o" +
                "}";

        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        resultSet = queryExecution.execSelect();

        //LOG.debug("resultset is: " + asText(resultSet));

        for ( ;resultSet.hasNext(); ) {
            QuerySolution querySolution = resultSet.nextSolution();
            s = querySolution.get("s");
            o = querySolution.get("o");
            QueryResult queryResultobject = new QueryResult(s.asNode().toString(), o.asLiteral().getString(), morphoDitaService.getMorphoDiteResultProcessed(o.asLiteral().getString()));
            queryResultList.add(queryResultobject);

            //LOG.debug("the subject is: " + s.asNode().toString() + "and the label is: " + o.asLiteral().getString() + "\n");

        }
        printList(queryResultList);

        //Store all lables in one string and call morphoDita only once then map the queryResult objects to corresponding sub-array
        String ontologieLables = "";
        for(int i=0; i<queryResultList.size(); i++) {
            ontologieLables = ontologieLables + queryResultList.get(i).getLabel() + "\n";
        }

        System.out.println("string containing all ontologie lables" + "\n" + ontologieLables);


        return queryResultList;

        //return resultSet;

    }

//
//    public void readResultSet(ResultSet resultSet) {
//        LOG.debug("Reading the resultset for the second time" + asText(resultSet));
//        RDFNode s;
//        RDFNode o;
//        for ( ;resultSet.hasNext(); ) {
//            QuerySolution querySolution = resultSet.nextSolution();
//            s = querySolution.get("s");
//            o = querySolution.get("o");
//            LOG.debug("the subject is: " + s.asNode().getLocalName() + "and the label is: " + o.asNode().getLocalName() + "\n");
//        }
//    }




    public void printList(List<QueryResult> queryResultList) {
        for (int i=0; i<queryResultList.size(); i++) {
            System.out.println("type from the arraylist is: " + queryResultList.get(i).getType() + "   and label is: " + queryResultList.get(i).getLabel());
            System.out.println("\n");
        }
    }


}
