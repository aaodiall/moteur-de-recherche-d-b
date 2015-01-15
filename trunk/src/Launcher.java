import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.mongodb.DBCollection;

import controller.Evaluator;
import controller.EvaluatorRequest;
import controller.Parser;
import dao.MongoDB;


public class Launcher {
	
	public static final String ONTO_NAME = "cinema-onto";
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int choice = 0;
		boolean continuer = true;
		while (continuer) {
			System.out.println("Moteur de Recherche : que voulez-vous faire ?");
			System.out.println("1 : générer la base de données");
			System.out.println("2 : lancer les requêtes sans l'ontologie");
			System.out.println("3 : lancer les requêtes avec l'ontologie");
			System.out.println("4 : évaluer les requêtes");
			System.out.println("5 : quitter le programme");
			while (choice < 1 || choice > 5){
				System.out.print(" > ");
				try {
					choice = sc.nextInt();
				} catch (InputMismatchException e) {
					System.err.println("Erreur : entrez un nombre entre 1 et 5 !");
					sc.nextLine();
				}
				System.out.print("\n");
			}
			
			switch (choice) {
			case 1: generateDB();break;
			case 2: launchRequest(); break;
			case 3: launchRequestWithOnto(); break;
			case 4: evaluateRequest(); break;
			case 5: continuer = false; System.out.println("Fin du programme");break;
			}
			System.out.println();
			choice = 0;
		}
		sc.close();
	}
	
	
	
	public final static void generateDB(){
		MongoDB mongo=new MongoDB();
		DBCollection coll=mongo.connection();
		System.out.println("Génération du fichier inverse en cours...");
		Parser.stockerFicherInverse(mongo,coll);
		System.out.println("Génération du fichier inverse fait !");
	}
	
	
	public final static void launchRequest(){
		System.out.println("Lancement des requêtes présentes dans le fichier " + EvaluatorRequest.REQ + " : ");
		EvaluatorRequest.evaluatorRequest(false);
		System.out.println("Résultats des requêtes généré dans le dossier " + EvaluatorRequest.DOSSIER_REQ + " !");
	}

	public final static void launchRequestWithOnto(){
		System.out.println("Lancement des requêtes présentes dans le fichier " + EvaluatorRequest.REQ);
		File onto = new File(ONTO_NAME);
		System.out.println("Utilisation de l'ontologie " + onto.getAbsolutePath());
		EvaluatorRequest.evaluatorRequest(true);
		System.out.println("Résultats des requêtes généré dans le dossier " + EvaluatorRequest.DOSSIER_REQ + " !");
	}
	
	public final static void evaluateRequest(){
		System.out.println("Evaluation du résultat des requêtes...");
		System.out.println("Lecture des fichiers dans le dossier " + EvaluatorRequest.Q_REL);
		Evaluator.evaluatorFinal();
	}
}
