package frame;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class Word {

    @Getter
    private List<String> actions;

    public static Word emptyWord() {
        return new Word(new ArrayList<>());
    }

    public int size(){
        return actions.size();
    }

    public String get(int i){
        return actions.get(i);
    }

    public boolean isEmpty(){
        return actions.isEmpty();
    }

    public  Word subWord(int fromIndex, int toIndex){
        try {
            List<String> subList = getActions().subList(fromIndex, toIndex);
            return new Word(subList);
        } catch (Exception e) {
            return emptyWord();
        }
    }

    public  Word concat(String action){
        List<String> actions1 = new ArrayList<>();
        actions1.addAll(getActions());
        actions1.add(action);
        return new Word(actions1);
    }

    public Word concat(Word w){
        List<String> arrayList1 = new ArrayList<>();
        arrayList1.addAll(getActions());
        arrayList1.addAll(w.getActions());
        return new Word(arrayList1);
    }



    @Override
    public String toString(){
        if (isEmpty()){
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String action : actions){
            sb.append(action).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;

        return getActions().equals(word.getActions());
    }

    @Override
    public int hashCode() {
        return getActions().hashCode();
    }
}
