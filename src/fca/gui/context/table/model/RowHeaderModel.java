package fca.gui.context.table.model;

import javax.swing.table.AbstractTableModel;

import fca.core.context.Context;
import fca.exception.AlreadyExistsException;
import fca.exception.LMLogger;

/**
 * Mod�le utilis� pour construire les tables d'ent�te des �diteurs de contexte
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class RowHeaderModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 389252417445714521L;
	protected Context context; //Le contexte pour lequel le mod�le est construit
	
	/**
	 * Constructeur
	 * @param c Le Context � partir de laquelle le mod�le sera construit
	 */
	public RowHeaderModel(Context c) {
		super();
		context = c;
	}
	
	/**
	 * Permet de conna�tre le nombre de rang�e de cette ent�te
	 * @return Le int contenant le nombre de rang�es
	 */
	public int getRowCount() {
		return context.getObjectCount();
	}
	
	/**
	 * Permet de conna�tre le nombre de colonnes de cette ent�te
	 * @return Le int contenant le nombre de colonnes (toujours 1)
	 */
	public int getColumnCount() {
		return 1;
	}
	
	/**
	 * Ajuste le nom d'une rang�e
	 * @param rowIdx Le int contenant la position de la rang�e
	 * @param name La String contenant le nouveau nom de la rang�e
	 */
	public void setRowName(int rowIdx, String name) {
		try {
			context.setObjectAt(name, rowIdx);
		} catch (AlreadyExistsException e) {
			// If there, a message has already been show and log
			LMLogger.logWarning(e, false);
		}
	}
	
	/**
	 * Permet d'obtenir le nom d'une rang�e de l'ent�te
	 * @param rowIdx Le int contenant la position de la rang�e
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
		return context.getObjectAt(rowIdx);
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