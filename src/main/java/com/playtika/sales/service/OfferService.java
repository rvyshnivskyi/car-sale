package com.playtika.sales.service;

import com.playtika.sales.domain.Offer;
import com.playtika.sales.domain.Person;

import java.util.List;

public interface OfferService {

    Offer addOfferForSalePropose(Offer offer, long carId);

    List<Offer> getAllActiveOffers(long carId);

    Person acceptActiveOffer(long offerId);
}
