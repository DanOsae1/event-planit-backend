package com.osaebros.eventplanner.repository.specifications;

import com.osaebros.eventplanner.entity.ServiceProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SearchSpecificationTest {


    @Mock
    private Root<ServiceProvider> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate predicate;
    @Mock
    private Expression<String> stringExpression;
    @Mock
    private Order order;
    @Mock
    private Path<Object> objectPath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchForProvidersv2_OnlySearchTerm() {
        // Given
        String searchTerm = "test";
        when(root.get(anyString())).thenReturn(objectPath);
        when(criteriaBuilder.lower(any())).thenReturn(stringExpression);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(criteriaBuilder.and(any())).thenReturn(predicate);
        when(criteriaBuilder.asc(any())).thenReturn(order);

        // When
        Specification<ServiceProvider> spec = SearchSpecification.searchForProviders(searchTerm, null, null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        verify(criteriaBuilder, times(3)).like(any(), eq("%" + searchTerm.toLowerCase() + "%"));
        verify(criteriaBuilder).and(any());
        verify(query).orderBy(any(Order.class));
    }

//    @Test
//    void testSearchForProvidersv2_WithLocation() {
//        // Given
//        Double lat = 51.503399;
//        Double lon = -0.119519;
//        Double radius = 10.0;
//        when(root.get(anyString())).thenReturn(objectPath);
//        when(criteriaBuilder.function(eq("ST_DWithin"), eq(Boolean.class), any(), any(), any(), any())).thenReturn(predicate);
//        when(criteriaBuilder.function(eq("ST_Transform"), eq(Geometry.class), any(), any())).thenReturn(predicate);
//        when(criteriaBuilder.function(eq("ST_GeomFromText"), eq(Geometry.class), any(), any())).thenReturn(predicate);
//        when(criteriaBuilder.isTrue(any())).thenReturn(predicate);
//        when(criteriaBuilder.and(any())).thenReturn(predicate);
//        when(criteriaBuilder.literal(any())).thenReturn(stringExpression);
//        when(criteriaBuilder.asc(any())).thenReturn(order);
//
//        // When
//        Specification<ServiceProvider> spec = SearchSpecification.searchForProviders(null, lat, lon, radius);
//        Predicate result = spec.toPredicate(root, query, criteriaBuilder);
//
//        // Then
//        verify(criteriaBuilder).function(eq("ST_DWithin"), eq(Boolean.class), any(), any(), any(), any());
//        verify(criteriaBuilder).and(any());
//        verify(query).orderBy(any(Order.class));
//    }

//    @Test
//    void testSearchForProvidersv2_WithSearchTermAndLocation() {
//// Given
//        String searchTerm = "test";
//        Double lat = 51.503399;
//        Double lon = -0.119519;
//        Double radius = 10.0;
//        when(root.get(anyString())).thenReturn(objectPath);
//        when(criteriaBuilder.lower(any())).thenReturn(stringExpression);
//        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
//        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
//        when(criteriaBuilder.function(eq("ST_DWithin"), eq(Boolean.class), any(), any(), any(), any())).thenReturn(predicate);
//        when(criteriaBuilder.function(eq("ST_Transform"), eq(Geometry.class), any(), any())).thenReturn(predicate);
//        when(criteriaBuilder.function(eq("ST_GeomFromText"), eq(Geometry.class), any(), any())).thenReturn(predicate);
//        when(criteriaBuilder.isTrue(any())).thenReturn(predicate);
//        when(criteriaBuilder.and(any())).thenReturn(predicate);
//        when(criteriaBuilder.literal(any())).thenReturn(stringExpression);
//        when(criteriaBuilder.asc(any())).thenReturn(order);
//
//        // When
//        Specification<ServiceProvider> spec = SearchSpecification.searchForProviders(searchTerm, lat, lon, radius);
//        Predicate result = spec.toPredicate(root, query, criteriaBuilder);
//
//        // Then
//        verify(criteriaBuilder, times(3)).like(any(), eq("%" + searchTerm.toLowerCase() + "%"));
//        verify(criteriaBuilder).function(eq("ST_DWithin"), eq(Boolean.class), any(), any(), any(), any());
//        verify(criteriaBuilder).and(any());
//        verify(query).orderBy(any(Order.class));
//    }
}