package fca.core.lattice;

import java.util.Vector;

import fca.core.context.binary.BinaryContext;
import fca.core.rule.Rule;
import fca.core.util.BasicSet;
import fca.exception.AlreadyExistsException;
import fca.exception.InvalidTypeException;

/**
 * Structure d'un diagramme imbrique (NLD)
 * @author Geneviève Roberge
 * @version 1.0
 */
public class NestedLattice {
	
	/* Constante pour le format des composantes */
	// public static double NODE_SIZE = 1.8; //Diamète d'un noeud avant grossissement
	private ConceptLattice lattice; // Le treillis plat représenté dans ce NestedLattice
	private Vector<ConceptLattice> internalLattices; // La liste ordonnée des ConceptLattice imbriquées dans ce treillis
	
	private NestedConcept externalNestedConcept; // Le NestedConcept qui contient ce treillis
	
	private Vector<BasicSet> externalParentIntents; // La liste ordonnée des identifiant des concepts externes contenant ce treillis
	
	private NestedConcept topNestedConcept; // Le NestedConcept au sommet (source) de ce treillis
	
	private NestedConcept bottomNestedConcept; // Le NestedConcept au bas (sink) de ce treillis
	
	private BasicSet latticeAttributes; // La liste des attributs de ce niveau d'imbrication
	
	private BinaryContext globalContext; // Le contexte binaire contenant les relations des treillis externes et de ce treillis
	/**
	 * inverse="internalNestedLattice:fca.lattice.conceptual.NestedConcept"
	 */
	private Vector<NestedConcept> nestedConceptsList; // La liste des NestedConcepts inclus dans ce treillis
	
	private int nestedLevel; // Le niveau d'imbrication du treillis
	
	private String name; // Le nom du treillis
	private Vector<Rule> rules;
	
	/**
	 * Constructeur
	 * @param nc Le NestedConcept dans lequel est imbriqué ce treillis
	 * @param il La liste ordonné des ConceptLattice imbriqués à partir ce celui représenté dans ce
	 *        treillis
	 * @param bc Le contexte binaire contenant les contextes de tous les treillis externes
	 * @param n La String contenant le nom donné à ce treillis
	 */
	public NestedLattice(NestedConcept nc, Vector<ConceptLattice> il, BinaryContext bc, String n) throws AlreadyExistsException, InvalidTypeException {
		externalNestedConcept = nc;
		if (externalNestedConcept != null)
			nestedLevel = externalNestedConcept.getNestedLevel() + 1;
		else
			nestedLevel = 0;
		name = n;
		
		if (il.size() > 0) {
			/* Séparation du treillis courant et des treillis internes */
			lattice = il.elementAt(0);
			internalLattices = new Vector<ConceptLattice>();
			internalLattices.addAll(il);
			internalLattices.removeElementAt(0);
			BinaryContext context = lattice.getContext();
			globalContext = (BinaryContext) context.clone();
			if (bc != null)
				globalContext.addBinaryContext(bc, false);
			
			Vector<String> attributeList = context.getAttributes();
			latticeAttributes = new BasicSet();
			latticeAttributes.addAll(attributeList);
		} else {
			this.lattice = null;
			this.internalLattices = null;
			this.globalContext = (BinaryContext) bc.clone();
		}
		
		/* Construction de la liste des identifiants des noeuds externes parents */
		externalParentIntents = new Vector<BasicSet>();
		if (externalNestedConcept != null) {
			externalParentIntents.addAll(externalNestedConcept.getParentLattice().getExternalParentIntents());
			externalParentIntents.add(externalNestedConcept.getConcept().getIntent());
		}
		
		/* Construction des noeud du treillis */
		this.nestedConceptsList = new Vector<NestedConcept>();
		createNestedConcepts();
		
		/*
		 * Recherche des concepts faisant partie du treillis final (produit des treillis imbriqués)
		 * et contruction des extentions et intentions réduites
		 */
		if (externalNestedConcept == null) {
			findFinalConcepts(new Vector<BasicSet>());
			setReducedLabelling();
		}
		
		rules = new Vector<Rule>();
	}
	
	/**
	 * Permet d'obtenir le nom du treillis imbriqué
	 * @return La String contenant le nom du treillis
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Permet d'obtenir le treillis conceptuel relié à ce niveau du treillis imbriqué
	 * @return Le ConceptLattice représenté dans ce niveau du treillis imbriqué
	 */
	public ConceptLattice getConceptLattice() {
		return lattice;
	}
	
	/**
	 * Ajoute un treillis au plus profond de l'arbre d'imbrication de treillis
	 * @param newLattice Le ConceptLattice à ajouter au bas de l'arbre d'imbrication de treillis
	 */
	public void addInternalLattice(ConceptLattice newLattice) throws AlreadyExistsException, InvalidTypeException {
		internalLattices.add(newLattice);
		topNestedConcept.addInternalLattice(newLattice);
	}
	
