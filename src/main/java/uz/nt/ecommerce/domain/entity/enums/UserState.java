package uz.nt.ecommerce.domain.entity.enums;

import lombok.Getter;

@Getter
public enum UserState {

    START("START"),
    REGISTERED("REGISTERED"),
    CATEGORIES("CATEGORIES"),
    ORDERS("ORDERS"),
    BASKET("BASKETS"),
    PRODUCT_LIST("PRODUCT LIST"),
    PRODUCT("PRODUCT"),
    UPDATE_PRODUCT_AMOUNT("UPDATE PRODUCT AMOUNT"),
    CATEGORY_PRODUCT("CATEGORY PRODUCT"),
    CATEGORY_PRODUCT_AMOUNT("CATEGORY PRODUCT AMOUNT"),
    CATEGORY_PRODUCT_CONFIRM("CATEGORY PRODUCT CONFIRM"),
    SELECT_ORDER_STATUS("SELECT ORDER STATUS"),
    CATEGORY_CHILD("CATEGORY CHILD");

    private final String strName;
    UserState(String name) {
        this.strName = name;
    }
}
