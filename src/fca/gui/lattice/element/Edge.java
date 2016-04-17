package fca.gui.lattice.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import fca.gui.util.constant.LMColors.LatticeColor;

/**
 * D�finition d'une ar�te graphique pour un treillis
 * @author Genevi�ve Roberge
 * @author Ludovic Thomas
 * @version 2.0
 */
public class Edge extends GraphicalLatticeElement {
	
	/** Ar�te s�lectionn�e (finale ou non) */
	private static final Color EDGE_SELECTED = Color.BLACK;
	
	/** Coutour normal d'un noeud non-final */
	private static final Color EDGE_OUT_FOCUS = Color.LIGHT_GRAY;
	
	/**
	 * Le noeud d'o� part l'ar�te inverse="childrenEdges:fca.lattice.graphical.GraphicalConcept"
	 */
	private GraphicalConcept sourceNode;
	
	/**
	 * Le noeud o� se termine l'ar�te inverse="parentEdges:fca.lattice.graphical.GraphicalConcept"
	 */
	private GraphicalConcept destNode;
	
	/**
	 * Constructeur d'une ar�te graphique
	 * @param s Le ConceptNode d'o� part cette ar�te
	 * @param d Le ConceptNode o� arrive cette ar�te
	 */
	public Edge(GraphicalConcept s, GraphicalConcept d) {
		super();
		sourceNode = s;
		destNode = d;
	}
	
	/**
	 * Permet d'obtenir le noeud d'o� part l'ar�te
	 * @return Le ConceptNode d'o� part l'ar�te
	 */
	public GraphicalConcept getSource() {
		return sourceNode;
	}
	
	/**
	 * Permet d'obtenir le noeud o� arrive l'ar�te
	 * @return Le ConceptNode o� arrive l'ar�te
	 */
	public GraphicalConcept getDestination() {
		return destNode;
	}
	
	/**
	 * Permet d'afficher une ar�te
	 * @param g2 Le composant Graphics2D qui doit afficher l'ar�te
	 */
	public void paint(Graphics2D g2) {
		/* Les deux extrimit�s d'une ar�te doivent �tre affich�es pour afficher l'ar�te */
		if (sourceNode.isVisible() && destNode.isVisible()) {
			double sX = sourceNode.getShape().getX() + (sourceNode.getShape().getWidth()) / 2;
			double sY = sourceNode.getShape().getY() + (sourceNode.getShape().getHeight()) / 2;
			double dX = destNode.getShape().getX() + (destNode.getShape().getWidth()) / 2;
			double dY = destNode.getShape().getY() + (destNode.getShape().getHeight()) / 2;
			Line2D edge = new Line2D.Double(sX, sY, dX, dY);
			
			if (sourceNode.isOutOfFocus() || destNode.isOutOfFocus()) {
				g2.setStroke(new BasicStroke(1));
				g2.setPaint(EDGE_OUT_FOCUS);
			}

			else if (sourceNode.getNestedConcept().isFinalConcept() && destNode.getNestedConcept().isFinalConcept()) {
				Color edgeColorDarker = color.getColor().darker();
				if (isSelected || isHighlighted) {
					g2.setPaint(edgeColorDarker);
					// g2.setPaint(GraphicalLattice.EDGE_SELECTED_OUT);
					g2.setStroke(new BasicStroke(2));
				} else {
					g2.setPaint(edgeColorDarker);
					// g2.setPaint(GraphicalLattice.FINAL_OUT);
					g2.setStroke(new BasicStroke(1));
				}
			}

			else {
				if (isSelected || isHighlighted) {
					g2.setPaint(EDGE_SELECTED);
					g2.setStroke(new BasicStroke(2));
				} else {
					g2.setPaint(EDGE_OUT_FOCUS);
					g2.setStroke(new BasicStroke(1));
				}
			}
			g2.draw(edge);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.lattice.graphical.GraphicalLatticeElement#getColorDefault()
	 */
	@Override
	public LatticeColor getColorDefault() {
		if (sourceNode == null)
			return LatticeColor.DEFAULT;
		else
			return sourceNode.getParentLattice().getLatticeColor();
	}
}