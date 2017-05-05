import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Author: Shalu Achamma Sam
 * Digital Assignment.
 */
public class d extends Object {
    /** Namespace template  */
    private static final String URI_TEMPLATE = "http://example.com/travel#%s";
 
    /**
     * @param s
     *            a name to put in the namespace of my example
     * @return The full URI
     */
    private static final String uri(final String s) {
        return String.format(URI_TEMPLATE, s);
    }
 
    public static void main(String args[]) {
        // Create Model with fun facts about Java and its tools
        Model model = ModelFactory.createDefaultModel();
        d.addTravelStuff(model);
        // Output the model
        System.out.println("Our travel model:");
        model.write(System.out, "N3");
        // SPARQL query
        System.out.println("Find all paths from A to B in exactly two steps");
        dumpQueryResult(
                model,
                String.format(
                        "PREFIX tr: <%s> SELECT * WHERE {?a ?firstleg ?stopover . ?stopover ?secondleg ?b}",
                        uri("")));
        // PROV-O model with and without inference
        OntModel base = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        base.read("http://www.w3.org/ns/prov-o");
        OntModel inf = ModelFactory.createOntologyModel(
                OntModelSpec.OWL_MEM_MICRO_RULE_INF, base);
        // Create a Person in PROV
        Individual elmer = base.createIndividual("http://people/elmerfudd",
                base.getOntClass("http://www.w3.org/ns/prov#Person"));
        // Illustrate effect of inference
        System.out.println("RDF types in the model without inference:");
        listTypes(elmer);
        System.out.println("RDF types in the model with inference:");
        listTypes(inf.getIndividual("http://people/elmerfudd"));
    }
 
    /**
     * Helper method for populating a Model with some toy Resources and
     * Properties.
     * 
     * @param model
     *            The Model to populate.
     */
    private static final void addTravelStuff(final Model model) {
        model.setNsPrefix("tr", uri(""));
        final Property byTrain = model.createProperty(uri("ByTrain"));
        final Property ba = model.createProperty(uri("Indigo"));
        final Property af = model.createProperty(uri("SpiceJet"));
        final Resource Chennai = model.createResource(uri("Chennai"));
        final Resource lhr = model.createResource(uri("Hyderabad"))
                .addProperty(ba, Chennai).addProperty(af, Chennai);
        final Resource lgw = model.createResource(uri("Coimbatore")).addProperty(
                ba, Chennai);
        model.createResource(uri("Tirupati")).addProperty(byTrain, lhr)
                .addProperty(byTrain, lgw);
    }
 
    /**
     * Helper method for displaying results of a query.
     */
    private static void dumpQueryResult(final Model model,
            final String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        qe.close();
    }
 
    /**
     * Output all rdf:types of the given Individual.
     * 
     * @param p
     *            An Individual in an OWL Model.
     */
    private static void listTypes(final Individual p) {
        for (Iterator<Resource> i = p.listRDFTypes(false); i.hasNext();) {
            System.out.println(String.format("  %s has type %s", p.getURI(),
                    i.next()));
        }
    }
 
}
