package com.xwintop.xJavaFxTool.services.littleTools.geo;

import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * 坐标转换接口
 *
 * @author Pro
 * @date 2022/3/18
 */
public abstract class CoordinateTransform {

    /**
     * 创建本坐标系与wgs84互转机制
     *
     * @param crsDesc        坐标系统具体描述
     * @throws FactoryException 由于坐标系填写错误解析可能出现异常
     */
    public CoordinateTransform(String crsDesc) throws FactoryException {

    }

    /**
     * 将本坐标系转为wgs84坐标系坐标
     *
     * @param x       x
     * @param y       y
     * @param offsetX 转换前加入x偏移量
     * @param offsetY 转换前加入y偏移量
     * @return 转换后坐标
     * @throws TransformException 转换过程可能报错
     */
    public abstract Point transformToWgs84(double x, double y, double offsetX, double offsetY) throws TransformException;

    /**
     * @param wgs84Point wgs84坐标
     * @param offsetX    转换后加入x偏移量
     * @param offsetY    转换后加入y偏移量
     * @return 转换后坐标
     * @throws TransformException 转换过程可能报错
     */
    public abstract Point transform(Point wgs84Point, double offsetX, double offsetY) throws TransformException;

}
