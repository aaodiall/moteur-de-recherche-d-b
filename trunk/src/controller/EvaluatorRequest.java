package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sparql.Synonym;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import dao.MongoDB;

public class EvaluatorRequest {

	public static final String REQ="req.txt";
	public static final String DOSSIER_REQ = "REQ";
	public static final String Q_REL = "qrels";

	/*
	public  static void main( String args[] ){
		evaluatorRequest(false);
		//MongoDB mongo=new MongoDB();
		//DBCollection coll=mongo.connection();
		//displayResults(searchPhrase(mongo, coll, "personnage,Intouchable"));
	}
	*/
	
	
	
	public static void evaluatorRequest(boolean withSPARQL){
		MongoDB mongo=new MongoDB();
		DBCollection coll=mongo.connection();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(REQ));
			ArrayList<String> requetes =fileToList(reader);
			for(int i=0; i< requetes.size();i++){
				//find synonymes if withSPARQL is true
				HashMap<String, Float> newreq = new HashMap<String, Float>();
				if (withSPARQL){
					newreq = Synonym.changeRequest(requetes.get(i).split(","));
				}
				else{
					String reqModif = requetes.get(i).replaceAll(",", " ");
					reqModif = reqModif.replaceAll("[ ]+", " ");
					String[] req = reqModif.split(" ");
					for (int j = 0 ; j < req.length ; j++)
						newreq.put(req[j], (float) 1.0);
				}
				//search phrase
				DBObject[] result =searchPhrase(mongo,coll,newreq.keySet());
				//evaluer la requete
				Object[] evaluation=evaluationRequest(result, newreq) ;
				//copie dans fichier
				display(evaluation, i+1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public  static ArrayList<String> fileToList(BufferedReader reader){

		ArrayList<String> req = new ArrayList<String>();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				req.add(line);
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return req;
	}

	public static  DBObject[] searchPhrase(MongoDB mongo,DBCollection coll,Set<String> array){
		int i =0;
		DBObject[] results=new DBObject[array.size()];
		for(String str : array){
			results[i]=mongo.findTerme(coll, str);
			i++;
		}
		return results;

	}

	public static void display(Object[] documents,int numero){
		PrintWriter out;
		try {
			out = new PrintWriter(DOSSIER_REQ+"/"+"req"+numero+".txt");
			for(int i=documents.length-1; i>=0; i--){
				out.println((String)documents[i]);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}


	public static Object[] evaluationRequest(DBObject[] results, HashMap<String, Float> request) throws IOException{
		HashMap<String, Float> vector = new HashMap<String, Float>();
		for(DBObject aResult : results){
			if(aResult!=null){
				JSONParser json=new JSONParser();
				try {
					Object obj= json.parse(aResult.toString());
					JSONObject document = (JSONObject)new JSONParser().parse(obj.toString());
					JSONArray documentContain=(JSONArray)document.get("document");

					for(int i=0; i< documentContain.size();i++){
						JSONObject obj2=(JSONObject)documentContain.get(i);
						Float n = vector.get(obj2.get("nom"));
						Float ponderation = request.get(document.get("terme").toString());
						if(n==null){
							n=Float.parseFloat(obj2.get("poids").toString())*ponderation;
						}else{
							n+=Float.parseFloat(obj2.get("poids").toString())*ponderation;
						}
						vector.put(obj2.get("nom").toString(), n);


					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
		}
		vector= (HashMap<String, Float>) MapUtil.sortByValue( vector );


		return  vector.keySet().toArray();
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
				e.printStackTrace();
			}
		}
	}

}
