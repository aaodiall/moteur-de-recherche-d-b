package sparql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Synonym {

	public static List<String> getSynonyms(String term){
		// Structure that contains the synonyms of the term
		List<String> synonymes = new ArrayList<String>();
		// Connection to the client
        SparqlClient sparqlClient = new SparqlClient("localhost:3030/space");
        // Test of the connection
        String query = "ASK WHERE { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(query);
        if (serverIsUp) {
            System.out.println("server is UP");
            // Creation and execution of the SPARQL query to get the labels of an object with the label "term"
            System.out.println("Récupération des synonymes de " + term + "...");
            query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
            		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
            		"PREFIX owl:  <http://www.w3.org/2002/07/owl#>"+
            		"PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>"+
            		"SELECT ?name WHERE"+
            		"{"+
            		"?perstype rdfs:label \"" + term + "\"@fr."+
            		"?perstype rdfs:label ?name."+
            		"}";
            System.out.println("Lancement de la requête : " + query);
            Iterable<Map<String, String>> results = sparqlClient.select(query);
            // Put the results in a list
            System.out.print("OK !\nRécupération des synonymes dans une liste...");
            for (Map<String, String> result : results) {
            	synonymes.add(result.get("name"));
            }
            System.out.println("OK !");
        }
        else{
        	System.out.println("service is DOWN");
        }
		return synonymes;
	}
	
	
	
	
	
	public static void main(String[] args) {
		String word = "Omar Sy";
		List<String> synonymes = getSynonyms(word);
		System.out.println("Affichage des synonymes de " + word + " : ");
		for (String syn : synonymes){
			System.out.println("  " + syn);
		}
	}
}
