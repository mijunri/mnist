package classificationTree;

import classificationTree.node.InnerNode;
import classificationTree.node.LeafNode;
import classificationTree.node.Node;
import classificationTree.node.SiftResult;
import frame.Learner;
import frame.Teacher;
import frame.Word;
import lombok.Data;
import mealy.*;


import java.util.*;

@Data
public class ClassificationTree implements Learner {

    private String name;
    private Teacher teacher;

    private InnerNode root;
    private Set<Track> trackSet = new HashSet<>();
    private MealyAutomata hypothesis;

    private Map<MealyLocation, LeafNode> locationNodeMap;
    private Map<LeafNode, MealyLocation> nodeLocationMap;


    public ClassificationTree(String name, Teacher teacher) {
        this.name = name;
        this.teacher = teacher;
    }

    @Override
    public void init() {
        root = new InnerNode(Word.emptyWord());
        LeafNode emptyLeaf = new LeafNode(Word.emptyWord());
        String key = answer(emptyLeaf.getWord(), root.getWord());
        emptyLeaf.setOutput(key);
        emptyLeaf.setInit(true);
        emptyLeaf.setPreNode(root);
        root.add(key, emptyLeaf);
        refineSymbolTrack(emptyLeaf);
    }

    private String answer(Word prefix, Word suffix) {
        Word word = prefix.concat(suffix);
        String answer = teacher.membership(word);
        return answer;
    }

    public void refineSymbolTrack(LeafNode leafNode) {
        Word prefix = leafNode.getWord();
        String value = "0";
        Word word = prefix.concat(value);
        LeafNode target = sift(word).getLeafNode();
        Track track = new Track(leafNode, target, value);
        trackSet.add(track);
    }

    private SiftResult sift(Word word) {
        Node currentNode = root;
        while (currentNode.isInnerNode()) {
            InnerNode node = (InnerNode) currentNode;
            Word suffix = node.getWord();
            String key = answer(word, suffix);
            Node next = node.getChild(key);
            if (next == null) {
                String output = teacher.membership(word);
                LeafNode leafNode = new LeafNode(word, word.isEmpty(), output);
                node.add(key, leafNode);
                leafNode.setPreNode(node);
                refineSymbolTrack(leafNode);
                return new SiftResult(leafNode, true);
            }
            currentNode = next;
        }
        return new SiftResult((LeafNode) currentNode, false);
    }

    @Override
    public void learn() {

    }

