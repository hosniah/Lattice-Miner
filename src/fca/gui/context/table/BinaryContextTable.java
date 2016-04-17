package fca.gui.context.table;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import fca.core.context.Context;
import fca.core.context.binary.BinaryContext;
import fca.gui.context.table.model.BinaryContextTableModel;
import fca.gui.context.table.model.ContextTableModel;
import fca.gui.util.DialogBox;
import fca.messages.GUIMessages;

public class BinaryContextTable extends ContextTable implements KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3564403570829707874L;
	/* Gestion du langage */
	private static int NEW_ATT_NAME = 1;
	private static int NEW_OBJ_NAME = 2;
	private static int MODIFY = 3;
	
	private int draggedRow = -1;
	
	/**
	 * Constructeur.
	 * @param bc Le {@link BinaryContext} de cette table
	 */
	public BinaryContextTable(BinaryContext bc) {
		super(new BinaryContextTableModel(bc));
		getTableHeader().addMouseListener(new ColumnHeaderListener());
		
		RowHeaderListener rowListener = new RowHeaderListener();
		getRowHeader().addMouseListener(rowListener);
		getRowHeader().addMouseMotionListener(rowListener);
		
		addKeyListener(this);
	}
	
	/**
	 * Constructeur.
	 * @param model Le {@link BinaryContextTableModel} de cette table
	 */
	public BinaryContextTable(BinaryContextTableModel model) {
		super(model);
		getTableHeader().addMouseListener(new ColumnHeaderListener());
		
		RowHeaderListener rowListener = new RowHeaderListener();
		getRowHeader().addMouseListener(rowListener);
		getRowHeader().addMouseMotionListener(rowListener);
	}
	
	@Override
	public void setModelFromContext(Context c) {
		setModel(new BinaryContextTableModel((BinaryContext) c));
		getRowHeader().repaint();
		
		RowHeaderListener rowListener = new RowHeaderListener();
		getRowHeader().addMouseListener(rowListener);
		getRowHeader().addMouseMotionListener(rowListener);
	}
	
	/**
	 * Permet d'indiquer si le déplacement des rangées est permis.
	 * @param b Un boolean indiquant si le déplacement est permis.
	 */
	public void setMoveRowAllowed(boolean b) {
		((ContextTableModel) getModel()).setMoveRowAllowed(b);
	}
	
	/**
	 * Permet de savoir si le déplacement des rangées est permis.
	 * @return Un boolean indiquant si le déplacement est permis.
	 */
	public boolean isMoveRowAllowed() {
		return ((ContextTableModel) getModel()).isMoveRowAllowed();
	}
	
	/**
	 * Permet de déplacer une rangée.
	 * @param startIdx Un int indiquant la rangée à déplacer
	 * @param endIdx Un int indiquant la destination de la rangée à déplacer
	 */
	public void moveRow(int startIdx, int endIdx) {
		if (((ContextTableModel) getModel()).hasMovedRow(startIdx, endIdx))
			setModelFromContext(((ContextTableModel) getModel()).getContext());
	}
	
	@Override
	public boolean isRowSelected(int rowIdx) {
		boolean superSelected = super.isRowSelected(rowIdx);
		
		return superSelected || draggedRow == rowIdx;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int col = columnAtPoint(e.getPoint());
		int row = rowAtPoint(e.getPoint());
		
		if (row > -1 && col > -1 && e.getClickCount() == 2) {
			if (getValueAt(row, convertColumnIndexToModel(col)).equals("X")) //$NON-NLS-1$
				getModel().setValueAt("", row, convertColumnIndexToModel(col)); //$NON-NLS-1$
			else
				getModel().setValueAt("X", row, convertColumnIndexToModel(col)); //$NON-NLS-1$
			repaint();
		}
	}
	
	private class ColumnHeaderListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			int col = columnAtPoint(e.getPoint());
			if (col > -1 && e.getClickCount() == 2) {
				String newName = DialogBox.showInputQuestion(thisTable, getBinaryTableText(NEW_ATT_NAME),
						getBinaryTableText(MODIFY));
				
				if (newName != null)
					thisTable.setColumnName(col, newName);
			}
		}
		
		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}
		
		public void mousePressed(MouseEvent e) {
		}
		
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	private class RowHeaderListener implements MouseListener, MouseMotionListener {
		private boolean drag = false;
		private int dragStartIdx = -1;
		
		public void mouseClicked(MouseEvent e) {
			int row = rowAtPoint(e.getPoint());
			if (row > -1 && e.getClickCount() == 2) {
				String newName = DialogBox.showInputQuestion(thisTable, getBinaryTableText(NEW_OBJ_NAME),
						getBinaryTableText(MODIFY));
				
				if (newName != null)
					thisTable.setRowName(row, newName);
			}
		}
		
		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}
		
		public void mousePressed(MouseEvent e) {
			setRowSelectionAllowed(true);
			drag = true;
			dragStartIdx = rowAtPoint(e.getPoint());
			draggedRow = dragStartIdx;
			
			if (dragStartIdx < 0)
				drag = false;
		}
		
		public void mouseReleased(MouseEvent e) {
			setRowSelectionAllowed(false);
			drag = false;
			dragStartIdx = -1;
			draggedRow = -1;
		}
		
		public void mouseDragged(MouseEvent e) {
			int endIdx = rowAtPoint(e.getPoint());
			if (drag && dragStartIdx != endIdx) {
				moveRow(dragStartIdx, endIdx);
				dragStartIdx = endIdx;
				draggedRow = dragStartIdx;
			}
		}
		
		public void mouseMoved(MouseEvent e) {
		}
	}
	
	private String getBinaryTableText(int textId) {
		if (textId == NEW_ATT_NAME) {
			return GUIMessages.getString("GUI.enterNewAttributeName")+" : "; //$NON-NLS-1$ //$NON-NLS-2$
		}

		else if (textId == NEW_OBJ_NAME) {
			return GUIMessages.getString("GUI.enterNewObjectName")+" : "; //$NON-NLS-1$ //$NON-NLS-2$
		}

		else if (textId == MODIFY) {
			return GUIMessages.getString("GUI.modify"); //$NON-NLS-1$
		} else
			return ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_ENTER) || (e.getKeyCode() == KeyEvent.VK_SPACE)) {
			int col = getSelectedColumn();
			int row = getSelectedRow();
			
			if (getValueAt(row, convertColumnIndexToModel(col)).equals("X")) //$NON-NLS-1$
				getModel().setValueAt("", row, convertColumnIndexToModel(col)); //$NON-NLS-1$
			else
				getModel().setValueAt("X", row, convertColumnIndexToModel(col)); //$NON-NLS-1$
			repaint();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}
	
}