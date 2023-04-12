package sowbreira;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import sowbreira.controle.RootManager;

/**
 *
 * @author Paulo Sobreira Created on 26 de Maio de 2005, 10:12
 */
public class MainFrame extends javax.swing.JFrame implements KeyListener {
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenu menuInicial;
	private javax.swing.JMenu menuSobre;
	private javax.swing.JMenu menuAvancado;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem escolherDiretorioMenu;
	private javax.swing.JMenuItem salvar;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JTree jTreeDetalhe;
	private DefaultTreeModel model;
	private JTabbedPane tabbedPane;
	private RootManager manager;

	/**
	 * Creates new form MainFrame
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public MainFrame() throws IOException, ClassNotFoundException {
		setTitle("SIBURADI - Sistema de busca rapida em diretorios");
		initComponents();

		manager = new RootManager(this);
		tabbedPane.addKeyListener(this);

		Thread starter = new Thread(manager);
		starter.start();
		jSplitPane1.setDividerLocation(400);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	public javax.swing.JMenuItem getEscolherDiretorioMenu() {
		return escolherDiretorioMenu;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	private void menuItens() {
		menuBar = new javax.swing.JMenuBar();
		menuInicial = new javax.swing.JMenu();
		escolherDiretorioMenu = new javax.swing.JMenuItem();
		menuSobre = new javax.swing.JMenu();
		menuInicial.setText("Menu");

		JMenuItem novaAba = new JMenuItem("Nova Aba");

		novaAba.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.novaAba();
			}
		});

		menuInicial.add(novaAba);

		escolherDiretorioMenu.setText("Carregar Diretorio - F1");
		menuInicial.add(escolherDiretorioMenu);
		menuAvancado = new javax.swing.JMenu();
		escolherDiretorioMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.escolherDiretorio();
			}
		});

		JMenuItem renomearDiretorio = new JMenuItem("Renomear Diretorio - F2");
		renomearDiretorio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.renomearNo();
			}
		});
		menuInicial.add(renomearDiretorio);

		menuBar.add(menuInicial);
		setJMenuBar(menuBar);

		JMenuItem procurarArquivo = new JMenuItem("Procurar - F3");
		procurarArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.procurarArquivo();
			}
		});
		menuInicial.add(procurarArquivo);

		salvar = new javax.swing.JMenuItem();
		salvar.setText("Salvar - F4");
		salvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.salvar();
			}
		});

		menuInicial.add(salvar);

		JMenuItem ordenarRoot = new JMenuItem("Ordenar - F5");
		ordenarRoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.ordenarRoot();
			}
		});
		menuInicial.add(ordenarRoot);

		JMenuItem salvarTudo = new JMenuItem("Salvar Tudo - F6");
		salvarTudo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.salvarTudo();
			}
		});
		menuInicial.add(salvarTudo);

		menuSobre.setText("Sobre");

		JMenu editar = new JMenu("Editar");
		JMenuItem copiar = new JMenuItem("Copiar");
		copiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.copiarNo();
			}
		});

		JMenuItem colar = new JMenuItem("Colar");
		colar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.colarNo();
			}
		});

		JMenuItem recortar = new JMenuItem("Recortar");
		recortar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.recortarNo();
			}
		});
		editar.add(recortar);
		editar.add(copiar);
		editar.add(colar);
		menuBar.add(editar);

		JMenuItem sobre = new JMenuItem("Autor");
		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "Feito por Paulo Sobreira \n"
						+ "sowbreira@gmail.com \n"
						+ "https://sowbreira-26fe1.firebaseapp.com/ \n"
						+ "Julho de 2005 - Abril 2023";
				JOptionPane.showMessageDialog(MainFrame.this, msg, "Sobre",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuSobre.add(sobre);

		menuSobre.setText("Sobre");

		menuAvancado.setText("Avançado");

		JMenuItem importar = new JMenuItem("Importar");
		importar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.importar();
			}
		});

		JMenuItem exportar = new JMenuItem("Exportar");

		exportar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.exportar();
			}
		});

		menuAvancado.add(importar);
		menuAvancado.add(exportar);
		menuBar.add(menuAvancado);

		/*
		 * Sobre so no final :P
		 */
		menuBar.add(menuSobre);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() { // GEN-BEGIN:initComponents
		jPanel1 = new javax.swing.JPanel();
		jPanel1.addKeyListener(this);
		jSplitPane1 = new javax.swing.JSplitPane();
		jSplitPane1.addKeyListener(this);
		jScrollPane2 = new javax.swing.JScrollPane();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTreeDetalhe = new javax.swing.JTree();
		jTreeDetalhe.addKeyListener(this);
		jScrollPane3 = new javax.swing.JScrollPane();
		jPanel2 = new javax.swing.JPanel();
		jPanel2.addKeyListener(this);
		jScrollPane4 = new javax.swing.JScrollPane();
		progressBar = new javax.swing.JProgressBar();

		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		menuItens();
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		addKeyListener(this);
		jPanel1.setLayout(new java.awt.BorderLayout());

		tabbedPane = new JTabbedPane();
		tabbedPane.setDoubleBuffered(true);
		tabbedPane.setName("");
		tabbedPane.setAutoscrolls(true);
		jSplitPane1.setLeftComponent(tabbedPane);
		jTreeDetalhe.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Conteudo do diretório")));
		jSplitPane1.setRightComponent(new JScrollPane(jTreeDetalhe));

		jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel2.add(jScrollPane4, java.awt.BorderLayout.CENTER);

		jPanel2.add(progressBar, java.awt.BorderLayout.SOUTH);

		jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

	} // GEN-END:initComponents

	/**
	 *
	 */
	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt) { // GEN-FIRST:event_exitForm
	} // GEN-LAST:event_exitForm

	public static void main(String[] args)
			throws IOException, ClassNotFoundException {
		new MainFrame();
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	public javax.swing.JTree getJTreeDetalhe() {
		return jTreeDetalhe;
	}

	public javax.swing.JProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		int keyCoode = e.getKeyCode();

		if (keyCoode == KeyEvent.VK_DELETE) {
			manager.apagarNo();
		}

		if (keyCoode == KeyEvent.VK_F1) {
			manager.escolherDiretorio();
		}

		if (keyCoode == KeyEvent.VK_F2) {
			manager.renomearNo();
		}

		if (keyCoode == KeyEvent.VK_F3) {
			manager.procurarArquivo();
		}

		if (keyCoode == KeyEvent.VK_F4) {
			manager.salvar();
		}

		if (keyCoode == KeyEvent.VK_F5) {
			manager.ordenarRoot();
		}

		if (keyCoode == KeyEvent.VK_F6) {
			manager.salvarTudo();
		}

		if (e.isControlDown() && (keyCoode == KeyEvent.VK_C)) {
			manager.copiarNo();
		}

		if (e.isControlDown() && (keyCoode == KeyEvent.VK_X)) {
			manager.recortarNo();
		}

		if (e.isControlDown() && (keyCoode == KeyEvent.VK_V)) {
			manager.colarNo();
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	// End of variables declaration//GEN-END:variables
}
