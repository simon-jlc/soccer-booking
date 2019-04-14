package com.soccerbooking.urban

enum FieldType {

    INDOOR(0, "Intérieur"), EXTERIEUR(1, "Extérieur"), FILM_INDOOR(8, "Filmé indoor")

    int id
    String desc

    FieldType(int id, String desc) {
        this.id = id
        this.desc = desc
    }
}