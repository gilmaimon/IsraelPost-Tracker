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
            if(getStringOrNull(branchJson, "name") == null) continue;
            resultBranches.add(
                    new Branch(
                            branchJson.getInt("id"),
                            getStringOrNull(branchJson, "name"),
                            getStringOrNull(branchJson, "address"),
                            getStringOrNull(branchJson, "active"),
                            getStringOrNull(branchJson, "phone")
                    )
            );
        }
        return resultBranches;
    }

    static String getStringOrNull(JSONObject object, String key) throws JSONException {
        String value = object.getString(key);
        if(value.equals("null")) return null;
        if(value.trim().equals("")) return null;
        return value;
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
