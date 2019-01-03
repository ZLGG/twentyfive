package com.example.twentyfive.vo;

import lombok.Data;

@Data
public class CmptAreaVo {
    private Long id;
    private String code;
    private String name;
    private String parent_code;
    private String store_code;
    private String post_code;
    private Long tenant_id;
    private String create_time;
    private String cteate_person;
    private String update_time;
    private String update_person;
    private int dr;
}
