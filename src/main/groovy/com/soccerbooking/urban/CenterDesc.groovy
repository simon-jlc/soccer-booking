package com.soccerbooking.urban

enum CenterDesc {

    PUTEAUX(2) {

        @Override
        Set<FieldType> fieldTypes() {
            [ FieldType.FILM_INDOOR, FieldType.EXTERIEUR ]
        }
    }, ASNIERES(7) {

        @Override
        Set<FieldType> fieldTypes() {
            [ FieldType.INDOOR, FieldType.EXTERIEUR, FieldType.FILM_INDOOR ]
        }
    }, LADEFENSE(12) {

        @Override
        Set<FieldType> fieldTypes() {
            [ FieldType.INDOOR, FieldType.FILM_INDOOR ]
        }
    }

    int id

    CenterDesc(int id) {
        this.id = id
    }

    static CenterDesc[] parseOptions(final OptionAccessor options) {
        def centersStr = options.'atCenter' as String
        return centersStr.split(",")
    }

    static CenterDesc findById(final int id) {
        for (CenterDesc c : values()) {
            if (c.id == id) {
                return c
            }
        }
    }

    abstract Set<FieldType> fieldTypes();
}