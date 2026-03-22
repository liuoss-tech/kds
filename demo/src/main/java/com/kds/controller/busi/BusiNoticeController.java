package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.NoticeFormDTO;
import com.kds.model.dto.NoticeQueryDTO;
import com.kds.model.vo.NoticeDetailVO;
import com.kds.model.vo.NoticeReceiptSummaryVO;
import com.kds.model.vo.NoticeVO;
import com.kds.service.BusiNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/busi/notice")
public class BusiNoticeController {

    @Autowired
    private BusiNoticeService busiNoticeService;

    @GetMapping("/manage/page")
    public Result<Page<NoticeVO>> getManagePage(NoticeQueryDTO queryDTO) {
        return Result.success(busiNoticeService.getManagePage(queryDTO));
    }

    @PostMapping
    public Result<?> publishNotice(@Validated @RequestBody NoticeFormDTO formDTO) {
        busiNoticeService.publishNotice(formDTO);
        return Result.success("通知发布成功");
    }

    @GetMapping("/{id}")
    public Result<NoticeDetailVO> getNoticeDetail(@PathVariable Long id) {
        return Result.success(busiNoticeService.getNoticeDetail(id));
    }

    @GetMapping("/{id}/receipts")
    public Result<NoticeReceiptSummaryVO> getNoticeReceipts(@PathVariable Long id) {
        return Result.success(busiNoticeService.getNoticeReceipts(id));
    }

    @GetMapping("/view/page")
    public Result<Page<NoticeVO>> getViewPage(NoticeQueryDTO queryDTO) {
        return Result.success(busiNoticeService.getViewPage(queryDTO));
    }

    @PostMapping("/{id}/confirm")
    public Result<?> confirmNotice(@PathVariable Long id) {
        busiNoticeService.confirmNotice(id);
        return Result.success("确认成功");
    }
}