package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import dao.MongoDB;

import model.Etiquette;
import model.FichierInverse;
import model.OccurenceDocument;

public class Parser {

	private static final String NOM_DOSSIER = "CORPUS";
	private static final String STOP_LIST = "stopliste.txt";

	public List<OccurenceDocument> parser(String terme) {
		return null;

	}

	public  static void main( String args[] ){
		MongoDB mongo=new MongoDB();
		DBCollection coll=mongo.connection();
		//stockerFicherInverse(mongo, coll);
		DBObject object =mongo.findTerme(coll, "cinéma");
		DBObject[] object1 =searchPhrase(mongo, "cinéma cinéma");
		
		displayResults(object1);
		

	}

	// Méthode qui sauve le fichier inverse dans la base de données
	public static void test() {
		//FichierInverse fichierInverse = FichierInverse.getInstance();

		MongoDB mongo=new MongoDB();
		DBCollection coll=mongo.connection();
		stockerFicherInverse(mongo, coll);
		mongo.findTerme(coll, "cinéma");



	}

	
	public static void displayResults(DBObject[] results){
		
		for(DBObject aResult : results){
			JSONParser json=new JSONParser();
			try {
				Object obj= json.parse(aResult.toString());
				JSONObject document = (JSONObject)new JSONParser().parse(obj.toString());
				JSONArray documentContain=(JSONArray)document.get("document");
				
				for(int i=2; i< documentContain.size();i++){
					JSONObject obj2=(JSONObject)documentContain.get(i);
					System.out.println("document=" + obj2.get("nom")+ " poids=" + obj2.get("poids") );
					
				}
				System.out.println(" ");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static  DBObject[] searchPhrase(MongoDB mongo,String request){
		DBCollection coll=mongo.connection();
		String [] array= request.split(" ");
		DBObject[] results=new DBObject[array.length];
		for(int i=0; i<array.length;i++){
			results[i]=mongo.findTerme(coll, array[i]);
		}
		return results;
		
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
				String text1 = sbd.toString();

				// on supprime la ponctuation (trouver un regex qui regoupe tous les accent)
				// ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ ^[a-zA-Z]{3,7}$ Ééèêù
				text1 = text1
						.replaceAll(
								"[^a-zA-Z0-9ÀÁÂÄÅáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ\\s]",
								" ").replaceAll("\\s+", " ");
				;

				// on supprime les mots de la stopliste
				for (String s : stoplist) {
					text1 = text1.replaceAll("[ .,]" + s + "[ .,]", " ");
				}

				// On renvoie une table de hachage avec les mots et leur nombre d'occurence
				HashMap<String, Integer> wordsCounted = countWords(text1);

				for (String s : wordsCounted.keySet()) {
					Etiquette etiquette= new Etiquette(s, wordsCounted.get(s),nomDoc);

					mongo.addEtiquette(coll, etiquette);
					//fichierInverse.addEtiquette(s, nomDoc); // TODO : Remplacer par un stockage dans la base de données
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
		ArrayList<Element> elts = new ArrayList<Element>();
		if (el.children().first() != null){
			for (Element child : el.children()){
				ArrayList<Element> eltsChild = subElements(child);
				elts.addAll(eltsChild);
			}
		}
		else {
			elts.add(el);
		}
		return elts;
	}
}