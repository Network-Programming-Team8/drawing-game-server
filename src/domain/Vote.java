package domain;

import java.util.HashMap;
import java.util.Map;

public class Vote {

    private final Map<Integer, Integer> voteMap;

    public Vote(){
        voteMap = new HashMap<>();
    }

    public void addVote(int id){
        if(voteMap.containsKey((id))){
            int numOfVote = voteMap.get(id);
            voteMap.remove(id);
            voteMap.put(id, numOfVote+1);
        }
        else{
            voteMap.put(id, 1);
        }
    }

    public Map<Integer, Integer> getVoteMap() { return voteMap; }
}
