package com.gmg.jeukhaeng.area.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum AreaInfo {

    SEOUL("1", "서울특별시", Map.ofEntries(
            Map.entry("1", "강남구"),
            Map.entry("2", "강동구"),
            Map.entry("3", "강북구"),
            Map.entry("4", "강서구"),
            Map.entry("5", "관악구"),
            Map.entry("6", "광진구"),
            Map.entry("7", "구로구"),
            Map.entry("8", "금천구"),
            Map.entry("9", "노원구"),
            Map.entry("10", "도봉구"),
            Map.entry("11", "동대문구"),
            Map.entry("12", "동작구"),
            Map.entry("13", "마포구"),
            Map.entry("14", "서대문구"),
            Map.entry("15", "서초구"),
            Map.entry("16", "성동구"),
            Map.entry("17", "성북구"),
            Map.entry("18", "송파구"),
            Map.entry("19", "양천구"),
            Map.entry("20", "영등포구"),
            Map.entry("21", "용산구"),
            Map.entry("22", "은평구"),
            Map.entry("23", "종로구"),
            Map.entry("24", "중구"),
            Map.entry("25", "중랑구")
    )),

    INCHEON("2", "인천광역시", Map.ofEntries(
            Map.entry("1", "강화군"),
            Map.entry("2", "계양구"),
            Map.entry("3", "미추홀구"),
            Map.entry("4", "남동구"),
            Map.entry("5", "동구"),
            Map.entry("6", "부평구"),
            Map.entry("7", "서구"),
            Map.entry("8", "연수구"),
            Map.entry("9", "옹진군"),
            Map.entry("10", "중구")
    )),

    DAEJEON("3", "대전광역시", Map.ofEntries(
            Map.entry("1", "대덕구"),
            Map.entry("2", "동구"),
            Map.entry("3", "서구"),
            Map.entry("4", "유성구"),
            Map.entry("5", "중구")
    )),

    DAEGU("4", "대구광역시", Map.ofEntries(
            Map.entry("1", "남구"),
            Map.entry("2", "달서구"),
            Map.entry("3", "달성군"),
            Map.entry("4", "동구"),
            Map.entry("5", "북구"),
            Map.entry("6", "서구"),
            Map.entry("7", "수성구"),
            Map.entry("8", "중구"),
            Map.entry("9", "군위군")
    )),

    GWANGJU("5", "광주광역시", Map.ofEntries(
            Map.entry("1", "광산구"),
            Map.entry("2", "남구"),
            Map.entry("3", "동구"),
            Map.entry("4", "북구"),
            Map.entry("5", "서구")
    )),

    BUSAN("6", "부산광역시", Map.ofEntries(
            Map.entry("1", "강서구"),
            Map.entry("2", "금정구"),
            Map.entry("3", "기장군"),
            Map.entry("4", "남구"),
            Map.entry("5", "동구"),
            Map.entry("6", "동래구"),
            Map.entry("7", "부산진구"),
            Map.entry("8", "북구"),
            Map.entry("9", "사상구"),
            Map.entry("10", "사하구"),
            Map.entry("11", "서구"),
            Map.entry("12", "수영구"),
            Map.entry("13", "연제구"),
            Map.entry("14", "영도구"),
            Map.entry("15", "중구"),
            Map.entry("16", "해운대구")
    )),

    ULSAN("7", "울산광역시", Map.ofEntries(
            Map.entry("1", "중구"),
            Map.entry("2", "남구"),
            Map.entry("3", "동구"),
            Map.entry("4", "북구"),
            Map.entry("5", "울주군")
    )),

    SEJONG("8", "세종특별자치시", Map.ofEntries(
            Map.entry("1", "세종특별자치시")
    )),

    GYEONGGI("31", "경기도", Map.ofEntries(
            Map.entry("1", "가평군"),
            Map.entry("2", "고양시"),
            Map.entry("3", "과천시"),
            Map.entry("4", "광명시"),
            Map.entry("5", "광주시"),
            Map.entry("6", "구리시"),
            Map.entry("7", "군포시"),
            Map.entry("8", "김포시"),
            Map.entry("9", "남양주시"),
            Map.entry("10", "동두천시"),
            Map.entry("11", "부천시"),
            Map.entry("12", "성남시"),
            Map.entry("13", "수원시"),
            Map.entry("14", "시흥시"),
            Map.entry("15", "안산시"),
            Map.entry("16", "안성시"),
            Map.entry("17", "안양시"),
            Map.entry("18", "양주시"),
            Map.entry("19", "양평군"),
            Map.entry("20", "여주시"),
            Map.entry("21", "연천군"),
            Map.entry("22", "오산시"),
            Map.entry("23", "용인시"),
            Map.entry("24", "의왕시"),
            Map.entry("25", "의정부시"),
            Map.entry("26", "이천시"),
            Map.entry("27", "파주시"),
            Map.entry("28", "평택시"),
            Map.entry("29", "포천시"),
            Map.entry("30", "하남시"),
            Map.entry("31", "화성시")
    )),

    GANGWON("32", "강원특별자치도", Map.ofEntries(
            Map.entry("1", "강릉시"),
            Map.entry("2", "고성군"),
            Map.entry("3", "동해시"),
            Map.entry("4", "삼척시"),
            Map.entry("5", "속초시"),
            Map.entry("6", "양구군"),
            Map.entry("7", "양양군"),
            Map.entry("8", "영월군"),
            Map.entry("9", "원주시"),
            Map.entry("10", "인제군"),
            Map.entry("11", "정선군"),
            Map.entry("12", "철원군"),
            Map.entry("13", "춘천시"),
            Map.entry("14", "태백시"),
            Map.entry("15", "평창군"),
            Map.entry("16", "홍천군"),
            Map.entry("17", "화천군"),
            Map.entry("18", "횡성군")
    )),

    CHUNGCHEONGBUK("33", "충청북도", Map.ofEntries(
            Map.entry("1", "괴산군"),
            Map.entry("2", "단양군"),
            Map.entry("3", "보은군"),
            Map.entry("4", "영동군"),
            Map.entry("5", "옥천군"),
            Map.entry("6", "음성군"),
            Map.entry("7", "제천시"),
            Map.entry("8", "진천군"),
            Map.entry("9", "청원군"),
            Map.entry("10", "청주시"),
            Map.entry("11", "충주시"),
            Map.entry("12", "증평군")
    )),

    CHUNGCHEONGNAM("34", "충청남도", Map.ofEntries(
            Map.entry("1", "공주시"),
            Map.entry("2", "금산군"),
            Map.entry("3", "논산시"),
            Map.entry("4", "당진시"),
            Map.entry("5", "보령시"),
            Map.entry("6", "부여군"),
            Map.entry("7", "서산시"),
            Map.entry("8", "서천군"),
            Map.entry("9", "아산시"),
            Map.entry("11", "예산군"),
            Map.entry("12", "천안시"),
            Map.entry("13", "청양군"),
            Map.entry("14", "태안군"),
            Map.entry("15", "홍성군"),
            Map.entry("16", "계룡시")
    )),

    GYEONGSANGBUK("35", "경상북도", Map.ofEntries(
            Map.entry("1", "경산시"),
            Map.entry("2", "경주시"),
            Map.entry("3", "고령군"),
            Map.entry("4", "구미시"),
            Map.entry("6", "김천시"),
            Map.entry("7", "문경시"),
            Map.entry("8", "봉화군"),
            Map.entry("9", "상주시"),
            Map.entry("10", "성주군"),
            Map.entry("11", "안동시"),
            Map.entry("12", "영덕군"),
            Map.entry("13", "영양군"),
            Map.entry("14", "영주시"),
            Map.entry("15", "영천시"),
            Map.entry("16", "예천군"),
            Map.entry("17", "울릉군"),
            Map.entry("18", "울진군"),
            Map.entry("19", "의성군"),
            Map.entry("20", "청도군"),
            Map.entry("21", "청송군"),
            Map.entry("22", "칠곡군"),
            Map.entry("23", "포항시")
    )),

    GYEONGSANGNAM("36", "경상남도", Map.ofEntries(
            Map.entry("1", "거제시"),
            Map.entry("2", "거창군"),
            Map.entry("3", "고성군"),
            Map.entry("4", "김해시"),
            Map.entry("5", "남해군"),
            Map.entry("6", "마산시"),
            Map.entry("7", "밀양시"),
            Map.entry("8", "사천시"),
            Map.entry("9", "산청군"),
            Map.entry("10", "양산시"),
            Map.entry("12", "의령군"),
            Map.entry("13", "진주시"),
            Map.entry("14", "진해시"),
            Map.entry("15", "창녕군"),
            Map.entry("16", "창원시"),
            Map.entry("17", "통영시"),
            Map.entry("18", "하동군"),
            Map.entry("19", "함안군"),
            Map.entry("20", "함양군"),
            Map.entry("21", "합천군")
    )),

    JEONBUK("37", "전북특별자치도", Map.ofEntries(
            Map.entry("1", "고창군"),
            Map.entry("2", "군산시"),
            Map.entry("3", "김제시"),
            Map.entry("4", "남원시"),
            Map.entry("5", "무주군"),
            Map.entry("6", "부안군"),
            Map.entry("7", "순창군"),
            Map.entry("8", "완주군"),
            Map.entry("9", "익산시"),
            Map.entry("10", "임실군"),
            Map.entry("11", "장수군"),
            Map.entry("12", "전주시"),
            Map.entry("13", "정읍시"),
            Map.entry("14", "진안군")
    )),

    JEONNAM("38", "전라남도", Map.ofEntries(
            Map.entry("1", "강진군"),
            Map.entry("2", "고흥군"),
            Map.entry("3", "곡성군"),
            Map.entry("4", "광양시"),
            Map.entry("5", "구례군"),
            Map.entry("6", "나주시"),
            Map.entry("7", "담양군"),
            Map.entry("8", "목포시"),
            Map.entry("9", "무안군"),
            Map.entry("10", "보성군"),
            Map.entry("11", "순천시"),
            Map.entry("12", "신안군"),
            Map.entry("13", "여수시"),
            Map.entry("16", "영광군"),
            Map.entry("17", "영암군"),
            Map.entry("18", "완도군"),
            Map.entry("19", "장성군"),
            Map.entry("20", "장흥군"),
            Map.entry("21", "진도군"),
            Map.entry("22", "함평군"),
            Map.entry("23", "해남군"),
            Map.entry("24", "화순군")
    )),

    JEJU("39", "제주특별자치도", Map.ofEntries(
            Map.entry("1", "남제주군"),
            Map.entry("2", "북제주군"),
            Map.entry("3", "서귀포시"),
            Map.entry("4", "제주시")
    ));

    private final String areaCode;
    private final String areaName;
    private final Map<String, String> sigunguMap;

    public static int getAreaCodeByName(String areaName) {
        return Arrays.stream(values())
                .filter(area -> area.getAreaName().equalsIgnoreCase(areaName))
                .findFirst()
                .map(area -> Integer.parseInt(area.getAreaCode()))
                .orElse(0);
    }

    public static int getSigunguCodeByName(String areaName, String sigunguName) {
        return Arrays.stream(values())
                .filter(area -> area.getAreaName().equalsIgnoreCase(areaName))
                .findFirst()
                .flatMap(area -> area.getSigunguMap().entrySet().stream()
                        .filter(entry -> entry.getValue().equalsIgnoreCase(sigunguName))
                        .map(Map.Entry::getKey)
                        .findFirst())
                .map(Integer::parseInt)
                .orElse(0);
    }
}