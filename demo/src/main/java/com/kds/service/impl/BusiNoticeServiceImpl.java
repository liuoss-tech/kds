package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiNoticeMapper;
import com.kds.mapper.BusiNoticeReceiptMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.BusiStudentParentMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.dto.NoticeFormDTO;
import com.kds.model.dto.NoticeQueryDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiNotice;
import com.kds.model.entity.BusiNoticeReceipt;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.BusiStudentParent;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.NoticeDetailVO;
import com.kds.model.vo.NoticeReceiptSummaryVO;
import com.kds.model.vo.NoticeReceiptVO;
import com.kds.model.vo.NoticeVO;
import com.kds.service.BusiNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusiNoticeServiceImpl extends ServiceImpl<BusiNoticeMapper, BusiNotice> implements BusiNoticeService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private BusiNoticeMapper busiNoticeMapper;

    @Autowired
    private BusiNoticeReceiptMapper busiNoticeReceiptMapper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Autowired
    private BusiStudentParentMapper busiStudentParentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private static final Integer SCOPE_ALL = 1;
    private static final Integer SCOPE_CLASS = 2;

    private static final Integer NOTICE_TYPE_DAILY = 1;
    private static final Integer NOTICE_TYPE_FEE = 2;
    private static final Integer NOTICE_TYPE_HOLIDAY = 3;

    private static final Integer NOTICE_LEVEL_NORMAL = 1;
    private static final Integer NOTICE_LEVEL_TOP = 2;
    private static final Integer NOTICE_LEVEL_URGENT = 3;

    private static final Integer STUDENT_STATUS_READING = 1;

    @Override
    public Page<NoticeVO> getManagePage(NoticeQueryDTO queryDTO) {
        Long userId = loginHelper.getUserId();
        Page<BusiNotice> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<BusiNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiNotice::getIsDeleted, 0);

        if (loginHelper.isTeacher() && !loginHelper.isAdmin()) {
            wrapper.eq(BusiNotice::getPublisherId, userId);
        }

        if (queryDTO.getTitle() != null && !queryDTO.getTitle().isEmpty()) {
            wrapper.like(BusiNotice::getTitle, queryDTO.getTitle());
        }
        if (queryDTO.getNoticeType() != null) {
            wrapper.eq(BusiNotice::getNoticeType, queryDTO.getNoticeType());
        }
        if (queryDTO.getNoticeLevel() != null) {
            wrapper.eq(BusiNotice::getNoticeLevel, queryDTO.getNoticeLevel());
        }
        if (queryDTO.getStartDate() != null) {
            wrapper.ge(BusiNotice::getPublishTime, queryDTO.getStartDate().atStartOfDay());
        }
        if (queryDTO.getEndDate() != null) {
            wrapper.le(BusiNotice::getPublishTime, queryDTO.getEndDate().atTime(LocalTime.MAX));
        }

        wrapper.orderByDesc(BusiNotice::getPublishTime);

        Page<BusiNotice> noticePage = this.page(pageParam, wrapper);
        return convertToNoticeVOPage(noticePage);
    }

    @Override
    @Transactional
    public void publishNotice(NoticeFormDTO formDTO) {
        if (!loginHelper.isTeacher()) {
            throw new BusinessException("只有教师才能发布通知");
        }

        if (formDTO.getTargetScope().equals(SCOPE_ALL)) {
            if (!loginHelper.isAdmin()) {
                throw new BusinessException("只有管理员才能发布全园通知");
            }
        } else if (formDTO.getTargetScope().equals(SCOPE_CLASS)) {
            if (formDTO.getTargetClassId() == null) {
                throw new BusinessException("请选择目标班级");
            }
            List<Long> classIds = loginHelper.getClassIds();
            if (classIds == null || classIds.isEmpty()) {
                throw new BusinessException("您还没有分配班级，无法发布通知");
            }
            if (!classIds.contains(formDTO.getTargetClassId())) {
                throw new BusinessException("您只能为自己的班级发布通知");
            }
        }

        BusiNotice notice = new BusiNotice();
        BeanUtils.copyProperties(formDTO, notice);
        notice.setPublisherId(loginHelper.getUserId());
        notice.setPublishTime(LocalDateTime.now());

        this.save(notice);

        generateReceipts(notice);
    }

    private void generateReceipts(BusiNotice notice) {
        List<Long> studentIds = new ArrayList<>();

        if (notice.getTargetScope().equals(SCOPE_ALL)) {
            LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(BusiStudent::getStatus, STUDENT_STATUS_READING);
            studentWrapper.eq(BusiStudent::getIsDeleted, 0);
            List<BusiStudent> students = busiStudentMapper.selectList(studentWrapper);
            studentIds = students.stream().map(BusiStudent::getId).collect(Collectors.toList());
        } else if (notice.getTargetScope().equals(SCOPE_CLASS) && notice.getTargetClassId() != null) {
            LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(BusiStudent::getClassId, notice.getTargetClassId());
            studentWrapper.eq(BusiStudent::getStatus, STUDENT_STATUS_READING);
            studentWrapper.eq(BusiStudent::getIsDeleted, 0);
            List<BusiStudent> students = busiStudentMapper.selectList(studentWrapper);
            studentIds = students.stream().map(BusiStudent::getId).collect(Collectors.toList());
        }

        if (!studentIds.isEmpty()) {
            List<BusiNoticeReceipt> receipts = studentIds.stream().map(studentId -> {
                BusiNoticeReceipt receipt = new BusiNoticeReceipt();
                receipt.setNoticeId(notice.getId());
                receipt.setStudentId(studentId);
                receipt.setIsConfirmed(0);
                return receipt;
            }).collect(Collectors.toList());
            for (BusiNoticeReceipt receipt : receipts) {
                busiNoticeReceiptMapper.insert(receipt);
            }
        }
    }

    @Override
    public NoticeDetailVO getNoticeDetail(Long id) {
        BusiNotice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        NoticeDetailVO vo = new NoticeDetailVO();
        BeanUtils.copyProperties(notice, vo);

        vo.setNoticeTypeText(getNoticeTypeText(notice.getNoticeType()));
        vo.setNoticeLevelText(getNoticeLevelText(notice.getNoticeLevel()));
        vo.setTargetScopeText(notice.getTargetScope().equals(SCOPE_ALL) ? "全园" : "指定班级");

        if (notice.getTargetClassId() != null) {
            BusiClass busiClass = busiClassMapper.selectById(notice.getTargetClassId());
            if (busiClass != null) {
                vo.setTargetClassName(busiClass.getClassName());
            }
        }

        if (notice.getPublisherId() != null) {
            SysUser publisher = sysUserMapper.selectById(notice.getPublisherId());
            if (publisher != null) {
                vo.setPublisherName(publisher.getRealName());
            }
        }

        if (loginHelper.isParent()) {
            List<Long> childIds = loginHelper.getChildIds();
            if (childIds != null && !childIds.isEmpty()) {
                LambdaQueryWrapper<BusiNoticeReceipt> receiptWrapper = new LambdaQueryWrapper<>();
                receiptWrapper.eq(BusiNoticeReceipt::getNoticeId, id);
                receiptWrapper.in(BusiNoticeReceipt::getStudentId, childIds);
                receiptWrapper.eq(BusiNoticeReceipt::getIsConfirmed, 1);
                long confirmedCount = busiNoticeReceiptMapper.selectCount(receiptWrapper);
                vo.setConfirmStatus(confirmedCount > 0 ? 1 : 0);
            }
        }

        return vo;
    }

    @Override
    public NoticeReceiptSummaryVO getNoticeReceipts(Long noticeId) {
        BusiNotice notice = this.getById(noticeId);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        List<Long> classStudentIds = new ArrayList<>();
        if (notice.getTargetScope().equals(SCOPE_ALL)) {
            LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(BusiStudent::getStatus, STUDENT_STATUS_READING);
            studentWrapper.eq(BusiStudent::getIsDeleted, 0);
            List<BusiStudent> students = busiStudentMapper.selectList(studentWrapper);
            classStudentIds = students.stream().map(BusiStudent::getId).collect(Collectors.toList());
        } else if (notice.getTargetScope().equals(SCOPE_CLASS) && notice.getTargetClassId() != null) {
            LambdaQueryWrapper<BusiStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(BusiStudent::getClassId, notice.getTargetClassId());
            studentWrapper.eq(BusiStudent::getStatus, STUDENT_STATUS_READING);
            studentWrapper.eq(BusiStudent::getIsDeleted, 0);
            List<BusiStudent> students = busiStudentMapper.selectList(studentWrapper);
            classStudentIds = students.stream().map(BusiStudent::getId).collect(Collectors.toList());
        }

        NoticeReceiptSummaryVO summaryVO = new NoticeReceiptSummaryVO();
        summaryVO.setTotal(classStudentIds.size());

        LambdaQueryWrapper<BusiNoticeReceipt> receiptWrapper = new LambdaQueryWrapper<>();
        receiptWrapper.eq(BusiNoticeReceipt::getNoticeId, noticeId);
        receiptWrapper.in(BusiNoticeReceipt::getStudentId, classStudentIds);
        List<BusiNoticeReceipt> receipts = busiNoticeReceiptMapper.selectList(receiptWrapper);

        int confirmed = 0;
        List<NoticeReceiptVO> details = new ArrayList<>();

        Map<Long, BusiStudent> studentMap = null;
        if (!classStudentIds.isEmpty()) {
            List<BusiStudent> students = busiStudentMapper.selectBatchIds(classStudentIds);
            studentMap = students.stream().collect(Collectors.toMap(BusiStudent::getId, s -> s));
        }

        for (BusiNoticeReceipt receipt : receipts) {
            NoticeReceiptVO receiptVO = new NoticeReceiptVO();
            BeanUtils.copyProperties(receipt, receiptVO);

            if (studentMap != null) {
                BusiStudent student = studentMap.get(receipt.getStudentId());
                if (student != null) {
                    receiptVO.setStudentName(student.getStudentName());
                }
            }

            if (receipt.getParentUserId() != null) {
                SysUser parentUser = sysUserMapper.selectById(receipt.getParentUserId());
                if (parentUser != null) {
                    receiptVO.setParentName(parentUser.getRealName());
                }
            }

            if (receipt.getStudentId() != null && receipt.getParentUserId() != null) {
                LambdaQueryWrapper<BusiStudentParent> relationWrapper = new LambdaQueryWrapper<>();
                relationWrapper.eq(BusiStudentParent::getStudentId, receipt.getStudentId());
                relationWrapper.eq(BusiStudentParent::getParentUserId, receipt.getParentUserId());
                BusiStudentParent relation = busiStudentParentMapper.selectOne(relationWrapper);
                if (relation != null) {
                    receiptVO.setRelation(relation.getRelation());
                }
            }

            details.add(receiptVO);

            if (receipt.getIsConfirmed() != null && receipt.getIsConfirmed() == 1) {
                confirmed++;
            }
        }

        summaryVO.setConfirmed(confirmed);
        summaryVO.setUnconfirmed(summaryVO.getTotal() - confirmed);
        summaryVO.setDetails(details);

        return summaryVO;
    }

    @Override
    public Page<NoticeVO> getViewPage(NoticeQueryDTO queryDTO) {
        Page<BusiNotice> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<BusiNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusiNotice::getIsDeleted, 0);

        if (loginHelper.isTeacher() && !loginHelper.isAdmin()) {
            Long userId = loginHelper.getUserId();
            wrapper.ne(BusiNotice::getPublisherId, userId);
        }

        List<Long> classIds = getViewableClassIds();
        if (classIds.isEmpty()) {
            return new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), 0);
        }

        wrapper.and(w -> w
                .eq(BusiNotice::getTargetScope, SCOPE_ALL)
                .or()
                .in(BusiNotice::getTargetClassId, classIds)
        );

        if (queryDTO.getNoticeType() != null) {
            wrapper.eq(BusiNotice::getNoticeType, queryDTO.getNoticeType());
        }
        if (queryDTO.getNoticeLevel() != null) {
            wrapper.eq(BusiNotice::getNoticeLevel, queryDTO.getNoticeLevel());
        }

        wrapper.orderByDesc(BusiNotice::getNoticeLevel)
                .orderByDesc(BusiNotice::getPublishTime);

        Page<BusiNotice> noticePage = this.page(pageParam, wrapper);
        Page<NoticeVO> voPage = convertToNoticeVOPage(noticePage);

        if (loginHelper.isParent()) {
            List<Long> childIds = loginHelper.getChildIds();
            if (childIds != null && !childIds.isEmpty()) {
                for (NoticeVO vo : voPage.getRecords()) {
                    LambdaQueryWrapper<BusiNoticeReceipt> receiptWrapper = new LambdaQueryWrapper<>();
                    receiptWrapper.eq(BusiNoticeReceipt::getNoticeId, vo.getId());
                    receiptWrapper.in(BusiNoticeReceipt::getStudentId, childIds);
                    receiptWrapper.eq(BusiNoticeReceipt::getIsConfirmed, 1);
                    long count = busiNoticeReceiptMapper.selectCount(receiptWrapper);
                    vo.setConfirmStatus(count > 0 ? 1 : 2);
                }
            }
        }

        if (queryDTO.getConfirmStatus() != null) {
            final Integer confirmStatusFilter = queryDTO.getConfirmStatus();
            voPage.setRecords(voPage.getRecords().stream()
                    .filter(vo -> confirmStatusFilter.equals(vo.getConfirmStatus()))
                    .collect(Collectors.toList()));
        }

        return voPage;
    }

    private List<Long> getViewableClassIds() {
        if (loginHelper.isParent()) {
            List<Long> childIds = loginHelper.getChildIds();
            if (childIds == null || childIds.isEmpty()) {
                return new ArrayList<>();
            }
            return busiStudentMapper.selectBatchIds(childIds).stream()
                    .map(BusiStudent::getClassId)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (loginHelper.isTeacher()) {
            return loginHelper.getClassIds();
        } else if (loginHelper.isAdmin()) {
            LambdaQueryWrapper<BusiClass> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(BusiClass::getIsDeleted, 0);
            return busiClassMapper.selectList(classWrapper).stream()
                    .map(BusiClass::getId)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void confirmNotice(Long noticeId) {
        if (!loginHelper.isParent()) {
            throw new BusinessException("只有家长才能确认通知");
        }

        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || childIds.isEmpty()) {
            throw new BusinessException("您还没有绑定幼儿，无法确认通知");
        }

        BusiNotice notice = this.getById(noticeId);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        boolean canConfirm = false;
        if (notice.getTargetScope().equals(SCOPE_ALL)) {
            canConfirm = true;
        } else if (notice.getTargetScope().equals(SCOPE_CLASS)) {
            List<Long> childClassIds = busiStudentMapper.selectBatchIds(childIds).stream()
                    .map(BusiStudent::getClassId)
                    .distinct()
                    .collect(Collectors.toList());
            if (childClassIds.contains(notice.getTargetClassId())) {
                canConfirm = true;
            }
        }

        if (!canConfirm) {
            throw new BusinessException("您无法确认此通知");
        }

        Long userId = loginHelper.getUserId();

        LambdaQueryWrapper<BusiNoticeReceipt> receiptWrapper = new LambdaQueryWrapper<>();
        receiptWrapper.eq(BusiNoticeReceipt::getNoticeId, noticeId);
        receiptWrapper.in(BusiNoticeReceipt::getStudentId, childIds);
        receiptWrapper.eq(BusiNoticeReceipt::getIsConfirmed, 0);

        List<BusiNoticeReceipt> unconfirmedReceipts = busiNoticeReceiptMapper.selectList(receiptWrapper);

        for (BusiNoticeReceipt receipt : unconfirmedReceipts) {
            receipt.setIsConfirmed(1);
            receipt.setParentUserId(userId);
            receipt.setConfirmTime(LocalDateTime.now());
            busiNoticeReceiptMapper.updateById(receipt);
        }
    }

    private Page<NoticeVO> convertToNoticeVOPage(Page<BusiNotice> noticePage) {
        List<BusiNotice> records = noticePage.getRecords();
        if (records.isEmpty()) {
            return new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        }

        List<Long> classIds = records.stream()
                .map(BusiNotice::getTargetClassId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> classNameMap = null;
        if (!classIds.isEmpty()) {
            List<BusiClass> classes = busiClassMapper.selectBatchIds(classIds);
            classNameMap = classes.stream()
                    .collect(Collectors.toMap(BusiClass::getId, BusiClass::getClassName));
        }

        List<Long> publisherIds = records.stream()
                .map(BusiNotice::getPublisherId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> publisherNameMap = null;
        if (!publisherIds.isEmpty()) {
            List<SysUser> publishers = sysUserMapper.selectBatchIds(publisherIds);
            publisherNameMap = publishers.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
        }

        final Map<Long, String> finalClassNameMap = classNameMap;
        final Map<Long, String> finalPublisherNameMap = publisherNameMap;

        List<NoticeVO> voList = records.stream().map(notice -> {
            NoticeVO vo = new NoticeVO();
            BeanUtils.copyProperties(notice, vo);

            vo.setNoticeTypeText(getNoticeTypeText(notice.getNoticeType()));
            vo.setNoticeLevelText(getNoticeLevelText(notice.getNoticeLevel()));
            vo.setTargetScopeText(notice.getTargetScope().equals(SCOPE_ALL) ? "全园" : "指定班级");

            if (finalClassNameMap != null && notice.getTargetClassId() != null) {
                vo.setTargetClassName(finalClassNameMap.get(notice.getTargetClassId()));
            }

            if (finalPublisherNameMap != null && notice.getPublisherId() != null) {
                vo.setPublisherName(finalPublisherNameMap.get(notice.getPublisherId()));
            }

            return vo;
        }).collect(Collectors.toList());

        Page<NoticeVO> voPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private String getNoticeTypeText(Integer type) {
        if (type == null) return "";
        if (type == 1) return "日常通知";
        if (type == 2) return "缴费通知";
        if (type == 3) return "放假通知";
        return "";
    }

    private String getNoticeLevelText(Integer level) {
        if (level == null) return "";
        if (level == 1) return "普通";
        if (level == 2) return "置顶";
        if (level == 3) return "强提醒";
        return "";
    }
}