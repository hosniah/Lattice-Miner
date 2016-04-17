package fca.gui.context.table;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import fca.core.context.Context;
import fca.gui.context.table.model.RowHeaderModel;

/**
 * Ent�te de rang�es pour les �diteurs de contexte
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class RowHeaderTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3163169425022746913L;
	protected Color cellBackground = new Color(240, 240, 240); //Couleur des cellules de l'ent�te
	
	/**
	 * Constructeur
	 * @param model Le RowHeaderModel � partir duquel est b�tie cette table
	 */
	public RowHeaderTable(RowHeaderModel model) {
		super(model);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setCellSelectionEnabled(true);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
	}
	
	/**
	 * Assigne un nouveau mod�le � la table pour repr�senter le contete donn�
	 * @param c Le Context qui permettra de construire le nouveau mod�le
	 */
	public void setModelFromContext(Context c) {
		setModel(new RowHeaderModel(c));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	@Override
	public TableCellRenderer getCellRenderer(int rowId, int colId) {
		DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) super.getCellRenderer(rowId, colId);
		
		dtcr.setBackground(cellBackground);
		return dtcr;
	}
	
}