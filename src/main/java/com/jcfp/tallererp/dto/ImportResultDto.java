package com.jcfp.tallererp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImportResultDto {
    private boolean success;
    private String message;
    private int totalRecords;
    private int successfulRecords;

    public ImportResultDto() {}

    public ImportResultDto(boolean success, String message, int totalRecords, int successfulRecords) {
        this.success = success;
        this.message = message;
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
    }

    @Override
    public String toString() {
        return "ImportResultDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", totalRecords=" + totalRecords +
                ", successfulRecords=" + successfulRecords +
                '}';
    }
}