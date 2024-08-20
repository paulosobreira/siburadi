package sowbreira.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author Paulo Sobreira
 * 
 */
public class ImportardorExportador {
	/**
	 * @param root
	 * @param treeGeral
	 * @throws Exception
	 */
	public void importar(DefaultMutableTreeNode root, JTree treeGeral)
			throws Exception {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		final File arqImportar = fileChooser.getSelectedFile();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) carregarArquivoZip(arqImportar);

		while (node.children().hasMoreElements()) {
			DefaultMutableTreeNode element = (DefaultMutableTreeNode) node
					.children().nextElement();
			root.add(element);
		}

		DefaultTreeModel model = new DefaultTreeModel(root);
		treeGeral.setModel(model);
	}

	private Object carregarArquivoZip(File arqImportar) throws IOException,
			ClassNotFoundException {
		ZipInputStream zin = new ZipInputStream(
				new FileInputStream(arqImportar));
		zin.getNextEntry();
		ByteArrayOutputStream arrayDinamico = new ByteArrayOutputStream();
		int byt = zin.read();

		while (-1 != byt) {
			arrayDinamico.write(byt);
			byt = zin.read();
		}

		arrayDinamico.flush();

		ByteArrayInputStream bin = new ByteArrayInputStream(arrayDinamico
				.toByteArray());
		XMLDecoder decoder = new XMLDecoder(bin);
		return decoder.readObject();

		// ZipInputStream zin = new ZipInputStream(
		// new FileInputStream(arqImportar));
		// ZipEntry entry = zin.getNextEntry();
		// ByteArrayOutputStream arrayDinamico = new ByteArrayOutputStream();
		// int byt = zin.read();
		//
		// while (-1 != byt) {
		// arrayDinamico.write(byt);
		// byt = zin.read();
		// }
		//
		// arrayDinamico.flush();
		//
		// ByteArrayInputStream bin = new ByteArrayInputStream(arrayDinamico
		// .toByteArray());
		// ObjectInputStream ois = new ObjectInputStream(bin);
		//
		// zin.close();
		//
		// return ois.readObject();
	}

	public void exportar(DefaultMutableTreeNode root) throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		final File arqExportar = fileChooser.getSelectedFile();

		ziparArquivo(arqExportar, root);
	}

	public void ziparArquivo(File arqExportar, DefaultMutableTreeNode root)
			throws IOException {
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
		encoder.writeObject(root);
		encoder.flush();
		ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(arqExportar));
		ZipEntry entry = new ZipEntry(arqExportar.getName());
		zipOutputStream.putNextEntry(entry);
		zipOutputStream.write(byteArrayOutputStream.toByteArray());
		zipOutputStream.closeEntry();
		zipOutputStream.close();

		// ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// ObjectOutputStream oos = new ObjectOutputStream(bos);
		//
		// oos.writeObject(root);
		// oos.flush();
		//
		// FileOutputStream fileOutputStream = new
		// FileOutputStream(arqExportar);
		//
		// ZipOutputStream zipOutputStream = new
		// ZipOutputStream(fileOutputStream);
		// ZipEntry entry = new ZipEntry("root");
		// zipOutputStream.putNextEntry(entry);
		// zipOutputStream.write(bos.toByteArray());
		// zipOutputStream.closeEntry();
		// zipOutputStream.close();
	}

	public static void salvar(final DefaultMutableTreeNode root,
			final boolean sair) throws IOException {
		final ImportardorExportador ie = new ImportardorExportador();
		Thread bgSave = new Thread(new Runnable() {
			public void run() {
				try {
					ie.gravarArquivo(new File("data/root"), root);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "ERRO",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} finally {
					if (sair) {
						System.exit(0);
					}
				}
			}
		});
		bgSave.start();
	}

	public static void salvar(final DefaultMutableTreeNode root)
			throws IOException {
		salvar(root, false);
	}

	/**
	 * @param root
	 * @throws IOException
	 */
	public static void salvarSair(DefaultMutableTreeNode root)
			throws IOException {
		salvar(root, true);
	}

	public void gravarArquivo(File arqExportar, DefaultMutableTreeNode root)
			throws IOException {

		FileOutputStream fileOutputStream = new FileOutputStream(arqExportar);
		ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
		oos.writeObject(root);
		oos.flush();
		fileOutputStream.close();
	}

	private Object carregarArquivo(File arqImportar) throws IOException,
			ClassNotFoundException {
		FileInputStream inputStream = new FileInputStream(arqImportar);
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		return ois.readObject();
	}

	public static DefaultMutableTreeNode carregarRoot(
			DefaultMutableTreeNode root) throws IOException,
			ClassNotFoundException {
		ImportardorExportador ie = new ImportardorExportador();
		Object objRoot = null;

		try {
			objRoot = ie.carregarArquivo(new File("data/root"));
		} catch (Exception e) {
		}

		if ((objRoot == null) || !(objRoot instanceof DefaultMutableTreeNode)) {
			return root;
		}

		return (DefaultMutableTreeNode) objRoot;
	}

	public static DefaultMutableTreeNode carregarRoot(
			DefaultMutableTreeNode root, File file) throws IOException,
			ClassNotFoundException {
		ImportardorExportador ie = new ImportardorExportador();
		Object objRoot = null;

		try {
			objRoot = ie.carregarArquivo(file);
		} catch (Exception e) {
		}

		if ((objRoot == null) || !(objRoot instanceof DefaultMutableTreeNode)) {
			return root;
		}

		return (DefaultMutableTreeNode) objRoot;
	}

	public static File[] filesInDataDir() {
		File file = new File("data");

		return file.listFiles();
	}

	public static void main(String[] args) {
		File[] files = filesInDataDir();

		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].isDirectory());
			System.out.println(files[i].getName());
		}
	}

	public static void salvar(final DefaultMutableTreeNode root,
			final String nome) throws IOException {
		ImportardorExportador ie = new ImportardorExportador();
		ie.gravarArquivo(new File("data/" + nome), root);
	}
}
