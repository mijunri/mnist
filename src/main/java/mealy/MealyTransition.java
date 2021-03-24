package mealy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MealyTransition {
    private MealyLocation sourceLocation;
    private MealyLocation targetLocation;
    private Guard guard;

    public String getSourceId() {
        return sourceLocation.getId();
    }

    public String getTargetId(){
        return targetLocation.getId();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sourceLocation.getId()).append(", ").append(guard.toString());
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }
}
