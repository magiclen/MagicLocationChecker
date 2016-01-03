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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用來計算一個二維座標點是否在一個平面空間上，以及距離這塊平面空間的距離長度。
 *
 * @author Magic Len
 */
public class LocationChecker2D {

    // -----類別常數-----
    /**
     * 判斷是否浮點數相等的誤差值。
     */
    protected static final double EPSILON = 0.000000000001;

    // -----類別方法-----
    /**
     * 判斷兩浮點數是否相等。
     *
     * @param a 傳入第一個浮點數
     * @param b 傳入第二個浮點數
     * @return 傳回兩浮點數是否相等
     */
    protected static boolean doubleEquals(final double a, final double b) {
	return Math.abs(a - b) < EPSILON;
    }

    // -----類別類別-----
    /**
     * 座標點類別，有X軸和Y軸。
     */
    public static class Vertex implements Comparable<Vertex>, Cloneable {

	// -----類別方法-----
	/**
	 * 建立座標點。
	 *
	 * @param x 傳入X座標值
	 * @param y 傳入Y座標值
	 * @return 傳回座標點
	 */
	public static Vertex vertex(final double x, final double y) {
	    return new Vertex(x, y);
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
	 * 計算兩座標點間的距離。
	 *
	 * @param from 傳入第一個座標
	 * @param to 傳入第二個座標
	 * @return 傳回兩座標點間的距離
	 */
	public static double computeDistance(final Vertex from, final Vertex to) {
	    return Math.hypot(from.x - to.x, from.y - to.y);
	}

	/**
	 * 計算從一個座標點到一個座標點的角度，以正向X軸作為0度。
	 *
	 * @param from 傳入第一個座標
	 * @param to 傳入第二個座標
	 * @return 傳回兩座標點的角度
	 */
	public static double computeAngle(final Vertex from, final Vertex to) {
	    final double dx = to.x - from.x, dy = to.y - from.y;
	    final double m = dy / dx;
	    final double angle = Math.atan(m) * 180 / Math.PI;
	    if (dx >= 0) {
		if (angle >= 0) {
		    return angle;
		} else {
		    return 360 + angle;
		}
	    } else {
		return 180 + angle;
	    }
	}

	/**
	 * 計算傳入的座標點序列的方向。
	 *
	 * @param vertices 傳入座標點序列
	 * @return 若傳回值大於0，表示為順時針；等於0，表示為單點或是直線；小於0，表示為逆時針。
	 */
	protected static int whatDirection(final Vertex... vertices) {
	    final int verticesLength = vertices.length;
	    if (vertices.length < 3) {
		return 0;
	    }
	    double sum = 0;
	    for (int i = 0; i < verticesLength; ++i) {
		final Vertex v1 = vertices[i];
		final Vertex v2 = vertices[(i + 1) % verticesLength];
		final Vertex v3 = vertices[(i + 2) % verticesLength];
		sum += (v2.x - v1.x) * (v2.y + v1.y) + (v3.x - v2.x) * (v3.y + v2.y) + (v1.x - v3.x) * (v1.y + v3.y);
	    }
	    return doubleEquals(sum, 0) ? 0 : (sum > 0 ? 1 : -1);
	}

	// -----物件常數-----
	/**
	 * X軸座標的值。
	 */
	public final double x;
	/**
	 * Y軸座標的值。
	 */
	public final double y;

	// -----建構子-----
	/**
	 * 建構子，建立座標物件。
	 *
	 * @param x 傳入X座標值
	 * @param y 傳入Y座標值
	 */
	protected Vertex(final double x, final double y) {
	    this.x = x;
	    this.y = y;
	}

	// -----物件方法-----
	/**
	 * 計算此座標點與另一座標點間的距離。
	 *
	 * @param to 傳入另一個座標
	 * @return 傳回兩座標點間的距離
	 */
	public double computeDistance(final Vertex to) {
	    return computeDistance(this, to);
	}

	/**
	 * 計算從此座標點到另一個座標點的角度，以正向X軸作為0度。
	 *
	 * @param to 傳入另一個座標
	 * @return 傳回兩座標點的角度
	 */
	public double computeAngle(final Vertex to) {
	    return computeAngle(this, to);
	}

	/**
	 * 位移座標點成新的座標。
	 *
	 * @param offset 傳入位移量
	 * @return 傳回新的座標點
	 */
	public Vertex offset(final Vertex offset) {
	    return vertex(x + offset.x, y + offset.y);
	}

	/**
	 * 將座標點轉成字串。
	 *
	 * @return 傳回座標點字串
	 */
	@Override
	public String toString() {
	    return toString(15);
	}

	/**
	 * 將座標點轉成字串。
	 *
	 * @param numberOfDecimalDigits 傳入最大的小數位數(最大15位)
	 * @return 傳回座標點字串
	 */
	public String toString(int numberOfDecimalDigits) {
	    final StringBuilder sb = new StringBuilder("0");
	    if (numberOfDecimalDigits > 0) {
		sb.append(".");
		if (numberOfDecimalDigits > 15) {
		    numberOfDecimalDigits = 15;
		}
		for (int i = 0; i < numberOfDecimalDigits; ++i) {
		    sb.append("#");

		}
	    }
	    final DecimalFormat df = new DecimalFormat(sb.toString());
	    return String.format("(%s, %s)", df.format(x), df.format(y));
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 43 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
	    hash = 43 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
	    return hash;
	}

	@Override
	public boolean equals(final Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (this == obj) {
		return true;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    final Vertex other = (Vertex) obj;
	    if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
		return false;
	    }
	    return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
	}

	@Override
	public int compareTo(final Vertex vertex) {
	    final long x1 = Double.doubleToLongBits(this.x), x2 = Double.doubleToLongBits(vertex.x);
	    final long y1 = Double.doubleToLongBits(this.y), y2 = Double.doubleToLongBits(vertex.y);
	    if (x1 < x2) {
		return -1;
	    } else if (x1 == x2) {
		if (y1 < y2) {
		    return -1;
		} else if (y1 == y2) {
		    return 0;
		} else {
		    return 1;
		}
	    } else {
		return 1;
	    }
	}

	@Override
	public Vertex clone() throws CloneNotSupportedException {
	    return (Vertex) super.clone();
	}
    }

