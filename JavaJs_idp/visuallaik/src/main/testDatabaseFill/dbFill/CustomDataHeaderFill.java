package dbFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.hibernate.models.CustomDataHeader;
import dbFill.utils.StringUtils;

import java.util.*;

public class CustomDataHeaderFill {

    private final static int TASK_NAME_LEN = 10;

    public Set<CustomDataHeader> fillWithCustomDataHeader(Session session, int customHeaderCount)
    {
        Map<String, CustomDataHeader> customDataHeaders = new HashMap<String, CustomDataHeader>();
        Transaction transaction = session.beginTransaction();
        Set<CustomDataHeader> topCustomDataHeaders = insertRandomCustomDataHeaders(session, customHeaderCount);
        transaction.commit();
        return topCustomDataHeaders;
    }

    private Set<CustomDataHeader> insertRandomCustomDataHeaders(Session session, int count)
    {
        Set<CustomDataHeader> customDataHeaders = new HashSet<CustomDataHeader>();
        for (int i = 0; i < count; i++) {
            CustomDataHeader dataHeader = new CustomDataHeader();
            dataHeader.setValue(StringUtils.generateRandomString(TASK_NAME_LEN));
            customDataHeaders.add(dataHeader);
            session.save(dataHeader);
        }
        return customDataHeaders;
    }
}
