package com.gmg.jeukhaeng.trip.service;

import com.gmg.jeukhaeng.content.entity.Content;
import com.gmg.jeukhaeng.content.repository.ContentRepository;
import com.gmg.jeukhaeng.trip.dto.TripCreateRequestDto;
import com.gmg.jeukhaeng.trip.dto.TripCreateResponseDto;
import com.gmg.jeukhaeng.trip.entity.Trip;
import com.gmg.jeukhaeng.trip.entity.TripContent;
import com.gmg.jeukhaeng.trip.repository.TripRepository;
import com.gmg.jeukhaeng.user.entity.User;
import com.gmg.jeukhaeng.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public TripCreateResponseDto createTrip(TripCreateRequestDto requestDto) {
        // 현재 로그인한 사용자 정보 가져오기
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Trip 생성
        Trip trip = Trip.builder()
                .user(user)
                .title(requestDto.getTitle())
                .days(requestDto.getDays())
                .build();

        // TripContent 생성 및 추가
        if (requestDto.getTripContent() != null) {
            for (TripCreateRequestDto.DayContentDto dayContent : requestDto.getTripContent()) {
                int day = dayContent.getDay();
                List<Long> contentIds = dayContent.getContentIds();

                if (contentIds != null) {
                    for (int i = 0; i < contentIds.size(); i++) {
                        Long contentId = contentIds.get(i);

                        // Content가 존재하지 않으면 예외 처리
                        Content content = contentRepository.findById(String.valueOf(contentId))
                                .orElseThrow(() -> new RuntimeException("Content ID " + contentId + "에 해당하는 콘텐츠를 찾을 수 없습니다."));

                        // TripContent 생성
                        TripContent tripContent = TripContent.builder()
                                .trip(trip)
                                .content(content)
                                .day(day)
                                .sequence(i + 1) // 1부터 시작하는 순서
                                .build();

                        trip.addTripContent(tripContent);
                    }
                }
            }
        }

        // Trip 저장
        Trip savedTrip = tripRepository.save(trip);

        log.info("여행 일정이 생성되었습니다. tripId: {}, title: {}, user: {}",
                savedTrip.getTripId(), savedTrip.getTitle(), user.getEmail());

        // 응답 DTO 생성
        return TripCreateResponseDto.builder()
                .tripId(savedTrip.getTripId())
                .message("여행 일정이 성공적으로 생성되었습니다.")
                .build();
    }
}
