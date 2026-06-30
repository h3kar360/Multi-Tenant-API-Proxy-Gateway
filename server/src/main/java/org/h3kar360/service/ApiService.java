package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ApiResponseDto;
import org.h3kar360.dto.InputApiInfoDto;
import org.h3kar360.dto.UpdateApiInfoDto;
import org.h3kar360.model.Api;
import org.h3kar360.model.Client;
import org.h3kar360.repository.ApiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final ApiRepository apiRepository;

    public ApiResponseDto addApi(Client client, InputApiInfoDto inputApiInfoDto) {
        Api api = new Api();

        api.setApiName(inputApiInfoDto.getApiName());
        api.setApiUrl(inputApiInfoDto.getApiUrl());

        if(inputApiInfoDto.getConnectTimeout() != null)
            api.setConnectTimeout(inputApiInfoDto.getConnectTimeout());
        if(inputApiInfoDto.getReadTimeout() != null)
            api.setReadTimeout(inputApiInfoDto.getReadTimeout());

        api.setClient(client);

        Api savedApi = apiRepository.save(api);
        return toDto(savedApi);
    }

    public List<ApiResponseDto> getAllApi(long clientId) {
        List<Api> apis = apiRepository.findByClientId(clientId);

        return apis.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ApiResponseDto updateApi(long clientId, long apiId, UpdateApiInfoDto updateApiInfoDto) {
        Api selectedApi = apiRepository.findByIdAndClientId(clientId, apiId)
                .orElseThrow(() -> new RuntimeException("API not found"));

        if(updateApiInfoDto.getApiName() != null)
            selectedApi.setApiName(updateApiInfoDto.getApiName());
        if(updateApiInfoDto.getApiUrl() != null)
            selectedApi.setApiUrl(updateApiInfoDto.getApiUrl());
        if(updateApiInfoDto.getConnectTimeout() != null)
            selectedApi.setConnectTimeout(updateApiInfoDto.getConnectTimeout());
        if(updateApiInfoDto.getReadTimeout() != null)
            selectedApi.setReadTimeout(updateApiInfoDto.getReadTimeout());

        Api updatedApi = apiRepository.save(selectedApi);

        return toDto(updatedApi);
    }

    public void deleteApi(long clientId, long apiId) {
        Api selectedApi = apiRepository.findByIdAndClientId(clientId, apiId)
                .orElseThrow(() -> new RuntimeException("API not found"));

        apiRepository.deleteById(selectedApi.getId());
    }

    private ApiResponseDto toDto(Api api) {
        ApiResponseDto dto = new ApiResponseDto();
        dto.setId(api.getId());
        dto.setApiName(api.getApiName());
        dto.setApiUrl(api.getApiUrl());
        dto.setConnectTimeout(api.getConnectTimeout());
        dto.setReadTimeout(api.getReadTimeout());
        return dto;
    }
}
