package utility;

import java.util.ArrayList;

public class Face {

    public HalfEdge outerComponent;

    public ArrayList<HalfEdge> innerComponents;

    public Face() {
        innerComponents = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Face f) {
            return f.outerComponent.equals(outerComponent);
        }
        return false;
    }
}