    /**
     * 座標點包含索引和角度、距離資訊，用來進行排序。
     */
    private static class VertexWithInfo implements Comparable<VertexWithInfo> {

	final int index;
	final Vertex vertex;
	final double angle;
	final double distance;

	VertexWithInfo(final int index, final Vertex vertex, final double angle, final double diatance) {
	    this.index = index;
	    this.vertex = vertex;
	    this.angle = angle;
	    this.distance = diatance;
	}

	@Override
	public int compareTo(final VertexWithInfo vertexWithAngle) {
	    if (doubleEquals(this.angle, vertexWithAngle.angle)) {
		if (doubleEquals(this.distance, vertexWithAngle.distance)) {
		    return 0;
		} else if (this.distance > vertexWithAngle.distance) {
		    return 1;
		} else {
		    return -1;
		}
	    } else if (this.angle > vertexWithAngle.angle) {
		return 1;
	    } else {
		return -1;
	    }
	}
    }

    // -----物件常數-----
    /**
     * 按照順時針或逆時針儲存平面空間的頂點。
     */
    protected final Vertex[] areaVertices;

    // -----物件變數-----
    /**
     * 最下(右)和最上(左)的座標點。
     */
    private Vertex downRight, upLeft;

    // -----建構子-----
    /**
     * 建構子，傳入平面空間頂點。
     *
     * @param autoCCW 傳入是否要自動使用逆時針的方式排序
     * @param vertices 傳入平面空間頂點，需為順時針或逆時針
     */
    public LocationChecker2D(final boolean autoCCW, final Vertex... vertices) {
	final List<Vertex> verticesList = new ArrayList<>();

	// 略過空的座標點和重複的座標點，順便尋找出最下(右)、最上(左)的座標點。
	for (final Vertex vertex : vertices) {
	    if (vertex == null) {
		continue;
	    } else if (verticesList.contains(vertex)) {
		continue;
	    }
	    if (downRight == null || doubleEquals(downRight.y, vertex.y) && downRight.x < vertex.x || downRight.y > vertex.y) {
		downRight = vertex;
	    }
	    if (upLeft == null || doubleEquals(upLeft.y, vertex.y) && upLeft.x > vertex.x || upLeft.y < vertex.y) {
		upLeft = vertex;
	    }
	    verticesList.add(vertex);
	}

	// 檢查平面空間的座標數量，做少需為3個座標點
	final int verticesSize = verticesList.size();
	if (verticesSize < 3) {
	    throw new ArrayIndexOutOfBoundsException("The number of vertices small than 3.");
	} else if (downRight == upLeft) {
	    throwNotAnAreaException();
	}

	final int downRightIndex = verticesList.indexOf(downRight);
	final double basedAngle = downRight.computeAngle(upLeft);

	areaVertices = new Vertex[verticesSize];

	if (autoCCW) {
	    final int upLeftIndex = verticesList.indexOf(upLeft);

	    // 儲存被選擇的座標點
	    final boolean[] chosen = new boolean[verticesSize];
	    Arrays.fill(chosen, false);

	    int counter = 0;

	    // 以角度排序最下(右)開始的座標點，順便用角度檢查是否可以形成區域
	    final List<VertexWithInfo> bottomUp = new ArrayList<>();

	    chosen[downRightIndex] = true;
	    areaVertices[counter++] = downRight;

	    boolean differentAngle = false;
	    for (int i = 1; i < verticesSize; ++i) {
		final int index = (downRightIndex + i) % verticesSize;
		final Vertex vertex = verticesList.get(index);
		final double angle = downRight.computeAngle(vertex);
		if (!differentAngle && !doubleEquals(angle, basedAngle)) {
		    differentAngle = true;
		}
		final double distance = downRight.computeDistance(vertex);
		bottomUp.add(new VertexWithInfo(index, vertex, angle, distance));
	    }
	    if (!differentAngle) {
		throwNotAnAreaException();
	    }
	    bottomUp.sort(null);

	    // 篩選至最上(左)的座標點
	    final int bottomUpSize = bottomUp.size();
	    for (int i = 0; i < bottomUpSize; ++i) {
		final VertexWithInfo vwia = bottomUp.get(i);
		final int index = vwia.index;
		chosen[index] = true;
		areaVertices[counter++] = vwia.vertex;
		if (index == upLeftIndex) {
		    break;
		}
	    }

	    // 以角度排序最上(左)開始的座標點
	    final List<VertexWithInfo> topDown = new ArrayList<>();

	    for (int i = 1; i < verticesSize; ++i) {
		final int index = (upLeftIndex + i) % verticesSize;
		if (chosen[index]) {
		    continue;
		}
		chosen[index] = true;
		final Vertex vertex = verticesList.get(index);
		topDown.add(new VertexWithInfo(index, vertex, upLeft.computeAngle(vertex), upLeft.computeDistance(vertex)));
	    }
	    topDown.sort(null);

	    final int topDownSize = topDown.size();
	    for (int i = 0; i < topDownSize; ++i) {
		final VertexWithInfo vwia = topDown.get(i);
		final int index = vwia.index;
		chosen[index] = true;
		areaVertices[counter++] = vwia.vertex;
	    }
	} else {
	    int i;
	    for (i = 1; i < verticesSize; ++i) {
		final int index = (downRightIndex + i) % verticesSize;
		final Vertex vertex = verticesList.get(index);
		final double angle = downRight.computeAngle(vertex);
		if (!doubleEquals(angle, basedAngle)) {
		    break;
		}
	    }
	    if (i == verticesSize) {
		throwNotAnAreaException();
	    }
	    verticesList.toArray(areaVertices);
	}
    }

