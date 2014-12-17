package sparql;

import java.util.ArrayList;
import java.util.HashMap;
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
            // Creation and execution of the SPARQL query to get the labels of an object with the label "term"
            query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
            		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
            		"PREFIX owl:  <http://www.w3.org/2002/07/owl#>"+
            		"PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>"+
            		"SELECT ?name WHERE"+
            		"{"+
            		"?perstype rdfs:label \"" + term + "\"@fr."+
            		"?perstype rdfs:label ?name."+
            		"FILTER ( lang(?name) = \"fr\" )"+
            		"}";
            Iterable<Map<String, String>> results = sparqlClient.select(query);
            // Put the results in a list
            for (Map<String, String> result : results) {
            	synonymes.add(result.get("name"));
            }
        }
        else{
        	System.out.println("service is DOWN");
        }
		return synonymes;
	}
	
	
	// Méthode qui prend en argument une requete, et qui la modifie
	public static HashMap<String, Float> changeRequest(String[] requete){
		HashMap<String, Float> res = new HashMap<String, Float>();
		for (String mot : requete){
			ArrayList<String> synonymes = (ArrayList<String>) getSynonyms(mot);
			if (synonymes.size() == 0){
				res.put(mot, (float) 1.0);
			}
			else{
				float ponderation = (float) (1.0/(float)(synonymes.size()));
				for (String syn : synonymes){
					res.put(syn, ponderation);
				}
			}
		}
		return res;
		
	}
	
	
	public static void main(String[] args) {
		/*
		String word = "personnage";
		List<String> synonymes = getSynonyms(word);
		System.out.println("Affichage des synonymes de " + word + " : ");
		for (String syn : synonymes){
			System.out.println("  " + syn);
		}
		*/
		String[] req = {"personnage", "Intouchable"};
		HashMap<String, Float> newreq = changeRequest(req);
		for (String mot : newreq.keySet() ){
			System.out.println(mot + "   pondération : " + newreq.get(mot));
		}
	}
}
