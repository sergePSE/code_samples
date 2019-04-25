package dbFill;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import server.hibernate.models.Context;
import server.hibernate.models.CustomDataValue;

public class CustomDataValueFill {

    public void fillCustomContextData(Session session, Context taskContext, Long taskTime, String value)
    {
        Transaction transaction = session.beginTransaction();
        fillCustomData(session, taskContext, taskTime, value);
        transaction.commit();
    }

    private void fillCustomData(Session session, Context context, long time, String value)
    {
        CustomDataValue customDataHeader = new CustomDataValue();
        customDataHeader.setTime(time);
        customDataHeader.setValue(value);
        customDataHeader.setContext(context);
        session.save(customDataHeader);
    }
}
