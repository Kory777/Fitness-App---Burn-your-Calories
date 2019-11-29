package com.example.burncalories.view;

import com.example.burncalories.Account;
import com.example.burncalories.AccountData;

import java.util.List;

public class RankItem {
    //先写成Account 后面要创建一个新的类来处理排名
    private List<AccountData>data;
    private String rankTitle;

    public RankItem(String rankTitle,List<AccountData>data){
        this.rankTitle = rankTitle;
        this.data = data;
    }

    public List<AccountData> getData() {
        return data;
    }

    public void setData(List<AccountData> data) {
        this.data = data;
    }

    public String getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(String rankTitle) {
        this.rankTitle = rankTitle;
    }
}
