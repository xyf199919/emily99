/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    /**
     * The max image depth level.
     */
    public static final int MAX_DEPTH = 7;
    public static final double MULLAT = 37.892195547244356;
    public static final double MULLON = -122.2998046875;
    public static final double MLRLAT = 37.82280243352756;
    public static final double MLRLON = -122.2119140625;
    public static final double MLON = MLRLON - MULLON;
    public static final double MLAT = MULLAT - MLRLAT;

    /**
     * Takes a user query and finds the grid of images that best matches the query. These images
     * will be combined into one big image (rastered) by the front end. The grid of images must obey
     * the following properties, where image in the grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel (LonDPP)
     * possible, while still covering less than or equal to the amount of longitudinal distance
     * per pixel in the query box for the user viewport size.</li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the above
     * condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params The RasterRequestParams containing coordinates of the query box and the browser
     *               viewport width and height.
     * @return A valid RasterResultParams containing the computed results.
     */
    public RasterResultParams getMapRaster(RasterRequestParams params) {
        if (params.lrlon < params.ullon || params.lrlat > params.ullat) {
            System.out.println("the value of longitude and latitude is wrong");
        }
        if (params.lrlon > MLRLON || params.ullon < MULLON 
                || params.lrlat < MLRLAT || params.ullat > MULLAT) {
            System.out.println("the value of longitude and latitude is wrong");
        }
        double mlondpp = lonDPP(params.lrlon, params.ullon, params.w);
        String[][] renderGrid;

        for (int i = 0; i <= MAX_DEPTH; i++) {
            int minX = 0;
            int minY = 0;
            int maxX = 0;
            int maxY = 0;
            double tempLon = MLON / Math.pow(2, i);
            double templat = MLAT / Math.pow(2, i);
            double initUllon = MULLON;
            double initUllat = MULLAT;
            double initLrlon = initUllon + tempLon;
            double initLrlat = initUllat - templat;
            for (int j = 0; j < Math.pow(2, i); j++) {
                for (int w = 0; w < Math.pow(2, i); w++) {
                    if ((initLrlon + (tempLon * w) >= params.ullon
                            && initUllon + (tempLon * w) <= params.ullon)
                            && (initLrlat - (templat * j) <= params.ullat
                            && initUllat - (templat * j) >= params.ullat)) {
                        minX = w;
                        minY = j;
                    }
                    if ((initLrlon + (tempLon * w) >= params.lrlon
                            && initUllon + (tempLon * w) <= params.lrlon)
                            && (initLrlat - (templat * j) <= params.lrlat
                            && initUllat - (templat * j) >= params.lrlat)) {
                        maxX = w;
                        maxY = j;
                    }
                }
            }
            double temporary = lonDPP(initLrlon + (tempLon * maxX),
                    initUllon + (tempLon * minX), 256 * (1 + maxX - minX));

            if (temporary < mlondpp || i == MAX_DEPTH) {
                int countX = minX;
                int countY = minY;
                renderGrid = new String[maxY - minY + 1][maxX - minX + 1];
                for (int a = 0; a < maxY - minY + 1; a++) {
                    for (int b = 0; b < maxX - minX + 1; b++) {
                        renderGrid[a][b] = "d" + Integer.toString(i) + "_x"
                                + Integer.toString(countX) + "_y"
                                + Integer.toString(countY) + ".png";
                        countX++;
                    }
                    countX = minX;
                    countY++;
                }
                RasterResultParams.Builder rv = new RasterResultParams.Builder();
                rv.setDepth(i);
                rv.setRasterLrLat(initLrlat - (templat * maxY));
                rv.setRasterLrLon(initLrlon + (tempLon * maxX));
                rv.setRasterUlLon(initUllon + (tempLon * minX));
                rv.setRasterUlLat(initUllat - (templat * minY));
                rv.setRenderGrid(renderGrid);
                rv.setQuerySuccess(true);
                return rv.create();
            }
        }



        return RasterResultParams.queryFailed();
    }

    /**
     * Calculates the lonDPP of an image or query box
     *
     * @param lrlon Lower right longitudinal value of the image or query box
     * @param ullon Upper left longitudinal value of the image or query box
     * @param width Width of the query box or image
     * @return lonDPP
     */
    private double lonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }
}
