package abstraction.eq7Distributeur1;

import java.util.HashMap;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

public class Distributeur1Stock extends Distributeur1Acteur{
	
	protected HashMap<Chocolat, Double> stockChoco;

	
	//	protected int totalStocksCB;  // La quantité totale de stock de chocolat bas de gamme 
	//	protected int totalStocksCML;  // La quantité totale de stock de chocolat moyenne gamme labellise
	//	protected int totalStocksCMNL;  // La quantité totale de stock de chocolat moyenne gamme non labellise
	//	protected int totalStocksCH;  // La quantité totale de stock de chocolat haute gamme
	
		/**
		 * initialise les indicateurs de stock
	 * @author ghaly
	 */
	protected void initialise_indic_stock() {
		stock_BQ.setValeur(this, 0);
		stock_HQ_BE.setValeur(this, 0);
		stock_MQ.setValeur(this, 0);
		stock_MQ_BE.setValeur(this, 0);
	}
	
	/**
	 * actualise les indicateurs de stock pour chaque gamme
	 * @author Ghaly
	 */
	
	protected void actualise_indic_stock() {
		initialise_indic_stock();
		for (ChocolatDeMarque marque : Filiere.LA_FILIERE.getChocolatsProduits()) {
			Chocolat gamme = marque.getChocolat();
    		if (gamme == Chocolat.C_BQ) {
    			stock_BQ.ajouter(this, get_valeur(Var_Stock_choco, marque));
    		}
    		if (gamme == Chocolat.C_MQ) {
    			stock_MQ.ajouter(this,get_valeur(Var_Stock_choco, marque));
    		}
    		if (gamme == Chocolat.C_MQ_BE) {
    			stock_MQ_BE.ajouter(this, get_valeur(Var_Stock_choco, marque));
    		}
    		if (gamme == Chocolat.C_HQ_BE) {
    			stock_HQ_BE.ajouter(this, get_valeur(Var_Stock_choco, marque));    		
    		}
		}
	}
	
	/**
	 * @author ghaly,theo
	 * actualise la moyenne des couts d'un chocolat de marque a une etape donnée
	 * La formule employee pour la moyenne est importante, il faut donc bien l'expliquer
	 * Ici, on utilise une moyenne arithmetique non ponderee qui prend en compte tous les achats depuis le debut de la simulation
	 */
	protected void actualise_cout(ChocolatDeMarque marque,Double cout) {
		Chocolat gamme = marque.getChocolat();
		Double stock_gamme = 0.;
		int nb_total_achats = 0;
		for (ChocolatDeMarque choco : Filiere.LA_FILIERE.getChocolatsProduits()) {
			nb_total_achats += nombre_achats.get(choco);
			if (choco.getChocolat()==gamme) {
				stock_gamme += get_valeur(Var_Stock_choco, marque);
			}

		}
		Double nv_cout = (cout_chocolat.get(gamme)*nb_total_achats + cout)/(nb_total_achats+1);
		cout_chocolat.replace(gamme, nv_cout);
	}

	protected void actualise_cout_marque(ChocolatDeMarque choco, Double cout) {
		int n = nombre_achats.get(choco);
		mettre_a_jour(Var_Cout_Choco, choco, (get_valeur(Var_Cout_Choco, choco)*n+cout)/(n+1) );
	}
	
	/**
	 * @author Ghaly
	 * renvoit le cout total de stockage actuel de la marque à l'instant t
	 */
	protected double get_cout_stockage(ChocolatDeMarque marque) {
		return cout_stockage_distributeur.getValeur() * get_valeur(Var_Stock_choco, marque);
	}
	
	/**
	 * @author Theo and Ghaly
	 */
	public void initialiser() {
		super.initialiser();

		//Initialisation des stocks dans Acteur (on est oblige a cause des variables)
		
		//initialisation des indicateurs de stock
		stock_BQ.setValeur(this, 0);
		stock_HQ_BE.setValeur(this, 0);
		stock_MQ.setValeur(this, 0);
		stock_MQ_BE.setValeur(this, 0);
		
		for (ChocolatDeMarque marque : Filiere.LA_FILIERE.getChocolatsProduits()) {
			Chocolat gamme = marque.getChocolat();
    		if (gamme == Chocolat.C_BQ) {
    			stock_BQ.ajouter(this, valeur_stock_initial);
    		}
    		if (gamme == Chocolat.C_MQ) {
    			stock_MQ.ajouter(this, valeur_stock_initial);
    		}
    		if (gamme == Chocolat.C_MQ_BE) {
    			stock_MQ_BE.ajouter(this, valeur_stock_initial);
    		}
    		if (gamme == Chocolat.C_HQ_BE) {
    			stock_HQ_BE.ajouter(this, valeur_stock_initial);    		
    		}
		}
	}
	
	
	/**
	 * @return 
	 * 
	 */
	public void next() {
		super.next();
		
		//initialisation des indicateurs de stocks
		actualise_indic_stock();
		
		//Actualisation du stock total
		double newstock = 0.;
		for (ChocolatDeMarque marque : Filiere.LA_FILIERE.getChocolatsProduits()) {
			newstock += get_valeur(Var_Stock_choco, marque);
		}
		totalStocks.setValeur(this, newstock, this.cryptogramme);
		
		
		//Affichage des stocks dans les Journaux
		for (ChocolatDeMarque marque : Filiere.LA_FILIERE.getChocolatsProduits()) {
			journal_stock.ajouter("Stock de "+marque+" : "+get_valeur(Var_Stock_choco, marque)+" T");
		}
		
		//System.out.print(totalStocks.getValeur()==(stock_BQ.getValeur()+stock_HQ_BE.getValeur()+stock_MQ.getValeur()+stock_MQ_BE.getValeur()));
		//Prise en compte des couts de stockage
		if (totalStocks.getValeur()*cout_stockage_distributeur.getValeur() > 0) {
			double cout_STOCK =  totalStocks.getValeur()*cout_stockage_distributeur.getValeur();
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme,Filiere.LA_FILIERE.getBanque(),cout_STOCK );	
			journal_stock.ajouter("Cout de stockage : "+cout_STOCK);
		}
	}
	
}