	/**
	 * Retire le dernier treillis imbriqué
	 */
	public void removeInternalLattice() {
		topNestedConcept.removeInternalLattice();
		
		if (internalLattices.size() > 0)
			internalLattices.removeElementAt(internalLattices.size() - 1);
	}
	
	/**
	 * Permet d'obtenir la liste ordonnée des identifiants des noeuds externes
	 * @return Le Vector contenant la liste ordonnée des identifiants des noeuds externes
	 */
	public Vector<BasicSet> getExternalParentIntents() {
		return externalParentIntents;
	}
	
	/**
	 * Permet d'obtenir le noeud au sommet du treillis
	 * @return Le NestedConcept au sommet du treillis
	 */
	public NestedConcept getTopNestedConcept() {
		return topNestedConcept;
	}
	
	/**
	 * Permet d'obtenir le noeud au bas du treillis
	 * @return Le NestedConcept au bas du treillis
	 */
	public NestedConcept getBottomNestedConcept() {
		return bottomNestedConcept;
	}
	
	/**
	 * Permet d'obtenir la liste des noeuds du treillis
	 * @return Le Vector contenant les NestedConcept du treillis
	 */
	public Vector<NestedConcept> getNestedConceptsList() {
		return nestedConceptsList;
	}
	
	/**
	 * Permet d'obtenir tous les treillis imbriqués dans ce treillis
	 * @return Le Vector content les treillis imbriqués dans ce treillis
	 */
	public Vector<ConceptLattice> getInternalLattices() {
		return internalLattices;
	}
	
	/**
	 * Permet d'enlever les r?les dans tous les noeuds du treillis
	 */
	public void clearRules() {
		for (int i = 0; i < nestedConceptsList.size(); i++) {
			NestedConcept nc = nestedConceptsList.elementAt(i);
			nc.clearRuleSet();
			
			if (nc.getInternalNestedLattice() != null)
				nc.getInternalNestedLattice().clearRules();
		}
	}
	
	/**
	 * Permet d'assigner les r?les aux noeuds du treillis
	 * @param r Le RuleSet contenant les regles a assigner dans le treillis
	 */
	public void setRules(Vector<Rule> r) {
		clearRules();
		rules = new Vector<Rule>();
		rules.addAll(r);
		for (int i = 0; i < rules.size(); i++) {
			Rule currRule = rules.elementAt(i);
			BasicSet intent = currRule.getAntecedent().union(currRule.getConsequence());
			
			NestedConcept nc = getNestedConceptWithIntent(intent);
			if (nc != null)
				nc.addRule(currRule);
		}
	}
	
	public Vector<Rule> getRules() {
		return rules;
	}
	
