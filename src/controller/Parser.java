package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import model.FichierInverse;
import model.OccurenceDocument;

public class Parser {

	private static final String NOM_DOSSIER = "CORPUS";
	private static final String STOP_LIST = "stopliste.txt";

	public List<OccurenceDocument> parser(String terme) {
		return null;

	}

	// Méthode qui créé le fichier inverse
	public static void generateFichierInverse() {
		FichierInverse fichierInverse = FichierInverse.getInstance();
		// Parcourir le dossier
		File CORPUS = new File(NOM_DOSSIER);
		String[] listeFichier = CORPUS.list();
		for (int i = 2; i < listeFichier.length; i++) {
			String nomDoc = listeFichier[i];
			File input = new File(NOM_DOSSIER + "/" + nomDoc);

			try {
				// charge les mots de la stopliste.txt dans une liste
				BufferedReader reader = new BufferedReader(new FileReader(STOP_LIST));
				List<String> stoplist = new ArrayList<String>();

				String line;
				while ((line = reader.readLine()) != null) {

					stoplist.add(line);
					stoplist.add(line.substring(0, 1).toUpperCase()
							+ line.substring(1));

				}
				reader.close();
				// on parse le fichier html
				Document doc = Jsoup.parse(input, "UTF-8", "");

				// Remove all script and style elements.
				doc.select("script, style, footer, .footer").remove();

				// on recupère le texte du body
				Element body = doc.body();

				// on supprime la ponctuation (trouver un regex qui regoupe tous
				// les accent)
				// ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ
				// ^[a-zA-Z]{3,7}$ Ééèêù
				String text1 = body
						.text()
						.replaceAll(
								"[^-a-zA-Z0-9ÀÁÂÄÅáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ\\s]",
								" ").replaceAll("\\s+", " ");
				;
				//System.out.println(text1);

				// on supprime les mots de la stopliste
				for (String s : stoplist) {
					text1 = text1.replaceAll("[ .,]" + s + "[ .,]", " ");

				}

				// On renvoie une table de hachage avec les mots et leur nombre d'occurence
				HashMap<String, Integer> wordsCounted = countWords(text1);

				for (String s : wordsCounted.keySet()) {
					fichierInverse.addEtiquette(s, nomDoc); // TODO : Remplacer par un stockage dans la base de données
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	// Returns a Hashmap with the words and the number of time this words appears in the document
		public static HashMap<String, Integer> countWords(String text){
			ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
			HashMap<String, Integer> wordCounted = new HashMap<String, Integer>();
			for (String word : words ){
				Integer n = wordCounted.get(word.toLowerCase());
				n = (n == null) ? 1 : ++n;
				wordCounted.put(word.toLowerCase(), n);
			}
			return wordCounted;
		}
}