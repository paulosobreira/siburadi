package sowbreira.controle;

import java.io.File;

import javax.swing.JProgressBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author Paulo Sobreira Criado Em 10:49:47
 */
public class TreeParser {
	private int contadorArquivos;
	private long selecaoTamanho;
	private int cont;
	private javax.swing.JProgressBar progress;

	/**
	 * @param frame
	 * @param dirRaiz
	 */
	public TreeParser(JProgressBar jProgressBar1,
			DefaultTreeModel modelodestino, File dirRaiz) {
		super();
		this.progress = jProgressBar1;
		int size = dirRaiz.listFiles().length;
		progress.setMaximum(size);
	}

	public DefaultMutableTreeNode inserirNovoNo(File dirRaiz,
			DefaultTreeModel modelodestino) {
		int size = dirRaiz.listFiles().length;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				dirRaiz.getName());
		filtraJar(dirRaiz, root);
		root.setUserObject(
				dirRaiz.getName() + " - " + formataTamanho(selecaoTamanho));
		DefaultMutableTreeNode novoNo = new DefaultMutableTreeNode(
				dirRaiz.getName());
		novoNo.setUserObject(root);

		DefaultMutableTreeNode superRoot = (DefaultMutableTreeNode) modelodestino
				.getRoot();
		modelodestino.insertNodeInto(novoNo, superRoot,
				superRoot.getChildCount());
		progress.setValue(size);
		return novoNo;
	}

	public void filtraJar(File aFile, DefaultMutableTreeNode node) {
		File[] files = aFile.listFiles();
		progress.setValue(cont++);
		for (int i = 0; i < files.length; i++)
			if (files[i].isFile()) {
				String name = files[i].getName();
				long t = files[i].length();
				selecaoTamanho += t;
				contadorArquivos++;

				DefaultMutableTreeNode arquivoAtual = new DefaultMutableTreeNode(
						name + " - " + formataTamanho(t));
				node.add(arquivoAtual);
			} else {
				String name = files[i].getName();
				DefaultMutableTreeNode diretorioAtual = new DefaultMutableTreeNode(
						name);
				node.add(diretorioAtual);
				filtraJar(files[i], diretorioAtual);
			}

		return;
	}

	public String formataTamanho(long t) {
		String tamanho = "";

		if (t <= 1024) {
			tamanho = t + "B";
		}

		if (t > 1024) {
			tamanho = (t / 1024) + "KB";
		}

		if (t > 1048576) {
			tamanho = (t / 1048576) + "MB";
		}

		return tamanho;
	}
}
