package com.gmg.jeukhaeng.content.controller;

import com.gmg.jeukhaeng.content.batch.ContentBatchScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentBatchScheduler contentBatchScheduler;

    @PostMapping("/sync")
    public String syncContentFromApi() {
        contentBatchScheduler.fetchAndSaveContent();
        return "공공 API로부터 관광지 데이터를 동기화했습니다.";
    }
}
