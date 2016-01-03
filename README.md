MagicLocationChecker
=================================

# Introduction

**MagicLocationChecker** is a Java library which can compute the area of a polygonal region on the (geocentric) coordinates, and compute the distance between two vertices or between a polygonal region and a vertex. It can also check whether a vertex is in a polygonal region or not. It uses [GeographicLib](http://geographiclib.sourceforge.net/ "GeographicLib") library to compute the area of geocentric polygonal regions.

# Usage

## LocationChecker2D Class

**LocationChecker2D** class is in the *org.magiclen.magiclocationchecker* package. It is used for two dimensional coordinate systems.

### Initialize

**LocationChecker2D** can easily create a polygonal region by inputting the vertices of it.

For example, if you want to create an triangle region, you have to know all of the three vertices of this triangle, and input them into the constructor of **LocationChecker2D**. The code will be,

    LocationChecker2D lc = new LocationChecker2D(false, LocationChecker2D.Vertex.vertex(5, 7), LocationChecker2D.Vertex.vertex(9, 2), LocationChecker2D.Vertex.vertex(-4, 8));

The vertices you input have to be sorted by clockwise(CW) or counterclockwise(CCW) order. You can pass `true` to the first parameter to sort them automatically in CCW order.

### Compute the area of a polygonal region

You can use **computeArea** method to compute the area of a polygonal region.

For example,

    LocationChecker2D lc = new LocationChecker2D(false, LocationChecker2D.Vertex.vertex(5, 7), LocationChecker2D.Vertex.vertex(9, 2), LocationChecker2D.Vertex.vertex(-4, 8));
    double area = lc.computeArea();
    System.out.println(area);

The result is 20.5.

### Check a vertex whether it is in a polygonal region or not

You can use **isInTheLocation** method to check whether a vertex is in a polygonal region or not.

For example,

    LocationChecker2D lc = new LocationChecker2D(false, LocationChecker2D.Vertex.vertex(5, 7), LocationChecker2D.Vertex.vertex(9, 2), LocationChecker2D.Vertex.vertex(-4, 8));
    System.out.println(lc.isInTheLocation(LocationChecker2D.Vertex.zeroVertex()));
    System.out.println(lc.isInTheLocation(LocationChecker2D.Vertex.vertex(5, 7))); // This vertex is exactly at the edge of the region.
    System.out.println(lc.isInTheLocation(LocationChecker2D.Vertex.vertex(6, 5.75))); // This vertex is exactly at the edge of the region.
    System.out.println(lc.isInTheLocation(LocationChecker2D.Vertex.vertex(0, 6.5)));
    System.out.println(lc.isInTheLocation(LocationChecker2D.Vertex.vertex(-5, 3)));

The result is,

    false
    true
    true
    true
    false

### Compute the distance between a vertex and a polygonal region

You can use **computeDistance** method to check whether a vertex is in a polygonal region or not.

For example,

    LocationChecker2D lc = new LocationChecker2D(false, LocationChecker2D.Vertex.vertex(5, 7), LocationChecker2D.Vertex.vertex(9, 2), LocationChecker2D.Vertex.vertex(-4, 8));
    System.out.println(lc.computeDistance(LocationChecker2D.Vertex.zeroVertex()));
    System.out.println(lc.computeDistance(LocationChecker2D.Vertex.vertex(5, 7))); // This vertex is exactly at the edge of the region.
    System.out.println(lc.computeDistance(LocationChecker2D.Vertex.vertex(6, 5.75))); // This vertex is exactly at the edge of the region.
    System.out.println(lc.computeDistance(LocationChecker2D.Vertex.vertex(0, 6.5)));
    System.out.println(lc.computeDistance(LocationChecker2D.Vertex.vertex(-5, 3)));

The result is,

    5.587442366156626
    0.0
    0.0
    0.0
    4.958855099964006

Also, if you want to compute the distance or angle between two vertices, you can use **computeDistance** method or **computeAngle** method. Each of them is in **Vertex** class.

For example,

    System.out.println(LocationChecker2D.Vertex.zeroVertex().computeDistance(LocationChecker2D.Vertex.vertex(3, 4)));
    System.out.println(LocationChecker2D.Vertex.zeroVertex().computeAngle(LocationChecker2D.Vertex.vertex(1, 1)));

The result is,

    5.0
    45.0

## GeographyChecker Class

**GeographyChecker** class is in the *org.magiclen.magiclocationchecker* package. It extends **LocationChecker2D** class above. It can deal with geocentric coordinates.

### Initialize

For example, if you want to create an region where National Taiwan University of Science and Technology(NTUST) is, you have to input all of the vertices of this region that are in geocentric coordinate system into the constructor of **GeographyChecker**. The code will be,

    GeographyChecker ntust = new GeographyChecker(false,
    		GeographyChecker.Vertex.vertex("25.011390, 121.541024"),
    		GeographyChecker.Vertex.vertex("25.012858, 121.543207"),
    		GeographyChecker.Vertex.vertex("25.011823, 121.544151"),
    		GeographyChecker.Vertex.vertex("25.012542, 121.545020"),
    		GeographyChecker.Vertex.vertex("25.015576, 121.542660"),
    		GeographyChecker.Vertex.vertex("25.013019, 121.539672"),
    		GeographyChecker.Vertex.vertex("25.011400, 121.540943"));

### Compute the area of NTUST

You can use **computeArea** method to compute the area of a polygonal region.

For example,

    double area = ntust.computeArea();
    System.out.println(area);

The result is 0.12026928132063523 (km<sup>2</sup>).

### Check a vertex(such as a person) whether it(he/she) is in NTUST

You can use **isInTheLocation** method to check whether a vertex is in a polygonal region or not.

For example,

    System.out.println(ntust.isInTheLocation(GeographyChecker.Vertex.vertex("25.013232, 121.542081"))); // in NTUST
    System.out.println(ntust.isInTheLocation(GeographyChecker.Vertex.vertex("25.017423, 121.539731"))); // in NTU
    System.out.println(ntust.isInTheLocation(GeographyChecker.Vertex.vertex("25.033872, 121.564643"))); // in Taipei 101

The result is,

    true
    false
    false

### Compute the distance between a vertex and NTUST

You can use **computeDistance** method to check whether a vertex is in a polygonal region or not.

For example,

    System.out.println(ntust.computeDistance(GeographyChecker.Vertex.vertex("25.013232, 121.542081"))); // in NTUST
    System.out.println(ntust.computeDistance(GeographyChecker.Vertex.vertex("25.017423, 121.539731"))); // in NTU
    System.out.println(ntust.computeDistance(GeographyChecker.Vertex.vertex("25.033872, 121.564643"))); // in Taipei 101

The result is (in kilometers),

    0.0
    0.35301599080637824
    3.0050552573806186

# License

    Copyright 2015-2016 magiclen.org

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

# What's More?

Please check out our web page at

http://magiclen.org/location-checker/
