package org.camunda.platform.runtime.example.service.rest;

import org.camunda.platform.runtime.example.service.rest.dto.Dto;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RestServiceImpl implements RestService {

    private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Override
    public <T extends Dto> T getById(String url, Long id, Class dtoClass) {
        return (T) this.restTemplate.getForObject(url + id, dtoClass);
    }

    @Override
    public <T extends Dto> List<T> getAll(String url, Class<? extends Dto[]> dtoClass) {
        return (List<T>) Arrays.asList(restTemplate.getForEntity(url, dtoClass).getBody());
    }

    @Override
    public <T extends Dto> T create(String url, Dto dto, Class dtoClass) {
        return (T) this.restTemplate.postForObject(url, dto, dtoClass);
    }

    @Override
    public <T extends Dto> T update(String url, Dto dto, Class dtoClass) {
        return (T) this.restTemplate.patchForObject(url, dto, dtoClass);
    }
}
