package fca.core.lattice.operator.search;

import fca.core.lattice.ConceptLattice;
import fca.core.lattice.FormalConcept;
import fca.core.lattice.operator.Operator;
import fca.core.util.BasicSet;

/**
 * Op�rateur de recherche approximative pour l'extension pour un treillis plat
 * @author Ludovic Thomas
 * @version 1.0
 */
public class SearchApproximateExtentSimple extends Operator<ConceptLattice, BasicSet, FormalConcept> {
	
	/**
	 * Constructeur de l'op�rateur de recherche approximative pour l'extension pour un treillis plat
	 * @param data le treillis imbriqu� o� l'on doit faire la recherche
	 */
	public SearchApproximateExtentSimple(ConceptLattice data) {
		super(data, "SearchApproximateExtentSimple"); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.lattice.conceptual.operators.Operator#perform(java.lang.Object)
	 */
	@Override
	public FormalConcept perform(BasicSet entry) {
		return performRec(entry, data.getTopConcept());
	}
	
	/**
	 * Recherche recursive pour une extension donn�e et un concept
	 * @param extent l'extension recherch�e
	 * @param fc le concept courant dans la recherche recursive
	 * @return le concept trouv�
	 */
	private FormalConcept performRec(BasicSet extent, FormalConcept fc) {
		FormalConcept result = fc;
		for (FormalConcept children : fc.getChildren()) {
			BasicSet extentChildren = children.getExtent();
			if ((extent.size() <= extentChildren.size()) && (extentChildren.isIncluding(extent))) {
				result = performRec(extent, children);
			}
		}
		return result;
	}
	
}
