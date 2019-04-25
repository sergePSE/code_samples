package server.dbDump.dbCheck;

import org.hibernate.Session;
import org.hibernate.StatelessSession;

public interface IDbCheck {
    IDbCleanIterator isLimitReached(StatelessSession session);
}
