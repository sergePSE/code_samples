package dbFill.taskContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RanksGenerator implements ITaskValueGenerator {
    public final static int MAX_RANK = 16;
    public final static int MAX_RANK_RANGES = 3;
    public String getStringValue() {
        List<Integer> orderedRanks = getRandomUniqueRanks();
        List<String> rankPairs = new ArrayList<String>();
        for (int i = 0; i < orderedRanks.size(); i+=2) {
            rankPairs.add(String.format("%d-%d", orderedRanks.get(i), orderedRanks.get(i + 1)));
        }
        return String.join(",", rankPairs);
    }

    private List<Integer> getRandomUniqueRanks()
    {
        List<Integer> ranks = new ArrayList<Integer>();
        int rankRanges = (int)((MAX_RANK_RANGES - 1) * Math.random() + 1);
        for (int i = 0; i < rankRanges * 2; i++) {
            int rank = (int)(MAX_RANK * Math.random());
            while(ranks.contains(rank))
            {
                rank = (int)(MAX_RANK * Math.random());
            }
            ranks.add(rank);
        }
        Collections.sort(ranks);
        return ranks;
    }
}