    // -----物件方法-----
    /**
     * 傳回平面空間頂點。
     *
     * @return 傳回平面空間頂點
     */
    public Vertex[] getAreaVertices() {
	return areaVertices.clone();
    }

    /**
     * 計算面積。
     *
     * @return 傳回面積(平方單位)
     */
    public double computeArea() {
	final int areaVerticesLength = areaVertices.length;

	double matrixValue;
	double sum;

	sum = 0;
	for (int i = 0; i < areaVerticesLength; i++) {
	    sum += areaVertices[i].x * areaVertices[(i + 1) % areaVerticesLength].y;
	}
	matrixValue = sum;
	sum = 0;
	for (int i = areaVerticesLength; i >= 1; i--) {
	    sum += areaVertices[i % areaVerticesLength].x * areaVertices[(i - 1)].y;
	}
	matrixValue = matrixValue - sum;
	return Math.abs(matrixValue / 2);
    }

    /**
     * <p>
     * 判斷傳入的座標點是否在平面空間內。
     * </p>
     *
     * <p>
     * 演算法參考自：PNPOLY - Point Inclusion in Polygon Test W. Randolph Franklin
     * (WRF)
     * </p>
     *
     * @param vertex 傳入座標點
     * @return 傳回傳入的座標點是否在平面空間內
     */
    public boolean isInTheLocation(final Vertex vertex) {
	if (vertex == null) {
	    return false;
	}
	final int areaVerticesLength = areaVertices.length;

	int i, j;
	boolean c = false;
	for (i = 0, j = areaVerticesLength - 1; i < areaVerticesLength; j = i++) {
	    final Vertex v1 = areaVertices[i];
	    final Vertex v2 = areaVertices[j];
	    if (((v1.y >= vertex.y) != (v2.y >= vertex.y)) && (vertex.x <= (v2.x - v1.x) * (vertex.y - v1.y) / (v2.y - v1.y) + v1.x)) {
		c = !c;
	    }
	}
	return c;
    }

