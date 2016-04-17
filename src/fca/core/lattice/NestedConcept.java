package fca.core.lattice;

import java.util.Iterator;
import java.util.Vector;

import fca.core.context.binary.BinaryContext;
import fca.core.rule.Rule;
import fca.core.util.BasicSet;
import fca.exception.AlreadyExistsException;
import fca.exception.InvalidTypeException;

/**
 * Structure d'un noeud pour les diagrammes imbriques (NLD)
 * @author Genevi�ve Roberge
 * @version 1.0
 */

public class NestedConcept {
	
	private FormalConcept concept; //Concept courant
	/**
	 * inverse="childrenConcepts:fca.lattice.conceptual.NestedConcept"
	 */
	private NestedConcept externalConcept; //Concept contenant ce concept
	
	private BasicSet newIntent; //Intent résultant du produit des concepts
	
	private BasicSet newExtent; //Extent résultant du produit des concepts
	
	private BasicSet reducedIntent; //Intent réduit du produit des concepts
	
	private BasicSet reducedExtent; //Extent réduit du produit des concepts
	
	private Vector<ConceptLattice> internalLattices; //Vecteur contenant la liste ordonnée des CompleteConceptLattices
	
	private NestedLattice internalNestedLattice; //Treillis imbriqué contenu dans ce noeud
	/**
	 * inverse="topNestedConcept:fca.lattice.conceptual.NestedLattice"
	 */
	private NestedLattice parentNestedLattice; //Treilli imbriqué auquel appartient ce noeud
	
	private BinaryContext globalContext; //Contexte global qui est traité
	
	private boolean isFinalConcept; //Indique si le noeud fait partie du treillis résultant du produit des treillis imbriqués
	/**
	 * inverse="externalConcept:fca.lattice.conceptual.NestedConcept"
	 */
	private Vector<NestedConcept> childrenConcepts; //Liste des concepts enfants
	private Vector<NestedConcept> parentConcepts; //Liste des concepts parents
	
	private int nestedLevel; //Niveau d'imbrication du treillis auquel appartient ce noeud
	private Vector<Rule> currentRuleSet; //L'ensemble de r�gles actuellement associ�es � ce noeud
	
	/**
	 * Constructeur
	 * @param c Le FormalConcept pour lequel est construit le NestedConcept
	 * @param p Le NestedConcept parent qui a demandé la construction de ce NestedConcept
	 * @param nc Le NestedConcept qui contient le treillis auquel appartient le NestedConcept à
	 *        créer (peut être null)
	 * @param pl Le NestedLattice auquel appartient ce NestedConcept
	 * @param il Le Vector contenant la liste ordonnée des treillis à imbriquer dans ce noeud
	 * @param br Le BinaryContext contenant les relations contenus dans les treillis plus externes
	 *        et le treillis parent
	 */
	public NestedConcept(FormalConcept c, NestedConcept p, NestedConcept nc, NestedLattice pl,
			Vector<ConceptLattice> il, BinaryContext br) throws AlreadyExistsException, InvalidTypeException {
		this.externalConcept = nc;
		this.concept = c;
		this.parentConcepts = new Vector<NestedConcept>();
		this.parentNestedLattice = pl;
		this.internalLattices = il;
		
		if (p != null) {
			p.addChild(this);
			parentConcepts.add(p);
		} else
			parentNestedLattice.setTopNestedConcept(this);
		
		if (br != null)
			this.globalContext = (BinaryContext) br.clone();
		
		this.nestedLevel = parentNestedLattice.getNestedLevel();
		this.isFinalConcept = true;
		this.internalNestedLattice = null;
		this.reducedIntent = null;
		this.reducedExtent = null;
		
		createNewIntent();
		createNewExtent();
		createChildrenConcepts();
		createInternalNestedLattice();
	}
	
	/**
	 * Permet d'obtenir le treillis auquel appartient ce noeud
	 * @return Le NestedLattice auquel appartient ce noeud
	 */
	public NestedLattice getParentLattice() {
		return parentNestedLattice;
	}
	
	/**
	 * Permet d'obtenir le noeud concept qui est représenté par ce noeud imbriqué
	 * @return Le FormalConcept représenté par ce noeud du treillis
	 */
	public FormalConcept getConcept() {
		return concept;
	}
	
