package tester;

import java.util.ArrayList;

import model.FichierInverse;
import controller.Parser;

public class TestFichierInverse {

	public static void main(String[] args) {
		System.out.print("Generation du fichier inverse...");
		Parser.generateFichierInverse();
		System.out.println(" DONE !");
		//searchEngineOneWord("Cinéma");
		afficherFichierInverse();
	}

	// Afficher le fichier inverse
	public static void afficherFichierInverse() {
		FichierInverse fichierInverse = FichierInverse.getInstance();
		for (String terme : fichierInverse.getFichierInverse().keySet()) {
			System.out.println(terme);
			ArrayList<String> listDocs = (ArrayList<String>) fichierInverse.getFichierInverse().get(terme);
			for (String nomDoc : listDocs) {
				System.out.print(nomDoc + " ");
			}
			System.out.println("\n");
		}
	}

	// Affiche les documents dans lequel apparaissent les termes
	public static void searchEngineOneWord(String terme) {
		FichierInverse fichierInverse = FichierInverse.getInstance();
		ArrayList<String> listDocs = (ArrayList<String>) fichierInverse
				.getFichierInverse().get(terme);
		System.out.println(terme);
		if (listDocs != null) {
			for (String nomDoc : listDocs) {
				System.out.print(nomDoc + " ");
			}
		}
	}

}
