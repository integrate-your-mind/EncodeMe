package EncodingVisualizationStart;


/**
 * Huffman algorithm from Liang book
 * Reference in README.txt
 */
public class HuffmanTree {
    private Tree mTree;
    private int[] mCounts;


    public HuffmanTree() {
        mTree = null;
        mCounts = null;
    }

    public Tree getTree() {
        return mTree;
    }

    public String[] getCodes(Node root) {
        if (root == null) return null;
        String[] codes = new String[2 * 128];
        assignCodes(root, codes);
        return codes;
    }

    public int[] getCharacterFrequency(String text) {
        int[] counts = new int[256];
        try {
            for (int i = 0; i < text.length(); i++)
                counts[(int) text.charAt(i)]++;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public Tree getHuffmanTree(int[] counts) {
        Heap <Tree> treeHeap;
        //Create heap to store the trees
        treeHeap = new Heap <>();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0)
                treeHeap.add(new Tree(counts[i], (char) i));
        }

        while (treeHeap.getSize() > 1) {
            Tree t1 = treeHeap.remove();
            Tree t2 = treeHeap.remove();
            treeHeap.add(new Tree(t1, t2));
        }
        return treeHeap.remove();
    }

    public void setTextToBeEncoded(String text) {
        mCounts = getCharacterFrequency(text);
        mTree = getHuffmanTree(mCounts);
    }

    private void assignCodes(Node node, String[] codes) {
        if (node.left != null) {
            node.left.code = node.code + "0";
            assignCodes(node.left, codes);

            node.right.code = node.code + "1";
            assignCodes(node.right, codes);

        } else {
            codes[(int) node.element] = node.code;
        }
    }


    public Node getRoot() {
        return this.mTree.root;
    }

    /*public void clear(){
        mCounts = null;
    }*/

}