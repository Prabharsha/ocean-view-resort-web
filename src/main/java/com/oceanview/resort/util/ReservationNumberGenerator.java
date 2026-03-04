package com.oceanview.resort.util;

import com.oceanview.resort.dao.ReservationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton for generating unique reservation numbers.
 * Format: OVR-YYYY-NNNNNN (e.g. OVR-2026-000001)
 *
 * <p><b>Design Pattern: Singleton Pattern</b></p>
 * <p>Uses AtomicLong for thread-safe sequence generation.</p>
 */
public class ReservationNumberGenerator {

    private static final Logger log = LoggerFactory.getLogger(ReservationNumberGenerator.class);
    private final AtomicLong sequence = new AtomicLong(0);

    public ReservationNumberGenerator(ReservationDAO reservationDAO) {
        int currentYear = Year.now().getValue();
        long maxSeq = reservationDAO.findMaxSequenceByYear(currentYear);
        sequence.set(maxSeq);
        log.info("ReservationNumberGenerator initialised: next = OVR-{}-{}",
                 currentYear, String.format("%06d", maxSeq + 1));
    }

    /** Generates the next unique reservation number. */
    public String generateNext() {
        long nextVal = sequence.incrementAndGet();
        return String.format("OVR-%d-%06d", Year.now().getValue(), nextVal);
    }
}

