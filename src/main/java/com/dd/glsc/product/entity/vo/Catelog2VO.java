package com.dd.glsc.product.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2VO {
    private Long catalog1Id;
    private List<Catelog3VO> catalog3List;
    private Long id;
    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3VO {
        private Long catalog2Id;
        private Long id;
        private String name;
    }
}
