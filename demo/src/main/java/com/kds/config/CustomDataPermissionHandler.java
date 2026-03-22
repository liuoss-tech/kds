package com.kds.config;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.kds.common.context.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomDataPermissionHandler implements MultiDataPermissionHandler {

    private static final List<String> IGNORE_TABLES = Arrays.asList(
            "sys_user", "sys_role", "sys_permission", 
            "sys_user_role", "sys_role_permission",
            "busi_teacher_class", "busi_student_parent"
    );

    @Autowired
    private ApplicationContext applicationContext;

    private LoginHelper getLoginHelper() {
        try {
            return applicationContext.getBean(LoginHelper.class);
        } catch (Exception e) {
            log.warn("LoginHelper not available yet, skipping data scope filter");
            return null;
        }
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        String tableName = table.getName().toLowerCase();
        
        if (IGNORE_TABLES.contains(tableName)) {
            return null;
        }
        
        LoginHelper loginHelper = getLoginHelper();
        if (loginHelper == null) {
            return null;
        }

        Integer dataScope;
        try {
            dataScope = loginHelper.getDataScope();
        } catch (Exception e) {
            log.warn("Failed to get dataScope, skipping filter: {}", e.getMessage());
            return null;
        }

        log.debug("DataPermissionHandler - dataScope: {}, table: {}", dataScope, tableName);

        if (dataScope == null || dataScope == 1) {
            return null;
        }

        if (dataScope == 2) {
            List<Long> classIds = loginHelper.getClassIds();
            log.debug("DataPermissionHandler - classIds: {}", classIds);
            if (classIds != null && !classIds.isEmpty()) {
                String idsStr = String.join(",", classIds.stream().map(String::valueOf).toArray(String[]::new));
                try {
                    String condition;
                    if ("busi_student".equalsIgnoreCase(tableName) ||
                        "busi_life_record".equalsIgnoreCase(tableName) ||
                        "busi_attendance_record".equalsIgnoreCase(tableName) ||
                        "busi_leave_application".equalsIgnoreCase(tableName)) {
                        condition = "class_id IN (" + idsStr + ")";
                    } else if ("busi_class".equalsIgnoreCase(tableName)) {
                        condition = "id IN (" + idsStr + ")";
                    } else {
                        return null;
                    }
                    return CCJSqlParserUtil.parseCondExpression(condition);
                } catch (JSQLParserException e) {
                    log.error("Failed to parse SQL segment", e);
                }
            }
        } else if (dataScope == 4) {
            List<Long> childIds = loginHelper.getChildIds();
            log.debug("DataPermissionHandler - childIds: {}", childIds);
            if (childIds != null && !childIds.isEmpty()) {
                String idsStr = String.join(",", childIds.stream().map(String::valueOf).toArray(String[]::new));
                try {
                    String condition;
                    if ("busi_student".equalsIgnoreCase(tableName)) {
                        condition = "id IN (" + idsStr + ")";
                    } else if ("busi_leave_application".equalsIgnoreCase(tableName)) {
                        condition = "student_id IN (" + idsStr + ")";
                    } else {
                        return null;
                    }
                    return CCJSqlParserUtil.parseCondExpression(condition);
                } catch (JSQLParserException e) {
                    log.error("Failed to parse SQL segment", e);
                }
            }
        }

        return null;
    }
}
