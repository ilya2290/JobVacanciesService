/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi.tables;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a job vacancy entity in the database.
 * <p>
 * Maps to the "vacancies" table and contains attributes such as job title, company name,
 * description, remote status, URL, tags, job types, location, and creation timestamp.
 * <p>
 * Uses JPA annotations for ORM mapping and Lombok annotations for boilerplate code reduction.
 * <p>
 * Specifically:
 * - **Field names match JSON keys:** The field names (`slug`, `company_name`, `title`, etc.) are chosen to
 *   match the expected JSON keys from the API responses. This allows Jackson to automatically map
 *   JSON properties to the corresponding fields without requiring additional configuration.
 * - **Consistency with external data sources:** By aligning the field names with the keys used in the
 *   JSON payloads and the database schema, the class ensures consistency and avoids mapping errors.
 * - **Integration with HttpClient:** When using HttpClient to retrieve data, the response JSON fields
 *   should align with these field names, facilitating easier extraction and handling of the data.
 * <p>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vacancies")

public class VacancyTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column()
    private String slug;

    @Column()
    private String company_name;

    @Column()
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")

    private String description;

    @Column()
    private boolean remote;

    @Column()
    private String url;

    @Column()
    private String[] tags;

    @Column()
    private String[] job_types;

    @Column(nullable = false)
    private String location;

    @Column()
    private long created_at;

}
