package fca.gui.lattice.element;

import fca.gui.util.constant.LMColors.LatticeColor;

/**
 * Un element d'un treillis graphique : une arr�te ou un noeud
 * @author Ludovic Thomas
 * @version 1.0
 */
public abstract class GraphicalLatticeElement {
	
	/** Indique si l'element est s�lectionn�e ou non */
	protected boolean isSelected;
	
	/** Indique si l'element est en surbrillance (sans s�lection) ou non */
	protected boolean isHighlighted;
	
	/** La couleur de l'element */
	protected LatticeColor color;
	
	/**
	 * Constructeur d'un element de treillis
	 */
	public GraphicalLatticeElement() {
		isSelected = false;
		setColorDefault();
	}
	
	/**
	 * Permet de savoir si l'element est s�lectionn� ou non
	 * @return vrai si l'element est s�lectionn�, faux sinon
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Permet d'indiquer � un element si il est s�lectionn�
	 * @param s Le boolean indiquant si l'element est s�lectionn�
	 */
	public void setSelected(boolean s) {
		isSelected = s;
	}
	
	/**
	 * Permet de savoir si l'element est en surbrillance ou non
	 * @return vrai si l'element est en surbrillance, faux sinon
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}
	
	/**
	 * Permet d'indiquer � un element si il est en surbrillance
	 * @param s Le boolean indiquant si l'element est en surbrillance
	 */
	public void setHighlighted(boolean s) {
		isHighlighted = s;
	}
	
	/**
	 * @return la couleur de l'element
	 */
	public LatticeColor getColor() {
		return color;
	}
	
	/**
	 * Change la couleur de l'element
	 * @param color la nouvelle couleur de l'element
	 */
	public void setColor(LatticeColor color) {
		this.color = color;
	}
	
	/**
	 * Remet la couleur par defaut pour l'element
	 */
	public void setColorDefault() {
		this.color = getColorDefault();
	}
	
	/**
	 * @return la couleur par defaut pour l'element
	 */
	public abstract LatticeColor getColorDefault();
	
}