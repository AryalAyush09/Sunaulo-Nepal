////package com.project.sunauloNepal.services;
////
////
////import com.fasterxml.jackson.databind.ObjectMapper;
////
////import lombok.extern.slf4j.Slf4j;
////
////import org.springframework.core.io.ClassPathResource;
////import org.wololo.geojson.GeoJsonReader;
////
////import org.springframework.stereotype.Service;
////import org.locationtech.jts.geom.Geometry;
////import org.locationtech.jts.geom.Point;
////import java.io.InputStream;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Map;
////
////@Service
////@Slf4j
////public class GeoService {
////
////    private final List<ProvincePolygon> provincePolygons = new ArrayList<>();
////
////    public GeoService() {
////        loadProvinceGeoJson();
////    }
////
////    private void loadProvinceGeoJson() {
////        try {
////            String[] provinces = {
////                    "province1.geojson", "province2.geojson", "province3.geojson",
////                    "province4.geojson", "province5.geojson", "province6.geojson", "province7.geojson"
////            };
////
////            ObjectMapper mapper = new ObjectMapper();
////            GeoJsonReader reader = new GeoJsonReader();
////
////            for (String provinceFile : provinces) {
////                InputStream is = new ClassPathResource("geojson/" + provinceFile).getInputStream();
////                Map<?, ?> geoMap = mapper.readValue(is, Map.class);
////                String geoJsonString = mapper.writeValueAsString(geoMap.get("geometry"));
////                Geometry polygon = reader.read(geoJsonString);
////                provincePolygons.add(new ProvincePolygon(provinceFile.replace(".geojson", ""), polygon));
////            }
////
////            log.info("Loaded {} province polygons", provincePolygons.size());
////        } catch (Exception e) {
////            log.error("Failed to load province GeoJSON", e);
////        }
////    }
////
////    public String getProvinceFromLatLng(double latitude, double longitude) {
////        try {
////            Point point = new org.locationtech.jts.geom.GeometryFactory().createPoint(
////                    new org.locationtech.jts.geom.Coordinate(longitude, latitude)
////            );
////
////            for (ProvincePolygon provincePolygon : provincePolygons) {
////                if (provincePolygon.getPolygon().contains(point)) {
////                    return provincePolygon.getName();
////                }
////            }
////        } catch (Exception e) {
////            log.error("Error in calculating province for lat:{}, lng:{}", latitude, longitude, e);
////        }
////
////        return "UNKNOWN";
////    }
////
////    private static class ProvincePolygon {
////        private final String name;
////        private final Geometry polygon;
////
////        public ProvincePolygon(String name, Geometry polygon) {
////            this.name = name;
////            this.polygon = polygon;
////        }
////
////        public String getName() {
////            return name;
////        }
////
////        public Geometry getPolygon() {
////            return polygon;
////        }
////    }
////}
//package com;
//
//
