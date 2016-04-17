package fca.gui.lattice.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fca.gui.lattice.LatticePanel;
import fca.gui.lattice.element.GraphicalConcept;
import fca.gui.lattice.element.GraphicalLattice;
import fca.messages.GUIMessages;

/**
 * Panneau affichant la vue arborescente d'un treillis : chaque concept du treillis y est montr�,
 * plac� sous les noeuds qui le contiennent, dans l'ordre d'imbrication, s'il y a lieu
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class Tree extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8106841785269391252L;

	private GraphicalLattice lattice; //Treillis pour lequel l'arbre est construit
	
	private DefaultTreeModel treeModel; //Mod�le conceptuel de l'arbre
	
	private JTree tree; //Arbre affich�
	/**
	 * inverse="this$0:fca.gui.lattice.PanelTree$TreeGraphNode"
	 */
	private TreePath path; //Chemin pour la racine du mod�le
	
	private GraphicalConcept selectedConcept; //Concept graphique li� au noeud de l'arbre s�lectionn�
	
	private boolean isGlobalLatticeSelected; //Indique si le noeud racine est s�lectionn�
	
	/**
	 * Le viewer qui affiche le treillis
	 */
	LatticePanel viewer;
	
	/**
	 * Constructeur
	 * @param gl Le NestedGraphLattice � repr�senter dans l'arbre
	 */
	public Tree(GraphicalLattice gl, LatticePanel lp) {
		lattice = gl;
		viewer = lp;
		treeModel = null;
		tree = null;
		path = null;
		selectedConcept = null;
		isGlobalLatticeSelected = false;
		
		createTreeModel();
		createTree();
		
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
		add(tree, BorderLayout.WEST);
	}
	
	/**
	 * Permet d'obtenir l'arbre affich� dans ce panneau
	 * @return Le JTree affich� dans ce panneau
	 */
	public JTree getTree() {
		return tree;
	}
	
	/**
	 * Change l'arbre pour repr�senter un nouveau treillis
	 * @param gl Le GraphicalLattice qui doit maintenant �tre r�pr�sent� dans l'arbre
	 */
	public void setNewLattice(GraphicalLattice gl) {
		lattice = gl;
		path = null;
		selectedConcept = null;
		isGlobalLatticeSelected = false;
		
		treeModel = null;
		createTreeModel();
		tree.setModel(treeModel);
		tree.validate();
	}
	
	/**
	 * Permet d'obtenir le concept grpahique repr�sent� par le noeud s�lectionn� de l'arbre
	 * @return Le NestedGraphNode derri�re le noeud s�lectionn� de l'arbre
	 */
	public GraphicalConcept getSelectedNode() {
		return selectedConcept;
	}
	
	/**
	 * Indique si la racine de l'arbre est s�lectionn�e ou non La racine de l'arbre ne contient
	 * aucun concept graphique, il est donc utile de savoir si le noeud s�lectionn� de l'arbre est
	 * la racine ou non avant de demander d'obtenir le concept graphique derri�re le noeud
	 * s�lectionn� de l'arbre.
	 * @return Le boolean indiquand si la racine de l'arbre est s�lectionn�e
	 */
	public boolean isGlobalLatticeSelected() {
		return isGlobalLatticeSelected;
	}
	
	/**
	 * S�lectionne le noeud de l'arbre qui correspond au concept graphique donn�
	 * @param concept Le GraphicalConcept contenant le concept graphique choisi
	 */
	public void selectPathNode(GraphicalConcept concept) {
		selectedConcept = null;
		if (concept != null) {
			Object[] nodes = path.getPath();
			int level = -1;
			DefaultMutableTreeNode conceptNode = null;
			/* Recherche du noeud correspondant au concept graphique choisi */
			for (int i = 0; i < nodes.length; i++) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes[i];
				TreeGraphNode userNode = (TreeGraphNode) treeNode.getUserObject();
				if (userNode.getGraphicalConcept() != null && userNode.getGraphicalConcept().equals(concept)) {
					level = treeNode.getLevel();
					conceptNode = treeNode;
					break;
				}
			}
			/* Construction du chemin jusqu'au noeud cibl� de l'arbre */
			DefaultMutableTreeNode[] selectionPath = new DefaultMutableTreeNode[level + 1];
			DefaultMutableTreeNode currentNode = conceptNode;
			for (int i = level; i >= 0; i--) {
				selectionPath[i] = currentNode;
				currentNode = (DefaultMutableTreeNode) currentNode.getParent();
			}
			
			/* Ouverture de l'arbre pour voir le noeud cibl� et s�lection de ce noeud */
			TreePath selectionTreePath = new TreePath(selectionPath);
			tree.makeVisible(selectionTreePath);
			int selectionRow = tree.getRowForPath(selectionTreePath);
			tree.setSelectionInterval(selectionRow, selectionRow);
		}

		/* Aucun concept graphique re�u => Le noeud racine est s�lectionn� */
		else {
			tree.setSelectionInterval(0, 0);
		}
	}
	
	/**
	 * Cr�e le mod�le d'arbre qui permettra de construire et g�rer l'arbre, � partir des variables
	 * de ce panneau
	 */
	private void createTreeModel() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new TreeGraphNode(null));
		path = new TreePath(top);
		createTreeModelNodes(lattice, top);
		treeModel = new DefaultTreeModel(top);
	}
	
	/**
	 * Cr�e les noeuds du mod�le de l'arbre pour un treillis donn� et � partir d'un noeud d'arbre
	 * donn�
	 * @param currentLattice Le NestedGraphLattice pour lequel les noeuds sont construits
	 * @param parent Le DefaultMutableTreeNode � sous lequel les nouveaux noeuds seront cr��s
	 */
	private void createTreeModelNodes(GraphicalLattice currentLattice, DefaultMutableTreeNode parent) {
		/*
		 * Recherche dans les concepts du treillis pour construire les noeuds des concepts finaux
		 * seulement
		 */
		Vector<GraphicalConcept> latticeNodes = currentLattice.getNodesList();
		for (int i = 0; i < latticeNodes.size(); i++) {
			GraphicalConcept currentConcept = latticeNodes.elementAt(i);
			if (currentConcept.getNestedConcept().isFinalConcept()) {
				DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(new TreeGraphNode(currentConcept));
				parent.add(currentNode);
				path = path.pathByAddingChild(currentNode);
				
				/*
				 * Construction des noeuds des concepts finaux du treillis interne sous le noeud
				 * courant
				 */
				GraphicalLattice internalLattice = currentConcept.getInternalLattice();
				if (internalLattice != null)
					createTreeModelNodes(internalLattice, currentNode);
			}
		}
	}
	
	/**
	 * Construction de l'arbre � partir du mod�le d�j� cr�� appartenant � ce panneau
	 */
	private void createTree() {
		tree = new JTree(treeModel);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		tree.setSelectionPath(path);
		tree.setExpandsSelectedPaths(true);
		tree.addTreeSelectionListener(new LatticeTreeListener());
	}
	
	/**
	 * Objet qui sera plac� derri�re un noeud de l'arbre et qui contient le concept graphique
	 * associ� � chaque noeud ainsi que sa repr�sentation sous forme de cha�ne de caract�re (pour
	 * l'affichage dans l'arbre)
	 */
	private class TreeGraphNode {
		GraphicalConcept concept; //Concept graphique associ� au noeud
		String stringValue; //Valeur � afficher dans l'arbre
		
		/**
		 * Constructeur
		 * @param gc Le GraphicalConcept qui sera repr�sent� par le noeud
		 */
		public TreeGraphNode(GraphicalConcept gc) {
			concept = gc;
			/* Un concept graphique non null prendra l'intention du concept comme valeur � afficher */
			if (gc != null && gc.getNestedConcept().getConcept().getIntent() != null)
				stringValue = concept.getNestedConcept().getConcept().getIntent().toString();
			
			/* Un concept graphique null prendra une cha�ne fixe comme valeur � afficher */
			else
				stringValue = GUIMessages.getString("GUI.globalLattice"); //$NON-NLS-1$
		}
		
		/**
		 * Permet d'obtenir le concept graphique associ� � ce noeud
		 * @return Le GraphicalConcept associ� au noeud
		 */
		public GraphicalConcept getGraphicalConcept() {
			return concept;
		}
		
		/**
		 * Permet d'obtenir la repr�sentation de ce noeud sous forme de cha�ne de caract�res
		 * @return La String contenant la repr�sentation du noeud sous forme de cha�ne de caract�res
		 */
		@Override
		public String toString() {
			return stringValue;
		}
	}
	
	/**
	 * Classe charg�e de traiter les �v�nements de l'arbre
	 */
	private class LatticeTreeListener implements TreeSelectionListener {
		/* ======== TREESELECTIONLISTENER INTERFACE ======== */
		
		/* (non-Javadoc)
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			TreePath currentPath = e.getPath();
			if (lattice.isEditable())
				lattice.setOutOfFocus(true);
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
			TreeGraphNode selectedGraphConcept = (TreeGraphNode) selectedNode.getUserObject();
			
			/* S�lection d'un noeud autre que le noeud racine */
			if (selectedGraphConcept.getGraphicalConcept() != null) {
				selectedConcept = selectedGraphConcept.getGraphicalConcept();
				isGlobalLatticeSelected = false;
			}

			/* Selection du noeud racine */
			else {
				selectedConcept = null;
				isGlobalLatticeSelected = true;
			}
		}
	}
	
}