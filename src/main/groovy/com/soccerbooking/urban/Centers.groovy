package com.soccerbooking.urban

enum Centers {

    PUTEAUX(2) {

        @Override
        Set<FieldType> fieldTypes() {
            [ FieldType.EXTERIEUR, FieldType.FILM_INDOOR ]
        }
    }, ASNIERES(7) {

        @Override
        Set<FieldType> fieldTypes() {
            [ FieldType.INDOOR, FieldType.EXTERIEUR, FieldType.FILM_INDOOR ]
        }
    }, LADEFENSE(4) {

        @Override
        Set<FieldType> fieldTypes() {
            [ FieldType.INDOOR, FieldType.FILM_INDOOR ]
        }
    }

    int id

    Centers(int id) {
        this.id = id
    }

    static Centers[] parseOptions(final OptionAccessor options) {
        def centersStr = options.'atCenter' as String
        return centersStr.split(",")
    }

    abstract Set<FieldType> fieldTypes();
}