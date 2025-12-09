package utility;


public class GeometryUtility {

    public static double toTheLeftOf(MapPoint A, MapPoint B, MapPoint C) {
        return (B.getX() - A.getX()) * (C.getY() - A.getY()) - (B.getY() - A.getY()) * (C.getX() - A.getX());
    }
}
