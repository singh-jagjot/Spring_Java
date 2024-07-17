package com.backend.project.exception;

public class NoteProjectException extends Exception {
    private String msg;

    NoteProjectException(String msg){
        super(msg);
        this.msg = msg;
    }

    @Override
    public String toString(){
        return "NoteProjectException: " + msg;
    }
}
