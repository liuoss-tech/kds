package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.NoticeFormDTO;
import com.kds.model.dto.NoticeQueryDTO;
import com.kds.model.entity.BusiNotice;
import com.kds.model.vo.NoticeDetailVO;
import com.kds.model.vo.NoticeReceiptSummaryVO;
import com.kds.model.vo.NoticeVO;

public interface BusiNoticeService extends IService<BusiNotice> {

    Page<NoticeVO> getManagePage(NoticeQueryDTO queryDTO);

    void publishNotice(NoticeFormDTO formDTO);

    NoticeDetailVO getNoticeDetail(Long id);

    NoticeReceiptSummaryVO getNoticeReceipts(Long noticeId);

    Page<NoticeVO> getViewPage(NoticeQueryDTO queryDTO);

    void confirmNotice(Long noticeId);
}