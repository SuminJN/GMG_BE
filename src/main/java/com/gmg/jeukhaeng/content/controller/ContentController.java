package com.gmg.jeukhaeng.content.controller;

import com.gmg.jeukhaeng.content.batch.ContentBatchScheduler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentBatchScheduler contentBatchScheduler;

    @PostMapping("/sync")
    @Operation(summary = "공공데이터 업데이트", description = "공공데이터 API에서 불러오는 데이터를 DB에 주기적으로 업데이트해줍니다.(프론트 분들은 누르지말아주세요)")

    public String syncContentFromApi() {
        contentBatchScheduler.fetchAndSaveContent();
        return "공공 API로부터 관광지 데이터를 동기화했습니다.";
    }
}