	/**
	 * Permet d'obtenir le niveau d'imbrication du treillis qui contient ce noeud
	 * @return Le int contenant le niveau d'imbrication du noeud
	 */
	public int getNestedLevel() {
		return nestedLevel;
	}
	
	/**
	 * Permet d'obtenir le treillis qui est imbriqué dans ce noeud
	 * @return Le NestedLattice imbriqué dans ce noeud du treillis
	 */
	public NestedLattice getInternalNestedLattice() {
		return internalNestedLattice;
	}
	
	/**
	 * Permet d'obtenir le concept externe dans lequel est imbriqué dans ce noeud
	 * @return Le NestedConcept dans lequel est imbriqué ce noeud du treillis
	 */
	public NestedConcept getExternalConcept() {
		return externalConcept;
	}
	
	/**
	 * Permet d'obtenir l'intent calculé de ce noeud (union des attributs du noeud externe et du
	 * noeud interne)
	 * @return Le BasicSet contenant l'intent calculé de ce noeud du treillis
	 */
	public BasicSet getIntent() {
		return newIntent;
	}
	
	/**
	 * Permet d'obtenir l'extent calculé de ce noeud (intersection des objets du noeud externe et
	 * du noeud interne)
	 * @return Le BasicSet contenant l'extent calculé de ce noeud du treillis
	 */
	public BasicSet getExtent() {
		return newExtent;
	}
	
	/**
	 * Permet d'ajuster l'intent reduit de ce noeud Comme chaque parent va appeler cette fonction,
	 * l'intention réduite va se calculer au fur et à mesure et enlevant toujours les attributs
	 * reçus en paramètre.
	 * @param attributes Le BasicSet contenant l'intent avec les attributs rencontrés avant ce
	 *        noeud
	 * @return Le BasicSet contenant l'intent avec les nouveaux attributs rencontrés dans ce noeud
	 */
	public BasicSet setReducedIntent(BasicSet attributes) {
		/*
		 * Si reducedIntent n'a jamais été calculé, il est initialisé avec la valeur de l'intent
		 * calculé
		 */
		if (reducedIntent == null)
			reducedIntent = (BasicSet) newIntent.clone();
		
		/*
		 * Tous les attributs déjà rencontrés avant ce noeud sont retirés de l'intention
		 * réduite
		 */
		reducedIntent.removeAll(attributes);
		
		/*
		 * Si le concept courant fait parti du treillis final, ses attributs sont ajoutés aux
		 * attributs rencontrés jusqu'à maintenant
		 */
		BasicSet newAttributes = (BasicSet) attributes.clone();
		if (isFinalConcept)
			newAttributes.addAll(reducedIntent);
		
		/*
		 * Si le concept courant contient un treillis imbriqué, les attributs rencontrés dans les
		 * treillis imbriqués internes sont ajoutés aux attributs rencontrés jusqu'à maintenant,
		 * et l'intention réduite des noeuds du treillis interne est calculée pendant cette
		 * recherche.
		 */
		if (internalNestedLattice != null)
			internalNestedLattice.getTopNestedConcept().setReducedIntent(newIntent);
		//newAttributes.addAll(internalNestedLattice.getTopNestedConcept().setReducedIntent(newAttributes));
		
		/*
		 * Les attributs retournés sont ceux rencontrés dans ce noeud, dans ses treillis internes
		 * et dans ses noeuds enfants. L'intention réduite des noeuds enfants est calculée au
		 * cours de cette recherche.
		 */
		BasicSet returnedIntent = (BasicSet) newAttributes.clone();
		returnedIntent.removeAll(attributes);
		for (int i = 0; i < childrenConcepts.size(); i++)
			returnedIntent.addAll((childrenConcepts.elementAt(i)).setReducedIntent(newAttributes));
		
		return returnedIntent;
	}
	
