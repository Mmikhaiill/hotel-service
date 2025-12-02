package ru.example.hotel.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO для параметров пагинации и сортировки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    private String sortField;

    @Builder.Default
    private boolean sortAscending = true;

    public int getOffset() {
        return page * size;
    }
}
