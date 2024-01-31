package com.xwintop.xJavaFxTool.services.littleTools.geo;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jaitools.jts.CoordinateSequence2D;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Code that Changed the World
 *
 * @author Pro
 * @date 2022/3/18
 */
public class WKTCoordinateTransform extends CoordinateTransform {

    private final MathTransform transformTo84;

    private final MathTransform transformToEPSG;

    public WKTCoordinateTransform(String crsDesc) throws FactoryException {
        super(crsDesc);
        CoordinateReferenceSystem crs = CRS.parseWKT(crsDesc);
        transformTo84 = CRS.findMathTransform(crs, DefaultGeographicCRS.WGS84, true);
        transformToEPSG = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs, true);
    }

    @Override
    public Point transformToWgs84(double x, double y, double offsetX, double offsetY) throws TransformException {
        Point p = new Point(new CoordinateSequence2D(x + offsetX, y + offsetY), new GeometryFactory());
        return (Point) JTS.transform(p, transformTo84);
    }

    @Override
    public Point transform(Point wgs84Point, double offsetX, double offsetY) throws TransformException {
        Point point = (Point) JTS.transform(wgs84Point, transformToEPSG);
        point.getCoordinate().setX(point.getX() + offsetX);
        point.getCoordinate().setY(point.getY() + offsetY);
        return point;
    }
}
