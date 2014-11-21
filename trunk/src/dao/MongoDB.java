package dao;

import java.util.ArrayList;

import model.Etiquette;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDB {
	public static final String DB="Moteur_Recherche";
	public static final String HOST="localhost";
	public static final String TABLE="fichier_inverse";


	public  static void main( String args[] ){
		/* try{   
			// To connect to mongodb server
	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
	         // Now connect to your databases
	         DB db = mongoClient.getDB( "Moteur" );
	         System.out.println("Connect to database successfully");
	         DBCollection coll = db.getCollection("mycol2");

		 System.out.println("Collection created successfully");
	       addEtiquette(coll, new Etiquette("gamma", 1, "alpha.html"));
	      findTerme(coll, "gamma");
	      findAll(coll);
	       //System.out.println("Etiquette: "+eti.getDocument());
	      }catch(Exception e){
		     System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  }*/
	}

	public  DBCollection connection(){
		DBCollection coll=null;
		try{   
			// To connect to mongodb server
			MongoClient mongoClient = new MongoClient(HOST , 27017 );
			// Now connect to your databases
			DB db = mongoClient.getDB( DB );
			System.out.println("Connect to database successfully");
			coll = db.getCollection(TABLE);
			System.out.println("Collection created successfully");

		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return coll;

	}



	public  void addEtiquette( DBCollection coll, Etiquette etiquette){

		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("terme", etiquette.getTerme());
		//pas optimal bien gerer le update
		DBCursor cursor = coll.find(whereQuery);
		if(cursor.hasNext()) {
			
			BasicDBObject listItem = new BasicDBObject("document", new BasicDBObject("nom",etiquette.getDocument())
			.append("poids", etiquette.getPoids()));
			BasicDBObject updateQuery = new BasicDBObject("$push", listItem);
			coll.update(whereQuery,updateQuery);

		}else{
			//BasicDBObject listItem = new BasicDBObject("document", new BasicDBObject("nom",etiquette.getDocument())
			//.append("poids", etiquette.getPoids()));
			ArrayList<BasicDBObject> document = new ArrayList<BasicDBObject>();
			document.add(new BasicDBObject("nom", etiquette.getDocument()).append("poids", etiquette.getPoids()));
			//document.add(new BasicDBObject("poids", etiquette.getPoids()));
			BasicDBObject doc = new BasicDBObject("terme", etiquette.getTerme()).
					append("document",  document);
			coll.insert(doc);
			//System.out.println("Document inserted successfully");
		}


	}

	public DBObject findTerme(DBCollection coll, String terme){
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put("terme",terme);
		BasicDBObject except = new BasicDBObject("_id", 0).
				append("terme",  0);
		DBCursor cursor = coll.find(whereQuery,except);
		if(cursor.hasNext()){
			return cursor.next();
		}
		else return null;


	}
	public  void findAll(DBCollection coll){

		DBCursor cursor = coll.find();
		int i=1;
		while (cursor.hasNext()) { 
			System.out.println("Inserted Document: "+i); 
			System.out.println(cursor.next()); 
			i++;
		}
	}
}

