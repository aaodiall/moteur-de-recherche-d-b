package model;

public class OccurenceDocument {
	
	private int nbOccurence;
	private String nomDocument;
	
	
	
	public OccurenceDocument(int nbOccurence, String nomDocument) {
		super();
		this.nbOccurence = nbOccurence;
		this.nomDocument = nomDocument;
	}
	public OccurenceDocument() {
		super();
	}
	
	//getters setters
	public int getNbOccurence() {
		return nbOccurence;
	}
	public void setNbOccurence(int nbOccurence) {
		this.nbOccurence = nbOccurence;
	}
	public String getNomDocument() {
		return nomDocument;
	}
	public void setNomDocument(String nomDocument) {
		this.nomDocument = nomDocument;
	}
	
	

}