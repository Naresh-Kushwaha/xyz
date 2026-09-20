package com.navratri.app.activity;

/**
 * The fixed set of Navratri activities the whole platform is organized around:
 * profiles, events, companion requests, and groups all reference this enum so
 * matching can compare "same activity" cheaply instead of fuzzy string matching.
 */
public enum ActivityType {
    GARBA,
    DANDIYA,
    DECORATION,
    LIGHTING,
    FOOD,
    PHOTOGRAPHY,
    REELS,
    CULTURAL,
    CITY_WALK,
    COMMUNITY,
    SOCIALIZING
}
