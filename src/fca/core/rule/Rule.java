package fca.core.rule;

import fca.core.util.BasicSet;
import fca.messages.CoreMessages;

/**
 * Cette classe permet de d�crire les caract�ristiques des r�gles d'association. Celles-ci sont de
 * la forme: <code>ant�c�dent --> cons�quence (support, confiance)</code>
 * @author Inconnu
 * @version 1.0
 */
public class Rule {
	
	/**
	 * Ant�c�dent de la r�gle
	 */
	private BasicSet antecedent;
	
	/**
	 * Cons�quence de la r�gle
	 */
	private BasicSet consequence;
	
	/**
	 * Support de la r�gle
	 */
	private double support;
	
	/**
	 * Confiance de la r�gle
	 */
	private double confidence;
	
	/**
	 * Mesure d'interet de la regle
	 */
	private double lift;
	
	/**
	 * Construit une r�gle � partir de l'ant�c�dent, la cons�quence, le support et la confiance
	 * @param ant l'antecedent de la r�gle
	 * @param cons la cons�quence de la r�gle
	 * @param supp le support de la r�gle
	 * @param conf la confiance de la r�gle
	 */
	public Rule(BasicSet ant, BasicSet cons, double supp, double conf, double li) {
		antecedent = ant;
		consequence = cons;
		support = supp;
		confidence = conf;
		lift = li;
	}
	
	/**
	 * Construit une r�gle � partir de l'ant�c�dent et la cons�quence
	 * @param ant l'antecedent de la r�gle
	 * @param cons la cons�quence de la r�gle
	 */
	public Rule(BasicSet ant, BasicSet cons) {
		antecedent = ant;
		consequence = cons;
		support = 1.0;
		confidence = 1.0;
		lift = 1.0;
	}
	
	/**
	 * @return la confiance de la r�gle
	 */
	public double getConfidence() {
		return confidence;
	}
	
	/**
	 * Change la confiance de la r�gle
	 * @param conf la nouvelle confiance de la r�gle
	 */
	public void setConfidence(double conf) {
		confidence = conf;
	}
	
	/**
	 * @return le support de la r�gle
	 */
	public double getSupport() {
		return support;
	}
	
	/**
	 * Change le support de la r�gle
	 * @param supp le nouveau support de la r�gle
	 */
	public void setSupport(double supp) {
		support = supp;
	}
	
	/**
	 * @return l'antecedent de la r�gle
	 */
	public BasicSet getAntecedent() {
		return antecedent;
	}
	
	/**
	 * Change l'antecedent de la r�gle
	 * @param ant le nouvel antecedent de la r�gle
	 */
	public void setAntecedent(BasicSet ant) {
		antecedent = ant;
	}
	
	/**
	 * @return la consequence de la r�gle
	 */
	public BasicSet getConsequence() {
		return consequence;
	}
	
	/**
	 * Change la consequence de la r�gle
	 * @param cons la nouvelle consequence de la r�gle
	 */
	public void setConsequence(BasicSet cons) {
		consequence = cons;
	}
	
	/**
	 * @return la mesure de lift de la regle
	 */
	public double getLift(){
		return lift;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object ruleObj) {
		if (!(ruleObj instanceof Rule))
			return false;
		
		Rule rule = (Rule) ruleObj;
		return ((antecedent.equals(rule.getAntecedent())) && (consequence.equals(rule.getConsequence())));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = ""; //$NON-NLS-1$
		str += antecedent;
		str += " => "; //$NON-NLS-1$
		str += consequence;
		str += "\t\t"; //$NON-NLS-1$
		
		// Enregistrement du support de la r�gle
		str += "("+CoreMessages.getString("Core.support")+" = "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String suppStr = Double.toString((((int) (support * 100.0))) / 100.0);
		str += suppStr;
		str += " ; "; //$NON-NLS-1$
		
		// Enregistrement de la confiance de la r�gle
		str += CoreMessages.getString("Core.confidence")+" = "; //$NON-NLS-1$ //$NON-NLS-2$
		String confStr = Double.toString((((int) (confidence * 100.0))) / 100.0);
		str += confStr;
		str += ")"; //$NON-NLS-1$
		
		return str;
	}
	
}