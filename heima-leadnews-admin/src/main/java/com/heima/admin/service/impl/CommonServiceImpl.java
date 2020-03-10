package com.heima.admin.service.impl;

import com.heima.admin.dao.CommonDao;
import com.heima.admin.service.CommonService;
import com.heima.admin.service.impl.commfilter.BaseCommonFilter;
import com.heima.model.admin.dtos.CommonDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonDao commonDao;

//    @Autowired
//    private ApplicationContext context;

    @Autowired
    private BeanFactory beanFactory;

    @Override
    public ResponseResult list(CommonDto dto) {
        if(!dto.getName().isList()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        // where条件
        String where = getWhere(dto);
        // 表名
        String tableName = dto.getName().name().toLowerCase();
        // 查询开始条件
        int start = (dto.getPage() - 1) * dto.getSize();
        if(start < 0){
            start = 0;
        }
        // 开始查询
        List<?> list = null;
        int total = 0;
        if(StringUtils.isEmpty(where)){
            list = commonDao.list(tableName, start, dto.getSize());
            total = commonDao.listCount(tableName);
        }else {
            list = commonDao.listForWhere(tableName, where, start, dto.getSize());
            total = commonDao.listCountForWhere(tableName, where);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        // 后处理bean
        doFilter(dto, "list");

        return ResponseResult.okResult(map);

    }

    private void doFilter(CommonDto dto, String name) {
        BaseCommonFilter baseCommonFilter = findFilter(dto);
    }

    /**
     * 找到当前spring容器中的bean
     * @param dto
     * @return
     */
    private BaseCommonFilter findFilter(CommonDto dto) {
        String name = dto.getName().name();
        // 判断当前spring容器中是否有该名称的bean
        if(beanFactory.containsBean(name)){
            return beanFactory.getBean(name, BaseCommonFilter.class);
        }
        return null;
    }

    /**
     * 封装where条件
     * @param dto
     * @return
     */
    private String getWhere(CommonDto dto) {
        StringBuffer where = new StringBuffer();
        if(dto.getWhere() != null){
            dto.getWhere().stream().forEach(w -> {
                // 字段不为空，字段名和值不能相等
                if(StringUtils.isNotEmpty(w.getFiled()) && StringUtils.isNotEmpty(w.getValue()) && !w.getFiled().equalsIgnoreCase(w.getValue())){
                    String tempF = parseValue(w.getFiled());
                    String tempV = parseValue(w.getValue());
                    // 字段名不能喝数字匹配
                    if(!tempF.matches("\\d*") && !tempF.equalsIgnoreCase(tempV)){
                        // = 条件
                        if("eq".equals(w.getType())){
                            where.append(" and ").append(tempF).append("=\'").append(tempV).append("\'");
                        }
                        // like条件
                        if ("like".equals(w.getType())) {
                            where.append(" and ").append(tempF).append(" like \'%").append(tempV).append("%\'");
                        }
                        // between条件
                        if ("between".equals(w.getType())) {
                            String temp[] = tempV.split(",");
                            where.append(" and ").append(tempF).append(temp[0]).append(" and ").append(temp[1]);
                        }
                    }
                }
            });
        }
        return where.toString();
    }

    /**
     * 排除sql特殊符号带来的异常
     * @param value
     * @return
     */
    private String parseValue(String value) {
        if(StringUtils.isNotEmpty(value)){
            return value.replace(".*([';#%]+|(--)+).*", "");
        }
        return value;
    }

    @Override
    public ResponseResult update(CommonDto dto) {
        String model = dto.getModel();
        String where = getWhere(dto);
        String tableName = dto.getName().name().toLowerCase();
        if("add".equals(model)){
            // 新增
            if(StringUtils.isNotEmpty(where)){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"新增数据不能设置条件");
            }else {
                return addData(dto, tableName);
            }
        }else {
            // 更新
            if(StringUtils.isEmpty(where)){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"修改条件不能为空");
            }else {
                return updateData(dto, tableName, where);
            }
        }
    }

    /**
     * 添加数据
     * @param dto
     * @param tableName
     * @return
     */
    private ResponseResult addData(CommonDto dto, String tableName) {
        String[] sql = getInsertSql(dto);
        if(!dto.getName().isAdd()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if(sql == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"传入的参数值不能为空");
        }
        int insert = commonDao.insert(tableName, sql[0], sql[1]);
        if(insert > 0){
            doFilter(dto, "add");
        }
        return ResponseResult.okResult(insert);
    }

    /**
     * 拼接插入sql语句
     * @param dto
     * @return 返回数组，第一个元素是字段字符串，第二个是值字符串
     */
    private String[] getInsertSql(CommonDto dto) {
        StringBuffer fields = new StringBuffer();
        StringBuffer values = new StringBuffer();
        // 线程安全的迭加器
        AtomicInteger count = new AtomicInteger();
        if(dto.getSets() != null){
            dto.getSets().stream().forEach(w -> {
                if(StringUtils.isEmpty(w.getValue())){
                    count.incrementAndGet();
                }else {
                    String tempF = parseValue(w.getFiled());
                    String tempV = parseValue(w.getValue());
                    if(!tempF.matches("\\d*")&&!tempF.equalsIgnoreCase(tempV)) {
                        // 逗号处理
                        if(fields.length() > 0){
                            fields.append(",");
                            values.append(",");
                        }
                        fields.append(tempF);
                        values.append("\'").append(tempV).append("\'");
                    }
                }
            });
        }
        if(count.get() > 0){
            return null;
        }
        return new String[]{fields.toString(), values.toString()};
    }

    /**
     * 更新数据
     * @param dto
     * @param tableName
     * @param where
     * @return
     */
    private ResponseResult updateData(CommonDto dto, String tableName, String where) {
        String sets = getSets(dto);
        if(!dto.getName().isUpdate()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if(sets == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"修改的参数值不能为空");
        }
        int update = commonDao.update(tableName, where, sets);
        if(update > 0){
            doFilter(dto, "update");
        }
        return ResponseResult.okResult(update);
    }

    /**
     * 拼接update语句中的set
     * @param dto
     * @return
     */
    private String getSets(CommonDto dto) {
        StringBuffer sets = new StringBuffer();
        AtomicInteger count = new AtomicInteger();
        if(dto.getSets() != null){
            dto.getSets().stream().forEach(w -> {
                if(StringUtils.isEmpty(w.getValue())){
                    count.incrementAndGet();
                }else {
                    String tempF = parseValue(w.getFiled());
                    String tempV = parseValue(w.getValue());
                    if(!tempF.matches("\\d*")&&!tempF.equalsIgnoreCase(tempV)) {
                        if (sets.length() > 0) {
                            sets.append(",");
                        }
                        sets.append(tempF).append("=\'").append(tempV).append("\'");
                    }
                }
            });
        }
        if(count.get() > 0){
            return null;
        }
        return sets.toString();
    }

    @Override
    public ResponseResult delete(CommonDto dto) {
        String where = getWhere(dto);
        String tableName = dto.getName().name().toLowerCase();
        if(!dto.getName().isDelete()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if(StringUtils.isEmpty(where)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"删除条件不合法");
        }
        int delete = commonDao.delete(tableName, where);
        if(delete > 0){
            doFilter(dto, "delete");
        }
        return ResponseResult.okResult(delete);
    }
}
