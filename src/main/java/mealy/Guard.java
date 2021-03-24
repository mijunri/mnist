package mealy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.CORBA.INTERNAL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guard {

    public static final String MAX;

    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 28 * 14; i++) {
            sb.append("1");
        }
        MAX = sb.toString();
    }

    private boolean lowerBoundOpen;

    private boolean upperBoundOpen;

    private String lowerBound;

    private String upperBound;

    public static Guard bottomGuard(String action) {
        return new Guard(false,false,action,action);
    }

    public boolean isLowerBoundClose() {
        return !lowerBoundOpen;
    }

    public boolean isUpperBoundClose() {
        return !upperBoundOpen;
    }


    //转成整型再比较
    public boolean isPass(String value) {

        if (isLowerBoundOpen() && isUpperBoundOpen()) {
            if (value.compareTo(lowerBound) > 0 && value.compareTo(upperBound) < 0) {
                return true;
            }
        }
        if (isLowerBoundClose() && isUpperBoundOpen()) {
            if (value.compareTo(lowerBound) >= 0 && value.compareTo(upperBound) < 0) {
                return true;
            }
        }
        if (isLowerBoundOpen() && isUpperBoundClose()) {
            if (value.compareTo(lowerBound) > 0 && value.compareTo(upperBound) <= 0) {
                return true;
            }
        }
        if (isLowerBoundClose() && isUpperBoundClose()) {
            if (value.compareTo(lowerBound) >= 0 && value.compareTo(upperBound) <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (isLowerBoundOpen()) {
            stringBuilder.append("(");
        } else {
            stringBuilder.append("[");
        }
        stringBuilder.append(lowerBound).append(",");
        stringBuilder.append(upperBound);
        if (isUpperBoundOpen()) {
            stringBuilder.append(")");
        } else {
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    public Guard copy() {
        return new Guard(lowerBoundOpen, upperBoundOpen, lowerBound, upperBound);
    }

}
