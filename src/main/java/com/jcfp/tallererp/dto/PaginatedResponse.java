package com.jcfp.tallererp.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> items;
    private long total;
}