package fca.gui.context.table.model;

import javax.swing.table.AbstractTableModel;

import fca.core.context.Context;
import fca.exception.AlreadyExistsException;
import fca.messages.GUIMessages;

/**
 * Mod�le de table contenant l'ent�te de rang�es d'un �diteur de contexte imbriqu� Outaouais
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class NestedRowHeaderModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 78105587410502211L;
	protected Context context; //Le contexte repr�sent� par ce mod�le
	
	/**
	 * Constructeur
	 * @param ctx Le contexte � partir duquel sera construit le mod�le
	 */
	public NestedRowHeaderModel(Context ctx) {
		super();
		context = ctx;
	}
	
	/**
	 * Permet de conna�tre le nombre de rang�es de ce mod�le
	 * @return Le int contenant le nombre de rang�es de la table
	 */
	public int getRowCount() {
		/* La table contient une rang�e par objet de la relation plus une rang�e d'ent�te */
		return context.getObjectCount() + 1;
	}
	
	/**
	 * Permet d'obtenir le nombre de colonnes de la table
	 * @return Le int contenant le nombre de colonnes de la table (toujours 1)
	 */
	public int getColumnCount() {
		return 1;
	}
	
	/**
	 * Ajoute une rang�e dans la relation associ�e au mod�le
	 */
	public void addRow() {
		context.addObject();
	}
	
	/**
	 * Ajuste le nom de la rang�e sp�cifi�e
	 * @param rowIdx Le int contenant la position de la rang�e choisie
	 * @param name La String contenant le nouveau nom de la rang�e
	 */
	public void setRowName(int rowIdx, String name) throws AlreadyExistsException {
		context.setObjectAt(name, rowIdx);
	}
	
	/**
	 * Permet d'obtenir le nom d'une rang�e donn�e
	 * @param rowIdx Le int contenant la position de la rang�e choisie
	 * @return La String contenant le nom de la rang�e
	 */
	public String getRowName(int rowIdx) {
		return context.getObjectAt(rowIdx);
	}
	
	/**
	 * Retourne la valeur d'une position dans le mod�le
	 * @param rowIdx Le int contenant la rang�e de la valeur recherch�e
	 * @param colIdx Le int contenant la colonne de la valeur recherch�e
	 * @return L'Object de type String contenant le nom de la rang�e rowIdx dans le mod�le
	 */
	public Object getValueAt(int rowIdx, int colIdx) {
		if (rowIdx == 0)
			return new String(GUIMessages.getString("GUI.attributes")); //$NON-NLS-1$
		else
			return context.getObjectAt(rowIdx - 1);
	}
	
	/**
	 * Permet de savoir si une position donn�e est modifiable par l'interface
	 * @param rowIdx Le int contenant la rang�e de la position recherch�e
	 * @param colIdx Le int contenant la colonne de la position recherch�e
	 * @return Le boolean indiquant si la position est modifiable (toujours faux)
	 */
	@Override
	public boolean isCellEditable(int rowIdx, int colIdx) {
		return false;
	}
}