package server.dbDump.dbCheck;

import org.hibernate.StatelessSession;
import java.time.Duration;
import java.util.Calendar;

public class DateDbCheck implements IDbCheck {
    private long minRequiredDate;
    private long maxAcceptableDate;

    public DateDbCheck(int minDays, long maxDays) {
        long nowMs = Calendar.getInstance().getTimeInMillis();
        this.minRequiredDate = nowMs - Duration.ofDays(minDays).toMillis();
        this.maxAcceptableDate = nowMs - Duration.ofDays(maxDays).toMillis();
    }

    @Override
    public IDbCleanIterator isLimitReached(StatelessSession session) {
        Long minBigDate = ((Long)session.createQuery("select MIN(xValue) from GraphSampleData ")
                .getSingleResult());
        if (minBigDate == null)
            return null;
        long minDate = minBigDate.longValue();
        if (minDate > maxAcceptableDate)
            return null;
        return new DbCleanIterator(minDate, minRequiredDate);
    }
}
