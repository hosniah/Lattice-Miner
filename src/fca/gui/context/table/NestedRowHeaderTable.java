package fca.gui.context.table;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import fca.core.context.Context;
import fca.gui.context.table.model.NestedRowHeaderModel;

/**
 * Table contenant l'ent�te de rang�es d'un �diteur de contexte imbriqu�
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class NestedRowHeaderTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4723276332527154696L;
	protected Color cellBackground = new Color(240, 240, 240); //Couleur de fond des cellules
	
	/**
	 * Constructeur
	 * @param model Le NestedRowHeaderModel associ� � cette table
	 */
	public NestedRowHeaderTable(NestedRowHeaderModel model) {
		super(model);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setCellSelectionEnabled(true);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
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
	
	/**
	 * Permet de changer le mod�le associ� � cette table pour qu'il r�fl�te une nouvelle relation
	 * @param ctx Le Context � ins�rer dans le mod�le
	 */
	public void setModelFromRelation(Context ctx) {
		setModel(new NestedRowHeaderModel(ctx));
	}
}