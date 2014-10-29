package controller;

import java.util.LinkedHashSet;
import java.util.List;

import model.FichierInverse;
import model.OccurenceDocument;

public class Parser {

	
	public List<OccurenceDocument> parser(String terme){
		return null;
		
	}
	
	
	// Méthode qui créé le fichier inverse
	public FichierInverse generateFichierInverse(){
		FichierInverse fichierInverse = FichierInverse.getInstance();
		
		return fichierInverse;
	}
	
	//Méthode qui renvoie la liste de termes pour un document donné
	public LinkedHashSet<String> getListTermes(String nomDocument) {
		return null;
	}
	
}