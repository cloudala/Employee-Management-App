package org.example.springbootapp.util;

import org.example.springbootapp.domain.entity.Person;

public class CsvValidationResult {
    private int rowNumber;
    private String errorMessage;

    public CsvValidationResult(int rowNumber, String errorMessage) {
        this.rowNumber = rowNumber;
        this.errorMessage = errorMessage;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "Row " + rowNumber + ": " + errorMessage;
    }
}


