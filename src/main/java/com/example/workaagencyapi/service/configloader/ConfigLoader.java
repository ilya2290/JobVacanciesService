/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi.service.configloader;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class for loading and managing application settings related to pagination and vacancy parsing.
 **/
@Getter
@Component
public class ConfigLoader {

    @Value("${max.page.count.parse}")
    private int maxPageCountParse;

    @Value("${is.limited.pagination}")
    private boolean isLimitedPagination;

    @Value("${pagination.enable}")
    private boolean paginationEnable;

    @Value("${vacancies.refresh.count}")
    private int vacanciesRefreshCount;

    @Value("${refresh.parsing.pages}")
    private int refreshParsingPages;

    @Value("${page.vacancies.count}")
    private int pageVacanciesCount;

}
