package umc.stockoneqback.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.exception.ApplicationException;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.role.exception.StoreErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    @Transactional
    public Long save(String name, String sector, String address) {
        validateAlreadyExistStore(name);
        Store store = Store.createStore(name, sector, address);

        return storeRepository.save(store).getId();
    }

    private void validateAlreadyExistStore(String name) {
        if (storeRepository.existsByName(name)) {
            throw ApplicationException.type(StoreErrorCode.ALREADY_EXIST_STORE);
        }
    }

    public Store findById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> ApplicationException.type(StoreErrorCode.STORE_NOT_FOUND));
    }

    public Store findByName(String storeName) {
        return storeRepository.findByName(storeName)
                .orElseThrow(() -> ApplicationException.type(StoreErrorCode.STORE_NOT_FOUND));
    }
}
