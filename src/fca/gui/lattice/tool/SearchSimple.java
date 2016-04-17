package fca.gui.lattice.tool;

import java.util.Vector;

import fca.core.lattice.ConceptLattice;
import fca.core.lattice.DataCel;
import fca.core.lattice.FormalConcept;
import fca.core.lattice.operator.search.ApproximationSimple;
import fca.core.lattice.operator.search.SearchApproximateExtentSimple;
import fca.core.lattice.operator.search.SearchApproximateIntentSimple;
import fca.core.util.BasicSet;
import fca.core.util.Triple;
import fca.exception.LatticeMinerException;
import fca.gui.lattice.LatticePanel;
import fca.gui.lattice.element.GraphicalConcept;
import fca.gui.lattice.element.GraphicalLattice;
import fca.gui.lattice.element.GraphicalLatticeElement;
import fca.gui.lattice.element.LatticeStructure;
import fca.gui.util.DialogBox;
import fca.gui.util.constant.LMHistory;
import fca.gui.util.constant.LMColors.LatticeColor;
import fca.messages.GUIMessages;

/**
 * Panneau de recherche pour les treillis simples
 * @author Ludovic Thomas
 * @version 1.0
 */
public class SearchSimple extends Search {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6752228292115688209L;

	/**
	 * Constructeur
	 * @param l Le {@link GraphicalLattice} pour lequel ce panneau affiche les attributs et objets
	 * @param lp Le {@link LatticePanel} dans lequel est affiché le treillis
	 */
	public SearchSimple(GraphicalLattice l, LatticePanel lp) {
		super(l, lp);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.gui.lattice.PanelSearch#exactMatchAction()
	 */
	@Override
	public void exactMatchAction() {
		super.exactMatchAction();
		openResultButton.setEnabled(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.gui.lattice.PanelAttObjSearch#approximateMatch()
	 */
	@Override
	public GraphicalLattice approximateMatch() throws LatticeMinerException {
		
		GraphicalLattice res = (GraphicalLattice) lattice.clone();
		
		ApproximationSimple approximHaute = new ApproximationSimple(res.getNestedLattice().getConceptLattice());
		DataCel dataCel = new DataCel(getSelectedExtent(), getSelectedIntent());
		Triple<ConceptLattice, ConceptLattice, ConceptLattice> latticeL1L2UI = approximHaute.perform(dataCel);
		
		// Reinitialise le treillis sans aucune selection
		res.setOutOfFocus(true);
		
		// Affiche UI et UExt, UInt s'il y a une intersection
		if (latticeL1L2UI.getThird() != null) {
			GraphicalConcept conceptUExt = res.getNestedNodeByIntent(latticeL1L2UI.getThird().getBottomConcept().getIntent());
			Vector<GraphicalLatticeElement> filterUExt = conceptUExt.getFilter();
			GraphicalConcept conceptUInt = res.getNestedNodeByIntent(latticeL1L2UI.getThird().getTopConcept().getIntent());
			Vector<GraphicalLatticeElement> idealUInt = conceptUInt.getIdeal();
			
			// Affiche UI
			Vector<GraphicalLatticeElement> intersection = idealUInt;
			intersection.retainAll(filterUExt);
			intersection.add(conceptUExt);
			intersection.add(conceptUInt);
			
			res.showSubLattice(intersection, LatticeColor.ORANGE);
			
			// Affiche UExt et UInt
			conceptUExt.setColor(LatticeColor.PINK);
			conceptUInt.setColor(LatticeColor.PINK);
			
			// Construction du nouveau treillis graphique résultant de l'approximation
			ConceptLattice latticeUI = new ConceptLattice(latticeL1L2UI.getThird().getContext());
			LatticeStructure structUI = new LatticeStructure(latticeUI, latticeUI.getContext(), LatticeStructure.BEST);
			resultOperation = new GraphicalLattice(latticeUI, structUI);
			
			openResultButton.setEnabled(true);
		} else {
			openResultButton.setEnabled(false);
			resultOperation = null;
			res = null;
		}
		
		return res;
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.gui.lattice.PanelAttObjSearch#approximateMatchAction()
	 */
	@Override
	public void approximateMatchAction() throws LatticeMinerException {
		if (!isSearchOnObjects() && !isSearchOnAttributes()) {
			DialogBox.showMessageInformation(viewer, NO_SEARCH, GUIMessages.getString("GUI.noSearch")); //$NON-NLS-1$
		} else {
			GraphicalLattice displayedLattice = approximateMatch();
			
			if (displayedLattice != null) {
				displayedLattice.setEditable(false);
				viewer.changeDisplayedLattice(displayedLattice, LMHistory.APPROXIMATION);
				
				viewer.lockHistory();
				viewer.getFrame().getTreePanel().selectPathNode(displayedLattice.getTopNode());
				viewer.unlockHistory();
				
				displayedLattice.setEditable(true);
				
			} else {
				viewer.setSelectedNodes(new Vector<GraphicalConcept>());
				viewer.getRootLattice().setOutOfFocus(true);
				viewer.repaint();
				DialogBox.showMessageInformation(viewer, NO_RESULT, GUIMessages.getString("GUI.resultForSearch")); //$NON-NLS-1$
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.gui.lattice.PanelSearch#searchApproximateNodeWithExtent(fca.general.BasicSet)
	 */
	@Override
	protected GraphicalConcept searchApproximateNodeWithExtent(BasicSet extent) {
		SearchApproximateExtentSimple search = new SearchApproximateExtentSimple(
				lattice.getNestedLattice().getConceptLattice());
		FormalConcept formalResult = search.perform(extent);
		return lattice.getGraphicalConcept(formalResult);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.gui.lattice.PanelSearch#searchApproximateNodeWithIntent(fca.general.BasicSet)
	 */
	@Override
	protected GraphicalConcept searchApproximateNodeWithIntent(BasicSet intent) {
		SearchApproximateIntentSimple search = new SearchApproximateIntentSimple(
				lattice.getNestedLattice().getConceptLattice());
		FormalConcept formalResult = search.perform(intent);
		return lattice.getGraphicalConcept(formalResult);
	}
	
}