package EncodingVisualizationStart;

public class Tree implements Comparable <Tree> {
    Node root;

    public Tree(Tree t1, Tree t2) {
        root = new Node();
        root.left = t1.root;
        root.right = t2.root;
        root.weight = t1.root.weight + t2.root.weight;
    }

    public Tree(int weight, char element) {
        root = new Node(weight, element);
    }

    @Override
    public int compareTo(Tree o) {
        if (root.weight < o.root.weight) //reverse the order
            return 1;
        else if (root.weight == o.root.weight)
            return 0;
        else
            return -1;
    }
}

