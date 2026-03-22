package com.kds.controller.busi;

import com.kds.common.result.Result;
import com.kds.model.dto.RecipeFormDTO;
import com.kds.model.vo.RecipeVO;
import com.kds.service.BusiRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/busi/recipe")
public class BusiRecipeController {

    @Autowired
    private BusiRecipeService busiRecipeService;

    @GetMapping
    public Result<RecipeVO> getRecipe(@RequestParam Long classId, @RequestParam String date) {
        return Result.success(busiRecipeService.getRecipe(classId, date));
    }

    @GetMapping("/parent")
    public Result<RecipeVO> getParentRecipe(@RequestParam Long studentId, @RequestParam String date) {
        return Result.success(busiRecipeService.getParentRecipe(studentId, date));
    }

    @PostMapping
    public Result<?> saveRecipe(@Validated @RequestBody RecipeFormDTO formDTO) {
        busiRecipeService.saveRecipe(formDTO);
        return Result.success("食谱保存成功");
    }
}