package tester;

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

public class TestJsoupLib {

	public static void main(String[] args) {
		File input = new File("CORPUS/D1.html");
		
		
		try {
			//charge les mots de la stopliste.txt dans une liste
			 BufferedReader reader = new BufferedReader(new FileReader("stopliste.txt"));
			 List<String> stoplist = new ArrayList<String>();

			String line;
	        while ((line = reader.readLine()) != null) {          
	        
	            stoplist.add(line);
	            stoplist.add( line.substring(0, 1).toUpperCase() + line.substring(1));
	           
	        }
	        reader.close();
	        
	        //on parse le fichier html
			Document doc = Jsoup.parse(input, "UTF-8", "");
			
			// Remove all script and style elements.
			doc.select("script, style, footer, .footer").remove();
			
			//on recupère le texte du body 
			Element body=doc.body();
			
			// On récupère tout les sous Element du body
			ArrayList<Element> subElements = subElements(body);

			// On construit la chaine de caractère à partir de cette liste
			StringBuilder sbd = new StringBuilder();
			for (Element el : subElements){
				sbd.append(el.text());
				sbd.append(" ");
			}
			String text1 = sbd.toString();
			
			//on supprime la ponctuation (trouver un regex qui regoupe tous les accent)
			//ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ ^[a-zA-Z]{3,7}$ Ééèêù 
			text1=text1.replaceAll("[^a-zA-Z0-9ÀÁÂÄÅáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ\\s]", " ").replaceAll("\\s+", " ");
			 
			//on supprime les mots de la stopliste
			for(String s: stoplist){
				text1=text1.replaceAll( "[ .,]"+ s +"[ .,]" , " ");
			 }
			 
			 // On compte les mots
			 HashMap<String, Integer> wordsCounted = countWords(text1);
			 
			//on affiche les mots et leur nombre d'occurence
			 for(String s : wordsCounted.keySet()){
			        System.out.println(s + " : " + wordsCounted.get(s));
			 }
			 
		} catch (IOException e) {
			e.printStackTrace();
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