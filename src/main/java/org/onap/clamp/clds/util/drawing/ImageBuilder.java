/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.clamp.clds.util.drawing;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.UUID;
import org.apache.batik.svggen.SVGGraphics2D;

public class ImageBuilder {

    private Point currentPoint;
    private final int baseLength;
    private final int rectHeight;
    private final SVGGraphics2D g2d;
    private final DocumentBuilder documentBuilder;

    private static final int LINE_THICKNESS = 2;
    private static final int CIRCLE_RADIUS = 17;

    ImageBuilder(SVGGraphics2D svgGraphics2D, DocumentBuilder documentBuilder,
            Point startingPoint, int baseLength, int rectHeight) {
        this.g2d = svgGraphics2D;
        this.documentBuilder = documentBuilder;
        this.currentPoint = new Point(startingPoint);
        this.baseLength = baseLength;
        this.rectHeight = rectHeight;
    }

    ImageBuilder rect(String dataElementId, RectTypes rectType, String text) {
        Point next = new Point(currentPoint.x + baseLength, currentPoint.y);
        Point p = coordinatesForRectangle(currentPoint, next);

        handleBasedOnRectType(rectType, text, p, baseLength, rectHeight);

        documentBuilder.pushChangestoDocument(g2d, dataElementId);
        currentPoint = next;
        return this;
    }

    ImageBuilder arrow() {
        String dataElementId = "Arrow-" + UUID.randomUUID().toString();
        Point to = new Point(currentPoint.x + baseLength, currentPoint.y);
        AwtUtils.drawArrow(g2d, currentPoint, to, LINE_THICKNESS);
        documentBuilder.pushChangestoDocument(g2d, dataElementId);
        currentPoint = to;
        return this;
    }

    ImageBuilder cicrle(String dataElementId, int lineThickness) {
        Point to = new Point(currentPoint.x + 2 * CIRCLE_RADIUS, currentPoint.y);
        Shape circleStart =
            new Ellipse2D.Double(currentPoint.x, currentPoint.y - CIRCLE_RADIUS,
                2 * CIRCLE_RADIUS, 2 * CIRCLE_RADIUS);

        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(lineThickness));
        g2d.draw(circleStart);
        g2d.setStroke(oldStroke);
        documentBuilder.pushChangestoDocument(g2d, dataElementId);
        currentPoint = to;
        return this;
    }

    DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    private void handleBasedOnRectType(RectTypes rectType, String text, Point p, int w, int h) {
        AwtUtils.rectWithText(g2d, text, p, w, h);
        switch (rectType) {
            case COLECTOR:
                g2d.drawLine(p.x + w / 6, p.y, p.x + w / 6, p.y + h);
                break;
            case MICROSERVICE:
                g2d.drawLine(p.x, p.y + 3 * h / 4, p.x + w, p.y + 3 * h / 4);
                break;
            case POLICY:
                g2d.drawLine(p.x, p.y + h / 2, p.x + w / 2, p.y);
                break;
        }
    }

    private Point coordinatesForRectangle(Point from, Point next) {
        int x = from.x;
        int y = from.y - next.y + LINE_THICKNESS / 2;
        return new Point(x,y);
    }

}
