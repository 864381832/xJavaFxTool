//package com.xwintop.xJavaFxTool.services.littleTools.geo;
//
//import com.xwintop.xJavaFxTool.utils.CoordinateTransformUtil;
//import org.jaitools.jts.CoordinateSequence2D;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.opengis.referencing.FactoryException;
//import org.opengis.referencing.operation.TransformException;
//
///**
// * Code that Changed the World
// *
// * @author Pro
// * @date 2022/3/18
// */
//public class BD09CoordinateTransform extends CoordinateTransform {
//
//    public BD09CoordinateTransform(String crsDesc) throws FactoryException {
//        super(crsDesc);
//    }
//
//    @Override
//    public Point transformToWgs84(double x, double y, double offsetX, double offsetY) throws TransformException {
//        Point p;
//        double[] toWGS84 = CoordinateTransformUtil.transformBD09ToWGS84(x + offsetX, y + offsetY);
//        p = new Point(new CoordinateSequence2D(toWGS84[0], toWGS84[1]), new GeometryFactory());
//        return p;
//    }
//
//    @Override
//    public Point transform(Point wgs84Point, double offsetX, double offsetY) throws TransformException {
//        Point p;
//        double[] toBD09 = CoordinateTransformUtil.transformWGS84ToBD09(wgs84Point.getX(), wgs84Point.getY());
//        p = new Point(new CoordinateSequence2D(toBD09[0] + offsetX, toBD09[1] + offsetY), new GeometryFactory());
//        return p;
//    }
//}
