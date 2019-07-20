package EncodingVisualizationStart;

public class Node {
    public Node left;
    public Node right;
    char element;
    int weight;
    String code = "";

    public Node() {

    }

    public Node(int weight, char element) {
        this.weight = weight;
        this.element = element;
    }

  /*  public void clear(){
        element = "";
        weight = 0;
        left.clear();
        right.clear();
        code = "";
    }*/
}
