package fca.core.lattice;

import fca.core.util.BasicSet;

/**
 * Repr�sente une cellule a partir des donn�es du contexte, mais qui n'est pas n�cessairement un
 * concept {@link FormalConcept}
 * @author Ludovic Thomas
 * @version 1.0
 */
public class DataCel {
	
	/**
	 * Intention de la cellule
	 */
	protected BasicSet intent;
	
	/**
	 * Extension de la cellule
	 */
	protected BasicSet extent;
	
	/**
	 * Constructeur d'une cellule
	 * @param e extension de la cellule
	 * @param i intention de la cellule
	 */
	public DataCel(BasicSet e, BasicSet i) {
		intent = i;
		extent = e;
	}
	
	/**
	 * @return l'intention de la cellule
	 */
	public BasicSet getIntent() {
		return intent;
	}
	
	/**
	 * @return l'extension de la cellule
	 */
	public BasicSet getExtent() {
		return extent;
	}
	
	/**
	 * Change l'intentiion de la celulle
	 * @param i la nouvelle intention de la cellule
	 */
	public void setIntent(BasicSet i) {
		intent = i;
	}
	
	/**
	 * Change l'extension de la celulle
	 * @param e la nouvelle extension de la cellule
	 */
	public void setExtent(BasicSet e) {
		extent = e;
	}
	
	/**
	 * Verifie l'�galit� de deux cellules : Deux cellule sont �gales s'ils ont m�mes extensions et
	 * m�mes intentions
	 * @param dc la cellule � comparer � la cellule courante
	 * @return vrai si les cellules sont �gales, faux sinon
	 */
	public boolean equals(DataCel dc) {
		return (hasSameIntent(dc) && hasSameExtent(dc));
	}
	
	/**
	 * Verifie l'�galit� des intentions de deux cellules
	 * @param dc la cellule � comparer � la cellule courante
	 * @return vrai si les cellules ont m�mes intentions, faux sinon
	 */
	public boolean hasSameIntent(DataCel dc) {
		return dc.getIntent().equals(intent);
	}
	
	/**
	 * Verifie l'�galit� des extensions de deux cellules
	 * @param dc la cellule � comparer � la cellule courante
	 * @return vrai si les cellules ont m�mes extensions, faux sinon
	 */
	public boolean hasSameExtent(DataCel dc) {
		return dc.getExtent().equals(extent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = new String("DataCel" + " {" + getIntent() + "," + getExtent() + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() {
		return new DataCel((BasicSet) extent.clone(), (BasicSet) intent.clone());
	}
	
}