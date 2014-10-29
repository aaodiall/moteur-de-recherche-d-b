package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FichierInverse {

	
	// Attributs
	private HashMap<String, List<String>> fichierInverse ;
	private static FichierInverse instance = null;
	
	
	// Constructeur
	private FichierInverse(){
		fichierInverse = new HashMap<String, List<String>>();
	}
	
	// Get instance
	public static FichierInverse getInstance(){
		if (instance == null){
			instance = new FichierInverse();
		}
		return instance;
	}
	
	// Getter
	public HashMap<String, List<String>> getFichierInverse() {
		return fichierInverse;
	}
	
	// Méthodes
	public void addEtiquette(String terme, String nomDocument){
		if (!fichierInverse.containsKey(terme)){
			List<String> list=new ArrayList<String>();
			list.add(nomDocument);
			fichierInverse.put(terme, list);
		}
		else{
			fichierInverse.get(terme).add(nomDocument);
		}
		
	}

	
}