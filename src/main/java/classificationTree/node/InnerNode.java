package classificationTree.node;

import frame.Word;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class InnerNode extends Node{
    private InnerNode preNode;
    private Map<String, Node> keyChildMap = new HashMap<>();

    public InnerNode(Word word) {
        super(word);
    }

    public void add(String key, Node node) {
        keyChildMap.put(key, node);
    }

    public Node getChild(String key) {
        Node node = keyChildMap.get(key);
        return node;
    }

    public List<Node> getChildList() {
        return new ArrayList<>(keyChildMap.values());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
