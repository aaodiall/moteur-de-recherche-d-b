package model;

public class Etiquette {
	
	private String terme;
	private int poids;
	private String document;
	


	public Etiquette(String terme, int poids, String document) {
		super();
		this.terme = terme;
		this.poids=poids;
		this.document = document;
	}

	public String getTerme() {
		return terme;
	}


	public void setTerme(String terme) {
		this.terme = terme;
	}


	

	public int getPoids() {
		return poids;
	}

	public void setPoids(int poids) {
		this.poids = poids;
	}

	public String getDocument() {
		return document;
	}


	public void setDocument(String document) {
		this.document = document;
	}

	

}
