package sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.Parser;

public class Synonym {

	public static String getSynonyms(String term){
		// Structure that contains the synonyms of the term
		String synonymes = new String();
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
            	synonymes += (result.get("name") + " ");
            }
        }
        else{
        	System.out.println("service is DOWN");
        }
        if (synonymes.isEmpty()){
        	synonymes = term;
        }
		return synonymes;
	}
	
	
	// Méthode qui prend en argument une requete, et qui la modifie
	public static HashMap<String, Float> changeRequest(String[] requete){
		HashMap<String, Float> res = new HashMap<String, Float>();
		// creation de la stoplist
		ArrayList<String> stoplist = Parser.creerStopList();
		// On parcours tout les mots de la requête initiale
		for (String mot : requete){
			String synonymes = getSynonyms(mot);
			// on supprime les mots de la stopliste
			for (String s : stoplist) {
				synonymes = synonymes.replaceAll("[ .,]" + s + "[ .,]", " ");
				synonymes = synonymes.replaceAll("^" + s + "[ .,]", "");
			}
			// On split synonymes dans une arraylist
			String[] tabSyn = synonymes.split(" ");
			if (tabSyn.length == 0){
				res.put(mot, (float) 1.0);
			}
			else{
				float ponderation = 1.0f/(float)tabSyn.length;
				for (int i = 0 ; i<tabSyn.length ; i++){
					if (res.get(tabSyn[i]) != null){
						res.put(tabSyn[i], ponderation + res.get(tabSyn[i]));
					}
					else{
						res.put(tabSyn[i], ponderation);						
					}
				}
			}
		}
		return res;
		
	}
	
	
	public static void main(String[] args) {
		String requete[] = new String[] {"lieu","naissance","Omar Sy"};
		//String requete[] = new String[] {"personnes","Intouchables"};
		HashMap<String, Float> newreq = changeRequest(requete);
		//System.out.println(getSynonyms(requete[1]));
		for (String mot : newreq.keySet()){
			System.out.println(mot + " : " + newreq.get(mot));
		}
	}
}
