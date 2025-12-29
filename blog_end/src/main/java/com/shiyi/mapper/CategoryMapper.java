package com.shiyi.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiyi.vo.ApiCategoryListVO;
import com.shiyi.vo.SystemCategoryCountVO;
import com.shiyi.vo.SystemCategoryListVO;
import com.shiyi.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 博客分类表 Mapper 接口
 * </p>
 *
 * @author blue
 * @since 2021-12-29
 */
@Repository
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 分页获取分类
     * @param objectPage 分页对象
     * @param name 分类名
     * @return
     */
    Page<SystemCategoryListVO> selectCategory(@Param("page")Page<Category> objectPage, @Param("name")String name);

    /**
     * 统计分类
     * @return
     */
    List<SystemCategoryCountVO> countArticle();

    List<Category> selectListByHome();

    /**
     * 获取首页分类列表
     * @return
     */
    List<ApiCategoryListVO> selectApitCategoryList();

}
