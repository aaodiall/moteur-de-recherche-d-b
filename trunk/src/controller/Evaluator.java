package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Scanner;

public class Evaluator {

	private static final String DOC_PATTERN = "D[0-9]+\\.html";
	private static final int PR5 = 5;
	private static final int PR10 = 10;
	private static final int PR25 = 25;
	
	
	// Evaluate all the requests
	public static void evaluatorFinal(){
		File[] resList = new File(EvaluatorRequest.DOSSIER_REQ).listFiles();
		File[] qrelList = new File(EvaluatorRequest.Q_REL).listFiles();
		float moyenne5total = 0.0f;
		float moyenne10total = 0.0f;
		float moyenne25total = 0.0f;
		// check the length
		if (resList.length != qrelList.length){
			System.err.println("Error : different number of files in the folders " + EvaluatorRequest.Q_REL + " and " + EvaluatorRequest.DOSSIER_REQ + "!");
			return;
		}
		for (int i = 0 ; i < resList.length ; i++){
			HashMap<String, Float> qrel = readQrelFile(qrelList[i]);
			ArrayList<String> res = getResultatFromReq(resList[i]);
			System.out.println("Requete " + (i+1));
			// Precision 5
			float moyenne5 = compareRequest(qrel, res, PR5);
			System.out.println(toString(moyenne5, PR5));
			moyenne5total += moyenne5;
			// Precision 10
			float moyenne10 = compareRequest(qrel, res, PR10);
			System.out.println(toString(moyenne10, PR10));
			moyenne10total += moyenne10;
			// Precision 25
			float moyenne25 = compareRequest(qrel, res, PR25);
			System.out.println(toString(moyenne25, PR25));
			moyenne25total += moyenne25;
		}
		// Affiche les moyennes
		System.out.println("Moyennes : ");
		System.out.println("Précision à 5 : " + moyenne5total/(float)resList.length);
		System.out.println("Précision à 10 : " + moyenne10total/(float)resList.length);
		System.out.println("Précision à 25 : " + moyenne25total/(float)resList.length);
	}
	
	
	// get a hashmap from the qrel file
	public static HashMap<String, Float> readQrelFile(File qrel){
		HashMap<String, Float> res = new LinkedHashMap<String, Float>();
		try {
			Scanner sc = new Scanner(qrel);
			sc.useLocale(Locale.FRANCE);
			while (sc.hasNext(DOC_PATTERN)){
				String docName = sc.next(DOC_PATTERN);
				Float eval = sc.nextFloat();
				res.put(docName, eval);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	public static float compareRequest(HashMap<String, Float> qrel, ArrayList<String> resultat, int precision){
		int valeurTotale = 0;
		for (int i = 0 ; i < precision ; i++){
			float valeur = qrel.get(resultat.get(i));
			if (valeur > 0)
				valeurTotale++;
		}
		return (float)valeurTotale / (float)precision;
	}
	
	
	// Read the req file and get the content
	public static ArrayList<String> getResultatFromReq(File reqFile){
		ArrayList<String> req = new ArrayList<String>();
		Scanner sc;
		try {
			sc = new Scanner(reqFile);
			while (sc.hasNext(DOC_PATTERN)){
				req.add(sc.next(DOC_PATTERN));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return req;
	}
	
	
	
	// Affiche une moyenne pour une précision donnée
	public static String toString(Float moyenne, int precision){
		return "Précision à " + precision + " : " + moyenne;
	}
	
	
}
