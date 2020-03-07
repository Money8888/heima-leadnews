package com.heima.media.service;

import com.heima.model.admin.pojos.AdChannel;

import java.util.List;

public interface AdChannelService {
    List<AdChannel> selectAll();
}