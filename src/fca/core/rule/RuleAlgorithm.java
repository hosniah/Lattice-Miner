package fca.core.rule;

import java.util.Vector;

import fca.core.lattice.ConceptLattice;
import fca.messages.CoreMessages;

/**
 * Classe abstraite d'algorithme recherche de r�gles.
 * @author Inconnu
 * @version 1.0
 */
public abstract class RuleAlgorithm {
	
	/**
	 * Le treillis associ�
	 */
	protected ConceptLattice lattice;
	
	/**
	 * Confiance minimale
	 */
	protected double minConfidence;
	
	/**
	 * Support minimal
	 */
	protected double minSupport;
	
	/** Ensemble de r�gles produites */
	protected Vector<Rule> rules;
	
	/**
	 * Constructeur d'un algorithme de recherche de r�gle
	 * @param lat le treillis graphique o� l'on recherche les r�gles
	 * @param minSupp le support minimum
	 * @param minConf la confiance minimum
	 */
	public RuleAlgorithm(ConceptLattice lat, double minSupp, double minConf) {
		lattice = lat;
		minConfidence = minConf;
		minSupport = minSupp;
		rules = new Vector<Rule>();
	}
	
	/**
	 * @return la confiance minimale
	 */
	public double getMinimumConfidence() {
		return minConfidence;
	}
	
	/**
	 * Affectation de la confiance minimale
	 * @param minConf la nouvelle confiance minimale
	 */
	public void setMinimumConfidence(double minConf) {
		minConfidence = minConf;
	}
	
	/**
	 * @return le support minimal
	 */
	public double getMinimumSupport() {
		return minSupport;
	}
	
	/**
	 * Affectation du support minimal
	 * @param minSupp le nouveau support minimal
	 */
	public void setMinimumSupport(double minSupp) {
		minSupport = minSupp;
	}
	
	/**
	 * @return la base genre sous forme de Vecteur
	 */
	public Vector<Rule> getRules() {
		return rules;
	}
	
	/**
	 * @return le nombre de r�gles
	 */
	public int size() {
		return rules.size();
	}
	
	/**
	 * G�n�ration de l'ensemble des r�gles de la base g�n�rique
	 */
	public abstract void run();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "*********** "+CoreMessages.getString("Core.rules")+" ***********\n\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = rules.elementAt(i);
			result += rule.toString();
			result += "\n"; //$NON-NLS-1$
		}
		
		result += "\n*****************************"; //$NON-NLS-1$
		return result;
	}
}