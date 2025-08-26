package com.gmg.jeukhaeng.trip.service;

import com.gmg.jeukhaeng.content.entity.Content;
import com.gmg.jeukhaeng.content.repository.ContentRepository;
import com.gmg.jeukhaeng.trip.dto.TripCreateRequestDto;
import com.gmg.jeukhaeng.trip.dto.TripCreateResponseDto;
import com.gmg.jeukhaeng.trip.dto.TripResponseDto;
import com.gmg.jeukhaeng.trip.dto.TripListResponseDto;
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
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

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

    // 사용자의 모든 여행 일정 조회
    @Transactional(readOnly = true)
    public List<TripListResponseDto> getUserTrips() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Trip> trips = tripRepository.findByUserOrderByCreatedAtDesc(user);
        
        return trips.stream()
                .map(this::convertToTripListDto)
                .collect(Collectors.toList());
    }

    // 특정 여행 일정 상세 조회
    @Transactional(readOnly = true)
    public TripResponseDto getTripDetail(Long tripId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Trip trip = tripRepository.findByTripIdAndUser(tripId, user)
                .orElseThrow(() -> new RuntimeException("여행 일정을 찾을 수 없습니다."));

        return convertToTripResponseDto(trip);
    }

    // Trip 엔티티를 TripListResponseDto로 변환
    private TripListResponseDto convertToTripListDto(Trip trip) {
        int totalContents = 0;
        Map<Integer, List<String>> dayContents = new HashMap<>();
        
        if (trip.getTripContents() != null && !trip.getTripContents().isEmpty()) {
            totalContents = trip.getTripContents().size();
            
            // 일자별 여행지 이름들 그룹화 (sequence 순서대로 정렬)
            Map<Integer, List<TripContent>> contentsByDay = trip.getTripContents().stream()
                    .collect(Collectors.groupingBy(TripContent::getDay));
            
            for (Map.Entry<Integer, List<TripContent>> entry : contentsByDay.entrySet()) {
                Integer day = entry.getKey();
                List<String> sortedTitles = entry.getValue().stream()
                        .sorted((tc1, tc2) -> Integer.compare(tc1.getSequence(), tc2.getSequence()))
                        .map(tc -> tc.getContent().getTitle())
                        .filter(title -> title != null)
                        .collect(Collectors.toList());
                dayContents.put(day, sortedTitles);
            }
        }

        return TripListResponseDto.builder()
                .tripId(trip.getTripId())
                .title(trip.getTitle())
                .days(trip.getDays())
                .totalContents(totalContents)
                .createdAt(trip.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .updatedAt(trip.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .dayContents(dayContents)
                .build();
    }

    // Trip 엔티티를 TripResponseDto로 변환
    private TripResponseDto convertToTripResponseDto(Trip trip) {
        // 사용자 정보 변환
        TripResponseDto.UserDto userDto = TripResponseDto.UserDto.builder()
                .userId(trip.getUser().getId())
                .email(trip.getUser().getEmail())
                .name(trip.getUser().getNickname())
                .build();

        // TripContent를 일자별로 그룹화
        Map<Integer, List<TripContent>> contentsByDay = trip.getTripContents().stream()
                .collect(Collectors.groupingBy(TripContent::getDay));

        // 일자별 컨텐츠 변환
        List<TripResponseDto.DayContentDto> dayContents = contentsByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int day = entry.getKey();
                    List<TripContent> dayTripContents = entry.getValue();
                    
                    List<TripResponseDto.TripContentDto> tripContentDtos = dayTripContents.stream()
                            .sorted((tc1, tc2) -> Integer.compare(tc1.getSequence(), tc2.getSequence()))
                            .map(this::convertToTripContentDto)
                            .collect(Collectors.toList());
                    
                    return TripResponseDto.DayContentDto.builder()
                            .day(day)
                            .contents(tripContentDtos)
                            .build();
                })
                .collect(Collectors.toList());

        return TripResponseDto.builder()
                .tripId(trip.getTripId())
                .title(trip.getTitle())
                .days(trip.getDays())
                .user(userDto)
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .tripContents(dayContents)
                .build();
    }

    // TripContent를 TripContentDto로 변환
    private TripResponseDto.TripContentDto convertToTripContentDto(TripContent tripContent) {
        TripResponseDto.ContentDto contentDto = TripResponseDto.ContentDto.builder()
                .contentId(tripContent.getContent().getContentId())
                .title(tripContent.getContent().getTitle())
                .addr(tripContent.getContent().getAddr())
                .tel(tripContent.getContent().getTel())
                .zipcode(tripContent.getContent().getZipcode())
                .firstImage(tripContent.getContent().getFirstImage())
                .firstImage2(tripContent.getContent().getFirstImage2())
                .contentTypeId(tripContent.getContent().getContentTypeId())
                .areaCode(tripContent.getContent().getAreaCode())
                .sigunguCode(tripContent.getContent().getSigunguCode())
                .mapX(tripContent.getContent().getMapX())
                .mapY(tripContent.getContent().getMapY())
                .build();

        return TripResponseDto.TripContentDto.builder()
                .tripContentId(tripContent.getTripContentId())
                .sequence(tripContent.getSequence())
                .content(contentDto)
                .build();
    }
}
