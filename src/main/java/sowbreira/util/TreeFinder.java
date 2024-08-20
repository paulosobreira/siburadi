package sowbreira.util;

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

    /**
     * @param model
     */
    public TreeFinder(DefaultTreeModel model, String valor) {
        DefaultMutableTreeNode superNode =
            (DefaultMutableTreeNode) model.getRoot();
        Enumeration enumeration = superNode.children();

        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode raiz =
                (DefaultMutableTreeNode) enumeration.nextElement();
            find(raiz, (DefaultMutableTreeNode) raiz.getUserObject(), valor);
        }
    }

    public HashMap getResultados() {
        return resultados;
    }

    public void setResultados(HashMap resultados) {
        this.resultados = resultados;
    }

    private void find(
        DefaultMutableTreeNode raizGeral, DefaultMutableTreeNode raiz,
        String valor
    ) {
            Enumeration enumeration = raiz.children();

            while (enumeration.hasMoreElements()) {
                DefaultMutableTreeNode no =
                    (DefaultMutableTreeNode) enumeration.nextElement();
                if (compare(no,valor)) {
                    List list = (List) resultados.get(raizGeral);

                    if (list == null) {
                        list = new ArrayList();
                        resultados.put(raizGeral, list);
                    }

                    list.add(no);
                    if (no.getChildCount()>0)
                        find(raizGeral, no, valor);
                } else {
                    find(raizGeral, no, valor);
                }
            }
 
    }
    /**
     * Passei um tempaum na inet procurando como fazer essa comparação case
     * insessitive ate sakar q dava um lower nas duas e comparava :P 
     */
    private boolean compare(DefaultMutableTreeNode no, String valor) {
        String analizado = no.getUserObject().toString().toLowerCase();
        return analizado.indexOf(valor.toLowerCase()) != -1 ; 
        
    }

    public int contarOcorrencias(Map resultados) {
        int valor = 0;

        for (Iterator iter = resultados.keySet().iterator(); iter.hasNext();) {
            DefaultMutableTreeNode geralNode =
                (DefaultMutableTreeNode) iter.next();
            List noRes = (List) resultados.get(geralNode);
            valor += noRes.size();
        }

        valor += resultados.size();

        return valor;
    }
}
