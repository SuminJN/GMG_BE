package com.gmg.jeukhaeng.area.service;

import com.gmg.jeukhaeng.area.dto.AreaContentResponseDto;
import com.gmg.jeukhaeng.area.dto.AreaInfoResponseDto;
import com.gmg.jeukhaeng.area.dto.PagedAreaContentResponseDto;
import com.gmg.jeukhaeng.area.entity.AreaInfo;
import com.gmg.jeukhaeng.content.entity.Content;
import com.gmg.jeukhaeng.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final ContentRepository contentRepository;

    public PagedAreaContentResponseDto getAreaContents(String area, String sigungu, Pageable pageable) {

        String areaCodeByName = String.valueOf(AreaInfo.getAreaCodeByName(area));
        String sigunguCodeByName = String.valueOf(AreaInfo.getSigunguCodeByName(area, sigungu));

        Page<Content> contentPage =
                contentRepository.findByAreaCodeAndSigunguCode(areaCodeByName, sigunguCodeByName, pageable);

        List<AreaContentResponseDto> result = new ArrayList<>();
        contentPage.getContent().forEach(content -> {
            AreaContentResponseDto dto = new AreaContentResponseDto();
            dto.setContentId(content.getContentId());
            dto.setTitle(content.getTitle());
            dto.setAddr1(content.getAddr());
            dto.setZipcode(content.getZipcode());
            dto.setTel(content.getTel());
            dto.setContentTypeId(content.getContentTypeId());
            dto.setAreaCode(content.getAreaCode());
            dto.setSigunguCode(content.getSigunguCode());
            dto.setFirstImage(content.getFirstImage());
            dto.setFirstImage2(content.getFirstImage2());
            dto.setMapX(content.getMapX());
            dto.setMapY(content.getMapY());

            result.add(dto);
        });

        return new PagedAreaContentResponseDto(
                result,
                contentPage.getNumber(),
                contentPage.getSize(),
                contentPage.getTotalElements(),
                contentPage.getTotalPages(),
                contentPage.isFirst(),
                contentPage.isLast(),
                contentPage.isEmpty()
        );
    }

    /**
     * 모든 지역 정보를 반환
     */
    public List<AreaInfoResponseDto> getAllAreaInfo() {
        return Arrays.stream(AreaInfo.values())
                .map(areaInfo -> new AreaInfoResponseDto(
                        areaInfo.getAreaName(),
                        areaInfo.getSigunguMap()
                ))
                .collect(Collectors.toList());
    }
}
