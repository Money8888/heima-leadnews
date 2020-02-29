package com.heima;

import io.mycat.config.model.rule.RuleAlgorithm;
import io.mycat.route.function.AbstractPartitionAlgorithm;
import lombok.Data;

/**
 * 根据数据表中的burst值计算分片id
 * 分片id的计算公式是: (dataId/Volume)*step + 分表ID/mod
 * - Volume是每组分片的每个数据节点DateNode的数据容量
 * - Step是每组分片的DateNode数量
 * - mode是表在每组分片中的节点数量
 * burst数据格式为:dataId-分表id
 */
@Data
public class HeiMaBurstRuleAlgorithm extends AbstractPartitionAlgorithm implements RuleAlgorithm {

    private Long volume;
    private Integer step;
    private Integer mod;


    @Override
    public Integer calculate(String burst) {
        if(burst != null){
            String[] temp = burst.split("-");
            if(temp.length == 2){
                Long dataId = Long.valueOf(temp[0]);
                Long tableId = Long.valueOf(temp[1]);
                // 计算
                try{
                    int posId = (int)(dataId / volume) * step + (int)(tableId / mod);
                    System.out.println("HEIMA RULE INFO ["+burst+"]-[{"+posId+"}]");
                    return posId;
                }catch (Exception e){
                    System.out.println("HEIMA RULE INFO ["+burst+"]-[{"+e.getMessage()+"}]");
                }

            }
        }
        return 0;
    }

    /**
     * 根据两个不同的burst值，算出处于两个之间的所有分片id值
     * @param beginValue
     * @param endValue
     * @return
     */
    @Override
    public Integer[] calculateRange(String beginValue, String endValue) {
        if(beginValue != null && endValue != null){
            Integer begin = calculate(beginValue);
            Integer end = calculate(endValue);
            if(begin == null || end == null){
                return new Integer[0];
            }
            if(end >= begin){
                int len = end - begin + 1;
                Integer[] range = new Integer[len];
                for(int i = 0; i < len; i++){
                    range[i] = begin + i;
                }
                return range;
            }
        }
        return new Integer[0];
    }
}
