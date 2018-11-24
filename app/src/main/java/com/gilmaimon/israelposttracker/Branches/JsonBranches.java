package com.gilmaimon.israelposttracker.Branches;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonBranches extends BranchesProvider {

    private List<Branch> allBranches = null;
    private boolean available = true;

    private static List<Branch> LoadAllBranchesFromJson(String jsonString) throws JSONException {
        List<Branch> resultBranches = new ArrayList<>();
        JSONArray branchesJson = new JSONArray(jsonString);
        for(int iBranch = 0; iBranch < branchesJson.length(); iBranch++) {
            JSONObject branchJson = branchesJson.getJSONObject(iBranch);
            resultBranches.add(
                    new Branch(
                            branchJson.getInt("number"),
                            branchJson.getString("name"),
                            branchJson.getString("location")
                     )
            );
        }
        return resultBranches;
    }

    public JsonBranches(String jsonString){
        try {
            allBranches = LoadAllBranchesFromJson(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            available = false;
        }
    }

    @Override
    public boolean available() {
        return available;
    }

    @NonNull
    @Override
    List<Branch> _getAllImpl() {
        return new ArrayList<>(allBranches);
    }

    @Override
    Branch _getBranchImpl(int id) {
        for(int iBranch = 0; iBranch < allBranches.size(); iBranch++) {
            if(allBranches.get(iBranch).getId() == id) {
                return allBranches.get(iBranch);
            }
        }
        return null;
    }
}
