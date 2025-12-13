package utility.graph;

import java.util.Objects;

/**
 * An arc from v1 to v2.
 *
 * @param v1
 * @param v2
 */
public record Edge(Vertex v1, Vertex v2) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge edge)) return false;
        return Objects.equals(v1, edge.v1) && Objects.equals(v2, edge.v2)
                || Objects.equals(v1, edge.v2) && Objects.equals(v2, edge.v1);
    }

    public boolean nonOrientedEquals(Edge edge) {
        return Objects.equals(v1, edge.v1) && Objects.equals(v2, edge.v2)
                || Objects.equals(v1, edge.v2) && Objects.equals(v2, edge.v1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1, v2);
    }
}
