/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi.service;

import com.example.workaagencyapi.repositories.VacanciesRepository;
import com.example.workaagencyapi.service.config.ConfigLoader;
import com.example.workaagencyapi.tables.VacancyTable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing job vacancies,
 * including fetching, parsing, and saving data from external APIs,
 * and providing various vacancy-related operations.
 */
@Getter
@Service
public class VacancyService {

    private static final String API_URL = "https://www.arbeitnow.com/api/job-board-api";
    private static final Logger logger = LogManager.getLogger(VacancyService.class);

    private final ConfigLoader configLoader;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public final VacanciesRepository vacanciesRepository;

    @Getter
    private List<VacancyTable> vacancyList = new ArrayList<>();

    /**
     * Constructs a new instance of {@link VacancyService} with the specified dependencies.
     *
     * @param vacanciesRepository the repository used for interacting with the database
     * @param configLoader        the configuration loader for retrieving application settings
     * @param restTemplate        the RestTemplate for making HTTP requests
     * @param objectMapper        the ObjectMapper for JSON serialization and deserialization
     */
    @Autowired
    public VacancyService(VacanciesRepository vacanciesRepository, ConfigLoader configLoader, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.vacanciesRepository = vacanciesRepository;
        this.configLoader = configLoader;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Initializes the service by loading all pages of vacancies and saving them to the database.
     * This method is automatically called after the bean's properties have been set,
     * due to the {@link PostConstruct} annotation.
     */
    @PostConstruct
    public void initialize() {
        this.loadAllPages();
        this.saveVacanciesToDBService();
    }

    /**
     * Checks if a vacancy already exists in the database based on its URL.
     *
     * @param vacancy the vacancy to check
     * @return true if the vacancy exists, false otherwise
     */
    public boolean isVacancyExisting(VacancyTable vacancy) {
        return this.getVacanciesExistingURLSFromDB().contains(vacancy.getUrl());
    }

    /**
     * Retrieves a page of vacancies with the specified page number.
     *
     * @param page the page number to retrieve
     * @return a page of {@link VacancyTable} entities
     */
    public Page<VacancyTable> getPaginatedVacancies(int page) {
        Pageable pageable = PageRequest.of(page, this.configLoader.getPageVacanciesCount(), Sort.by(Sort.Order.desc("id")));
        return vacanciesRepository.findAll(pageable);
    }

    /**
     * Retrieves the top 10 most popular job titles with their occurrence counts.
     *
     * @return a list of maps where each map contains a title and its count
     */
    public List<Map<String, Integer>> getTop10PopularTitles() {
        List<Map<String, Object>> results = vacanciesRepository.findTopPopularTitles();

        return results.stream()
                .map(result -> Map.of(
                        (String) result.get("title"),
                        ((Number) result.get("count")).intValue()
                ))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all existing vacancy URLs from the database.
     *
     * @return a list of existing vacancy URLs
     */
    public List<String> getVacanciesExistingURLSFromDB() {
        assert vacanciesRepository != null;
        return vacanciesRepository.findAllUrl();
    }

    /**
     * Retrieves the count of vacancies by city and sorts them in descending order.
     *
     * @return a map of city names to their vacancy counts, sorted in descending order
     */
    public Map<String, Integer> getVacanciesCountByCity() {
        List<Map<String, Object>> cityCountsAsMap = vacanciesRepository.findCityCountsAsMap();
        Map<String, Integer> cityCounts = new HashMap<>();

        for (Map<String, Object> result : cityCountsAsMap) {
            String location = (String) result.get("location");
            Integer cityCount = ((Number) result.get("city_count")).intValue();
            cityCounts.put(location, cityCount);
        }

        return cityCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, _) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Fetches job data from an external API and adds it to the vacancy list.
     *
     * @param page the page number to fetch
     */
    public void jobParsingService(int page) {
        HttpURLConnection connection = null;
        Scanner scanner = null;

        try {
            URL url = URI.create(String.format("%s?page=%d", API_URL, page)).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IllegalStateException(String.format("HttpResponseCode: %d", responseCode));
            }

            StringBuilder inline = new StringBuilder();
            scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            JsonNode root = objectMapper.readTree(inline.toString());
            JsonNode jobArray = root.path("data");

            for (JsonNode node : jobArray) {
                VacancyTable vacancy = objectMapper.treeToValue(node, VacancyTable.class);
                vacancyList.add(vacancy);
            }
        } catch (IOException e) {
            logger.error("Error occurred while parsing job data: ", e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Loads job data from all available pages and adds it to the vacancy list.
     */
    public void loadAllPages() {
        int page = 1;

        boolean hasMorePages = true;

        while (hasMorePages && (!configLoader.isLimitedPagination() || page <= configLoader.getMaxPageCountParse())) {
            int initialSize = vacancyList.size();
            jobParsingService(page);

            if (vacancyList.size() == initialSize) {
                hasMorePages = false;
            } else {
                page++;
            }
        }
    }

    /**
     * Saves incoming vacancies to the database, checking for existence if configured.
     */
    public void saveIncomingVacanciesToDB() {
        saveVacanciesToDB(configLoader.getVacanciesRefreshCount(), true);
    }

    /**
     * Saves a specified number of vacancies to the database, optionally checking if they already exist.
     *
     * @param maxVacancies   the maximum number of vacancies to save
     * @param checkExistence whether to check for existing vacancies before saving
     *   <p>
     *   Vacancies are saved from the end of the list to the beginning. If `checkExistence` is true, only new
     *   vacancies (not already in the database) are saved. The vacancy list is cleared after saving.
     */
    private void saveVacanciesToDB(int maxVacancies, boolean checkExistence) {
        if (vacancyList == null || vacancyList.isEmpty()) {
            logger.info("Vacancies list is empty!");

            return;
        }

        int saveCount = Math.min(maxVacancies, vacancyList.size());

        for (int i = saveCount - 1; i >= 0; i--) {
            VacancyTable currentVacancy = vacancyList.get(i);

            if (checkExistence) {
                if (!isVacancyExisting(currentVacancy)) {
                    vacanciesRepository.save(currentVacancy);

                    logger.info(STR."Added vacancy: \{currentVacancy}");
                }
            }
            else
                vacanciesRepository.save(currentVacancy);
        }
        this.vacancyList.clear();
        logger.info("Vacancy list cleared. Size after clearing: 0");
    }

    /**
     * Saves all vacancies currently in the list to the database without checking for existence.
     */
    public void saveVacanciesToDBService() {
        saveVacanciesToDB(vacancyList.size(), false);
    }

    /**
     * Scheduled task to fetch job data from the API at regular intervals.
     */
    @Scheduled(fixedRateString = "${parse.scheduling.interval}")
    public void scheduledJobParsingService() {
        this.jobParsingService(this.configLoader.getRefreshParsingPages());

        logger.info("Scheduled job parsing service is done!");
    }

    /**
     * Scheduled task to save vacancies to the database at regular intervals.
     */
    @Scheduled(fixedRateString = "${save.scheduling.interval}")
    public void scheduledSaveVacanciesToDBService() {
        this.saveIncomingVacanciesToDB();

        logger.info("Scheduled job saving service is done!");
    }

}