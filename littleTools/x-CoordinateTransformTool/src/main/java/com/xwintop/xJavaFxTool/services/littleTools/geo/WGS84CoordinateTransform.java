//package com.xwintop.xJavaFxTool.services.littleTools.geo;
//
//import org.jaitools.jts.CoordinateSequence2D;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.opengis.referencing.FactoryException;
//
///**
// * Code that Changed the World
// *
// * @author Pro
// * @date 2022/3/18
// */
//public class WGS84CoordinateTransform extends CoordinateTransform {
//
//    public WGS84CoordinateTransform(String crsDesc) throws FactoryException {
//        super(crsDesc);
//    }
//
//    @Override
//    public Point transformToWgs84(double x, double y, double offsetX, double offsetY) {
//        return new Point(new CoordinateSequence2D(x + offsetX, y + offsetY), new GeometryFactory());
//    }
//
//    @Override
//    public Point transform(Point wgs84Point, double offsetX, double offsetY) {
//        wgs84Point.getCoordinate().setX(wgs84Point.getX() + offsetX);
//        wgs84Point.getCoordinate().setY(wgs84Point.getY() + offsetY);
//        return wgs84Point;
//    }
//}
