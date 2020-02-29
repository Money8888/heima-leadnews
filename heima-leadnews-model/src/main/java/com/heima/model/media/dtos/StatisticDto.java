package com.heima.model.media.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StatisticDto {

    private Short type;
    private Date stime;
    private Date etime;

    private List<String> time;

}
