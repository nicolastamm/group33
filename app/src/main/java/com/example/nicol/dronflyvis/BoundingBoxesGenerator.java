/*
  @author RastaMan aka Nicolas Andreas Tamm Garetto aka NATG
 */
package com.example.nicol.dronflyvis;

import java.util.ArrayList;

public abstract class BoundingBoxesGenerator {
    static ArrayList<Node[]> getBoundingBoxes(ArrayList<Node> markers, double fov, float flightHeight, float aspectRatio, float horizontalOverlap, float verticalOverlap) {
        ArrayList<Node[]> boundingBoxes = new ArrayList<>();
        double fotoWidth = (2.0 * flightHeight) * (Math.tan(Math.toRadians(fov / 2.0)));
        double fotoHeight = fotoWidth * aspectRatio; //Assuming 4:3 aspect ratio
        fotoWidth *= horizontalOverlap; //70% horizontal overlap.
        fotoHeight *= verticalOverlap; //85% vertical overlap.
        Double[] borderCoordinates = Rastering.searchForBorderCoordinates(markers);
        double polygonHeight = Math.abs(borderCoordinates[2] - borderCoordinates[3]);
        polygonHeight *= 111325.0;

        double polygonWidth = Math.abs(borderCoordinates[0] - borderCoordinates[1]);
        polygonWidth *= 111325.0 * Math.cos(Math.toRadians(borderCoordinates[2]));

        int verticalAmountFotos = (int) Math.ceil(polygonHeight / fotoHeight);
        int horizontalAmountFotos = (int) Math.ceil(polygonWidth / fotoWidth);

        int subPolycols = 0;
        int subPolyrows = 0;
        for (int i = 9; i >= 0; i--) {
            if ((i + 1) * fotoWidth < 300.0) {
                subPolycols = i;
                break;
            }
        }
        for (int i = 11; i >= 0; i--) {
            if ((i + 1) * fotoHeight < 300.0) {
                subPolyrows = i;
                break;
            }
        }

        double fotoWidthCoord = Rastering.metersToLong(fotoWidth, borderCoordinates[3]);
        double fotoHeightCoord = Rastering.metersToLat(fotoHeight);
        double subPolyWidth = subPolycols * fotoWidthCoord;
        double subPolyHeight = subPolyrows * fotoHeightCoord;

        double traversedLongitude = 0;
        double traversedLatitude = 0;
        for (int i = 0; borderCoordinates[0] + traversedLongitude - (subPolyWidth / 2.0) <= borderCoordinates[1] + (subPolyWidth / 2.0); i++) {
            for (int j = 0; borderCoordinates[2] + traversedLatitude - (subPolyHeight / 2.0) <= borderCoordinates[3] + (subPolyHeight / 2.0); j++) {
                //Also save the bounding box for later use.
                boundingBoxes.add(new Node[]
                        {
                                new Node
                                        (
                                                borderCoordinates[0] + traversedLongitude - Rastering.metersToLong(fotoWidth / 2.0, borderCoordinates[2]),
                                                borderCoordinates[2] + traversedLatitude - Rastering.metersToLat(fotoHeight / 2.0),
                                                2
                                        ),
                                new Node
                                        (
                                                borderCoordinates[0] + traversedLongitude - Rastering.metersToLong(fotoWidth / 2.0, borderCoordinates[2]),
                                                borderCoordinates[2] + traversedLatitude + subPolyHeight + Rastering.metersToLat(fotoHeight / 2.0),
                                                2
                                        ),
                                new Node
                                        (
                                                borderCoordinates[0] + traversedLongitude + subPolyWidth + Rastering.metersToLong(fotoWidth / 2.0,
                                                        borderCoordinates[2]), borderCoordinates[2] + traversedLatitude + subPolyHeight + Rastering.metersToLat(fotoHeight / 2.0),
                                                2
                                        ),
                                new Node
                                        (
                                                borderCoordinates[0] + traversedLongitude + subPolyWidth + Rastering.metersToLong(fotoWidth / 2.0, borderCoordinates[2]),
                                                borderCoordinates[2] + traversedLatitude - Rastering.metersToLat(fotoHeight / 2.0),
                                                2
                                        )
                        });
                traversedLatitude += subPolyHeight;
                traversedLatitude += Rastering.metersToLat(fotoHeight);
            }
            traversedLatitude = 0;
            traversedLongitude += subPolyWidth + Rastering.metersToLong(fotoWidth, borderCoordinates[2]);
        }

        return boundingBoxes;
    }
}
