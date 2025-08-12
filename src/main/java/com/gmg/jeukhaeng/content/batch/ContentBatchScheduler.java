package com.gmg.jeukhaeng.content.batch;

import com.gmg.jeukhaeng.content.api.ContentApiClient;
import com.gmg.jeukhaeng.content.dto.ContentDto;
import com.gmg.jeukhaeng.content.entity.Content;
import com.gmg.jeukhaeng.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContentBatchScheduler {

    private final ContentApiClient apiClient;
    private final ContentRepository contentRepository;

    public void fetchAndSaveContent() {
        for (int areaCode = 1; areaCode <= 39; areaCode++) {
            int totalCount = apiClient.fetchTotalCount(areaCode);

            if (totalCount == 0) {
                log.info(" areaCode {}: 콘텐츠 없음", areaCode);
                continue;
            }

            int totalPages = (int) Math.ceil(totalCount / 1000.0);
            log.info(" areaCode {}: 총 {}건, 총 {}페이지", areaCode, totalCount, totalPages);

            for (int page = 1; page <= totalPages; page++) {
                List<ContentDto> contents = apiClient.fetchContents(page, areaCode);
                log.info(" page {}/{} → 콘텐츠 {}건", page, totalPages, contents.size());

                for (ContentDto dto : contents) {
                    if (!contentRepository.existsByContentId(dto.getContentId())) {
                        Content content = Content.builder()
                                .contentId(dto.getContentId())
                                .title(dto.getTitle())
                                .addr(dto.getAddr1())
                                .tel(dto.getTel())
                                .zipcode(dto.getZipcode())
                                .firstImage(dto.getFirstImage())
                                .firstImage2(dto.getFirstImage2())
                                .contentTypeId(dto.getContenttypeid())
                                .areaCode(dto.getAreacode())
                                .sigunguCode(dto.getSigungucode())
                                .mapX(dto.getMapx())
                                .mapY(dto.getMapy())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        contentRepository.save(content);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledFetch() {
        log.info("== 관광지 자동 수집 시작 ==");
        fetchAndSaveContent();
        log.info("== 관광지 자동 수집 종료 ==");
    }
}
