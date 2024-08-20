package sowbreira.controle;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import sowbreira.MainFrame;
import sowbreira.util.ImportardorExportador;
import sowbreira.util.ObjectCloner;

/**
 * @author Paulo Sobreira
 */
public class RootManager implements Runnable, TreeSelectionListener {
	private MainFrame frame;
	private JTabbedPane tabbedPane;
	private Map rootMap = new HashMap();
	private DefaultMutableTreeNode copyPasteNode;

	public RootManager(MainFrame frame) {
		super();

		this.frame = frame;
		this.tabbedPane = frame.getTabbedPane();

		File[] files = ImportardorExportador.filesInDataDir();
		frame.getProgressBar().setMaximum(files.length);

		for (int i = 0; i < files.length; i++) {
			rootMap.put(files[i].getName(), files[i]);
		}
	}

	public void run() {
		List keys = new ArrayList(rootMap.keySet());
		Collections.sort(keys);

		int progressCont = 0;

		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			File file = (File) rootMap.get(key);
			DefaultMutableTreeNode node = carregarRoot(file);
			rootMap.put(key, node);

			JTree tree = new JTree();
			tree.setCursor(new Cursor(Cursor.HAND_CURSOR));
			tree.setModel(new DefaultTreeModel(node));
			tree.addTreeSelectionListener(this);
			tree.setCellRenderer(new DefaultTreeCellRenderer() {
				public Icon getLeafIcon() {
					return getDefaultClosedIcon();
				}
			});
			tree.addKeyListener(frame);
			tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					frame.getJTreeDetalhe().getSelectionModel()
							.clearSelection();
				}
			});
			tabbedPane.addTab(key, new JScrollPane(tree));
			frame.getProgressBar().setValue(++progressCont);
		}
		if (tabbedPane.getComponentCount() < 1) {
			novaAba();
		}
	}

	public void salvarTudo() {
		frame.getProgressBar().setValue(0);
		frame.getProgressBar().setMaximum(rootMap.keySet().size());
		Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
		frame.setCursor(cur);
		try {

			for (Iterator iter = rootMap.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootMap
						.get(key);
				try {
					ImportardorExportador.salvar(node, key);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "ERRO",
							JOptionPane.ERROR_MESSAGE);
				}
				int value = frame.getProgressBar().getValue();
				frame.getProgressBar().setValue(++value);
			}
			JOptionPane.showMessageDialog(frame, "Salvo com sucesso",
					"Salvar Tudo", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERRO",
					JOptionPane.ERROR_MESSAGE);
		}
		cur = new Cursor(Cursor.DEFAULT_CURSOR);
		frame.setCursor(cur);
	}

	public void salvar() {
		String key = null;
		Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
		frame.setCursor(cur);
		for (Iterator iter = rootMap.keySet().iterator(); iter.hasNext();) {
			key = (String) iter.next();

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootMap
					.get(key);

			if (node == obterRootAtual()) {
				break;
			}
		}

		try {
			ImportardorExportador.salvar(obterRootAtual(), key);
			JOptionPane.showMessageDialog(frame, "Salvo com sucesso", "Salvar",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERRO",
					JOptionPane.ERROR_MESSAGE);
		}
		cur = new Cursor(Cursor.DEFAULT_CURSOR);
		frame.setCursor(cur);
	}

	public JTree obterJTreeAtual() {
		if (tabbedPane.getComponentCount() < 1) {
			JOptionPane.showMessageDialog(null,
					"Não existe JTree selecionada. Verifique nova aba.", "ERRO",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		JScrollPane pane = (JScrollPane) tabbedPane
				.getComponentAt(tabbedPane.getSelectedIndex());

		return (JTree) pane.getViewport().getComponent(0);
	}

	public DefaultMutableTreeNode obterRootAtual() {
		JTree obterJTreeAtual = obterJTreeAtual();
		if (obterJTreeAtual == null) {
			return null;
		}
		return (DefaultMutableTreeNode) obterJTreeAtual.getModel().getRoot();
	}

	public void ordenarRoot() {
		HashMap map = new HashMap();
		Enumeration e = obterRootAtual().children();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
					.nextElement();
			map.put(node.getUserObject().toString(), node);
		}

		List list = new ArrayList(map.keySet());
		Collections.sort(list);
		obterRootAtual().removeAllChildren();

		ArrayList merda = new ArrayList();

		for (Iterator iter = list.iterator(); iter.hasNext();) {
			obterRootAtual().add((DefaultMutableTreeNode) map.get(iter.next()));
		}
		JTree obterJTreeAtual = obterJTreeAtual();
		if (obterJTreeAtual != null) {
			obterJTreeAtual.setModel(new DefaultTreeModel(obterRootAtual()));
		}
	}

	public void renomearNo() {
		JTree obterJTreeAtual = obterJTreeAtual();
		if (obterJTreeAtual == null) {
			return;
		}
		TreePath path = obterJTreeAtual.getSelectionPath();
		TreePath detPath = frame.getJTreeDetalhe().getSelectionPath();
		DefaultMutableTreeNode node = null;
		DefaultTreeModel model = null;
		boolean renomearNoDetalhe = false;

		if (path != null) {
			node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (node == obterRootAtual()) {
				return;
			}

			model = (DefaultTreeModel) obterJTreeAtual.getModel();
		}

		if (detPath != null) {
			node = (DefaultMutableTreeNode) detPath.getLastPathComponent();

			model = (DefaultTreeModel) frame.getJTreeDetalhe().getModel();
			renomearNoDetalhe = true;
		}

		if ((node == null) || (model == null)) {
			return;
		}

		String value = JOptionPane.showInputDialog(frame,
				"Renomear nó selecionado", node.getUserObject());

		if (value == null) {
			return;
		}

		if (renomearNoDetalhe) {
			node.setUserObject(value);
			model.nodeChanged(node);
		} else {
			DefaultMutableTreeNode userObj = (DefaultMutableTreeNode) node
					.getUserObject();
			userObj.setUserObject(value);
			model.nodeChanged(userObj);
		}
		ordenarRoot();
		selecionarNo(obterJTreeAtual, node);
	}

	public DefaultMutableTreeNode carregarRoot(File file) {
		DefaultMutableTreeNode root = null;

		try {
			root = new DefaultMutableTreeNode("Carregando..");
			root = (DefaultMutableTreeNode) ImportardorExportador
					.carregarRoot(root, file);

			if ("Carregando..".equals(root.getUserObject())) {
				root = new DefaultMutableTreeNode(
						"F1 para Carregar Diretorios");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERRO",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		return root;
	}

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		JTree obterJTreeAtual = obterJTreeAtual();
		if (obterJTreeAtual == null) {
			return;
		}

		TreePath path = obterJTreeAtual.getSelectionPath();

		if (path == null) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();

		if (node.getUserObject() instanceof DefaultMutableTreeNode) {
			frame.getJTreeDetalhe().setModel(new DefaultTreeModel(
					(DefaultMutableTreeNode) node.getUserObject()));
		}
	}

	public DefaultMutableTreeNode apagarNo() {
		JTree tree = obterJTreeAtual();
		if (tree == null) {
			return null;
		}
		TreePath geralPath = tree.getSelectionPath();
		TreePath detPath = frame.getJTreeDetalhe().getSelectionPath();
		DefaultMutableTreeNode node = null;

		if ((geralPath != null) && (detPath == null)) {
			node = (DefaultMutableTreeNode) geralPath.getLastPathComponent();

			if (node == tree.getModel().getRoot()) {
				return null;
			}

			frame.getJTreeDetalhe().setModel(new DefaultTreeModel(
					new DefaultMutableTreeNode("Conteudo do diretorio")));

			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.removeNodeFromParent(node);
		}

		if (detPath != null) {
			node = (DefaultMutableTreeNode) detPath.getLastPathComponent();

			DefaultTreeModel model = (DefaultTreeModel) frame.getJTreeDetalhe()
					.getModel();
			model.removeNodeFromParent(node);
		}

		return node;
	}

	public void escolherDiretorio() {
		if (!frame.getEscolherDiretorioMenu().isEnabled()) {
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = fileChooser.showOpenDialog(frame);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		final File dirRaiz = fileChooser.getSelectedFile();
		Thread parser = new Thread(new Runnable() {
			public void run() {
				try {
					frame.getEscolherDiretorioMenu().setEnabled(false);
					JTree obterJTreeAtual = obterJTreeAtual();
					if (obterJTreeAtual == null) {
						return;
					}
					TreeParser treeParser = new TreeParser(
							frame.getProgressBar(),
							(DefaultTreeModel) obterJTreeAtual.getModel(),
							dirRaiz);
					DefaultMutableTreeNode node = treeParser.inserirNovoNo(
							dirRaiz,
							(DefaultTreeModel) obterJTreeAtual.getModel());
					ordenarRoot();
					selecionarNo(obterJTreeAtual, node);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Erro no parse");
				} finally {
					frame.getEscolherDiretorioMenu().setEnabled(true);

				}
			}
		});
		parser.start();
	}

	public void procurarArquivo() {
		String value = JOptionPane.showInputDialog(frame, "Procurar Por");

		if (value == null) {
			return;
		}

		JTree obterJTreeAtual = obterJTreeAtual();

		if (obterJTreeAtual == null) {
			return;
		}

		DefaultTreeModel geralmodel = (DefaultTreeModel) obterJTreeAtual
				.getModel();
		TreePath path = obterJTreeAtual.getSelectionPath();
		DefaultMutableTreeNode node = null;

		if (path != null) {
			node = (DefaultMutableTreeNode) path.getLastPathComponent();
		}

		TreeFinder finder = new TreeFinder(geralmodel, value, node);
		Map resultados = finder.getResultados();
		List resultList = finder.getKeySetsOrdenados();
		int resultadosCont = finder.contarOcorrencias(resultados);
		boolean nTime = false;
		JFrame dummyFrame = new JFrame();
		dummyFrame.setVisible(true);
		for (int i = 0; i < resultList.size(); i++) {
			DefaultMutableTreeNode geralNode = (DefaultMutableTreeNode) resultList
					.get(i);
			List noRes = (List) resultados.get(geralNode);

			for (int j = 0; j < noRes.size(); j++) {
				if (nTime) {
					if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
							dummyFrame,
							"Mais " + (--resultadosCont)
									+ " Ocorrencias, Proximo?",
							"Proxima subpasta : " + geralNode.toString(),
							JOptionPane.YES_NO_OPTION)) {
						dummyFrame.dispose();
						return;
					}
				}

				DefaultMutableTreeNode no = (DefaultMutableTreeNode) noRes
						.get(j);
				selecionarNo(obterJTreeAtual, geralNode);
				selecionarNo(frame.getJTreeDetalhe(), no);
				nTime = true;
			}
		}
		dummyFrame.dispose();
	}

	private void selecionarNo(JTree tree, DefaultMutableTreeNode node) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		TreePath path = new TreePath(model.getPathToRoot(node));
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
	}

	public void importar() {
		ImportardorExportador importardorExportador = new ImportardorExportador();

		try {
			if (tabbedPane.getComponentCount() < 1) {
				JOptionPane.showMessageDialog(null,
						"Para executar esta operção crie uma aba antes", "ERRO",
						JOptionPane.ERROR_MESSAGE);

			} else {
				JTree obterJTreeAtual = obterJTreeAtual();
				if (obterJTreeAtual == null) {
					return;
				}
				importardorExportador.importar(obterRootAtual(),
						obterJTreeAtual);
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage(), "ERRO",
					JOptionPane.ERROR_MESSAGE);

			e1.printStackTrace();
		}
	}

	public void exportar() {
		ImportardorExportador importardorExportador = new ImportardorExportador();

		try {
			importardorExportador.exportar(obterRootAtual());
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), "ERRO",
					JOptionPane.ERROR_MESSAGE);

			e1.printStackTrace();
		}
	}

	public void novaAba() {
		String key = JOptionPane.showInputDialog(frame,
				"Nome do Arquivo raiz a ser criado?"
						+ "(Mesmo nome que aparecerá na aba)");
		if (key == null || "".equals(key)) {
			JOptionPane.showMessageDialog(null,
					"Nome da aba não pode ser vazio", "ERRO",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(
				"F1 para Carregar Diretorios");
		rootMap.put(key, node);

		JTree tree = new JTree();
		tree.setCursor(new Cursor(Cursor.HAND_CURSOR));
		tree.setModel(new DefaultTreeModel(node));
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Icon getLeafIcon() {
				return getDefaultClosedIcon();
			}
		});
		tree.addKeyListener(frame);
		tabbedPane.addTab(key, new JScrollPane(tree));
	}

	public void copiarNo() {
		JTree obterJTreeAtual = obterJTreeAtual();
		if (obterJTreeAtual == null) {
			return;
		}
		TreePath geralPath = obterJTreeAtual.getSelectionPath();
		TreePath detPath = frame.getJTreeDetalhe().getSelectionPath();
		DefaultMutableTreeNode node = null;

		if ((geralPath != null) && (detPath == null)) {
			node = (DefaultMutableTreeNode) geralPath.getLastPathComponent();

			if (node == obterRootAtual()) {
				return;
			}
		}

		if (detPath != null) {
			node = (DefaultMutableTreeNode) detPath.getLastPathComponent();
		}

		try {
			copyPasteNode = (DefaultMutableTreeNode) ObjectCloner
					.deepCopy(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recortarNo() {
		try {
			copyPasteNode = (DefaultMutableTreeNode) ObjectCloner
					.deepCopy(apagarNo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void colarNo() {
		JTree tree = obterJTreeAtual();
		if (tree == null) {
			return;
		}
		TreePath geralPath = tree.getSelectionPath();
		TreePath detPath = frame.getJTreeDetalhe().getSelectionPath();
		DefaultMutableTreeNode node = null;

		if (copyPasteNode == null) {
			return;
		}

		if ((geralPath != null) && (detPath == null)) {
			node = (DefaultMutableTreeNode) geralPath.getLastPathComponent();

			if (node != tree.getModel().getRoot()) {
				return;
			}

			try {
				DefaultMutableTreeNode incomNode = (DefaultMutableTreeNode) ObjectCloner
						.deepCopy(copyPasteNode);
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				model.insertNodeInto(incomNode, node, 0);
				ordenarRoot();
				selecionarNo(tree, node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (detPath != null) {
			node = (DefaultMutableTreeNode) detPath.getLastPathComponent();

			try {
				String mesg = "Colar na arvore de conteudo causará incosistencia\n"
						+ "quanto ao tamanho do diretório mostrado na \n"
						+ "arvore pincipal! Desja continuar?";

				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
						frame, mesg, "Colar na arvode de conteudo.",
						JOptionPane.YES_NO_OPTION)) {
					DefaultMutableTreeNode addNode = (DefaultMutableTreeNode) ObjectCloner
							.deepCopy(copyPasteNode);
					DefaultTreeModel model = (DefaultTreeModel) frame
							.getJTreeDetalhe().getModel();

					/**
					 * Caso tenho copiado um diretorio todo (o user Objec são os
					 * nos filhos)
					 */
					if (addNode
							.getUserObject() instanceof DefaultMutableTreeNode) {
						addNode = (DefaultMutableTreeNode) addNode
								.getUserObject();

						Enumeration enumeration = addNode.children();

						while (enumeration.hasMoreElements()) {
							DefaultMutableTreeNode element = (DefaultMutableTreeNode) enumeration
									.nextElement();
							addNode.add(element);
						}
					}

					model.insertNodeInto(addNode, node, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
