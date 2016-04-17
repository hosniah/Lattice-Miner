package fca.gui.context.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fca.core.context.Context;
import fca.gui.context.table.model.ContextTableModel;
import fca.gui.util.DialogBox;
import fca.messages.GUIMessages;

public abstract class ContextTable extends JTable implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7220746890355061875L;

	protected static int INVALID_ATT_NAME = 1;
	
	protected ContextTable thisTable;
	
	protected boolean moveRowAllowed;
	
	/**
	 * Constructeur.
	 * @param ctm Le ContextTableModel pour cette table
	 */
	public ContextTable(ContextTableModel ctm) {
		super(ctm);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setCellSelectionEnabled(true);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
		addMouseListener(this);
		
		//setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//addKeyListener(this);
		
		thisTable = this;
	}
	
	public abstract void setModelFromContext(Context c);
	
	@Override
	public TableCellRenderer getCellRenderer(int rowIdx, int colIdx) {
		TableCellRenderer renderer = new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				String btnValue = getModel().getValueAt(row, convertColumnIndexToModel(column)).toString();
				JButton cellBtn = new JButton(btnValue);
				cellBtn.setBorderPainted(false);
				
				if (hasFocus || isSelected)
					cellBtn.setBackground(new Color(225, 233, 252));
				else if (btnValue.equals("")) //$NON-NLS-1$
					cellBtn.setBackground(Color.LIGHT_GRAY);
				else
					cellBtn.setBackground(Color.WHITE);
				
				cellBtn.setForeground(Color.BLUE);
				return cellBtn;
			}
		};
		
		return renderer;
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		int rowIdx = rowAtPoint(event.getPoint());
		int colIdx = columnAtPoint(event.getPoint());
		
		String colName = ((ContextTableModel) getModel()).getColumnName(convertColumnIndexToModel(colIdx));
		String rowName = ((ContextTableModel) getModel()).getRowName(rowIdx);
		
		return "(" + rowName + ", " + colName + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public void setRowHeader(JTable header) {
		((ContextTableModel) getModel()).setRowHeader(header);
	}
	
	public JTable getRowHeader() {
		return ((ContextTableModel) getModel()).getRowHeader();
	}
	
	public void setColumnName(int colIdx, String value) {
		/*
		 * Il est nécessaire de convertir l'index de la colone à celui du modèle, au cas où les
		 * colonnes auraient été déplacées
		 */
		((ContextTableModel) getModel()).setColumnName(convertColumnIndexToModel(colIdx), value);
		String newName = ((ContextTableModel) getModel()).getColumnName(convertColumnIndexToModel(colIdx));
		
		//TableColumn column = getColumn(newName);
		//column.sizeWidthToFit();
		
		/* Un nom d'attribut ne peut apparaître qu'une seule fois dans une relation */
		if (newName.equals(value))
			setModelFromContext(((ContextTableModel) getModel()).getContext());
		else
			DialogBox.showMessageWarning(this, getContextTableText(INVALID_ATT_NAME), GUIMessages.getString("GUI.invalidAttributeName")); //$NON-NLS-1$
	}
	
	public void setRowName(int rowIdx, String value) {
		((ContextTableModel) getModel()).setRowName(rowIdx, value);
		String newName = ((ContextTableModel) getModel()).getRowName(rowIdx);
		
		/* Un nom d'attribut ne peut apparaître qu'une seule fois dans une relation */
		if (newName.equals(value))
			setModelFromContext(((ContextTableModel) getModel()).getContext());
		else
			DialogBox.showMessageWarning(this, getContextTableText(INVALID_ATT_NAME), GUIMessages.getString("GUI.invalidAttributeName")); //$NON-NLS-1$
	}
	
	/* ======== MOUSELISTENER INTERFACE ======== */

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public abstract void mouseClicked(MouseEvent e);
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}
	
	public String getContextTableText(int textId) {
		if (textId == INVALID_ATT_NAME) {
			return GUIMessages.getString("GUI.attributeNameNotValidOrAlreadyExist"); //$NON-NLS-1$
		}
		
		return ""; //$NON-NLS-1$
	}
	
}
