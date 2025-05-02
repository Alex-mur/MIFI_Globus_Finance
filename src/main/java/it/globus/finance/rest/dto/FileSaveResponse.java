package it.globus.finance.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileSaveResponse(
    String filename,

    @JsonProperty("download_url")
    String downloadUrl
) {}
