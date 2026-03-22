package com.kds.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class NoticeReceiptSummaryVO {

    private Integer total;

    private Integer confirmed;

    private Integer unconfirmed;

    private List<NoticeReceiptVO> details;
}