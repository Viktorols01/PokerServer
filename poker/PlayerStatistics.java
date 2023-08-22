package poker;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatistics {
    private int foldCount;
    private int matchCount;
    private int checkCount;
    private List<Integer> raiseList;

    public PlayerStatistics() {
        this.raiseList = new ArrayList<Integer>();
    }

    public void addFold() {
        this.foldCount++;
    }

    public int getFolds() {
        return this.foldCount;
    }

    public void addMatch() {
        this.matchCount++;
    }

    public int getMatches() {
        return this.matchCount;
    }

    public void addCheck() {
        this.checkCount++;
    }

    public int getChecks() {
        return this.checkCount;
    }

    public void addRaise(int n) {
        this.raiseList.add(n);
    }

    public int getRaises() {
        return this.raiseList.size();
    }

    public int getAverageRaise() {
        if (this.raiseList.size() == 0) {
            return 0;
        }

        int sum = 0;
        for (int raise : raiseList) {
            sum += raise;
        }
        sum /= this.raiseList.size();
        return sum;
    }
}
