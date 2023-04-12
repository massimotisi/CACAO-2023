package abstraction.eq3Producteur3;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;

public class Producteur3 extends Producteur3CC  {
	/*
	 * ChampsH est un dictionnaire qui contient les champs Haut de Gamme
	 * On associe a un ensemble d'hectars un int qui correspond  leur step de plantaison 
	 *
	 *private HashMap<Integer,String> ChampsH;//UTILE ?
	 *
	 * ChampsM est un dictionnaire qui contient les champs Moyen de Gamme
	 * On associe a un ensemble d'hectars un int qui correspond  leur step de plantaison 
	 *
	 *private HashMap<Integer,String> ChampsM;//UTILE ?
	 *
	 * On cree un dictionnaire qui associe  la clef H ou M le dico ChampsM ou ChapmsH
	 */

	private Champs fields;
	private Integer HectaresLibres; /*Repertorie le nombre d'hectares libres que l'on possede*/
	private Integer HectaresUtilises; /*Repertorie le nombre d'hectares que l'on utilise*/
	private Integer CoutStep; /* Tout nos couts du step, reinitialises a zero au debut de chaque step et payes a la fin du step*/
	/*
	 * Je n'ai pas trouve le type du champs donc j'ai choisit String. A CHANGER
	 * Il faudra aussi penser a se mettre d'accord sur les tailles des champs initiaux.
	 */
	public Producteur3() {
		super();
		this.fields = new Champs();
		this.Stock = new Stock();
		this.CoutStep = 0;
		this.HectaresLibres= 0;
		this.HectaresUtilises=950000;
	}

	public void initialiser() {
		super.initialiser();
		new Producteur3();		
	}
	
	public Champs getFields() {
		return this.fields;
	}
	protected Stock getStock() {
		// TODO Auto-generated method stub
		return this.Stock;
	}
  
	/**
	 * @author BOCQUET Gabriel, Dubus-Chanson Victor
	 */
	public void next() {
		super.next();
		HarvestToStock(Filiere.LA_FILIERE.getEtape());
		updateHectaresLibres(Filiere.LA_FILIERE.getEtape());
		if (Filiere.LA_FILIERE.getEtape() % 12 == 0) {
			changeHectaresAndCoutsLies(variationBesoinHectares(Filiere.LA_FILIERE.getEtape()));
		}
		this.getJAchats().ajouter(Color.yellow, Color.BLACK, "Coût du step : " + this.CoutStep);
		this.getJGeneral().ajouter(Color.cyan, Color.BLACK, 
				"Step Actuelle : " + Filiere.LA_FILIERE.getEtape()+", Taille total des Champs utilisés : "+ this.HectaresUtilises+", Taille des champs libres" + this.HectaresLibres + ", Nombre d'employe : Pas encore calculé"+ "Resultat du step : pas encore calculé");
	
	
		this.CoutStep = 0;
	}
	/*


	
	/**

	 * @author Dubus-Chanson Victor
	 */
	public void addCoutHectaresUtilises() {
		Integer coutEmployes = this.HectaresUtilises * 220;
		this.CoutStep = this.CoutStep + coutEmployes;
	}
	
	public String toString() {
		return this.getNom();
	}
	

	/**
	 * @author BOCQUET Gabriel
	 */
	//Cette fonction ajoute  a chaque step les feves recoltees
	
	public void HarvestToStock(int step) {
		LinkedList<Integer> quantite = this.getFields().HarvestHM(step);
		Stock Stock = this.getStock();
		if(quantite.get(0) > 0) {
		Stock.ajouter(Feve.F_HQ_BE, quantite.get(0));
		}
		if(quantite.get(1) > 0) {
		Stock.ajouter(Feve.F_MQ_BE, quantite.get(1));
		}
		this.getJStock().ajouter(Color.GREEN, Color.BLACK,"On a ajoute "+ quantite.get(1) +" tonnes au stock de Moyenne Gamme le step n°"  +Filiere.LA_FILIERE.getEtape());
		this.getJStock().ajouter(Color.GREEN, Color.BLACK,"A l'étape "  +Filiere.LA_FILIERE.getEtape() + " les stocks de Moyenne Gamme sont de " + this.getStock().getQuantite(Feve.F_MQ_BE));
		this.getJStock().ajouter(Color.LIGHT_GRAY, Color.BLACK,"On a ajoute "+ quantite.get(0) +"tonnes au stock de Haute Gamme le step n°"  +Filiere.LA_FILIERE.getEtape());
		this.getJStock().ajouter(Color.LIGHT_GRAY, Color.BLACK,"A l'étape "  +Filiere.LA_FILIERE.getEtape() + " les stocks de Haute Gamme sont de " + this.getStock().getQuantite(Feve.F_HQ_BE));
	}


	/**
	 * @author Dubus-Chanson Victor
	 */
	
	/*Calcule le nombre d'Hectares (uniquement positif ou nul) que l'on a besoin de rajouter a la partie cultivee (seulement tous les 6 mois)*/
	/*A modifier, a besoin des quantites de feves echangees (via stock)*/
	
	public Integer variationBesoinHectares(Integer CurrentStep) {
		Integer BesoinHQ = 0;
		Integer BesoinMQ = 0;
		Stock Stock = this.getStock();
		Double Quantite_HQ_BE= Stock.getQuantite(Feve.F_HQ_BE);
		Double Quantite_MQ_BE= Stock.getQuantite(Feve.F_MQ_BE);
		if (Quantite_HQ_BE < 50000) {
			BesoinHQ += 1000; /*560 tonnes de plus par an à partir de 5ans*/
			HashMap<Integer, Integer> ChampsH = this.fields.getChamps().get("H");
			ChampsH.put(CurrentStep, BesoinHQ);
			this.fields.getChamps().put("H", ChampsH);
		}
		if (Quantite_MQ_BE < 500000) {
			BesoinMQ += 1000; /*560 tonnes de plus par an à partir de 5ans*/
			HashMap<Integer, Integer> ChampsM = this.fields.getChamps().get("M");
			ChampsM.put(CurrentStep, BesoinMQ);
			this.fields.getChamps().put("M", ChampsM);
		}
		/*LinkedList<Integer> Besoin = new LinkedList<Integer>();
		Besoin.add(BesoinMQ);
		Besoin.add(BesoinHQ);
		return Besoin;*/
		return BesoinHQ + BesoinMQ;
	}
	
	public void achatHectares(Integer HectaresAAcheter) {
		Integer coutAchatHectares = HectaresAAcheter * 3250;
		this.CoutStep = this.CoutStep + coutAchatHectares;
	}
	
	/*A faire a chaque step et tous les 6mois avant changeHectaresAndCoutsLies*/
	public void updateHectaresLibres(Integer CurrentStep) {
		Champs Champs = this.getFields();
		Integer HectaresLiberes = Champs.destructionVieuxHectares(CurrentStep);
		this.HectaresLibres += HectaresLiberes;
		this.HectaresUtilises -= HectaresLiberes;
	}
	
	/*Modifie les variables de couts et d'hectares en fonction des resultats de variationBesoinHectares*/
	public void changeHectaresAndCoutsLies(Integer ajoutHectares) {
		this.HectaresUtilises = this.HectaresUtilises + ajoutHectares;
		Integer HectaresAAcheter = ajoutHectares - this.HectaresLibres;
		if (HectaresAAcheter > 0) {
			this.achatHectares(HectaresAAcheter);
		}
		this.HectaresLibres = this.HectaresLibres - ajoutHectares;
		if (this.HectaresLibres < 0) {
			this.HectaresLibres = 0;
		}
	}
}