	/**
	 * Permet d'obtenir le concept imbriqué qui possède l'intention spécifiée. Retourne null si
	 * aucun concept n'est trouvé. Version améliorée par recherche via opérateur de recherche
	 * @param intent Le BasicSet contenant l'intent recherché
	 * @return Le NestedConcept qui possède l'intention spécifiée
	 */
	public NestedConcept getNestedConceptWithIntent(BasicSet intent) {
		
		/* Le treillis ne possede aucun concept */
		if (topNestedConcept == null)
			return null;
		
		/* Chaque concept du treillis a plus d'attributs que ceux recherches */
		BasicSet topIntent = topNestedConcept.getConcept().getIntent();
		if (intent.size() < topIntent.size() || (intent.size() == topIntent.size() && !intent.containsAll(topIntent)))
			return null;
		
		BasicSet intersection = latticeAttributes.intersection(intent);
		
		/* Le concept recherche est le suprémum */
		if (intent.size() == intersection.size() && intent.size() == topIntent.size())
			return topNestedConcept;
		
		/* Le concept recherche est dans un treillis interne du suprémum */
		else if (intersection.size() == topNestedConcept.getConcept().getIntent().size()
				&& intersection.size() < intent.size() && topNestedConcept.getInternalNestedLattice() != null) {
			BasicSet remainingAtt = intent.difference(intersection);
			return topNestedConcept.getInternalNestedLattice().getNestedConceptWithIntent(remainingAtt);
		}

		/* Le concept recherche n'est pas le suprémum, ni dans le suprémum */
		else if (intersection.size() > 0) {
			/* Recherche du noeud dans le niveau courant */
			NestedConcept currentConcept = topNestedConcept;
			BasicSet currentIntent = currentConcept.getConcept().getIntent();
			BasicSet currentInter = intersection.intersection(currentIntent);
			BasicSet globalRemainder = intent.difference(latticeAttributes);
			BasicSet levelRemainder = intersection.difference(currentIntent);
			
			while (currentConcept != null && currentInter.size() != intersection.size()
					&& currentInter.size() == currentIntent.size()) {
				
				Vector<NestedConcept> children = currentConcept.getChildren();
				currentConcept = null;
				
				for (int i = 0; i < children.size() && currentConcept == null; i++) {
					NestedConcept child = children.elementAt(i);
					BasicSet childIntent = child.getConcept().getIntent();
					BasicSet childInter = intersection.intersection(childIntent);
					BasicSet childRemainder = childIntent.difference(childInter);
					
					if (childInter.size() > 0 && childRemainder.size() == 0) {
						currentConcept = child;
						currentIntent = currentConcept.getConcept().getIntent();
						currentInter = intersection.intersection(currentIntent);
						levelRemainder = intersection.difference(currentIntent);
					}
				}
			}
			
			/*
			 * Il y a trop d'attributs dans le premier concept trouvé qui contient tous les
			 * attributs demandes
			 */
			if (currentInter.size() != currentIntent.size())
				return null;
			
			/*
			 * Les attributs du niveau courant ont été trouvés et il ne reste pas d'attributs pour
			 * les niveaux internes
			 */
			if (levelRemainder.size() == 0 && globalRemainder.size() == 0)
				return currentConcept;
			
			/*
			 * Les attributs du niveau courant ont été trouvés et il reste des attributs à chercher
			 * dans les niveaux internes
			 */
			if (levelRemainder.size() == 0 && currentConcept.getInternalNestedLattice() != null)
				return currentConcept.getInternalNestedLattice().getNestedConceptWithIntent(globalRemainder);
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir le contexte entier qui est traité jusqu'à ce treillis
	 * @return Le BinaryContext complète qui est traitée jusqu'à ce treillis
	 */
	public BinaryContext getGlobalContext() {
		return (BinaryContext) globalContext.clone();
	}
	
	/**
	 * Permet d'obtenir le niveau d'imbrication du treillis
	 * @return Le int contenant le niveau d'imbrication du treillis
	 */
	public int getNestedLevel() {
		return nestedLevel;
	}
	
	/**
	 * Permet d'obtenir le NestedConcept qui contient ce treillis
	 * @return Le NestedConcept externe de ce treillis
	 */
	public NestedConcept getExternalNestedConcept() {
		return externalNestedConcept;
	}
	
	/**
	 * Permet d'ajouter un NestedConcept dans ce treillis
	 * @param nc Le NestedConcept à ajouter au treillis
	 */
	public void addNestedConcept(NestedConcept nc) {
		nestedConceptsList.add(nc);
	}
	
	/**
	 * Permet d'ajuster le noeud au sommet du treillis
	 * @param top Le NestedConcept au sommet du treillis
	 */
	public void setTopNestedConcept(NestedConcept top) {
		topNestedConcept = top;
	}
	
	/**
	 * Crée un NestedConcept pour chacun des concepts du CompleteConceptLattice représenté dans ce
	 * treillis
	 */
	private void createNestedConcepts() throws AlreadyExistsException, InvalidTypeException {
		/*
		 * Le concept du sommet va se charger de créer chacun de ses enfants qui eux créeront leurs
		 * enfants
		 */
		FormalConcept topConcept = lattice.getTopConcept();
		new NestedConcept(topConcept, null, externalNestedConcept, this, internalLattices, globalContext);
		nestedConceptsList.add(topNestedConcept);
		
		/* Recherche du noeud au bas (sink) du treillis */
		NestedConcept currentConcept = topNestedConcept;
		Vector<NestedConcept> currentChildren = currentConcept.getChildren();
		while (currentChildren != null && currentChildren.size() > 0) {
			currentConcept = currentChildren.elementAt(0);
			currentChildren = currentConcept.getChildren();
		}
		bottomNestedConcept = currentConcept;
	}
	
	/**
	 * Indique aux noeuds qui sont des concepts du treillis final, qu'ils sont finaux.
	 * @param extentList Le Vecteur contenant la liste des Extent rencontrés jusqu'à ce treillis
	 * @return Le Vector contenant les Extent rencontrés dans ce treillis
	 */
	public Vector<BasicSet> findFinalConcepts(Vector<BasicSet> extentList) {
		/*
		 * Comme les concepts finaux sont les plus bas concepts ayant une intention/extension
		 * donnée, la recherche est faite du bas vers le haut
		 */
		return bottomNestedConcept.findFinalConcepts(new Vector<BasicSet>(extentList));
	}
	
	/**
	 * Indique aux noeuds qui sont des concepts du treillis final, quel sont leur intent réduit et
	 * leur extent réduit.
	 */
	public void setReducedLabelling() {
		/* Recherche du bas vers le haut pour la première occurence des objets */
		bottomNestedConcept.setReducedExtent(new BasicSet());
		/* Recherche du haut vers le bas pour la première occurence des attributs */
		topNestedConcept.setReducedIntent(new BasicSet());
	}
}
