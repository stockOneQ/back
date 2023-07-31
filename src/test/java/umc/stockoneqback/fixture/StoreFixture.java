package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.role.domain.store.Store;

@Getter
@RequiredArgsConstructor
public enum StoreFixture {
    Z_YEONGTONG("Z 과일가게 영통점", "과일", "경기도 수원시 영통구 에듀타운로 65"),
    Z_SIHEUNG("Z 과일가게 시흥점", "과일", "경기도 시흥시 은계남로 12"),
    Y_YEONGTONG("Y 빵집 영통점", "빵", "경기도 수원시 영통구 영통로 195"),
    A_PASTA("A 파스타집 행당점", "파스타", "서울특별시 성동구 행당로 33"),
    B_CHICKEN("B 치킨집 강남점", "치킨", "서울특별시 강남구 강남대로 123"),
    C_COFFEE("C 커피숍 홍대점", "커피", "서울특별시 마포구 어울마당로 47"),
    D_PIZZA("D 피자집 신림점", "피자", "서울특별시 관악구 남부순환로 1803"),
    E_CHINESE("E 중국집 부평점", "중국음식", "인천광역시 부평구 동수로 128"),
    F_COMPANY("F 회사 서초사무실", "사무실", "서울특별시 서초구 서초대로 1234"),
    G_TTEOKBOKKI("G 떡볶이집 황현동점", "떡볶이", "대구광역시 수성구 황현로 56")
    ;

    private final String name;
    private final String sector;
    private final String address;

    public Store toStore() {
        return Store.createStore(name, sector, address);
    }
}
