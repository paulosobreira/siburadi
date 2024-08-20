package sowbreira.controle;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


/**
 * @author Paulo Sobreira
 * Criado Em 18:33:51
 */
public class TreeFinder {
    private HashMap resultados = new HashMap();
    private List keySetsOrdenados  = new ArrayList();

    public List getKeySetsOrdenados() {
        return keySetsOrdenados;
    }

    public void setKeySetsOrdenados(List keySetsOrdenados) {
        this.keySetsOrdenados = keySetsOrdenados;
    }

    /**
     * @param model
     * @param node
     */
    public TreeFinder(
        DefaultTreeModel model, String valor, DefaultMutableTreeNode node
    ) {
        DefaultMutableTreeNode superNode;

        if ((node != null) && (node != model.getRoot())) {
            superNode = (DefaultMutableTreeNode) node.getUserObject();
        } else {
            superNode = (DefaultMutableTreeNode) model.getRoot();
        }

        Enumeration enumeration = superNode.children();

        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode raiz =
                (DefaultMutableTreeNode) enumeration.nextElement();

            if (raiz.getUserObject() instanceof DefaultMutableTreeNode) {
                superNode = (DefaultMutableTreeNode) raiz.getUserObject();
            }
            else
                raiz = (DefaultMutableTreeNode) node.getUserObject();

            find(raiz, superNode, valor);
        }
    }

    public HashMap getResultados() {
        return resultados;
    }

    public void setResultados(HashMap resultados) {
        this.resultados = resultados;
    }

    private void find(Object key, DefaultMutableTreeNode raiz, String valor) {
        Enumeration enumeration = raiz.children();

        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode no =
                (DefaultMutableTreeNode) enumeration.nextElement();

            if (compare(no, valor)) {
                List list = (List) resultados.get(key);

                if (list == null) {
                    list = new ArrayList();
                    resultados.put(key, list);
                    keySetsOrdenados.add(key);
                }
                if (!list.contains(no))
                    list.add(no);

                if (no.getChildCount() > 0) {
                    find(key, no, valor);
                }
            } else {
                find(key, no, valor);
            }
        }
    }

    /**
     * Passei um tempaum na inet procurando como fazer essa comparaçao case
     * insessitive ate sakar q dava um lower nas duas e comparava :P
     */
    private boolean compare(DefaultMutableTreeNode no, String valor) {
        String analizado = no.getUserObject()
                             .toString()
                             .toLowerCase();

        return analizado.indexOf(valor.toLowerCase()) != -1;
    }

    public int contarOcorrencias(Map resultados) {
        int valor = 0;

        for (Iterator iter = resultados.keySet()
                                       .iterator(); iter.hasNext();) {
            DefaultMutableTreeNode geralNode =
                (DefaultMutableTreeNode) iter.next();
            List noRes = (List) resultados.get(geralNode);
            valor += noRes.size();
        }

        //valor += resultados.size();

        return valor;
    }
    
    
 
}
