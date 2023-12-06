package com.github.vssavin.usmancore.config;

/**
 * Provides password pattern configuration.
 *
 * @author vssavin on 05.12.2023
 */
public class UsmanAuthPasswordConfig {

    private int minLength = 4;

    private int maxLength = 0;

    private boolean atLeastOneDigit = false;

    private boolean atLeastOneLowerCaseLatin = false;

    private boolean atLeastOneUpperCaseLatin = false;

    private boolean atLeastOneSpecialCharacter = false;

    public UsmanAuthPasswordConfig minLength(int minLength) {
        this.minLength = minLength;
        return this;
    }

    public UsmanAuthPasswordConfig maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public UsmanAuthPasswordConfig atLeastOneDigit(boolean atLeastOneDigit) {
        this.atLeastOneDigit = atLeastOneDigit;
        return this;
    }

    public UsmanAuthPasswordConfig atLeastOneLowerCaseLatin(boolean atLeastOneLowerCaseLatin) {
        this.atLeastOneLowerCaseLatin = atLeastOneLowerCaseLatin;
        return this;
    }

    public UsmanAuthPasswordConfig atLeastOneUpperCaseLatin(boolean atLeastOneUpperCaseLatin) {
        this.atLeastOneUpperCaseLatin = atLeastOneUpperCaseLatin;
        return this;
    }

    public UsmanAuthPasswordConfig atLeastOneSpecialCharacter(boolean atLeastOneSpecialCharacter) {
        this.atLeastOneSpecialCharacter = atLeastOneSpecialCharacter;
        return this;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isAtLeastOneDigit() {
        return atLeastOneDigit;
    }

    public boolean isAtLeastOneLowerCaseLatin() {
        return atLeastOneLowerCaseLatin;
    }

    public boolean isAtLeastOneUpperCaseLatin() {
        return atLeastOneUpperCaseLatin;
    }

    public boolean isAtLeastOneSpecialCharacter() {
        return atLeastOneSpecialCharacter;
    }

    @Override
    public String toString() {
        return "UsmanAuthPasswordConfig{" + "minLength=" + minLength + ", maxLength=" + maxLength + ", atLeastOneDigit="
                + atLeastOneDigit + ", atLeastOneLowerCaseLatin=" + atLeastOneLowerCaseLatin
                + ", atLeastOneUpperCaseLatin=" + atLeastOneUpperCaseLatin + ", atLeastOneSpecialCharacter="
                + atLeastOneSpecialCharacter + '}';
    }

}
