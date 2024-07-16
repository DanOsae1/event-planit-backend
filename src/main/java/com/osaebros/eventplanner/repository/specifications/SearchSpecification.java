package com.osaebros.eventplanner.repository.specifications;

import com.osaebros.eventplanner.entity.ServiceProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SearchSpecification {




    public static Specification<ServiceProvider> searchForProviders(String searchTerm, Double lat, Double lon, Double sizeOfBoundaryInKM) {
        return (Root<ServiceProvider> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always order by name
            query.orderBy(criteriaBuilder.asc(root.get("username")));
            // Handle search term if not empty and not null
            if (searchTerm != null && !searchTerm.isEmpty()) {
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + searchTerm.toLowerCase() + "%");
                Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + searchTerm.toLowerCase() + "%");
                Predicate referencePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("userAccountRef")), "%" + searchTerm.toLowerCase() + "%");

                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate, referencePredicate));
            }

            // Handle geolocation if location is provided
            if (lat != null && lon != null && sizeOfBoundaryInKM != null) {
                // Create a point from the given lon and lat (note the order)
                String point = String.format("SRID=4326;POINT(%f %f)", lon, lat);

                // Convert KM to degrees (approximate)
                double sizeInDegrees = sizeOfBoundaryInKM * 1000;

                // Use PostGIS ST_DWithin function to find entities within the radius
                predicates.add(criteriaBuilder.isTrue(criteriaBuilder.function(
                        "ST_DWithin",
                        Boolean.class,
                        criteriaBuilder.function("ST_Transform", Geometry.class, root.get("location"), criteriaBuilder.literal(4326)),
                        criteriaBuilder.function("ST_GeomFromText", Geometry.class, criteriaBuilder.literal(point), criteriaBuilder.literal(4326)),
                        criteriaBuilder.literal(sizeInDegrees),
                        criteriaBuilder.literal(true)
                )));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


//centrLISED FORM class="mx-auto max-w-xl pb-12 pt-4"


//    public static Specification<Entity> findByCriteria(String searchTerm, Double lat, Double lon, Double radius, ServiceType serviceType) {
//        return (Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (searchTerm != null && !searchTerm.isEmpty()) {
//                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase() + "%"));
//            }
//
//            if (lat != null && lon != null && radius != null) {
//                double earthRadius = 6371.01; // Earth's radius in kilometers
//                double radius = sizeOfBoundaryInKM / earthRadius;
//
//                double minLat = lat - Math.toDegrees(radius);
//                double maxLat = lat + Math.toDegrees(radius);
//                double minLon = lon - Math.toDegrees(radius / Math.cos(Math.toRadians(lat)));
//                double maxLon = lon + Math.toDegrees(radius / Math.cos(Math.toRadians(lat)));
//
//                // Create the ST_MakeEnvelope expression
//                String envelope = String.format("ST_MakeEnvelope(%f, %f, %f, %f, 4326)", minLon, minLat, maxLon, maxLat);
//
//                predicates.add(criteriaBuilder.isTrue(criteriaBuilder.function(
//                        "ST_DWithin",
//                        Boolean.class,
//                        root.get("location"),
//                        criteriaBuilder.literal(envelope))));
//
//                // Create a point from the given lat and lon
//                String point = String.format("POINT(%f %f)", lon, lat);
//
//                // Use PostGIS ST_DWithin function to find entities within the radius
//                predicates.add(cb.isTrue(cb.function(
//                        "ST_DWithin",
//                        Boolean.class,
//                        root.get("location"),
//                        cb.function("ST_GeographyFromText", Geometry.class, cb.literal(point)),
//                        cb.literal(radius * 1000) // Convert radius to meters
//                )));
//            }
//
//            if (serviceType != null) {
//                predicates.add(cb.equal(root.get("serviceType"), serviceType));
//            }
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//    }
