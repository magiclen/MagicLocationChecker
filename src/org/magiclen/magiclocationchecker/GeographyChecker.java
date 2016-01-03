/*
 *
 * Copyright 2015-2016 magiclen.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.magiclen.magiclocationchecker;

import org.magiclen.geographiclib.Geodesic;
import org.magiclen.geographiclib.PolygonArea;
import org.magiclen.geographiclib.PolygonResult;

/**
 * 用來計算一個經緯度座標點是否在一個區域內，以及距離這塊區域的距離長度。
 *
 * @author Magic Len
 */
public class GeographyChecker extends LocationChecker2D {

    // -----類別常數-----
    /**
     * 地球赤道半徑(公里)。
     */
    private static final double EARTH_EQUATOR_RADIUS = 6378.1370;
    /**
     * 地球極半徑(公里)。
     */
    private static final double EARTH_POLE_RADIUS = 6356.752314245;

    // -----類別類別-----
    /**
     * 經緯度座標點類別，有經度和緯度。
     */
    public static class Vertex extends LocationChecker2D.Vertex {

	// -----類別方法-----
	/**
	 * 建立經緯度座標點。
	 *
	 * @param longitude 傳入經度(-180~180)
	 * @param latitude 傳入緯度(-90~90)
	 * @return 傳回經緯度座標點
	 */
	public static Vertex vertex(final double longitude, final double latitude) {
	    return new Vertex(longitude, latitude);
	}

	/**
	 * 建立經緯度座標點。
	 *
	 * @param vertex 傳入緯度和經度(lat,lon)
	 * @return 傳回經緯度座標點
	 */
	public static Vertex vertex(String vertex) {
	    vertex = vertex.replaceAll(" ", "");
	    final String[] tokens = vertex.split(",");
	    return new Vertex(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[0]));
	}

	/**
	 * 建立原點(0)。
	 *
	 * @return 傳回原點
	 */
	public static Vertex zeroVertex() {
	    return new Vertex(0, 0);
	}