	/**
	 * Permet d'ajuster l'extent reduit de ce noeud Comme chaque enfant va appeler cette fonction,
	 * l'extention réduite va se calculer au fur et à mesure et enlevant toujours les objets
	 * reçus en paramètre.
	 * @param objects L'Extent contenant les objets rencontrés avant ce noeud
	 * @return L'Extent contenant les objets recontrés dans ce noeud
	 */
	public BasicSet setReducedExtent(BasicSet objects) {
		/*
		 * Si reducedIntent n'a jamais été calculé, il est initialisé avec la valeur de l'intent
		 * calculé
		 */
		if (reducedExtent == null)
			reducedExtent = (BasicSet) newExtent.clone();
		
		/*
		 * Les objets rencontrés dans les treillis internes sont recueillis, en même temps que
		 * l'intention réduite des noeuds du treillis interne est calculée
		 */
		//BasicSet internalObjects = new BasicSet();
		if (internalNestedLattice != null)
			internalNestedLattice.getBottomNestedConcept().setReducedExtent(new BasicSet());
		//internalObjects = internalNestedLattice.getBottomNestedConcept().setReducedExtent(objects);
		
		/*
		 * Si le concept courant fait partie du treillis final, les objets rencontrés jusqu'à ce
		 * noeud seront augmentés des objets rencontrés dans les noeuds des treillis internes et
		 * des objets rencontrés dans ce noeud. L'extention réduite elle sera l'extention calculé
		 * initialement pour ce noeud moins les objets rencontrés avant ce noeud et ceux
		 * rencontrés dans les treillis internes
		 */
		BasicSet newObjects = (BasicSet) objects.clone();
		if (isFinalConcept) {
			//newObjects.addAll(internalObjects);
			reducedExtent.removeAll(newObjects);
			newObjects.addAll(reducedExtent);
		}
		
		/*
		 * Les objets retournés sont ceux recontrés dans ce noeud, dans les treillis internes du
		 * noeud et dans les noeuds parents du noeud. L'extention réduite des noeuds parents est
		 * calculée pendant cette recherche
		 */
		BasicSet returnedExtent = (BasicSet) newObjects.clone();
		returnedExtent.removeAll(objects);
		for (int i = 0; i < parentConcepts.size(); i++)
			returnedExtent.addAll((parentConcepts.elementAt(i)).setReducedExtent(newObjects));
		
		return returnedExtent;
	}
	
	/**
	 * Permet d'obtenir l'intent reduit de ce noeud
	 * @return Le BasicSet contenant l'intent réduit de ce noeud du treillis
	 */
	public BasicSet getReducedIntent() {
		if (reducedIntent == null)
			reducedIntent = (BasicSet) newIntent.clone();
		
		return reducedIntent;
	}
	
	/**
	 * Permet d'obtenir l'extent réduit de ce noeud
	 * @return Le BasicSet contenant l'extent réduit de ce noeud du treillis
	 */
	public BasicSet getReducedExtent() {
		if (reducedExtent == null)
			reducedExtent = (BasicSet) newExtent.clone();
		
		return reducedExtent;
	}
	
	/**
	 * Permet de supprimer toutes les r?les actuellement associ?s ?ce noeud
	 */
	public void clearRuleSet() {
		currentRuleSet = new Vector<Rule>();
	}
	
	/**
	 * Permet d'ajouter une regle aux regles actuellement associees a ce noeud
	 * @param rule La Rule qui contient le regle a ajouter
	 */
	public void addRule(Rule rule) {
		currentRuleSet.add(rule);
	}
	
	/**
	 * Permet d'obtenir les regles actuellement associees a ce noeud
	 * @return Le RuleSet qui contient les regles
	 */
	public Vector<Rule> getRuleSet() {
		return currentRuleSet;
	}
	
	/**
	 * Permet de savoir si ce noeud représente un concept dans le treillis constitué par produit
	 * des treillis interne et externe.
	 * @return Le boolean indiquant si le noeud est un concept du treillis final
	 */
	public boolean isFinalConcept() {
		return isFinalConcept;
	}
	
	/**
	 * Permet d'obtenir la liste des NestedConcept enfants de ce noeud
	 * @return Le Vector contenant la liste des enfants
	 */
	public Vector<NestedConcept> getChildren() {
		return childrenConcepts;
	}
	
