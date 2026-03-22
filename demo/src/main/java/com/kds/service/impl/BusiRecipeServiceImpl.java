package com.kds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kds.common.context.LoginHelper;
import com.kds.common.exception.BusinessException;
import com.kds.mapper.BusiClassMapper;
import com.kds.mapper.BusiRecipeMapper;
import com.kds.mapper.BusiStudentMapper;
import com.kds.mapper.SysUserMapper;
import com.kds.model.dto.RecipeFormDTO;
import com.kds.model.entity.BusiClass;
import com.kds.model.entity.BusiRecipe;
import com.kds.model.entity.BusiStudent;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.RecipeVO;
import com.kds.service.BusiRecipeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class BusiRecipeServiceImpl extends ServiceImpl<BusiRecipeMapper, BusiRecipe> implements BusiRecipeService {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private BusiClassMapper busiClassMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BusiStudentMapper busiStudentMapper;

    @Override
    public RecipeVO getRecipe(Long classId, String date) {
        if (classId == null) {
            throw new BusinessException("班级ID不能为空");
        }
        if (StringUtils.isEmpty(date)) {
            throw new BusinessException("日期不能为空");
        }

        LocalDate targetDate = LocalDate.parse(date);

        LambdaQueryWrapper<BusiRecipe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusiRecipe::getClassId, classId)
                .eq(BusiRecipe::getTargetDate, targetDate)
                .eq(BusiRecipe::getIsDeleted, 0);

        BusiRecipe recipe = this.getOne(queryWrapper);

        RecipeVO vo = new RecipeVO();
        vo.setClassId(classId);
        vo.setTargetDate(targetDate);

        BusiClass busiClass = busiClassMapper.selectById(classId);
        if (busiClass != null) {
            vo.setClassName(busiClass.getClassName());
        }

        if (recipe != null) {
            BeanUtils.copyProperties(recipe, vo);

            if (recipe.getPublisherId() != null) {
                SysUser publisher = sysUserMapper.selectById(recipe.getPublisherId());
                if (publisher != null) {
                    vo.setPublisherName(publisher.getRealName());
                }
            }
        }

        return vo;
    }

    @Override
    public void saveRecipe(RecipeFormDTO formDTO) {
        if (formDTO.getClassId() == null) {
            throw new BusinessException("班级ID不能为空");
        }
        if (formDTO.getTargetDate() == null) {
            throw new BusinessException("食谱日期不能为空");
        }

        Long userId = loginHelper.getUserId();
        List<Long> classIds = loginHelper.getClassIds();

        if (classIds == null || classIds.isEmpty()) {
            throw new BusinessException("您还没有分配班级");
        }

        if (!classIds.contains(formDTO.getClassId())) {
            throw new BusinessException("您只能操作所任班级的食谱");
        }

        LambdaQueryWrapper<BusiRecipe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusiRecipe::getClassId, formDTO.getClassId())
                .eq(BusiRecipe::getTargetDate, formDTO.getTargetDate())
                .eq(BusiRecipe::getIsDeleted, 0);

        BusiRecipe existRecipe = this.getOne(queryWrapper);

        BusiRecipe recipe = new BusiRecipe();
        if (existRecipe != null) {
            recipe.setId(existRecipe.getId());
        } else {
            recipe.setPublisherId(userId);
        }

        recipe.setClassId(formDTO.getClassId());
        recipe.setTargetDate(formDTO.getTargetDate());
        recipe.setBreakfast(formDTO.getBreakfast());
        recipe.setMorningSnack(formDTO.getMorningSnack());
        recipe.setLunch(formDTO.getLunch());
        recipe.setAfternoonSnack(formDTO.getAfternoonSnack());
        recipe.setDinner(formDTO.getDinner());

        this.saveOrUpdate(recipe);
    }

    @Override
    public RecipeVO getParentRecipe(Long studentId, String date) {
        if (studentId == null) {
            throw new BusinessException("幼儿ID不能为空");
        }
        if (StringUtils.isEmpty(date)) {
            throw new BusinessException("日期不能为空");
        }

        Long currentUserId = loginHelper.getUserId();
        List<Long> childIds = loginHelper.getChildIds();
        if (childIds == null || !childIds.contains(studentId)) {
            throw new BusinessException("您没有权限查看该幼儿的食谱");
        }

        BusiStudent student = busiStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("幼儿信息不存在");
        }

        RecipeVO vo = getRecipe(student.getClassId(), date);
        vo.setStudentName(student.getStudentName());

        return vo;
    }
}