    @Override
    public boolean check(Word counterExample) {
        try{
            errorIndexAnalyse(counterExample);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public void refine(Word ce) {
        ErrorIndexResult result = errorIndexAnalyse(ce);
        int errorIndex = result.getIndex();
        MealyLocation uLocation = hypothesis.reach(ce.subWord(0, errorIndex));
        String action = ce.get(errorIndex);
        LeafNode sourceNode = locationNodeMap.get(uLocation);
        Word uWord = sourceNode.getWord();
        Word nextWord;
        nextWord = uWord.concat(action);
        SiftResult siftResult = sift(nextWord);
        LeafNode targetNode = siftResult.getLeafNode();

        if (siftResult.isCompleteOperation()) {
            Track track = new Track(sourceNode, targetNode, action);
            trackSet.add(track);
            return;
        } else {
            MealyLocation vLocation = nodeLocationMap.get(targetNode);
            boolean isPass = checkIsPass(uLocation, vLocation, action);
            if (!isPass) {
                Track track = new Track(sourceNode, targetNode, action);
                trackSet.add(track);
                return;
            } else {
                Word suffix = ce.subWord(errorIndex + 1, ce.size());
                InnerNode innerNode = new InnerNode(suffix);

                InnerNode father = targetNode.getPreNode();
                String output = null;
                for (String r : father.getKeyChildMap().keySet()) {
                    if (father.getChild(r) == targetNode) {
                        output = r;
                    }
                }
                father.getKeyChildMap().put(output, innerNode);
                innerNode.setPreNode(father);
                targetNode.setPreNode(innerNode);

                boolean init = nextWord.isEmpty();
                String output1 = teacher.membership(nextWord);
                LeafNode newLeafNode = new LeafNode(nextWord, init, output1);
                newLeafNode.setPreNode(innerNode);
                innerNode.add(answer(targetNode.getWord(), suffix), targetNode);
                innerNode.add(answer(newLeafNode.getWord(), suffix), newLeafNode);

                //refine transition
                refineNode(targetNode);

                //add transition
                refineSymbolTrack(newLeafNode);
            }
        }

    }

    private ErrorIndexResult errorIndexAnalyse(Word ce) {
        for (int i = 0; i <= ce.size(); i++) {
            Word prefix = ce.subWord(0, i);
            Word suffix = ce.subWord(i, ce.size());
            MealyLocation location = hypothesis.reach(prefix);
            Word uWord = locationNodeMap.get(location).getWord();
            String key1 = answer(prefix, suffix);
            String key2 = answer(uWord, suffix);
            if (!key1.equals(key2)) {
                return new ErrorIndexResult(i - 1);
            }
        }
        throw new RuntimeException("找不到错误位置，请检查代码");
    }

    private boolean checkIsPass(MealyLocation qu, MealyLocation qv, String action) {
        List<MealyTransition> transitionList = getHypothesis().getTransitions(qu, qv);
        boolean isPass = false;
        for (MealyTransition t : transitionList) {
            if (t.getGuard().isPass(action)) {
                isPass = true;
                break;
            }
        }
        return isPass;
    }

    //把指向TargetNode的迁移重新分配
    private void refineNode(LeafNode targetNode) {
        Word suffix = targetNode.getPreNode().getWord();
        Iterator<Track> iterator = trackSet.iterator();
        List<LeafNode> newNodeWordList = new ArrayList<>();
        while (iterator.hasNext()) {
            Track track = iterator.next();
            if (track.getTarget().equals(targetNode)) {
                String action = track.getAction();
                Word prefix = track.getSource()
                        .getWord()
                        .concat(action);
                String key = answer(prefix, suffix);
                InnerNode innerNode = targetNode.getPreNode();
                LeafNode node = (LeafNode) innerNode.getChild(key);
                if (node == null) {
                    String output = teacher.membership(prefix);
                    node = new LeafNode(prefix,prefix.isEmpty(), output);
                    node.setPreNode(innerNode);
                    innerNode.add(key, node);
                    newNodeWordList.add(node);
                }
                track.setTarget(node);
            }
        }
        for (LeafNode newLeafNode : newNodeWordList) {
            refineSymbolTrack(newLeafNode);
        }
    }

    @Override
    public MealyAutomata buildHypothesis() {

        locationNodeMap = new HashMap<>();
        nodeLocationMap = new HashMap<>();

        List<MealyLocation> locationList = buildLocationList();
        List<MealyTransition> transitionList = buildTransitionList();

        MealyAutomata mealy = new MealyAutomata(name, locationList, transitionList);
        evidenceToDOTA(mealy);
        setHypothesis(mealy);
        return mealy;
    }

    private void evidenceToDOTA(MealyAutomata mealy) {
        for (MealyLocation l : mealy.getLocations()) {
            List<MealyTransition> transitions = mealy.getTransitions(l);
            transitions.sort(new MealyTranComparator());
            for (int i = 0; i < transitions.size(); i++) {
                if (i < transitions.size() - 1) {
                    Guard guard1 = transitions.get(i).getGuard();
                    Guard guard2 = transitions.get(i + 1).getGuard();
                    guard1.setUpperBound(guard2.getLowerBound());
                    guard1.setUpperBoundOpen(!guard2.isLowerBoundOpen());
                } else {
                    Guard guard1 = transitions.get(i).getGuard();
                    guard1.setUpperBound(Guard.MAX);
                    guard1.setUpperBoundOpen(true);
                }
            }
        }
    }

    private List<MealyTransition> buildTransitionList() {
        List<MealyTransition> transitionList = new ArrayList<>();
        for (Track track : trackSet) {
            LeafNode sourceNode = track.getSource();
            LeafNode targetNode = track.getTarget();
            MealyLocation sourceLocation = nodeLocationMap.get(sourceNode);
            MealyLocation targetLocation = nodeLocationMap.get(targetNode);
            String action = track.getAction();
            Guard guard = Guard.bottomGuard(action);
            MealyTransition transition = new MealyTransition(
                    sourceLocation, targetLocation, guard);
            if (!transitionList.contains(transition)) {
                transitionList.add(transition);
            }
        }
        return transitionList;
    }

    private List<MealyLocation> buildLocationList() {
        List<LeafNode> nodeList = leafList();
        List<MealyLocation> locationList = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            LeafNode node = nodeList.get(i);
            MealyLocation location = new MealyLocation(
                    String.valueOf(i + 1),
                    String.valueOf(i + 1),
                    node.isInit(),
                    node.getOutput());
            locationList.add(location);
            nodeLocationMap.put(node, location);
            locationNodeMap.put(location, node);
        }
        return locationList;
    }

    private List<LeafNode> leafList() {
        Map<Word, LeafNode> leafMap = getLeafMap();
        return new ArrayList<>(leafMap.values());
    }

    private Map<Word, LeafNode> getLeafMap() {
        Map<Word, LeafNode> leafMap = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            if (node.isLeaf()) {
                LeafNode leaf = (LeafNode) node;
                Word suffix = leaf.getWord();
                leafMap.put(suffix, leaf);
            } else {
                InnerNode innerNode = (InnerNode) node;
                queue.addAll(innerNode.getChildList());
            }
        }
        return leafMap;
    }

    @Override
    public MealyAutomata getFinalHypothesis() {
        return hypothesis;
    }
}
