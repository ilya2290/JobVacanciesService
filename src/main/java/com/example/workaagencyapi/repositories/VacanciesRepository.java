/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi.repositories;

import com.example.workaagencyapi.tables.VacancyTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for managing {@link VacancyTable} entities.
 * <p>
 * Provides methods to interact with the "vacancies" table in the database.
 */
@Repository
public interface VacanciesRepository extends JpaRepository<VacancyTable, Integer> {

    /**
     * Retrieves all URLs from the "vacancies" table.
     *
     * @return a list of URLs associated with vacancies
     */
    @Query("SELECT v.url FROM VacancyTable v")
    List<String> findAllUrl();

    /**
     * Retrieves a limited number of vacancies from the "vacancies" table.
     *
     * @param limit the maximum number of vacancies to retrieve
     * @return a list of {@link VacancyTable} entities up to the specified limit
     */
    @Query(value = "SELECT * FROM vacancies LIMIT :limit", nativeQuery = true)
    List<VacancyTable> findLimitedVacancies(@Param("limit") int limit);

    /**
     * Retrieves a list of cities along with the number of vacancies available in each city.
     * <p>
     * This method executes a JPQL query that groups vacancies by their location and counts the number
     * of vacancies for each city. The results are sorted in descending order based on the number of
     * vacancies.
     *
     * @return a {@link List} of {@link Map} objects, where each map represents a city and the count of
     * vacancies in that city. The keys of the map are "location" and "city_count", with the values
     * being the corresponding city name and the count of vacancies.
     */
    @Query("SELECT v.location AS location, COUNT(v) AS city_count " +
            "FROM VacancyTable v " +
            "GROUP BY v.location " +
            "ORDER BY city_count DESC")
    List<Map<String, Object>> findCityCountsAsMap();

    /**
     * Retrieves a list of the most popular vacancy titles based on their frequency.
     * <p>
     * This method executes a JPQL query that groups vacancies by their title, counts the number of
     * occurrences for each title, and filters out titles that occur only once. The results are sorted
     * in descending order based on the count of occurrences.
     *
     * @return a {@link List} of {@link Map} objects, where each map represents a vacancy title and its
     * frequency. The keys of the map are "title" and "count", with the values being the title
     * of the vacancy and the number of times that title appears.
     */
    @Query("SELECT v.title AS title, COUNT(v) AS count " +
            "FROM VacancyTable v " +
            "GROUP BY v.title " +
            "HAVING COUNT(v) > 1 " +
            "ORDER BY count DESC")
    List<Map<String, Object>> findTopPopularTitles();
}