	/**
	 * Permet d'ajouter un enfant à ce noeud
	 * @param child Le NestedConcept contenant l'enfant à ajouter
	 */
	public void addChild(NestedConcept child) {
		if (childrenConcepts == null)
			childrenConcepts = new Vector<NestedConcept>();
		
		if (childrenConcepts.indexOf(child) == -1)
			childrenConcepts.add(child);
	}
	
	/**
	 * Permet d'obtenir la liste des NestedConcepts parents de ce noeud
	 * @return Le Vector contenant la liste des parents
	 */
	public Vector<NestedConcept> getParents() {
		return parentConcepts;
	}
	
	/**
	 * Permet d'ajouter un parent à ce noeud
	 * @param parent Le NestedConcept contenant le parent à ajouter
	 */
	public void addParent(NestedConcept parent) {
		if (parent != null)
			if (parentConcepts.indexOf(parent) == -1)
				parentConcepts.add(parent);
	}
	
	/**
	 * Permet d'ajouter un treillis interne
	 * @param newLattice Le CompleteConceptLattice à ajouter comme treillis interne
	 */
	public void addInternalLattice(ConceptLattice newLattice) throws AlreadyExistsException, InvalidTypeException {
		/*
		 * Ajoute le nouveau treillis dans le treillis interne s'il existe, sinon crée le treillis
		 * interne
		 */
		if (internalNestedLattice != null)
			internalNestedLattice.addInternalLattice(newLattice);
		else {
			createInternalNestedLattice();
			/* Crée le treillis imbriqué dans les noeuds enfants */
			for (int i = 0; i < childrenConcepts.size(); i++)
				(childrenConcepts.elementAt(i)).addInternalLattice(newLattice);
		}
	}
	
	/**
	 * Permet de supprimer le dernier treillis interne
	 */
	public void removeInternalLattice() {
		/* Traitement du treillis interne */
		if (internalLattices.size() == 1) {
			internalNestedLattice = null;
		} else if (internalLattices.size() > 1) {
			internalNestedLattice.removeInternalLattice();
		}
		
		/* Traitement des noeuds enfants */
		for (int i = 0; i < childrenConcepts.size(); i++)
			(childrenConcepts.elementAt(i)).removeInternalLattice();
	}
	
	/**
	 * Permet de découvrir les noeuds qui font partie du treillis final, à partir de ce noeud
	 * @param extentList Le Vector contenant l'extention du noeud parent
	 * @return Le Vector contenant la liste des extent rencontrés dans ce noeud
	 */
	public Vector<BasicSet> findFinalConcepts(Vector<BasicSet> extentList) {
		/*
		 * Vérifie si le nouvel Intent calculé pour le noeud correspond aux total des attributs
		 * partagés par les objets de l'Extent dans le contexte global
		 */
		BasicSet globalContextIntent = new BasicSet();
		Vector<String> attributes = globalContext.getAttributes();
		globalContextIntent.addAll(attributes);
		
		Iterator<String> it = newExtent.iterator();
		while (it.hasNext()) {
			String obj = it.next();
			globalContextIntent = globalContextIntent.intersection(globalContext.getAttributesFor(obj));
		}
		boolean completeConcept = globalContextIntent.equals(newIntent);
		
		/*
		 * Vérifie que le nouveau concept est rencontré pour la première fois dans le treillis en
		 * traversant le treillis du bas vers le haut
		 */
		if (completeConcept) {
			for (int i = 0; i < extentList.size(); i++) {
				BasicSet currentExtent = extentList.elementAt(i);
				/*
				 * Des extentions de cardinalité différente sont automatiquement différentes
				 */
				if (currentExtent.size() == newExtent.size()) {
					isFinalConcept = false;
					Iterator<String> iter = newExtent.iterator();
					/*
					 * Le contenu des extentions est comparé seulement si les cardinalités sont
					 * égales
					 */
					while (iter.hasNext())
						if (!currentExtent.contains(iter.next()))
							isFinalConcept = true;
				}
			}
		} else
			isFinalConcept = false;
		
		/*
		 * Si le noeud n'est pas un concept, il ne doit pas contenir de treillis imbriqué
		 */
		if (!isFinalConcept)
			internalNestedLattice = null;
		
		/*
		 * Ajoute les Extent rencontrés dans le treillis interne à la liste des Extents
		 * rencontrés dans ce noeud
		 */
		Vector<BasicSet> newExtentList = new Vector<BasicSet>();
		if (isFinalConcept && internalNestedLattice != null) {
			Vector<BasicSet> findFinal = new Vector<BasicSet>(extentList);
			newExtentList = internalNestedLattice.findFinalConcepts(findFinal);
		}
		
		/* Ajoute l'Extent du concept à la liste des Extents rencontrés dans ce noeud */
		if (!newExtentList.contains(newExtent))
			newExtentList.add(newExtent);
		
		/*
		 * Construit un vecteur contenant les Extents rencontrés dans ce noeud ainsi que ceux
		 * rencontrés avant ce noeud
		 */
		Vector<BasicSet> allExtents = new Vector<BasicSet>(extentList);
		for (int i = 0; i < newExtentList.size(); i++) {
			BasicSet currentNewExtent = newExtentList.elementAt(i);
			
			if (!allExtents.contains(currentNewExtent))
				allExtents.add(currentNewExtent);
		}
		
		/*
		 * Ajoute les Extent rencontrés dans les noeuds parents de ce noeud à la liste des Extent
		 * rencontrés à partir de ce noeud
		 */
		for (int i = 0; i < parentConcepts.size(); i++) {
			Vector<BasicSet> upperExtents = (parentConcepts.elementAt(i)).findFinalConcepts(allExtents);
			for (int j = 0; j < upperExtents.size(); j++) {
				BasicSet currentUpperExtent = upperExtents.elementAt(j);
				if (!newExtentList.contains(currentUpperExtent))
					newExtentList.add(currentUpperExtent);
			}
		}
		
		/* Retourne tous les Extent qui ont été croisés suite au passage dans ce noeud */
		return new Vector<BasicSet>(newExtentList);
	}
	
