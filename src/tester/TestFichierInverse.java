package tester;

import java.util.ArrayList;

import model.FichierInverse;
import controller.Parser;

public class TestFichierInverse {

	public static void main(String[] args) {
		System.out.print("Generation du fichier inverse...");
		Parser.generateFichierInverse();
		System.out.println(" DONE !");
		searchEngineOneWord("Cinéma");
	}

	// Afficher le fichier inverse
	public void afficherFichierInverse() {
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

	//
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
