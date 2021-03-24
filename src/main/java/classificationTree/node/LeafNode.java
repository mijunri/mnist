package classificationTree.node;

import frame.Word;
import lombok.Data;

@Data
public class LeafNode extends Node {
    private boolean init;
    private InnerNode preNode;
    private String output;

    public LeafNode(Word word) {
        super(word);
    }

    public LeafNode(Word word, boolean init,String output) {
        super(word);
        this.init = init;
        this.output = output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeafNode)) return false;
        if (!super.equals(o)) return false;

        LeafNode leafNode = (LeafNode) o;

        if (isInit() != leafNode.isInit()) return false;
        return getOutput().equals(leafNode.getOutput());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isInit() ? 1 : 0);
        result = 31 * result + getOutput().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
