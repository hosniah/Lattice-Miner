package fca.gui.context.table.model;

import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fca.core.context.Context;
import fca.exception.AlreadyExistsException;
import fca.exception.LMLogger;
import fca.gui.context.table.RowHeaderTable;

/**
 * Mod�le d'une table de contexte
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public abstract class ContextTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2744215895161765243L;

	protected Context context;
	
	protected JTable rowHeader; //Table contenant l'ent�te des rang�es de la table
	
	protected JButton contextName; //�tiquette contenant le nom du contexte
	
	protected boolean moveRowAllowed;
	
	/**
	 * Constructeur
	 */
	public ContextTableModel(Context c) {
		super();
		context = c;
		rowHeader = new RowHeaderTable(new RowHeaderModel(c));
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return context.getObjectCount();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return context.getAttributeCount();
	}
	
	public void setColumnName(int colIdx, String name) {
		try {
			context.setAttributeAt(name, colIdx);
		} catch (AlreadyExistsException e) {
			// If there, a message has already been show and log
			LMLogger.logWarning(e, false);
		}
	}
	
	@Override
	public String getColumnName(int colIdx) {
		return context.getAttributeAt(colIdx);
	}
	
	public void setRowName(int rowIdx, String name) {
		try {
			context.setObjectAt(name, rowIdx);
		} catch (AlreadyExistsException e) {
			// If there, a message has already been show and log
			LMLogger.logWarning(e, false);
		}
	}
	
	public String getRowName(int rowIdx) {
		return context.getObjectAt(rowIdx);
	}
	
	/**
	 * Modifie la table d'ent�te des rang�es
	 * @param header La JTable contenant la nouvelle colonne d'ent�te pour les rang�es
	 */
	public void setRowHeader(JTable header) {
		MouseListener[] listeners = rowHeader.getMouseListeners();
		if (listeners.length > 0) {
			for (int i = 0; i < listeners.length; i++)
				header.addMouseListener(listeners[i]);
		}
		
		rowHeader = header;
	}
	
	/**
	 * Permet d'obtenir la table d'ent�te des rang�es
	 * @return La JTable contenant l'ent�te pour les rang�es
	 */
	public JTable getRowHeader() {
		return rowHeader;
	}
	
	/**
	 * Permet d'indiquer si le d�placement des rang�es est permis.
	 * @param b Un boolean indiquant si le d�placement est permis.
	 */
	public void setMoveRowAllowed(boolean b) {
		moveRowAllowed = b;
	}
	
	/**
	 * Permet de savoir si le d�placement des rang�es est permis.
	 * @return Un boolean indiquant si le d�placement est permis.
	 */
	public boolean isMoveRowAllowed() {
		return moveRowAllowed;
	}
	
	/**
	 * Permet de d�placer une rang�e.
	 * @param startIdx Un int indiquant la rang�e � d�placer
	 * @param endIdx Un int indiquant la destination de la rang�e � d�placer
	 * @return vrai si le deplacement s'est effectu� correctement, faux sinon
	 */
	public boolean hasMovedRow(int startIdx, int endIdx) {
		if (moveRowAllowed)
			return context.hasMovedObject(startIdx, endIdx);
		else
			return false;
	}
	
	/**
	 * Modifie le nom du contexte
	 * @param name String contenant le nouveau nom pour le contexte
	 */
	public void setContextName(String name) {
		contextName.setText(name);
	}
	
	/**
	 * Permet d'obtenir l'�tiquette qui contient le nom du contexte
	 * @return Le JLabel contenant le nom du contexte
	 */
	public JButton getContextName() {
		return contextName;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIdx, int colIdx) {
		return context.getValueAt(rowIdx, colIdx);
	}
	
	@Override
	public boolean isCellEditable(int rowIdx, int colIdx) {
		return true;
	}
	
	/**
	 * @return le context de la table
	 */
	public Context getContext() {
		return context;
	}
	
}