package ru.example.hotel.api.dto;

/**
 * Категория отеля (звёздность)
 */
public enum HotelCategory {
    ONE_STAR("*"),
    TWO_STARS("**"),
    THREE_STARS("***"),
    FOUR_STARS("****"),
    FIVE_STARS("*****");

    private final String displayValue;

    HotelCategory(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static HotelCategory fromDisplayValue(String value) {
        for (HotelCategory category : values()) {
            if (category.displayValue.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown hotel category: " + value);
    }
}
