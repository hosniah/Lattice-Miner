package fca.gui.context.table.model;

import java.util.Vector;

import fca.core.context.binary.BinaryContext;
import fca.core.context.nested.NestedContext;

/**
 * Mod�le de table pour les �diteur de sous-contextes d'un �diteur de contexte imbriqu�
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class SubContextTableModel extends BinaryContextTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8119831010936974951L;

	/**
	 * Constructeur
	 * @param bc Le BinaryContext qui permettra de contruire le mod�le
	 */
	public SubContextTableModel(BinaryContext bc) {
		super(bc);
	}
	
	/**
	 * Permet d'obtenir le contexte binaire repr�sent� dans cet �diteur
	 * @return Le BinaryContext repr�sent� dans l'�diteur
	 */
	public BinaryContext getBinaryContext() {
		return (BinaryContext) context;
	}
	
	/**
	 * Permet d'obtenir le contexte imbriqu� repr�sent� dans cet �diteur
	 * @return La NestedRelation repr�sent�e dans l'�diteur
	 */
	public NestedContext getNestedContext() {
		return (NestedContext) context;
	}
	
	/**
	 * Permet d'obtenir l'attribut repr�sent� dans une colonne donn�e
	 * @param columnIndex La position dans l'�diteur de l'attribut recherch�
	 * @return Le FormalAttribute contenant l'attribut de la colonne donn�e
	 */
	public String getAttributeAt(int columnIndex) {
		String attribute = context.getAttributeAt(columnIndex);
		return attribute;
	}
	
	/**
	 * Permet d'obtenir la liste ordon�e des relations de l'attribut dans la colonne donn�e, pour
	 * chacun des objets
	 * @param columnIndex La position dans l'�diteur de l'attribut recherch�
	 * @return Le Vector contenant les relation de l'attribut avec chacun des objets
	 */
	public Vector<String> getRelationsForAttributeAt(int columnIndex) {
		Vector<String> relations = new Vector<String>();
		for (int i = 0; i < getRowCount(); i++) {
			String value = context.getValueAt(i, columnIndex);
			relations.add(value);
		}
		return relations;
	}
}