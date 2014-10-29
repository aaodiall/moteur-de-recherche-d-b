package model;

import java.util.HashMap;
import java.util.List;


public class FichierInverse {

	
	// Attributs
	private HashMap<String, List<String>> fichierInverse;
	
	
	// Constructeur
	public FichierInverse(){
		fichierInverse = new HashMap<String, List<String>>();
	}
	
	
	// Méthodes
	public void addEtiquette(String terme, List<String> listeDocuments){
		fichierInverse.put(terme, listeDocuments);
	}
	
}