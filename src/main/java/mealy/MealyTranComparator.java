package mealy;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Comparator;

@Data
@AllArgsConstructor
public class MealyTranComparator implements Comparator<MealyTransition> {


    @Override
    public int compare(MealyTransition o1, MealyTransition o2) {
        int var1 = o1.getSourceId().compareTo(o2.getSourceId());
        if (var1 != 0){
            return var1;
        }
        int var3 = o1.getGuard().getLowerBound().compareTo(o2.getGuard().getLowerBound());
        if(var3 !=0){
            return var3;
        }
        int var4 = o1.getGuard().getUpperBound().compareTo(o2.getGuard().getUpperBound());
        if(var4 != 0){
            return var4;
        }
        return -1;
    }
}