	/**
	 * Crée l'intent de ce noeud en effectuant l'union des attributs du noeud interne et du noeud
	 * externe
	 */
	private void createNewIntent() {
		if (externalConcept != null)
			newIntent = concept.getIntent().union(externalConcept.getIntent());
		else
			newIntent = (BasicSet) concept.getIntent().clone();
	}
	
	/**
	 * Crée l'extent de ce noeud en effectuant l'intersection des objets du noeud interne et du
	 * noeud externe
	 */
	private void createNewExtent() {
		if (externalConcept != null)
			newExtent = concept.getExtent().intersection(externalConcept.getExtent());
		else
			newExtent = (BasicSet) concept.getExtent().clone();
	}
	
	/**
	 * Crée le NestedConceptLattice qui se trouve à l'intérieur du noeud
	 */
	private void createInternalNestedLattice() throws AlreadyExistsException, InvalidTypeException {
		/*
		 * Un NestedConceptLattice est créé seulement s'il reste des éléments dans la liste des
		 * CompleteConceptLattice donnée initialement
		 */
		if (internalLattices.size() > 0) {
			internalNestedLattice = new NestedLattice(this, internalLattices, globalContext,
					parentNestedLattice.getName());
		} else
			internalNestedLattice = null;
	}
	
	/**
	 * Crée les NestedConcept qui correspondent aux enfants de ce noeud
	 */
	private void createChildrenConcepts() throws AlreadyExistsException, InvalidTypeException {
		Vector<FormalConcept> children = concept.getChildren();
		childrenConcepts = new Vector<NestedConcept>();
		
		if (children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				FormalConcept childConcept = children.elementAt(i);
				/*
				 * Si l'identifiant de l'enfant est reconnu par le treillis, l'enfant a déjà été
				 * créé. On ne fait alors que lui ajouter un parent.
				 */
				NestedConcept child;
				if ((child = parentNestedLattice.getNestedConceptWithIntent(childConcept.getIntent())) != null) {
					child.addParent(this);
					addChild(child);
				}
				/* Si l'enfant n'a jamais été créé, il est créé par ce noeud-ci. */
				else {
					child = new NestedConcept(children.elementAt(i), this, externalConcept, parentNestedLattice,
							internalLattices, globalContext);
					parentNestedLattice.addNestedConcept(child);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = new String("NestedConcept" + " {" + getIntent() + "," + getExtent() + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return result;
	}
	
}
