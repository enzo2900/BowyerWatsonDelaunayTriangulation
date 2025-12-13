package utility;

import utility.graph.Graph2DTopologyBuilder;

import java.util.Objects;

public record Couple<L, R>(L left, R right) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Couple<?, ?> couple)) return false;
        return Objects.equals(left, couple.left) && Objects.equals(right, couple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
