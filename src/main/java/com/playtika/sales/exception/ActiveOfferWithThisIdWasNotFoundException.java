package com.playtika.sales.exception;

public class ActiveOfferWithThisIdWasNotFoundException extends RuntimeException {
    public ActiveOfferWithThisIdWasNotFoundException(long offerId) {
        super(String.format("Can't find active offer with id = [%d]", offerId));
    }
}
