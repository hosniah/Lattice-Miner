package fca.gui.lattice.element;

import fca.core.util.BasicSet;

/**
 * ConceptPosition : D�finition d'une structure repr�sentant la position relative d'un noeud dans un
 * graphe par rapport au sommet du graphe
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class ConceptPosition {
	
	double relPositionX; //Position en X par rapport au noeud racine du graphe
	
	double relPositionY; //Position en Y par rapport au noeud racine du graphe
	
	BasicSet nodeIntent; //Identifiant du noeud auquel est rattach�e cette structure
	
	/**
	 * Constructeur
	 * @param intent Le BasicSet contenant l'intention du noeud repr�sent�
	 * @param x Le double contenant la position relative du noeud en x
	 * @param y Le double concenant la position relative du noeud en y
	 */
	public ConceptPosition(BasicSet intent, double x, double y) {
		nodeIntent = intent;
		relPositionX = x;
		relPositionY = y;
	}
	
	/**
	 * Permet d'obtenir la position en x relative � la position du sommet du treillis d'appartenance
	 * @return Le double contenant la position relative en x
	 */
	public double getRelX() {
		return relPositionX;
	}
	
	/**
	 * Permet d'obtenir la position en y relative � la position du sommet du treillis d'appartenance
	 * @return Le double contenant la position relative en y
	 */
	public double getRelY() {
		return relPositionY;
	}
	
	/**
	 * Permet d'obtenir l'identifiant du concept auquel cette position est rattach�e
	 * @return Le BasicSet contenant l'intension (identifiant) du concept
	 */
	public BasicSet getIntent() {
		return nodeIntent;
	}
	
	/**
	 * Permet de modifier la coordonn�e relative en x de cette position
	 * @param x Le double contenant la nouvelle position relative en x
	 */
	public void setRelX(double x) {
		relPositionX = x;
	}
	
	/**
	 * Permet de modifier la coordonn�e relative en y de cette position
	 * @param y Le double contenant la nouvelle position relative en x
	 */
	public void setRelY(double y) {
		relPositionY = y;
	}
	
	/**
	 * Permet de modifier l'intention du noeud associ� � cette position
	 * @param intent la nouvelle intetion du noeud
	 */
	public void setIntent(BasicSet intent) {
		nodeIntent = intent;
	}
	
	/**
	 * Permet d'obtenir une copie de cette position
	 * @return L'Object contenant la copie de cette position
	 */
	@Override
	public Object clone() {
		return new ConceptPosition(nodeIntent, relPositionX, relPositionY);
	}
}
