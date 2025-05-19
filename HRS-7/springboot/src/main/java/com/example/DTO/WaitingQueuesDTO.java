package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitingQueuesDTO {
    private Integer queueNumber;
    private String nameHidden;
    private String registerTime;
}
