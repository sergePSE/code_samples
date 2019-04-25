package dbFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.TimeParameters;
import server.hibernate.models.Node;
import server.hibernate.models.Task;
import dbFill.taskContext.*;

import java.util.HashSet;
import java.util.Set;

public class TaskFill {
    private final double TASK_POSSIBILITY = 10.0/(3600*24); // 10 times per day in average
    private final double LAST_TASK_POSSIBILITY = 0.5;
    private final double MAX_TASK_STEPS_DURATION = 5;
    private final double IS_EXECUTABLE_POSSIBILITY = 0.5;
    private final int TASKS_IN_TRANSACTION = 1000;
    public Set<Task> fillTasks(Session session, TimeParameters timeParameters, Node node)
    {
        Set<Task> tasks = new HashSet<Task>();
        ValueGenerators valueGenerators = new ValueGenerators(new NameGenerator(), new ArgsGenerator(),
                new RanksGenerator());
        TaskTime taskTime = new TaskTime();
        taskTime.currentTime = System.currentTimeMillis();
        taskTime.timeParameters = timeParameters;

        Transaction transaction = session.beginTransaction();

        long tasksInTransaction = 0;
        for (taskTime.startTime = timeParameters.startDate; taskTime.startTime < timeParameters.endDate;
             taskTime.startTime+= timeParameters.durationStep) {
            if (Math.random() > TASK_POSSIBILITY)
                continue;
            Task task = insertRandomTask(session, valueGenerators, node, taskTime);
            tasks.add(task);
            if (tasksInTransaction++ > TASKS_IN_TRANSACTION)
            {
                tasksInTransaction = 0;
                transaction.commit();
                transaction = session.beginTransaction();
            }
        }
        if (Math.random() < LAST_TASK_POSSIBILITY)
        {
            Task task = insertRandomTask(session, valueGenerators, node, taskTime);
            tasks.add(task);
        }
        transaction.commit();
        return tasks;
    }


    private class ValueGenerators
    {
        ITaskValueGenerator nameGenerator;
        ITaskValueGenerator argsGenerator;
        ITaskValueGenerator ranksGenerator;

        public ValueGenerators(ITaskValueGenerator nameGenerator, ITaskValueGenerator argsGenerator,
                               ITaskValueGenerator ranksGenerator) {
            this.nameGenerator = nameGenerator;
            this.argsGenerator = argsGenerator;
            this.ranksGenerator = ranksGenerator;
        }
    }

    private class TaskTime
    {
        TimeParameters timeParameters;
        long startTime;
        long currentTime;
    }

    private Task insertRandomTask(Session session, ValueGenerators valueGenerators, Node node, TaskTime taskTime)
    {
        Task task = new Task();
        task.setName(valueGenerators.nameGenerator.getStringValue());
        task.setArgs(valueGenerators.argsGenerator.getStringValue());
        task.setRanks(valueGenerators.ranksGenerator.getStringValue());
        task.setIsExecutable(getExecutable());
        task.setNode(node);
        TaskTimeProps timeProps = getTaskStartTime(taskTime.startTime, taskTime.timeParameters.durationStep,
                taskTime.currentTime);
        task.setJobStartTime(timeProps.startTime);
        task.setIsEnded(timeProps.isEnded);

        session.save(task);
        return task;
    }

    private int getExecutable() {
        if (Math.random() > IS_EXECUTABLE_POSSIBILITY)
            return 1;
        return 0;
    }

    private class TaskTimeProps
    {
        public long startTime;
        public long endTime;
        public int isEnded;
    }

    private TaskTimeProps getTaskStartTime(long taskStartTime, long timestep, long currentTime)
    {
        TaskTimeProps taskTimeProps = new TaskTimeProps();
        taskTimeProps.startTime = taskStartTime;
        taskTimeProps.endTime = taskStartTime + (long)(Math.random() * MAX_TASK_STEPS_DURATION * timestep);
        taskTimeProps.isEnded = taskTimeProps.endTime > currentTime? 0 : 1;
        return taskTimeProps;
    }
}
