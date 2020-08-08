package com.medium.crawler.dtos;

import com.medium.crawler.model.Medium;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediumResponseDTO {
    Medium medium;
    String tags;
    int index;
}
