package utility;


public class GeometryUtility {

    public static double toTheLeftOf(MapPoint A, MapPoint B, MapPoint C) {
        return (B.getX() - A.getX()) * (C.getY() - A.getY()) - (B.getY() - A.getY()) * (C.getX() - A.getX());
    }

    public static double dotProduct(MapPoint A, MapPoint B, MapPoint C) {
        return (B.getX() -A.getX()) * (C.getX() - A.getX()) + (B.getY() - A.getY()) * (C.getY() - A.getY());
    }


    public static boolean isInsideCircleD(MapPoint A, MapPoint B, MapPoint C , MapPoint D) {
        double orientation = (B.getX() - A.getX()) * (C.getY() - A.getY()) -
                (B.getY() - A.getY()) * (C.getX()- A.getX());
        double ax = A.getX() - D.getX();
        double ay = A.getY() - D.getY();
        double bx = B.getX() - D.getX();
        double by = B.getY() - D.getY();
        double cx = C.getX() - D.getX();
        double cy = C.getY() - D.getY();

        double det = (ax*ax + ay*ay)*(bx*cy - cx*by)
                - (bx*bx + by*by)*(ax*cy - cx*ay)
                + (cx*cx + cy*cy)*(ax*by - bx*ay);
       // double det = inCircle( A,  B, C, D);
        if (orientation < 0) det = -det;

        return det >= 0;
    }

    public static double inCircle(MapPoint i, MapPoint j, MapPoint k, MapPoint l) {
        double xi = i.getX();
        double yi = i.getY();
        double xj = j.getX();
        double yj = j.getY();
        double xk = k.getX();
        double yk = k.getY();
        double xl = l.getX();
        double yl = l.getY();

        double a = xi * xi + yi*yi;
        double b = xj * xj + yj* yj;
        double c = xk * xk + yk * yk;
        double d = xl * xl + yl * yl;

        double[][] mat4 = new double[4][4];
        mat4[0][0] = xi;
        mat4[0][1] = yi;
        mat4[0][2] = a;
        mat4[0][3] = 1;
        mat4[1][0] = xj;
        mat4[1][1] = yj;
        mat4[1][2] = b;
        mat4[1][3] = 1;
        mat4[2][0] = xk;
        mat4[2][1] = yk;
        mat4[2][2] = c;
        mat4[2][3] = 1;
        mat4[3][0] = xl;
        mat4[3][1] = yl;
        mat4[3][2] = d;
        mat4[3][3] = 1;

        return determinantMatrix4(mat4);

    }

    public static double determinantMatrix4(double[][] mat4) {
        double determinant = 0;
        for(int i = 0; i < mat4.length ; i ++) {
            double a = mat4[0][i];
            if(i % 2 == 1) {
                a *= -1;
            }
            double[][] mat3 = new double[3][3];
            mat3[0][0] = mat4[1][(i+1)% mat4.length];
            mat3[0][1] =  mat4[1][(i+2)% mat4.length];
            mat3[0][2] =  mat4[1][(i+3)% mat4.length];
            mat3[1][0] = mat4[2][(i+1)% mat4.length];
            mat3[1][1] =  mat4[2][(i+2)% mat4.length];
            mat3[1][2] =  mat4[2][(i+3)% mat4.length];
            mat3[2][0] = mat4[3][(i+1)% mat4.length];
            mat3[2][1] =  mat4[3][(i+2)% mat4.length];
            mat3[2][2] =  mat4[3][(i+3)% mat4.length];
            determinant += a*determinantMatrix3(mat3);
        }
        return determinant;
    }

    public static double determinantMatrix3(double[][] mat3) {
        double determinant = 0;
        for(int i = 0; i < mat3.length ; i ++) {
            double a = mat3[0][i];
            if(i % 2 == 1) a *= -1;
            double[][] mat2 = new double[2][2];
            mat2[0][0] = mat3[1][(i+1)% mat3.length];
            mat2[0][1] =  mat3[1][(i+2)% mat3.length];
            mat2[1][0] =  mat3[2][(i+1)% mat3.length];
            mat2[1][1] =  mat3[2][(i+2)% mat3.length];
            determinant += a*determinantMatrix2(mat2);
        }
        return determinant;

    }

    public static double determinantMatrix2(double[][] mat2) {
        double value = mat2[0][0] * mat2[1][1] - mat2[1][0] * mat2[0][1];
        return value;
    }
}
