package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.Etiquette;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mongodb.DBCollection;

import dao.MongoDB;

public class Parser {

	private static final String NOM_DOSSIER = "CORPUS";
	private static final String STOP_LIST = "stopliste.txt";
	private static final HashMap<String,Integer> valueTag= createValueTag();

	

	public  static void main( String args[] ){
		MongoDB mongo=new MongoDB();
		DBCollection coll=mongo.connection();
		stockerFicherInverse(mongo,coll);

	}

	

	
	
	
	
	private static HashMap<String, Integer> createValueTag(){
		HashMap<String, Integer> valueTag = new HashMap<String, Integer>();
		valueTag.put("title", 7);
		valueTag.put("h1", 5);
		valueTag.put("h2", 4);
		valueTag.put("h3", 3);
		valueTag.put("b", 2);
		valueTag.put("strong", 2);
		valueTag.put("em", 2);
		return valueTag;
	}
	
	
	public static void stockerFicherInverse(MongoDB mongo,DBCollection coll){
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
					stoplist.add(line.substring(0, 1).toUpperCase()	+ line.substring(1));
				}
				reader.close();

				// on parse le fichier html
				Document doc = Jsoup.parse(input, "UTF-8", "");

				// Remove all script and style elements.
				doc.select("script, style, footer, .footer").remove();

				// on recupère le texte du body
				Element body = doc.body();

				// On récupère tout les sous Element du body
				ArrayList<Element> subElements = subElements(body);

				// On construit la chaine de caractère à partir de cette liste
				StringBuilder sbd = new StringBuilder();
				for (Element el : subElements){
					sbd.append(el.text());
					sbd.append(" ");
				}
				// On ajoute le titre à cette chaine de caractère
				for (int i1 = 0 ; i1 < valueTag.get("title") ; i1++){
					sbd.append(doc.title());
					sbd.append(" ");
				}
				String text1 = sbd.toString();

				// on supprime la ponctuation (trouver un regex qui regoupe tous les accent)
				// ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ ^[a-zA-Z]{3,7}$ Ééèêù
				text1 = text1.replaceAll("[^a-zA-Z0-9ÀÁÂÄÅáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ\\s]"," ").replaceAll("\\s+", " ");

				// on supprime les mots de la stopliste
				for (String s : stoplist) {
					text1 = text1.replaceAll("[ .,]" + s + "[ .,]", " ");
				}

				// On renvoie une table de hachage avec les mots et leur nombre d'occurence
				HashMap<String, Integer> wordsCounted = countWords(text1);

				for (String s : wordsCounted.keySet()) {
					Etiquette etiquette= new Etiquette(s, wordsCounted.get(s),nomDoc);
					mongo.addEtiquette(coll, etiquette);
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
			Integer n = wordCounted.get(word);
			n = (n == null) ? 1 : ++n;
			wordCounted.put(word, n);
		}
		return wordCounted;
	}

	// Create a ArrayList of Elements with all the subElements of an Element
	public static ArrayList<Element> subElements(Element el){
		// The element is put multiple time if the valueTag is > 1
		Integer value = valueTag.get(el.tagName());
		value = (value==null) ? 1 : value;
		ArrayList<Element> elts = new ArrayList<Element>();
		if (el.children().first() != null){
			for (Element child : el.children()){
				ArrayList<Element> eltsChild = subElements(child);
				for (int i = 0 ; i < value ; i++){
					elts.addAll(eltsChild);
				}
			}
		}
		else {
			for (int i = 0 ; i < value ; i++){
				
				elts.add(el);
			}
		}
		return elts;
	}
}