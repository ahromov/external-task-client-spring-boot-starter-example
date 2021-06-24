package org.camunda.platform.runtime.example.service.rest;

import org.camunda.platform.runtime.example.service.rest.dto.Dto;
import org.camunda.platform.runtime.example.service.vacation.dto.OrgUnitDto;

import java.util.List;

public interface RestService {

    <T extends Dto> T getById(String url, Long id, Class dtoClass);
    <T extends Dto> List<T> getAll(String url, Class<? extends Dto[]> dtoClass);
    <T extends Dto> T create(String url, Dto dto, Class dtoClass);
    <T extends Dto> T update(String url, Dto dto, Class dtoClass);
}
