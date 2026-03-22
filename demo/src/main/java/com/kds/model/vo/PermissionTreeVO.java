package com.kds.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PermissionTreeVO {
    private Long id;
    private Long parentId;
    private String name;
    private List<PermissionTreeVO> children;
}