	/**
	 * <p>
	 * 計算經緯度座標點間的距離。
	 * </p>
	 *
	 * <p>
	 * 演算法參考自：T Vincenty, "Direct and Inverse Solutions of Geodesics on the
	 * Ellipsoid with application of nested equations", Survey Review, vol
	 * XXIII no 176, 1975
	 * </p>
	 *
	 * @param from 傳入第一個座標
	 * @param to 傳入第二個座標
	 * @return 傳回兩經緯度座標點間的距離(公里)
	 */
	public static double computeDistance(final LocationChecker2D.Vertex from, final LocationChecker2D.Vertex to) {
	    final double degree2radian = Math.PI / 180.0;
	    final double a = EARTH_EQUATOR_RADIUS;
	    final double b = EARTH_POLE_RADIUS;
	    final double f = (a - b) / a, rf = (1.0 - f);
	    final double p1 = from.y, p2 = to.y;
	    final double L = (to.x - from.x) * degree2radian;
	    final double tanU1 = rf * Math.tan(p1 * degree2radian);
	    final double tanU2 = rf * Math.tan(p2 * degree2radian);
	    final double cosU1 = 1.0 / Math.sqrt(1.0 + Math.pow(tanU1, 2));
	    final double cosU2 = 1.0 / Math.sqrt(1.0 + Math.pow(tanU2, 2));
	    final double sinU1 = tanU1 * cosU1;
	    final double sinU2 = tanU2 * cosU2;
	    double lambda = L, lambdaP, cosAlpha2, sigma, sinSigma, cosSigma, cos2SigmaM, cos2SigmaM2;
	    do {
		final double sinLambda = Math.sin(lambda);
		final double cosLambda = Math.cos(lambda);
		sinSigma = Math.hypot(cosU2 * sinLambda, cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
		if (sinSigma == 0) {
		    return 0;
		}
		cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
		sigma = Math.atan2(sinSigma, cosSigma);
		final double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
		cosAlpha2 = 1.0 - Math.pow(sinAlpha, 2.0);
		cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosAlpha2;
		if (Double.isNaN(cos2SigmaM)) {
		    cos2SigmaM = 0;
		}
		cos2SigmaM2 = Math.pow(cos2SigmaM, 2.0);
		final double C = f / 16.0 * cosAlpha2 * (4.0 + f * (4.0 - 3.0 * cosAlpha2));
		lambdaP = lambda;
		lambda = L + (1.0 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1.0 + 2.0 * cos2SigmaM2)));
	    } while (Math.abs(lambda - lambdaP) > EPSILON);
	    final double sinSigma2 = Math.pow(sinSigma, 2.0);
	    final double a2 = Math.pow(a, 2.0);
	    final double b2 = Math.pow(b, 2.0);
	    final double u2 = cosAlpha2 * (a2 - b2) / b2;
	    final double A = 1.0 + u2 / 16384.0 * (4096.0 + u2 * (-768.0 + u2 * (320.0 - 175.0 * u2)));
	    final double B = u2 / 1024.0 * (256.0 + u2 * (-128.0 + u2 * (74 - 47 * u2)));
	    final double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4.0 * (cosSigma * (-1.0 + 2.0 * cos2SigmaM2) - B / 6.0 * cos2SigmaM * (-3.0 + 4.0 * sinSigma2) * (-3.0 + 4.0 * cos2SigmaM2)));
	    final double s = b * A * (sigma - deltaSigma);
	    return s;
	}

	// -----建構子-----
	/**
	 * 建構子，建立座標物件。
	 *
	 * @param longitude 傳入經度
	 * @param latitude 傳入緯度
	 */
	protected Vertex(final double longitude, final double latitude) {
	    super(longitude, latitude);
	    if (longitude < -180 || longitude > 180) {
		throw new NumberFormatException("The range of longitude is -180 ~ 180");
	    }
	    if (latitude < -90 || latitude > 90) {
		throw new NumberFormatException("The range of latitude is -90 ~ 90");
	    }
	}

	// -----物件方法-----
	/**
	 * 計算此經緯度座標點與另一經緯度座標點間的距離。
	 *
	 * @param to 傳入另一個經緯度座標
	 * @return 傳回兩經緯度座標點間的距離(公里)
	 */
	@Override
	public double computeDistance(final LocationChecker2D.Vertex to) {
	    return computeDistance(this, to);
	}

	/**
	 * 位移經緯度座標點成新的經緯度座標。
	 *
	 * @param offset 傳入位移量
	 * @return 傳回新的經緯度座標點
	 */
	@Override
	public Vertex offset(final LocationChecker2D.Vertex offset) {
	    return vertex(x + offset.x, y + offset.y);
	}
    }

    // -----建構子-----
    /**
     * 建構子，傳入區域的經緯度座標頂點。
     *
     * @param autoCCW 傳入是否要自動使用逆時針的方式排序
     * @param vertices 傳入區域的經緯度座標頂點，需為逆時針或順時針排序
     */
    public GeographyChecker(final boolean autoCCW, final Vertex... vertices) {
	super(autoCCW, vertices);
    }

    // -----物件方法-----
    /**
     * <p>
     * 計算面積。
     * </p>
     *
     * <p>
     * 透過GeographicLib計算面積。
     * </p>
     *
     * @return 傳回面積(平方公里)
     */
    @Override
    public double computeArea() {
	final int areaVerticesLength = areaVertices.length;

	final PolygonArea pa = new PolygonArea(Geodesic.WGS84, false);

	for (int i = 0; i < areaVerticesLength; ++i) {
	    final Vertex vertex = (Vertex) areaVertices[i];
	    pa.AddPoint(vertex.y, vertex.x);
	}
	final PolygonResult pr = pa.Compute();
	return Math.abs(pr.area / 1000000.0);
    }

    /**
     * 建立經緯度座標點。
     *
     * @param longitude 傳入經度(-180~180)
     * @param latitude 傳入緯度(-90~90)
     * @return 傳回經緯度座標點
     */
    @Override
    protected Vertex createVertex(final double longitude, final double latitude) {
	return Vertex.vertex(longitude, latitude);
    }

    /**
     * <p>
     * 計算傳入的經緯度座標點和這塊區域的距離。
     * </p>
     *
     * <p>
     * 先判斷經緯度座標點是否在這塊區域內，是的話回傳0；如果不是的話，再計算緯度座標點與所有邊線的最小距離。
     * </p>
     *
     * @param vertex 傳入緯度座標點
     * @return 傳回傳入的緯度座標點和這塊區域的距離(公里)，如果座標點在平面空間內，傳回0
     */
    @Override
    public double computeDistance(final LocationChecker2D.Vertex vertex) {
	return super.computeDistance(vertex);
    }
}