    /**
     * <p>
     * 計算傳入的座標點和平面空間的距離。
     * </p>
     *
     * <p>
     * 先判斷座標點是否在平面空間內，是的話回傳0；如果不是的話，再計算座標點與所有邊線的最小距離。
     * </p>
     *
     * @param vertex 傳入座標點
     * @return 傳回傳入的座標點和平面空間的距離，如果座標點在平面空間內，傳回0
     */
    public double computeDistance(final Vertex vertex) {
	if (isInTheLocation(vertex)) {
	    return 0;
	}
	// 計算座標點到每條邊的距離，取最小值
	double distance = Double.POSITIVE_INFINITY;
	final int areaVerticesLength = areaVertices.length;
	for (int i = 0; i < areaVerticesLength; ++i) {
	    final Vertex v1 = areaVertices[i];
	    final Vertex v2 = areaVertices[(i + 1) % areaVerticesLength];

	    // 計算斜率
	    final double m = (v2.y - v1.y) / (v2.x - v1.x);
	    final double mm = -1 / m;

	    // 直線方程式: ax + by + c = 0, m = -a/b  (垂直線方程式：ax + c = 0)
	    final double a, b, c, aa, bb, cc;
	    if (Double.isInfinite(m)) {
		a = 1;
		b = 0;
	    } else {
		a = m;
		b = -1;
	    }
	    c = -(a * v1.x + b * v1.y);
	    if (Double.isInfinite(mm)) {
		aa = 1;
		bb = 0;
	    } else {
		aa = mm;
		bb = -1;
	    }
	    cc = -(aa * vertex.x + bb * vertex.y);

	    // 解直線方程式(先消y)
	    final double xx, yy;
	    if (doubleEquals(bb, 0)) {
		// 如果第二式沒有y
		xx = -cc;
		yy = -(a * xx + c) / b;
	    } else {
		xx = -(c * bb - cc * b) / (a * bb - aa * b);
		yy = -(aa * xx + cc) / bb;
	    }
	    final Vertex v;
	    try {
		v = createVertex(xx, yy);
	    } catch (final Exception ex) {
		continue;
	    }

	    // 判斷是不是在線上，如果在線上，就直接算距離；如果不在線上，就找到兩端點最小的距離
	    final double d = v1.computeDistance(v2);
	    final double d1 = v.computeDistance(v1);
	    final double d2 = v.computeDistance(v2);
	    final double dd;
	    if (d1 < d && d2 < d) {
		// 在線上
		dd = vertex.computeDistance(v);
	    } else {
		// 不在線上
		dd = Math.min(vertex.computeDistance(v1), vertex.computeDistance(v2));
	    }
	    distance = Math.min(distance, dd);
	}
	return distance;
    }

    /**
     * 建立座標點。
     *
     * @param x 傳入X座標值
     * @param y 傳入Y座標值
     * @return 傳回座標點
     */
    protected Vertex createVertex(final double x, final double y) {
	return Vertex.vertex(x, y);
    }

    /**
     * 拋出不是區域的例外。
     *
     * @throws RuntimeException 拋出例外
     */
    private void throwNotAnAreaException() throws RuntimeException {
	throw new RuntimeException("Not an area.");
    }

    /**
     * 轉成字串。
     *
     * @return 傳回字串
     */
    @Override
    public String toString() {
	return String.format("%s: %f", Arrays.toString(areaVertices), computeArea());
    }
}
