package com.example.workaagencyapi;
/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

import com.example.workaagencyapi.api.VacanciesController;
import com.example.workaagencyapi.service.VacancyService;
import com.example.workaagencyapi.tables.VacancyTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the VacanciesController.
 */
class VacanciesControllerTest {

    @InjectMocks
    private VacanciesController vacanciesController;

    @Mock
    private VacancyService vacancyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case for retrieving vacancies with valid data.
     * Ensures that the method returns a page of vacancies and HTTP status OK.
     */
    @Test
    void testGetVacancies_ReturnsVacancies() {
        VacancyTable vacancy = new VacancyTable();
        Page<VacancyTable> vacanciesPage = new PageImpl<>(Collections.singletonList(vacancy));

        when(vacancyService.getPaginatedVacancies(0)).thenReturn(vacanciesPage);

        ResponseEntity<Page<VacancyTable>> response = vacanciesController.getVacancies(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vacanciesPage, response.getBody());
    }

    /**
     * Test case for retrieving vacancies when no data is available.
     * Ensures that the method returns HTTP status Not Found.
     */
    @Test
    void testGetVacancies_ReturnsNotFound() {
        Page<VacancyTable> vacanciesPage = Page.empty();
        when(vacancyService.getPaginatedVacancies(0)).thenReturn(vacanciesPage);

        ResponseEntity<Page<VacancyTable>> response = vacanciesController.getVacancies(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test case for retrieving vacancy counts by city with valid data.
     * Ensures that the method returns a map of city counts and HTTP status OK.
     */
    @Test
    void testGetCityCounts_ReturnsCityCounts() {
        Map<String, Integer> cityCounts = new HashMap<>();
        cityCounts.put("New York", 5);
        when(vacancyService.getVacanciesCountByCity()).thenReturn(cityCounts);

        ResponseEntity<Map<String, Integer>> response = vacanciesController.getCityCounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cityCounts, response.getBody());
    }

    /**
     * Test case for retrieving vacancy counts by city when no data is available.
     * Ensures that the method returns HTTP status Not Found.
     */
    @Test
    void testGetCityCounts_ReturnsNotFound() {
        // Arrange
        Map<String, Integer> cityCounts = Collections.emptyMap();
        when(vacancyService.getVacanciesCountByCity()).thenReturn(cityCounts);

        // Act
        ResponseEntity<Map<String, Integer>> response = vacanciesController.getCityCounts();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test case for retrieving the top 10 popular titles.
     * Ensures that the method returns only the top 10 popular titles.
     */
    @Test
    void testGetTopPopularTitles_ReturnsOnlyTop10Titles() {
        List<Map<String, Integer>> allVacancies = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Integer> titleCount = new HashMap<>();
            titleCount.put(STR."Developer\{i}", 100 - i);
            allVacancies.add(titleCount);
        }

        List<Map<String, Integer>> expectedTop10Titles = new ArrayList<>(allVacancies.subList(0, 10));

        when(vacancyService.getTop10PopularTitles()).thenReturn(expectedTop10Titles);

        ResponseEntity<List<Map<String, Integer>>> response = vacanciesController.getTopPopularTitles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTop10Titles, response.getBody());
    }

    /**
     * Test case for retrieving the top 10 popular titles when no data is available.
     * Ensures that the method returns HTTP status Not Found.
     */
    @Test
    void testGetTopPopularTitles_ReturnsNotFound() {
        List<Map<String, Integer>> topTitles = Collections.emptyList();
        when(vacancyService.getTop10PopularTitles()).thenReturn(topTitles);

        ResponseEntity<List<Map<String, Integer>>> response = vacanciesController.getTopPopularTitles();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
