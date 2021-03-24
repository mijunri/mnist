package classificationTree.node;

import frame.Word;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class Node {
    private Word word;

    public boolean isLeaf(){
        return this instanceof LeafNode;
    }

    public boolean isInnerNode(){
        return this instanceof InnerNode;
    }
}
