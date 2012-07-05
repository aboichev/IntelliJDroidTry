package com.example;

class Note {

    private byte id;

    public Note(byte id) {
        this.id = id;
    }

    public byte getId(){
        return id;
    }

    public static boolean isBlackKey(byte id) {

        byte remainder = (byte) (id %  12);
        if( remainder == 1 ||
                remainder == 3 ||
                remainder == 6 ||
                remainder == 8 ||
                remainder == 10 ) {
            return true;
        }
        return false;
    }
}
