package com.kds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.RecipeFormDTO;
import com.kds.model.vo.RecipeVO;

public interface BusiRecipeService extends IService<com.kds.model.entity.BusiRecipe> {
    RecipeVO getRecipe(Long classId, String date);
    void saveRecipe(RecipeFormDTO formDTO);
    RecipeVO getParentRecipe(Long studentId, String date);
}