package com.heima.crawler.service.impl;

import com.heima.common.common.util.HMStringUtils;
import com.heima.crawler.service.AdLabelService;
import com.heima.model.admin.pojos.AdChannelLabel;
import com.heima.model.admin.pojos.AdLabel;
import com.heima.model.mappers.admin.AdChannelLabelMapper;
import com.heima.model.mappers.admin.AdLabelMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@SuppressWarnings("all")
public class AdLabelServiceImpl implements AdLabelService {

    @Autowired
    private AdLabelMapper adLabelMapper;

    @Autowired
    private AdChannelLabelMapper adChannelLabelMapper;

    @Override
    public String getLabelIds(String labels) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("获取channel信息，标签：labels：{}", labels);
        List<AdLabel> adLabelList = new ArrayList<>();
        if(StringUtils.isNotEmpty(labels)){
            labels = labels.toLowerCase();
            // 以逗号分割转化为list
            List<String> labelList = Arrays.asList(labels.split(","));
            List<AdLabel> tmpLabels = adLabelMapper.queryAdLabelByLabels(labelList);
            if(tmpLabels != null && !tmpLabels.isEmpty()){
                adLabelList = addLabelList(tmpLabels, labelList);
            }else {
                adLabelList = addLabelList(labelList);
            }
        }

        List<String> labelIdList = adLabelList.stream().map(label -> HMStringUtils.toString(label.getId())).collect(Collectors.toList());
        String labelIds = HMStringUtils.listToStr(labelIdList, ",");
        log.info("获取channel信息完成，标签：labels：{},labelIds:{},耗时：{}", labels, labelIds, System.currentTimeMillis() - currentTimeMillis);
        return labelIds;
    }

    /**
     * 过滤保存
     * @param tmpLabels
     * @param labelList
     * @return
     */
    private List<AdLabel> addLabelList(List<AdLabel> tmpLabels, List<String> labelList) {
        List<String> tmpLabelList = tmpLabels.stream().map(x -> x.getName()).collect(Collectors.toList());
        List<String> unAddLabelList = labelList.stream().filter(x -> !tmpLabelList.contains(x)).collect(Collectors.toList());
        return addLabelList(unAddLabelList);
    }

    private List<AdLabel> addLabelList(List<String> labelList) {
        List<AdLabel> adLabelList = new ArrayList<>();
        if(labelList != null && !labelList.isEmpty()){
            for (String label : labelList) {
                adLabelList.add(addLabel(label));
            }
        }
        return adLabelList;
    }

    /**
     * 保存标签
     * @param label
     * @return
     */
    private AdLabel addLabel(String label) {
        AdLabel adLabel = new AdLabel();
        adLabel.setName(label);
        adLabel.setType(true);
        adLabel.setCreatedTime(new Date());
        adLabelMapper.insert(adLabel);
        return adLabel;
    }

    /**
     * 查询频道
     * @param labels
     * @return
     */
    @Override
    public Integer getAdChannelByLabelIds(String labelIds) {
        Integer channelId = null;
        try {
            channelId = getSecurityAdChannelByLabelIds(labelIds);
        }catch (Exception e){
            log.error("获取channel信息失败，errorMsg:{}", e.getMessage());
        }
        return channelId;
    }

    private Integer getSecurityAdChannelByLabelIds(String labelIds) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("获取channel信息，标签IDS：labelIds：{}", labelIds);
        Integer channelId = null;
        if(StringUtils.isNotEmpty(labelIds)){
            List<String> labelList = Arrays.asList(labelIds.split(","));
            List<AdLabel> adLabelList = adLabelMapper.queryAdLabelByLabelIds(labelList);
            if(adLabelList != null && !adLabelList.isEmpty()){
                // 多个标签只取第一个标签的所属频道
                channelId = geAdChannelIdByLabelId(adLabelList.get(0).getId());
            }
        }
        channelId = channelId == null ? 0 : channelId;
        log.info("获取channel信息完成，标签：labelIds：{},channelId:{},耗时：{}", labelIds, channelId, System.currentTimeMillis() - currentTimeMillis);
        return channelId;
    }

    private Integer geAdChannelIdByLabelId(Integer labelId) {
        Integer channelId = null;
        AdChannelLabel adChannelLabel = adChannelLabelMapper.selectByLabelId(labelId);
        if(adChannelLabel != null){
            channelId = adChannelLabel.getChannelId();
        }
        return channelId;
    }
}
