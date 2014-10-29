package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TestJsoupLib {

	public static void main(String[] args) {
		File input = new File("/home/alpha/Downloads/Omar.html");
		
		
		try {
			//charge les mots de la stopliste.txt dans une liste
			 BufferedReader reader = new BufferedReader(new FileReader("/home/alpha/Downloads/stopliste.txt"));
			 List<String> stoplist = new ArrayList<String>();

			String line;
	        while ((line = reader.readLine()) != null) {          
	        
	            stoplist.add(line);
	            stoplist.add( line.substring(0, 1).toUpperCase() + line.substring(1));
	           
	        }
	        
	        //on parse le fichier html
			Document doc = Jsoup.parse(input, "UTF-8", "");
			
			// Remove all script and style elements.
			doc.select("script, style, footer, .footer").remove();
			
			//on recupère le texte du body 
			Element body=doc.body();
			
			//on supprime la ponctuation (trouver un regex qui regoupe tous les accent)
			//ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ ^[a-zA-Z]{3,7}$ Ééèêù 
			String text1=body.text().replaceAll("[^-a-zA-Z0-9ÀÁÂÄÅáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ\\s]", " ").replaceAll("\\s+", " ");;
			 System.out.println(text1);
			 
				//on supprime les mots de la stopliste
			 for(String s: stoplist){
				 text1=text1.replaceAll( "[ .,]"+ s +"[ .,]" , " ");
			       
			 }
			 
			 System.out.println(text1);
			 // on supprime les mots redondants
			 LinkedHashSet<String> words=removeDuplicateString(text1);
			 
			// String[] mots= text1.split(" ");
			//on affiche les mots
			 for(String s: words){
				 
			        System.out.println(s);
			 }
		//	 System.out.println("linked="+m.size()+" mot="+mots.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static LinkedHashSet<String> removeDuplicateString(String s) {
	    return new LinkedHashSet<String>(Arrays.asList(s.split(" ")));
	}
}	