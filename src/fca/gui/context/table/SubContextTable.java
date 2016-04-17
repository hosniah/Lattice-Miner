package fca.gui.context.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fca.core.context.binary.BinaryContext;
import fca.core.context.nested.NestedContext;
import fca.exception.LatticeMinerException;
import fca.gui.context.table.model.SubContextTableModel;
import fca.gui.util.DialogBox;

/**
 * �diteur de contexte pour les sous-contextes d'un �diteur de contexte imbriqu�
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class SubContextTable extends BinaryContextTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4399632443271255691L;
	private Color levelColor; //Couleur associ�e au niveau de la relation dans l'�diteur
	
	/**
	 * Constructeur
	 * @param binCtx Le BinaryContext repr�sent� dans cet �diteur
	 * @param c La Color associ�e � ce contexte
	 */
	public SubContextTable(BinaryContext binCtx, Color c) {
		super(new SubContextTableModel(binCtx));
		setColumnSelectionAllowed(true);
		levelColor = c;
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.ContextTable#getCellRenderer(int, int)
	 */
	@Override
	public TableCellRenderer getCellRenderer(int rowIdx, int colIdx) {
		TableCellRenderer renderer = new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				String btnValue = getModel().getValueAt(row, column).toString();
				JButton cellBtn = new JButton(btnValue);
				cellBtn.setBorderPainted(false);
				
				if (table.isColumnSelected(column))
					cellBtn.setBackground(new Color(215, 215, 240));
				else
					cellBtn.setBackground(levelColor);
				
				return cellBtn;
			}
		};
		
		return renderer;
	}
	
	/**
	 * Permet d'obtenir l'attribut repr�sent� dans une colonne donn�e
	 * @param columnIdx La position dans l'�diteur de l'attribut recherch�
	 * @return La String contenant l'attribut de la colonne donn�e
	 */
	public String getAttributeAt(int columnIdx) {
		return ((SubContextTableModel) getModel()).getAttributeAt(convertColumnIndexToModel(columnIdx));
	}
	
	/**
	 * Permet d'obtenir la liste ordon�e des relations de l'attribut dans la colonne donn�e, pour
	 * chacun des objets
	 * @param columnIdx La position dans l'�diteur de l'attribut recherch�
	 * @return Le Vector contenant les relation de l'attribut avec chacun des objets
	 */
	public Vector<String> getRelationsForAttributeAt(int columnIdx) {
		return ((SubContextTableModel) getModel()).getRelationsForAttributeAt(convertColumnIndexToModel(columnIdx));
	}
	
	/**
	 * Supprime la colonne � la position donn�e, et donc aussi l'attribut associ� � cette colonne
	 * @param columnIdx La position dans l'�diteur de l'attribut � supprimer
	 */
	public void removeColumnAt(int columnIdx) {
		BinaryContext ctx = getBinaryContext();
		ctx.removeAttribute(getAttributeAt(columnIdx));
		SubContextTableModel newModel = new SubContextTableModel(ctx);
		setModel(newModel);
		validate();
	}
	
	/**
	 * Ajoute une colonne attribut dans l'�diteur
	 * @param attribute La String contenant l'attribut repr�sent� dans la nouvelle colonne
	 * @param relationValues La liste ordonn�e des relations de l'attributs avec les objets du
	 *        contexte
	 * @return Le boolean indiquant si l'attribut a �t� ajout�
	 */
	public boolean hasAddedColumn(String attribute, Vector<String> relationValues) {
		BinaryContext ctx = getBinaryContext();
		
		/* Un attribut ne peut appara�tre qu'une seule fois dans une relatin */
		if (ctx.getAttributes().contains(attribute))
			return false;
		
		/* Ajout de l'attribut et de ses relations avec les objets dans la relation binaire */
		String object = ""; //$NON-NLS-1$
		String value = ""; //$NON-NLS-1$
		
		try {
			ctx.addAttribute(attribute);
			for (int i = 0; i < relationValues.size(); i++) {
				object = ctx.getObjectAt(i);
				value = relationValues.elementAt(i);
				ctx.setValueAt(value, object, attribute);
			}
		} catch (LatticeMinerException e) {
			DialogBox.showMessageError(this, e);
		}
		
		/* Reconstruction du mod�le de l'�diteur en fonction du nouveau contexte */
		SubContextTableModel newModel = new SubContextTableModel(ctx);
		setModel(newModel);
		validate();
		return true;
	}
	
	/**
	 * Permet d'obtenir le contexte binaire repr�sent� dans cet �diteur
	 * @return Le BinaryContext contenu dans le NestedContext repr�sent� dans l'�diteur
	 */
	public BinaryContext getBinaryContext() {
		return ((SubContextTableModel) getModel()).getBinaryContext();
	}
	
	/**
	 * Permet d'obtenir le contexte imbriqu� repr�sent� dans cet �diteur
	 * @return Le NestedContext repr�sent� dans l'�diteur
	 */
	public NestedContext getNestedContext() {
		return ((SubContextTableModel) getModel()).getNestedContext();
	}
}