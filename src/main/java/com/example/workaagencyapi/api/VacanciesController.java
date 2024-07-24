/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi.api;

import com.example.workaagencyapi.service.VacancyService;
import com.example.workaagencyapi.tables.VacancyTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling requests related to vacancies.
 */
@RestController
public class VacanciesController {

    @Autowired
    VacancyService vacancyService;

    /**
     * Retrieves a paginated list of vacancies based on the specified page number.
     *
     * @param page the page number to retrieve, default is 1 (which corresponds to page 0 in pagination)
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link VacancyTable} entities,
     *         or {@link ResponseEntity#notFound()} if no vacancies are found
     */
    @GetMapping("/api/v1/vacancies")
    public ResponseEntity<Page<VacancyTable>> getVacancies(@RequestParam(defaultValue = "1") int page) {
        Page<VacancyTable> vacanciesPage = vacancyService.getPaginatedVacancies(page - 1);

        if (vacanciesPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(vacanciesPage);
    }

    /**
     * Retrieves a map of city names and the number of vacancies in each city.
     *
     * @return a {@link ResponseEntity} containing a {@link Map} where the key is the city name
     *         and the value is the count of vacancies, or {@link ResponseEntity#notFound()} if no data is available
     */
    @GetMapping("/api/v1/vacancies/city-counts")
    public ResponseEntity<Map<String, Integer>> getCityCounts() {
        Map<String, Integer> cityCounts = vacancyService.getVacanciesCountByCity();

        if (cityCounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cityCounts);
    }

    /**
     * Retrieves the top 10 most popular vacancy titles based on their frequency.
     *
     * @return a {@link ResponseEntity} containing a list of maps, where each map represents
     *         a vacancy title and its count, sorted by popularity, or {@link ResponseEntity#notFound()}
     *         if no popular titles are found
     */
    @GetMapping("/api/v1/top-popular-titles")
    public ResponseEntity<List<Map<String, Integer>>> getTopPopularTitles() {
        List<Map<String, Integer>> topTitles = vacancyService.getTop10PopularTitles();

        if (topTitles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(topTitles);
    }
}
